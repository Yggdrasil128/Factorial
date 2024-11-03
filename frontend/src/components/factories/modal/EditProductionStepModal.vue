<script setup lang="ts">
import { onBeforeRouteUpdate, type RouteLocationNormalizedLoadedGeneric, useRoute, useRouter } from 'vue-router';
import { useIconStore } from '@/stores/model/iconStore';
import { useProductionStepStore } from '@/stores/model/productionStepStore';
import { computed, type ComputedRef, reactive, ref, type Ref, watch } from 'vue';
import type { Machine, ProductionStep, Recipe } from '@/types/model/standalone';
import _ from 'lodash';
import { ElFormItem } from 'element-plus';
import CascaderSelect from '@/components/CascaderSelect.vue';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import EditModal from '@/components/EditModal.vue';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useMachineStore } from '@/stores/model/machineStore';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import { useCurrentGameVersionStore } from '@/stores/currentGameVersionStore';

const router = useRouter();
const route = useRoute();

const currentGameVersionStore = useCurrentGameVersionStore();
const productionStepStore = useProductionStepStore();
const iconStore = useIconStore();
const recipeStore = useRecipeStore();
const recipeModifierStore = useRecipeModifierStore();
const machineStore = useMachineStore();

const isSaving: Ref<boolean> = ref(false);
const productionStep: Ref<Partial<ProductionStep>> = ref({});
const original: Ref<Partial<ProductionStep>> = ref({});
const editModal = ref();

const hasChanges = computed(() => !_.isEqual(productionStep.value, original.value));

const formRules = reactive({
  recipeId: [{ required: true, message: 'Please select a recipe', trigger: 'blur' }],
  machineId: [{ required: true, message: 'Please select a machine', trigger: 'blur' }]
});

function initFromRoute(route: RouteLocationNormalizedLoadedGeneric): void {
  if (route.name === 'newProductionStep') {
    productionStep.value = {
      factoryId: Number(route.params.factoryId),
      machineId: 0,
      recipeId: 0,
      modifierIds: [],
      machineCount: '1'
    };
  } else {
    const productionStepId: number = Number(route.params.editProductionStepId);
    const currentProductionStep: ProductionStep | undefined = productionStepStore.map.get(productionStepId);
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
      machineCount: currentProductionStep.machineCount
    };
  }
  original.value = _.clone(productionStep.value);
  isSaving.value = false;
}

initFromRoute(route);
onBeforeRouteUpdate(initFromRoute);

async function submitForm(): Promise<void> {
  if (!productionStep.value) {
    return;
  }

  if (!(await editModal.value.validate())) {
    return;
  }

  // TODO
}

const recipes: ComputedRef<Recipe[]> = computed(() =>
  [...recipeStore.map.values()]
    .filter(recipe => recipe.gameVersionId === currentGameVersionStore.gameVersion?.id)
);

const recipe: ComputedRef<Recipe | undefined> = computed(() => {
  if (!productionStep.value.recipeId) return undefined;
  return recipeStore.map.get(productionStep.value.recipeId);
});

const machines: ComputedRef<Machine[]> = computed(() => {
  if (!recipe.value) return [];
  return recipe.value.applicableMachineIds
    .map(machineId => machineStore.map.get(machineId))
    .filter(machine => machine !== undefined) as Machine[];
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
      Insert production step description here...
    </template>

    <template #form>
      <el-form-item label="Recipe" prop="recipeId">
        <cascader-select v-model="productionStep.recipeId!"
                         :options="recipes" />
      </el-form-item>

      <el-form-item label="Machine" prop="machineId">
        <cascader-select v-model="productionStep.machineId!"
                         :options="machines"
                         :disabled="!productionStep.recipeId" />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped>

</style>