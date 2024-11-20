<script setup lang="ts">
import type { Factory, GlobalResource, Item } from '@/types/model/standalone';
import { useItemStore } from '@/stores/model/itemStore';
import { computed, type ComputedRef } from 'vue';
import { VisibleResourceContributors } from '@/types/userSettings';
import _ from 'lodash';
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useUserSettingsStore } from '@/stores/userSettingsStore';
import CustomElTooltip from '@/components/common/CustomElTooltip.vue';
import QuantityDisplay from '@/components/factories/resources/QuantityDisplay.vue';
import IconImg from '@/components/common/IconImg.vue';
import SaveResourceFactory from '@/components/factories/exportImportOverview/SaveResourceFactory.vue';

export interface SaveResourceProps {
  resource: GlobalResource;
}

const props: SaveResourceProps = defineProps<SaveResourceProps>();

const itemStore = useItemStore();
const factoryStore = useFactoryStore();
const userSettingsStore = useUserSettingsStore();


const item: ComputedRef<Item> = computed(() =>
  itemStore.getById(props.resource.itemId)!,
);

const factories: ComputedRef<Factory[]> = computed(() => {
  let factoryIds: number[];
  switch (userSettingsStore.visibleGlobalResourceContributors) {
    case VisibleResourceContributors.None:
      factoryIds = [];
      break;
    case VisibleResourceContributors.Producers:
      factoryIds = _.uniq([...props.resource.producerIds]);
      break;
    case VisibleResourceContributors.Consumers:
      factoryIds = _.uniq([...props.resource.consumerIds]);
      break;
    case VisibleResourceContributors.All:
      factoryIds = _.uniq([...props.resource.producerIds, ...props.resource.consumerIds]);
      break;
  }

  return factoryIds
    .map(factoryId => factoryStore.getById(factoryId))
    .filter(factory => !!factory) as Factory[];
});

</script>

<template>
  <div v-if="item" class="item">
    <div style="overflow: auto;">
      <div class="itemIcon">
        <icon-img :icon="item.iconId" :size="64" />
      </div>
      <div class="itemInfo">
        <div class="itemName">
          {{ item.name }}
        </div>
        <div class="itemBalance row items-center" style="gap: 16px;">
          <div>
            <custom-el-tooltip content="The supply/demand surplus (if positive), or deficit (if negative)">
              Net:
            </custom-el-tooltip>
            <quantity-display :quantity="resource.overProduced" color="auto" show-unit convert-unit />
          </div>

          <div>
            <custom-el-tooltip content="The total amount being exported">
              Exported:
            </custom-el-tooltip>
            <quantity-display :quantity="resource.produced" color="green" show-unit convert-unit />
          </div>

          <div>
            <custom-el-tooltip content="The total amount being imported">
              Imported:
            </custom-el-tooltip>
            <quantity-display :quantity="resource.consumed" color="red" show-unit convert-unit />
          </div>
        </div>
      </div>
    </div>
    <save-resource-factory
      v-for="factory in factories"
      :key="factory.id"
      :factory="factory"
      :item="item"
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