<script setup lang="ts">
import type { Item, ProductionEntry } from '@/types/model/standalone';
import { useItemStore } from '@/stores/model/itemStore';
import { computed, type ComputedRef } from 'vue';
import IconImg from '@/components/IconImg.vue';
import QuantityDisplay from '@/components/factories/resources/QuantityDisplay.vue';

export interface ResourceProductionEntryProps {
  productionEntry: ProductionEntry;
}

const props: ResourceProductionEntryProps = defineProps<ResourceProductionEntryProps>();

const itemStore = useItemStore();

const item: ComputedRef<Item | undefined> = computed(() => itemStore.map.get(props.productionEntry.itemId));

</script>

<template>
  <div>
    <quantity-display :quantity="productionEntry.quantity" />
    <icon-img :icon="item?.iconId" :size="24" style="margin-left: 3px;" />
    <span>{{ item?.name }}</span>
    <quantity-display :quantity="undefined" show-unit />
  </div>
</template>

<style scoped>

</style>