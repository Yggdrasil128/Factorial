<script setup lang="ts">
import type { Item, Machine, ProductionStep, Recipe } from '@/types/model/standalone';
import { computed, type ComputedRef } from 'vue';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useItemStore } from '@/stores/model/itemStore';
import { useMachineStore } from '@/stores/model/machineStore';
import { Delete, Edit } from '@element-plus/icons-vue';
import IconImg from '@/components/IconImg.vue';
import { ElButton, ElButtonGroup } from 'element-plus';
import QuantityDisplay from '@/components/factories/resources/QuantityDisplay.vue';
import MachineCountInput from '@/components/factories/resources/MachineCountInput.vue';
import ResourceProductionEntry from '@/components/factories/resources/ResourceProductionEntry.vue';

export interface ResourceProductionStepProps {
  productionStep: ProductionStep;
}

const props: ResourceProductionStepProps = defineProps<ResourceProductionStepProps>();

const recipeStore = useRecipeStore();
const itemStore = useItemStore();
const machineStore = useMachineStore();

const recipe: ComputedRef<Recipe | undefined> = computed(() => recipeStore.map.get(props.productionStep.recipeId));
const machine: ComputedRef<Machine | undefined> = computed(() => machineStore.map.get(props.productionStep.machineId));

const firstOutputItem: ComputedRef<Item | undefined> = computed(() => {
  if (!recipe.value) return undefined;
  if (recipe.value.products.length === 0) return undefined;
  return itemStore.map.get(recipe.value.products[0].itemId);
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
</script>

<template>
  <div class="step">
    <div style="overflow: auto">
      <div class="stepIcon">
        <icon-img :icon="machine?.iconId" :size="48" />
        <div style="vertical-align: top; display: inline; line-height: 48px">
          x
          <quantity-display :quantity="productionStep.machineCounts" color="none" />
          &emsp;
        </div>
        <icon-img :icon="recipeIconId" :size="48" />
      </div>
      <div class="stepInfo">
        <div class="stepName">Recipe: {{ recipeName }}</div>
        <div class="stepThroughput">
          <div>
            <resource-production-entry v-for="(output, index) in productionStep.outputs" :key="index"
                                       :production-entry="output" />
            <div v-if="productionStep.outputs.length === 0" class="nothing">(nothing)</div>
          </div>
          <span> &#x27F5;&ensp; </span>
          <div>
            <resource-production-entry v-for="(input, index) in productionStep.inputs" :key="index"
                                       :production-entry="input" />
            <div v-if="productionStep.inputs.length === 0" class="nothing">(nothing)</div>
          </div>
        </div>
      </div>
      <div class="stepActions">
        <machine-count-input :model-value="productionStep.machineCounts.withPrimaryChangelist"
                             :production-step-id="productionStep.id" />
        &ensp;
        <el-button-group>
          <el-button :icon="Edit" />
          <el-button type="danger" :icon="Delete" />
        </el-button-group>
      </div>
    </div>
  </div>
</template>

<style scoped>
.step {
  margin-left: 80px;
  background-color: #555555;
  border-radius: 16px;
  padding: 8px 8px 0;
  margin-top: 4px;
  margin-bottom: 4px;
}

.stepIcon {
  float: left;
}

.stepInfo {
  float: left;
  margin-left: 16px;
  margin-top: 2px;
  vertical-align: top;
  overflow: auto;
}

.stepName {
  font-size: 16px;
}

.stepThroughput {
  margin-top: 4px;
  display: flex;
  flex-wrap: wrap;
  max-width: 700px;
}

.stepThroughput > div {
  display: inline;
}

.stepThroughput span {
  line-height: 24px;
  vertical-align: top;
}

.stepThroughput span:not(:first-child) {
  margin-left: 5px;
}

.nothing {
  color: #a0a0a0;
}

.nothing {
  margin-top: 3px;
}

.stepActions {
  float: right;
  padding: 8px 8px 0;
  margin-bottom: 8px;
}
</style>