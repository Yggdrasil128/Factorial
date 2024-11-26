<script setup lang="ts">
import type { ProductionStepChange } from '@/components/factories/modal/ViewChangelistModal.vue';
import { useMachineStore } from '@/stores/model/machineStore';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import { computed, type ComputedRef, ref, type Ref } from 'vue';
import type { Changelist, Machine, Recipe, RecipeModifier } from '@/types/model/standalone';
import { isTruthy } from '@/utils/utils';
import IconImg from '@/components/common/IconImg.vue';
import { useRecipeIconService } from '@/services/useRecipeIconService';
import FractionInput from '@/components/common/input/FractionInput.vue';
import { ParsedFraction } from '@/utils/fractionUtils';
import { Check, Delete, Minus, Plus, Right } from '@element-plus/icons-vue';
import { ElButtonGroup } from 'element-plus';
import { until } from '@vueuse/core';
import CustomElTooltip from '@/components/common/CustomElTooltip.vue';
import { useChangelistApi } from '@/api/model/useChangelistApi';

export interface ViewChangelistModalProductionStepProps {
  changelist: Changelist;
  change: ProductionStepChange;
  productionStepIdsToClear: Set<number>;
}

const props: ViewChangelistModalProductionStepProps = defineProps<ViewChangelistModalProductionStepProps>();

const machineStore = useMachineStore();
const recipeStore = useRecipeStore();
const recipeModifierStore = useRecipeModifierStore();
const recipeIconService = useRecipeIconService();
const changelistApi = useChangelistApi();

const toBeCleared: Ref<boolean> = computed({
  get(): boolean {
    return props.productionStepIdsToClear.has(props.change.productionStep.id);
  },
  set(value: boolean): void {
    if (value) {
      props.productionStepIdsToClear.add(props.change.productionStep.id);
    } else {
      props.productionStepIdsToClear.delete(props.change.productionStep.id);
    }
  },
});

const machine: ComputedRef<Machine | undefined> = computed(() =>
  machineStore.getById(props.change.productionStep.machineId),
);

const recipe: ComputedRef<Recipe | undefined> = computed(() =>
  recipeStore.getById(props.change.productionStep.recipeId),
);

const recipeIconId: ComputedRef<number> = computed(() =>
  recipe.value ? recipeIconService.getRecipeIconId(recipe.value) : 0,
);

const modifiers: ComputedRef<RecipeModifier[]> = computed(() =>
  props.change.productionStep.modifierIds
    .map(recipeModifierStore.getById)
    .filter(isTruthy) as RecipeModifier[],
);

const currentMachineCount: ComputedRef<ParsedFraction> = computed(() =>
  ParsedFraction.of(props.change.productionStep.machineCount),
);

const changeModel: Ref<ParsedFraction> = computed({
  get() {
    if (toBeCleared.value) return ParsedFraction.ZERO;
    return ParsedFraction.of(props.change.change);
  },
  set(value: ParsedFraction) {
    if (!value) return;
    console.log('Set change to ' + value.toFraction());

    if (value.isZero()) {
      toBeCleared.value = true;
    } else {
      toBeCleared.value = false;

      buttonsDisabled.value = true;
      changelistApi.updateMachineCountChange(
        props.changelist.id,
        props.change.productionStep.id,
        value.toFraction(),
      ).finally(() => {
        buttonsDisabled.value = false;
      });
    }
  },
});

const plusButtonLoading: Ref<boolean> = ref(false);
const minusButtonLoading: Ref<boolean> = ref(false);
const buttonsDisabled: Ref<boolean> = ref(false);

function plusOne(): void {
  changeModel.value = changeModel.value.add(ParsedFraction.ONE);
  plusButtonLoading.value = true;
  until(buttonsDisabled).toBe(false, { timeout: 5000 })
    .finally(() => plusButtonLoading.value = false);
}

function minusOne(): void {
  let newValue = changeModel.value.subtract(ParsedFraction.ONE);
  if (newValue.add(currentMachineCount.value).isLessThanZero()) {
    newValue = currentMachineCount.value.negative();
  }
  changeModel.value = newValue;
  minusButtonLoading.value = true;
  until(buttonsDisabled).toBe(false)
    .then(() => minusButtonLoading.value = false);
}

function applyChange(): void {
  buttonsDisabled.value = true;
  if (toBeCleared.value) {
    toBeCleared.value = false;
    changelistApi.deleteChange(props.changelist.id, props.change.productionStep.id)
      .finally(() => buttonsDisabled.value = false);
  } else {
    changelistApi.applyChange(props.changelist.id, props.change.productionStep.id)
      .finally(() => buttonsDisabled.value = false);
  }
}

function deleteChange(): void {
  toBeCleared.value = false;
  buttonsDisabled.value = true;
  changelistApi.deleteChange(props.changelist.id, props.change.productionStep.id)
    .finally(() => buttonsDisabled.value = false);
}

</script>

<template>
  <div class="main column" :class="{toBeCleared: toBeCleared}">
    <div class="row items-center">
      <span>Recipe:</span>
      <IconImg :icon="recipeIconId" :size="24" />
      <span>{{ recipe?.name }}</span>

      <span style="margin-left: 20px;">Machine:</span>
      <IconImg :icon="machine?.iconId" :size="24" />
      <span>{{ machine?.name }}</span>
    </div>

    <div v-if="modifiers.length > 0"
         class="row items-center">
      <span>{{ modifiers.length === 1 ? 'Modifier' : 'Modifiers' }}:</span>
      <div v-for="(modifier, index) in modifiers" :key="index"
           class="row items-center" :style="{marginLeft: index > 0 ? 12 : 0}">
        <IconImg :icon="modifier.iconId" :size="24" />
        <span>{{ modifier.name }}</span>
      </div>
    </div>

    <div class="row items-center" style="gap: 4px;">
      Machine count:
      {{ currentMachineCount.toFraction() }}
      <el-icon :size="16">
        <Right />
      </el-icon>
      {{ changeModel.add(currentMachineCount).toFraction() }}
      <span v-if="toBeCleared" style="margin-left: 8px;">
        (This change will be deleted.)
      </span>
    </div>

    <div class="row items-center">
      <span>Change:</span>

      <FractionInput v-model:parsed-fraction="changeModel"
                     style="flex: 1 1 auto;" />

      <el-button-group style="flex: 0 0 auto;">
        <el-button
          :icon="Plus"
          @click="plusOne"
          :loading="plusButtonLoading"
          :disabled="buttonsDisabled"
        />
        <el-button
          :icon="Minus"
          @click="minusOne"
          :loading="minusButtonLoading"
          :disabled="buttonsDisabled || changeModel.add(currentMachineCount).isZero()"
        />
      </el-button-group>

      <el-button-group style="flex: 0 0 auto;">
        <custom-el-tooltip content="Apply">
          <el-button
            :icon="Check"
            @click="applyChange"
            :disabled="buttonsDisabled"
          />
        </custom-el-tooltip>
        <custom-el-tooltip content="Delete">
          <el-button
            :icon="Delete"
            class="destructive"
            @click="deleteChange"
            :disabled="buttonsDisabled || changeModel.add(currentMachineCount).isZero()"
          />
        </custom-el-tooltip>
      </el-button-group>
    </div>
  </div>
</template>

<style scoped>
.main {
  background-color: #282828;
  border-radius: 8px;
  padding: 4px 8px;
}

.main.toBeCleared {
  background-color: #362020;
}
</style>