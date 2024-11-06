<script setup lang="ts">
import type { Item, ProductionEntry } from '@/types/model/standalone';
import { useItemStore } from '@/stores/model/itemStore';
import { computed, type ComputedRef } from 'vue';
import IconImg from '@/components/common/IconImg.vue';
import QuantityDisplay from '@/components/factories/resources/QuantityDisplay.vue';

export interface ResourceProductionEntryProps {
  productionEntry: ProductionEntry;
}

const props: ResourceProductionEntryProps = defineProps<ResourceProductionEntryProps>();

const itemStore = useItemStore();

const item: ComputedRef<Item | undefined> = computed(() => itemStore.getById(props.productionEntry.itemId));

</script>

<template>
  <div style="vertical-align: central;">
    <quantity-display :quantity="productionEntry.quantity" />
    <icon-img :icon="item?.iconId" :size="24" style="margin-left: 4px; margin-right: 4px; margin-bottom: -4px;" />
    <span>{{ item?.name }}</span>
    <quantity-display :quantity="undefined" show-unit />
  </div>
</template>

<style scoped>

</style>