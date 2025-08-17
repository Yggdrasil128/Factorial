<script setup lang="ts">
import type {Item, Machine, ProductionStep, Recipe, RecipeModifier} from '@/types/model/standalone';
import {computed, type ComputedRef} from 'vue';
import {useRecipeStore} from '@/stores/model/recipeStore';
import {useItemStore} from '@/stores/model/itemStore';
import {useMachineStore} from '@/stores/model/machineStore';
import {CaretLeft, Delete, Edit} from '@element-plus/icons-vue';
import IconImg from '@/components/common/IconImg.vue';
import {ElButton, ElButtonGroup, ElPopconfirm, ElTooltip} from 'element-plus';
import QuantityDisplay from '@/components/factories/resources/QuantityDisplay.vue';
import {useRecipeModifierStore} from '@/stores/model/recipeModifierStore';
import {useRoute, useRouter} from 'vue-router';
import {useProductionStepApi} from '@/api/model/useProductionStepApi';
import ProductionEntriesTable from '@/components/factories/resources/ProductionEntriesTable.vue';
import ProductionStepMachineCountInput from '@/components/factories/resources/ProductionStepMachineCountInput.vue';
import CustomElTooltip from "@/components/common/CustomElTooltip.vue";

export interface ResourceProductionStepProps {
  productionStep: ProductionStep;
}

const props: ResourceProductionStepProps = defineProps<ResourceProductionStepProps>();

const router = useRouter();
const route = useRoute();

const recipeStore = useRecipeStore();
const recipeModifierStore = useRecipeModifierStore();
const itemStore = useItemStore();
const machineStore = useMachineStore();

const productionStepApi = useProductionStepApi();

const recipe: ComputedRef<Recipe | undefined> = computed(() => recipeStore.getById(props.productionStep.recipeId));
const machine: ComputedRef<Machine | undefined> = computed(() => machineStore.getById(props.productionStep.machineId));

const firstOutputItem: ComputedRef<Item | undefined> = computed(() => {
  if (!recipe.value) return undefined;
  if (recipe.value.products.length === 0) return undefined;
  return itemStore.getById(recipe.value.products[0].itemId);
});

const recipeIconId: ComputedRef<number> = computed(() => {
  if (!recipe.value) return 0;
  if (recipe.value.iconId) return recipe.value.iconId;
  if (!firstOutputItem.value) return 0;
  return firstOutputItem.value.iconId;
});

const recipeName: ComputedRef<string> = computed(() => {
  if (!recipe.value) return '';
  if (recipe.value.name) return recipe.value.name;
  if (!firstOutputItem.value) return '';
  return firstOutputItem.value.name;
});

const recipeModifiers: ComputedRef<RecipeModifier[]> = computed(() => {
  return props.productionStep.modifierIds
      .map(recipeModifierId => recipeModifierStore.getById(recipeModifierId))
      .filter(recipeModifier => recipeModifier !== undefined) as RecipeModifier[];
});

function editProductionStep(): void {
  router.push({
    name: 'editProductionStep', params: {
      factoryId: route.params.factoryId,
      editProductionStepId: props.productionStep.id,
    },
  });
}

function deleteProductionStep(): void {
  productionStepApi.delete(props.productionStep.id);
}

</script>

<template>
  <div class="step">
    <div class="row items-center">
      <icon-img :icon="machine?.iconId" :size="48"/>
      <div style="flex: 0 0 auto; margin-right: 12px;">
        ✕
        <quantity-display :quantity="productionStep.machineCounts" color="none"/>
      </div>
      <icon-img :icon="recipeIconId" :size="48"/>

      <div style="font-size: 20px; flex: 1 1 auto;">
        {{ recipeName }}
        <template v-if="recipeModifiers.length > 0">
          (
          <template v-for="(recipeModifier, index) in recipeModifiers" :key="recipeModifier.id">
            <icon-img :icon="recipeModifier.iconId" :size="24"/>
            {{ recipeModifier.name }}
            <template v-if="index < recipeModifiers.length - 1">
              ,
            </template>
          </template>
          )
        </template>
      </div>

      <div style="flex: 0 0 auto;">
        <!--        <machine-count-input :model-value="productionStep.machineCounts.withPrimaryChangelist"-->
        <!--                             :production-step-id="productionStep.id" />-->
        <ProductionStepMachineCountInput :production-step="productionStep"/>
        &ensp;
        <el-button-group>
          <custom-el-tooltip content="Edit">
            <el-button :icon="Edit" @click="editProductionStep"/>
          </custom-el-tooltip>

          <el-popconfirm
              title="Delete this production step?"
              width="200px"
              @confirm="deleteProductionStep">
            <template #reference>
              <span class="row center tooltipHelperSpan">
                <el-tooltip
                    effect="dark"
                    placement="top-start"
                    transition="none"
                    :hide-after="0"
                    content="Delete">
                  <el-button type="danger" :icon="Delete"/>
                </el-tooltip>
              </span>
            </template>
          </el-popconfirm>
        </el-button-group>
      </div>
    </div>

    <div class="row" style="margin-top: 8px;">
      <div>
        <div v-if="productionStep.outputs.length === 0" class="nothing">(nothing)</div>
        <ProductionEntriesTable v-else :production-entries="productionStep.outputs"/>
      </div>
      <el-icon :size="24">
        <CaretLeft/>
      </el-icon>
      <div>
        <div v-if="productionStep.inputs.length === 0" class="nothing">(nothing)</div>
        <ProductionEntriesTable v-else :production-entries="productionStep.inputs"/>
      </div>
    </div>
  </div>
</template>

<style scoped>
.step {
  background-color: #555555;
  border-radius: 16px;
  padding: 8px;
}

.nothing {
  color: #a0a0a0;
  margin-top: 6px;
}

</style>