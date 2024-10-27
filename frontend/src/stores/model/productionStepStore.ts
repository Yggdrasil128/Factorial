import { defineStore } from 'pinia';
import { reactive } from 'vue';
import type { ProductionStep } from '@/types/model/standalone';

export const useProductionStepStore
  = defineStore('productionStepStore', () => {
  const map: Map<number, ProductionStep> = reactive(new Map());
  return { map };
});