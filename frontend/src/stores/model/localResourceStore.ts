import {defineStore} from 'pinia';
import {reactive} from 'vue';
import type {LocalResource} from '@/types/model/standalone';

// eslint-disable-next-line @typescript-eslint/typedef
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
    return getAll().filter((localResource: LocalResource): boolean => localResource.saveId === saveId);
  }

  function getByFactoryId(factoryId: number | undefined): LocalResource[] {
    if (!factoryId) return [];
    return getAll().filter((localResource: LocalResource): boolean => localResource.factoryId === factoryId);
  }

  return { map, getAll, getById, getBySaveId, getByFactoryId };
});

export type LocalResourceStore = ReturnType<typeof useLocalResourceStore>;