import {defineStore} from 'pinia';
import {reactive} from 'vue';
import type {ProductionStep} from '@/types/model/standalone';

// eslint-disable-next-line @typescript-eslint/typedef
export const useProductionStepStore
  = defineStore('productionStepStore', () => {

  const map: Map<number, ProductionStep> = reactive(new Map());

  function getAll(): ProductionStep[] {
    return [...map.values()];
  }

  function getById(productionStepId: number | undefined): ProductionStep | undefined {
    return !productionStepId ? undefined : map.get(productionStepId);
  }

  function getBySaveId(saveId: number | undefined): ProductionStep[] {
    if (!saveId) return [];
    return getAll().filter((productionStep: ProductionStep): boolean => productionStep.saveId === saveId);
  }

  function getByFactoryId(factoryId: number | undefined): ProductionStep[] {
    if (!factoryId) return [];
    return getAll().filter((productionStep: ProductionStep): boolean => productionStep.factoryId === factoryId);
  }

  return { map, getAll, getById, getBySaveId, getByFactoryId };
});

export type ProductionStepStore = ReturnType<typeof useProductionStepStore>;