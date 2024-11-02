import { defineStore } from 'pinia';
import { reactive } from 'vue';
import type { Resource } from '@/types/model/standalone';

export const useResourceStore
  = defineStore('resourceStore', () => {
  const map: Map<number, Resource> = reactive(new Map());

  function getByFactoryId(factoryId?: number): Resource[] {
    if (!factoryId) {
      return [];
    }
    return [...map.values()].filter((resource: Resource) => resource.factoryId === factoryId);
  }

  return { map, getByFactoryId };
});