<script setup lang="ts">
import {onBeforeRouteLeave, useRoute, useRouter} from "vue-router";
import {computed, type ComputedRef, onMounted, ref, type Ref} from "vue";
import {sleep} from "@/utils/utils";
import {useProductionStepStore} from "@/stores/model/productionStepStore";
import {type RecipeStore, useRecipeStore} from "@/stores/model/recipeStore";
import type {Changelist, Item, LocalResource, ProductionStep, Recipe} from "@/types/model/standalone";
import {type LocalResourceStore, useLocalResourceStore} from "@/stores/model/localResourceStore";
import {ParsedFraction} from "@/utils/fractionUtils";
import {type ItemStore, useItemStore} from "@/stores/model/itemStore";
import IconImg from "@/components/common/IconImg.vue";
import FractionDisplay from "@/components/common/FractionDisplay.vue";
import {computedAsync} from "@vueuse/core";
import {ProductionStepApi, useProductionStepApi} from "@/api/model/useProductionStepApi";
import type {Fraction} from "@/types/model/basic";
import ItemIconName from "@/components/common/ItemIconName.vue";
import {type UserSettingsStore, useUserSettingsStore} from "@/stores/userSettingsStore";
import {OptimizeProductionStepMachineCountRounding} from "@/types/userSettings";
import {useCurrentGameAndSaveStore} from "@/stores/currentGameAndSaveStore";
import {useChangelistStore} from "@/stores/model/changelistStore";
import {Check, Close} from "@element-plus/icons-vue";
import {type ChangelistApi, useChangelistApi} from "@/api/model/useChangelistApi";

const router = useRouter();
const route = useRoute();

const visible: Ref<boolean> = ref(true);

onBeforeRouteLeave(async () => {
  if (visible.value) {
    await close();
  }
});

async function closeAndGoBack(): Promise<void> {
  await close();
  router.back();
}

async function close(): Promise<void> {
  visible.value = false;
  await sleep(400);
}

const productionStepStore = useProductionStepStore();
const resourceStore: LocalResourceStore = useLocalResourceStore();
const recipeStore: RecipeStore = useRecipeStore();
const itemStore: ItemStore = useItemStore();
const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const changelistStore = useChangelistStore();

const productionStepApi: ProductionStepApi = useProductionStepApi();
const changelistApi: ChangelistApi = useChangelistApi();

const userSettingsStore: UserSettingsStore = useUserSettingsStore();

const productionStepId: ComputedRef<number> = computed(() => Number(route.params.productionStepId));
const resourceId: ComputedRef<number> = computed(() => Number(route.params.resourceId));

const productionStep: ComputedRef<ProductionStep | undefined> = computed(() => productionStepStore.getById(productionStepId.value));
const resource: ComputedRef<LocalResource | undefined> = computed(() => resourceStore.getById(resourceId.value));
const resourceItem: ComputedRef<Item | undefined> = computed(() => itemStore.getById(resource.value?.itemId));
const recipe: ComputedRef<Recipe | undefined> = computed(() => recipeStore.getById(productionStep.value?.recipeId));

const recipeFirstOutputItem: ComputedRef<Item | undefined> = computed(() => {
  if (!recipe.value) return undefined;
  if (recipe.value.products.length === 0) return undefined;
  return itemStore.getById(recipe.value.products[0].itemId);
});

const recipeIconId: ComputedRef<number> = computed(() => {
  if (!recipe.value) return 0;
  if (recipe.value.iconId) return recipe.value.iconId;
  if (!recipeFirstOutputItem.value) return 0;
  return recipeFirstOutputItem.value.iconId;
});

const recipeName: ComputedRef<string> = computed(() => {
  if (!recipe.value) return '';
  if (recipe.value.name) return recipe.value.name;
  if (!recipeFirstOutputItem.value) return '';
  return recipeFirstOutputItem.value.name;
});

const recipeIsSameAsResource: ComputedRef<boolean> = computed(() => {
  return recipeName.value === resourceItem.value?.name && recipeIconId.value === resourceItem.value?.iconId;
});

const isProducer: ComputedRef<boolean> = computed(() => {
  if (!productionStep.value) return false;
  if (!resource.value) return false;
  if (!recipe.value) return false;
  let amount: ParsedFraction = ParsedFraction.ZERO;
  for (const productionEntry of productionStep.value.inputs) {
    if (productionEntry.itemId === resource.value.itemId) {
      amount = amount.subtract(ParsedFraction.of(productionEntry.quantity.current));
    }
  }
  for (const productionEntry of productionStep.value.outputs) {
    if (productionEntry.itemId === resource.value.itemId) {
      amount = amount.add(ParsedFraction.of(productionEntry.quantity.current));
    }
  }
  return amount.isPositive();
});

const resourceNet: ComputedRef<ParsedFraction> = computed(() => {
  if (!resource.value) return ParsedFraction.ZERO;
  return ParsedFraction.of(resource.value.overProduced.current);
});

const optimalMachineCount: Ref<ParsedFraction | undefined> = computedAsync(
    async () => {
      const fraction: Fraction = await productionStepApi.findSatisfaction(productionStepId.value, resourceId.value);
      return ParsedFraction.of(fraction);
    },
    undefined
);

const roundedOptimalMachineCount: Ref<ParsedFraction | undefined> = computed(() => {
  if (!optimalMachineCount.value) return undefined;
  const rounding: OptimizeProductionStepMachineCountRounding = userSettingsStore.optimizeProductionStep.machineCountRounding;
  switch (rounding) {
    case OptimizeProductionStepMachineCountRounding.None:
      return optimalMachineCount.value;
    case OptimizeProductionStepMachineCountRounding.RoundUp:
      return optimalMachineCount.value.roundUp();
    case OptimizeProductionStepMachineCountRounding.RoundDown:
      return optimalMachineCount.value.roundDown();
    case OptimizeProductionStepMachineCountRounding.RoundToNearest:
      return optimalMachineCount.value.roundToNearestInteger();
    default:
      return undefined;
  }
});

const selectedChangelistId: Ref<number | undefined> = ref(undefined);

const changelists: ComputedRef<Changelist[]> = computed(() => {
  return changelistStore.getBySaveId(currentGameAndSaveStore.currentSaveId)
      .sort((a, b) => a.ordinal - b.ordinal);
});

onMounted(() => {
  for (const changelist of changelists.value) {
    if (changelist.primary) {
      selectedChangelistId.value = changelist.id;
      break;
    }
  }
});

const changelistHasChangeAlready: ComputedRef<boolean> = computed(() => {
  if (!selectedChangelistId.value) return false;
  const changelist: Changelist | undefined = changelistStore.getById(selectedChangelistId.value);
  if (!changelist) return false;
  for (const productionStepChange of changelist.productionStepChanges) {
    if (productionStepChange.productionStepId === productionStepId.value) {
      return true;
    }
  }
  return false;
});

const isSaving: Ref<boolean> = ref(false);

function apply(): void {
  if (!roundedOptimalMachineCount.value) return;
  if (userSettingsStore.optimizeProductionStep.applyDirectly) {
    isSaving.value = true;
    productionStepApi.updateMachineCount(
        productionStepId.value,
        roundedOptimalMachineCount.value.toFraction(),
    ).then(() => {
      closeAndGoBack();
    });
    return;
  }

  if (!selectedChangelistId.value) return;
  if (!productionStep.value) return;

  const machineCountChange: Fraction = roundedOptimalMachineCount.value
      .subtract(ParsedFraction.of(productionStep.value.machineCount))
      .toFraction();

  isSaving.value = true;
  changelistApi.updateMachineCountChange(
      selectedChangelistId.value,
      productionStepId.value,
      machineCountChange
  ).then(() => {
    closeAndGoBack();
  });
}
</script>

<template>
  <el-dialog
      :model-value="visible"
      :before-close="closeAndGoBack"
      width="600px"
      title="Optimize production step"
      :close-on-click-modal="true"
  >
    <template v-if="resourceItem">
      <div v-if="resourceItem">
        Optimizing {{ recipeIsSameAsResource ? 'this' : '' }} production step
        <template v-if="!recipeIsSameAsResource">
          <icon-img :icon="recipeIconId" :size="24" style='vertical-align:middle;'/>
          <b>{{ recipeName }}</b>
        </template>
        for resource
        <ItemIconName :item="resourceItem"/>
        will adjust the machine count so that the {{ isProducer ? 'produced' : 'consumed' }} amount of
        <ItemIconName :item="resourceItem"/>
        from this production step matches your factory's total
        <ItemIconName :item="resourceItem"/>
        {{ isProducer ? 'consumption' : 'production' }}.
      </div>
      <div style="margin-top: 12px;">
        <table>
          <tbody>
          <tr>
            <td>Current machine count:</td>
            <td>
              <FractionDisplay :fraction="productionStep?.machineCount"/>
            </td>
          </tr>
          <tr>
            <td>
              <ItemIconName :item="resourceItem"/>
              {{ resourceNet.isPositive() ? 'surplus' : resourceNet.isNegative() ? 'deficit' : 'net' }} in factory:
            </td>
            <td>
              <FractionDisplay :fraction="resourceNet" color="auto" is-throughput force-sign/>
            </td>
          </tr>
          <tr>
            <td>Optimal machine count:</td>
            <td>
              <template v-if="optimalMachineCount === undefined">loading...</template>
              <template v-else>
                <FractionDisplay :fraction="optimalMachineCount"/>
                <template v-if="roundedOptimalMachineCount && !optimalMachineCount.equals(roundedOptimalMachineCount)">
                  <span>, rounded to </span>
                  <FractionDisplay :fraction="roundedOptimalMachineCount"/>
                </template>
              </template>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div style="margin-top: 12px;">
        <h3>Options</h3>
        <p>
          Round machine count:
          <el-select v-model="userSettingsStore.optimizeProductionStep.machineCountRounding" style="width: 240px;">
            <el-option label="No rounding"
                       :value="OptimizeProductionStepMachineCountRounding.None"/>
            <el-option label="Round up"
                       :value="OptimizeProductionStepMachineCountRounding.RoundUp"/>
            <el-option label="Round down"
                       :value="OptimizeProductionStepMachineCountRounding.RoundDown"/>
            <el-option label="Round to nearest integer"
                       :value="OptimizeProductionStepMachineCountRounding.RoundToNearest"/>
          </el-select>
        </p>
        <p>
          <el-radio-group v-model="userSettingsStore.optimizeProductionStep.applyDirectly">
            <el-radio :value="true">Apply this change directly</el-radio>
            <el-radio :value="false">
              Save to changelist:
              <el-select v-model="selectedChangelistId" style="width: 180px;"
                         :disabled="userSettingsStore.optimizeProductionStep.applyDirectly">
                <el-option v-for="changelist in changelists" :key="changelist.id"
                           :value="changelist.id"
                           :label="changelist.name"/>
              </el-select>
            </el-radio>
          </el-radio-group>
          <el-alert v-if="!userSettingsStore.optimizeProductionStep.applyDirectly && changelistHasChangeAlready"
                    type="warning" show-icon :closable="false" style="margin: 12px 0;">
            This changelist already contains a change for this production step.<br/>
            Applying this change will overwrite the existing change.
          </el-alert>
        </p>
      </div>
      <div style="margin-top: 24px; overflow: auto;">
        <div style="float: right">
          <el-button :icon="Close" @click="closeAndGoBack">
            Cancel
          </el-button>
          <el-button type="primary" :icon="Check" @click="apply()" :loading="isSaving"
                     :disabled="!roundedOptimalMachineCount || roundedOptimalMachineCount.toFraction() === productionStep?.machineCount">
            OK
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
table td {
  padding: 4px;

  &:first-child {
    text-align: right;
  }
}
</style>