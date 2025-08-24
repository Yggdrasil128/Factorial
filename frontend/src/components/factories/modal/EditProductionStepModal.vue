<script setup lang="ts">
import {
  onBeforeRouteUpdate,
  type RouteLocationNormalizedLoadedGeneric,
  type Router,
  useRoute,
  useRouter
} from 'vue-router';
import {type ProductionStepStore, useProductionStepStore} from '@/stores/model/productionStepStore';
import {computed, type ComputedRef, reactive, ref, type Ref, watch} from 'vue';
import type {Machine, ProductionStep, Recipe, RecipeModifier} from '@/types/model/standalone';
import _ from 'lodash';
import {ElFormItem, type FormRules} from 'element-plus';
import CascaderSelect from '@/components/common/input/CascaderSelect.vue';
import EditModal from '@/components/common/EditModal.vue';
import {type RecipeStore, useRecipeStore} from '@/stores/model/recipeStore';
import {type MachineStore, useMachineStore} from '@/stores/model/machineStore';
import {type RecipeModifierStore, useRecipeModifierStore} from '@/stores/model/recipeModifierStore';
import {type CurrentGameAndSaveStore, useCurrentGameAndSaveStore} from '@/stores/currentGameAndSaveStore';
import IconImg from '@/components/common/IconImg.vue';
import {elFormFractionValidator, ParsedFraction} from '@/utils/fractionUtils';
import {Minus, Plus} from '@element-plus/icons-vue';
import type {RuleItem} from 'async-validator/dist-types/interface';
import {ProductionStepApi, useProductionStepApi} from '@/api/model/useProductionStepApi';
import FractionInput from '@/components/common/input/FractionInput.vue';

const router: Router = useRouter();
const route: RouteLocationNormalizedLoadedGeneric = useRoute();

const currentGameAndSaveStore: CurrentGameAndSaveStore = useCurrentGameAndSaveStore();
const productionStepStore: ProductionStepStore = useProductionStepStore();
const recipeStore: RecipeStore = useRecipeStore();
const recipeModifierStore: RecipeModifierStore = useRecipeModifierStore();
const machineStore: MachineStore = useMachineStore();

const productionStepApi: ProductionStepApi = useProductionStepApi();

const isSaving: Ref<boolean> = ref(false);
const productionStep: Ref<Partial<ProductionStep>> = ref({});
const original: Ref<Partial<ProductionStep>> = ref({});
const editModal: Ref<InstanceType<typeof EditModal> | undefined> = ref();

const hasChanges: ComputedRef<boolean> = computed(() => !_.isEqual(productionStep.value, original.value));

const formRules: FormRules = reactive({
  recipeId: [{ required: true, message: 'Please select a recipe', trigger: 'blur' }],
  machineId: [{ required: true, message: 'Please select a machine', trigger: 'blur' }],
  machineCount: [{
    required: true, message: 'Please enter a valid machine count', trigger: 'blur',
    validator: elFormFractionValidator, allowZero: true, allowNegative: false,
  } as RuleItem],
});

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  if (route.name === 'newProductionStep') {
    productionStep.value = {
      factoryId: Number(route.params.factoryId),
      machineId: 0,
      recipeId: 0,
      modifierIds: [],
      machineCount: '1',
    };
  } else {
    const productionStepId: number = Number(route.params.editProductionStepId);
    const currentProductionStep: ProductionStep | undefined = productionStepStore.getById(productionStepId);
    if (!currentProductionStep) {
      console.error('ProductionStep with id ' + productionStepId + ' not found');
      router.push({ name: 'factories', params: { factoryId: route.params.factoryId } });
      return;
    }
    productionStep.value = {
      id: productionStepId,
      machineId: currentProductionStep.machineId,
      recipeId: currentProductionStep.recipeId,
      modifierIds: [...currentProductionStep.modifierIds],
      machineCount: currentProductionStep.machineCount,
    };
  }
  original.value = _.clone(productionStep.value);
  isSaving.value = false;
}

initFromRoute(route);
onBeforeRouteUpdate(initFromRoute);

async function submitForm(): Promise<void> {
  if (!(await editModal.value?.validate())) {
    return;
  }

  isSaving.value = true;

  if (route.name === 'newProductionStep') {
    await productionStepApi.create(productionStep.value);
  } else {
    await productionStepApi.edit(productionStep.value);
  }

  await router.push({ name: 'factories', params: { factoryId: route.params.factoryId } });
}

const recipes: ComputedRef<Recipe[]> = computed(() =>
  recipeStore.getByGameId(currentGameAndSaveStore.currentGameId),
);

const recipe: ComputedRef<Recipe | undefined> = computed(() => {
  if (!productionStep.value.recipeId) return undefined;
  return recipeStore.getById(productionStep.value.recipeId);
});

const machines: ComputedRef<Machine[]> = computed(() => {
  if (!recipe.value) return [];
  return recipe.value.applicableMachineIds
      .map((machineId: number) => machineStore.getById(machineId))
      .filter((machine: Machine | undefined) => machine !== undefined) as Machine[];
});

const modifiers: ComputedRef<RecipeModifier[]> = computed(() => {
  if (!recipe.value) return [];
  return recipe.value.applicableModifierIds
      .map((modifierId: number) => recipeModifierStore.getById(modifierId))
      .filter((modifier: RecipeModifier | undefined) => modifier !== undefined) as RecipeModifier[];
});

watch(() => productionStep.value.recipeId, () => {
  if (!productionStep.value.recipeId) {
    productionStep.value.machineId = 0;
    return;
  }
  if (!recipe.value) return;
  if (recipe.value.applicableMachineIds.length === 0) return;
  productionStep.value.machineId = recipe.value.applicableMachineIds[0];
});

const machineCountParsedFraction: Ref<ParsedFraction | undefined> = computed({
  get() {
    return productionStep.value.machineCount ? ParsedFraction.of(productionStep.value.machineCount) : undefined;
  },
  set(value: ParsedFraction | undefined) {
    productionStep.value.machineCount = value ? value.toFraction() : '';
  },
});

function incrementMachineCount(): void {
  if (machineCountParsedFraction.value) {
    machineCountParsedFraction.value = machineCountParsedFraction.value.add(ParsedFraction.ONE);
  }
}

function decrementMachineCount(): void {
  if (machineCountParsedFraction.value) {
    let newValue: ParsedFraction = machineCountParsedFraction.value.subtract(ParsedFraction.ONE);
    if (newValue.isNegative()) {
      newValue = ParsedFraction.ZERO;
    }
    machineCountParsedFraction.value = newValue;
  }
}

</script>

<template>
  <edit-modal
    :title="route.name === 'newProductionStep' ? 'New production step' : 'Edit production step'"
    form-label-width="120px"
    :form-model="productionStep"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="isSaving"
    @submit="submitForm"
    ref="editModal"
  >
    <template #description>
      A production step is a group of identical machines that are all processing the same recipe.
    </template>

    <template #form>
      <el-form-item label="Recipe" prop="recipeId">
        <cascader-select v-model="productionStep.recipeId!"
                         :options="recipes"
                         style="width: 100%;" />
      </el-form-item>

      <el-form-item label="Machine" prop="machineId">
        <cascader-select v-model="productionStep.machineId!"
                         :placeholder="!recipe ? 'Select recipe first' : 'Select'"
                         :options="machines"
                         :disabled="!productionStep.recipeId"
                         style="width: 100%;" />
      </el-form-item>

      <el-form-item label="Modifiers" prop="modifierIds">
        <el-select v-model="productionStep.modifierIds"
                   style="width: 100%;"
                   :placeholder="!recipe ? 'Select recipe first' : modifiers.length === 0 ? 'No applicable modifiers' : 'Select'"
                   :disabled="modifiers.length === 0"
                   multiple>
          <el-option v-for="modifier in modifiers"
                     :key="modifier.id"
                     :value="modifier.id"
                     :label="modifier.name">
            <icon-img :icon="modifier.iconId" :size="24" />
            {{ modifier.name }}
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="Machine count" prop="machineCount">
        <div style="display: flex; width: 100%; gap: 4px;">
          <fraction-input v-model:parsed-fraction="machineCountParsedFraction"
                          style="flex-grow: 1;" />
          <el-button :icon="Plus" @click="incrementMachineCount" />
          <el-button :icon="Minus" @click="decrementMachineCount"
                     :disabled="productionStep.machineCount === '0'"
                     style="margin-left: 0;" />
        </div>
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>