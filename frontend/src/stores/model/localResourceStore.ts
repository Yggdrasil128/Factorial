import { defineStore } from 'pinia';
import { reactive } from 'vue';
import type { LocalResource } from '@/types/model/standalone';

export const useLocalResourceStore
  = defineStore('localResourceStore', () => {

  const map: Map<number, LocalResource> = reactive(new Map());

  function getAll(): LocalResource[] {
    return [...map.values()];
  }

  function getById(localResourceId: number | undefined): LocalResource | undefined {
    return !localResourceId ? undefined : map.get(localResourceId);
  }

  function getBySaveId(saveId: number | undefined): LocalResource[] {
    if (!saveId) return [];
    return getAll().filter(localResource => localResource.saveId === saveId);
  }

  function getByFactoryId(factoryId: number | undefined): LocalResource[] {
    if (!factoryId) return [];
    return getAll().filter(localResource => localResource.factoryId === factoryId);
  }

  return { map, getAll, getById, getBySaveId, getByFactoryId };
});