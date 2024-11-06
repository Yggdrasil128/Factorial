import { defineStore } from 'pinia';
import { reactive } from 'vue';
import type { Factory } from '@/types/model/standalone';

export const useFactoryStore
  = defineStore('factoryStore', () => {

  const map: Map<number, Factory> = reactive(new Map());

  function getAll(): Factory[] {
    return [...map.values()];
  }

  function getById(factoryId: number | undefined): Factory | undefined {
    return !factoryId ? undefined : map.get(factoryId);
  }

  function getBySaveId(saveId: number | undefined): Factory[] {
    if (!saveId) return [];
    return getAll().filter(factory => factory.saveId === saveId);
  }

  return { map, getAll, getById, getBySaveId };
});