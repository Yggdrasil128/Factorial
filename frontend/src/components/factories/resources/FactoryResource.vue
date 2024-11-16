<script setup lang="ts">
import type { Item, ProductionStep, Resource } from '@/types/model/standalone';
import { ElTooltip } from 'element-plus';
import IconImg from '@/components/common/IconImg.vue';
import { useItemStore } from '@/stores/model/itemStore';
import { computed, type ComputedRef } from 'vue';
import QuantityDisplay from '@/components/factories/resources/QuantityDisplay.vue';
import _ from 'lodash';
import { useProductionStepStore } from '@/stores/model/productionStepStore';
import ResourceProductionStep from '@/components/factories/resources/ResourceProductionStep.vue';

export interface FactoryResourceProps {
  resource: Resource;
}

const props: FactoryResourceProps = defineProps<FactoryResourceProps>();

const itemStore = useItemStore();
const productionStepStore = useProductionStepStore();

const item: ComputedRef<Item> = computed(() =>
  itemStore.getById(props.resource.itemId)!
);

const productionSteps: ComputedRef<ProductionStep[]> = computed(() => {
  // TODO: add options to show consumers and/or producers
  const productionStepIds: number[] = _.uniq([...props.resource.producerIds]);
  return productionStepIds
    .map(productionStepId => productionStepStore.getById(productionStepId))
    .filter(productionStep => !!productionStep) as ProductionStep[];
});
</script>

<template>
  <div v-if="item" class="item">
    <div style="overflow: auto;">
      <div class="itemIcon">
        <icon-img :icon="item.iconId" :size="64" />
      </div>
      <div class="itemInfo">
        <div class="itemName">{{ item.name }}</div>
        <div class="itemBalance">
          <el-tooltip
            effect="dark"
            placement="top-start"
            transition="none"
            :hide-after="0"
            :content="'The amount being produced'"
          >
            Produced:
          </el-tooltip>
          <quantity-display :quantity="resource.produced" color="green" show-unit convert-unit />
          &emsp;
          <el-tooltip
            effect="dark"
            placement="top-start"
            transition="none"
            :hide-after="0"
            :content="'The amount being consumed'"
          >
            Consumed:
          </el-tooltip>
          <quantity-display :quantity="resource.consumed" color="red" show-unit convert-unit />
          &emsp;
          <el-tooltip
            effect="dark"
            placement="top-start"
            transition="none"
            :hide-after="0"
            :content="'The production surplus (if positive), or deficit (if negative)'"
          >
            Net:
          </el-tooltip>
          <quantity-display :quantity="resource.overProduced" color="auto" show-unit convert-unit />
        </div>
      </div>
    </div>
    <resource-production-step
      v-for="productionStep in productionSteps"
      :key="productionStep.id"
      :production-step="productionStep"
    />
  </div>
</template>

<style scoped>
.item {
  background-color: #4b4b4b;
  border-radius: 24px;
  padding: 8px;
  margin-bottom: 24px;
}

.itemIcon {
  float: left;
}

.itemInfo {
  float: left;
  margin-left: 16px;
  margin-top: 5px;
  vertical-align: top;
  overflow: auto;
}

.itemName {
  font-size: 24px;
}

.itemBalance {
  margin-top: 4px;
}
</style>