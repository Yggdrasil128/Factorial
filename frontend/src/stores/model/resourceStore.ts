import { defineStore } from 'pinia';
import { reactive } from 'vue';
import type { Resource } from '@/types/model/standalone';

export const useResourceStore
  = defineStore('resourceStore', () => {

  const map: Map<number, Resource> = reactive(new Map());

  function getAll(): Resource[] {
    return [...map.values()];
  }

  function getById(resourceId: number | undefined): Resource | undefined {
    return !resourceId ? undefined : map.get(resourceId);
  }

  function getBySaveId(saveId: number | undefined): Resource[] {
    if (!saveId) return [];
    return getAll().filter(resource => resource.saveId === saveId);
  }

  function getByFactoryId(factoryId: number | undefined): Resource[] {
    if (!factoryId) return [];
    return getAll().filter(resource => resource.factoryId === factoryId);
  }

  return { map, getAll, getById, getBySaveId, getByFactoryId };
});