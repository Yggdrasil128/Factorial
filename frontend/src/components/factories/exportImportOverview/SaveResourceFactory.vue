<script setup lang="ts">
import type { Factory, Item, ProductionEntry } from '@/types/model/standalone';
import IconImg from '@/components/common/IconImg.vue';
import QuantityDisplay from '@/components/factories/resources/QuantityDisplay.vue';
import { computed, type ComputedRef } from 'vue';

export interface SaveResourceFactoryProps {
  factory: Factory;
  item: Item;
}

const props: SaveResourceFactoryProps = defineProps<SaveResourceFactoryProps>();

const imports: ComputedRef<ProductionEntry[]> = computed(() =>
  props.factory.inputs.filter(entry => entry.itemId === props.item.id),
);

const exports: ComputedRef<ProductionEntry[]> = computed(() =>
  props.factory.outputs.filter(entry => entry.itemId === props.item.id),
);

</script>

<template>
  <div class="factory row">
    <IconImg :icon="factory.iconId" :size="40" />
    <div style="margin-left: 4px;">
      <div style="margin-bottom: 4px;">
        <b>{{ factory.name }}</b>
      </div>
      <div v-for="(input, index) in imports" :key="index" class="row items-center">
        Imports
        <quantity-display :quantity="input.quantity" color="red" convert-unit />
        <icon-img :icon="item.iconId" :size="24" />
        <span>{{ item.name }}</span>
        <quantity-display :quantity="undefined" show-unit convert-unit />
      </div>
      <div v-for="(output, index) in exports" :key="index" class="row items-center">
        Exports
        <quantity-display :quantity="output.quantity" color="green" convert-unit />
        <icon-img :icon="item.iconId" :size="24" />
        <span>{{ item.name }}</span>
        <quantity-display :quantity="undefined" show-unit convert-unit />
      </div>
    </div>
  </div>
</template>

<style scoped>
.factory {
  margin-left: 80px;
  background-color: #555555;
  border-radius: 16px;
  padding: 8px;
  margin-top: 4px;
  margin-bottom: 4px;
}
</style>