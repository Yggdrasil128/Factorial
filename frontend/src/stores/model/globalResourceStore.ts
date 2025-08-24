import {defineStore} from 'pinia';
import {reactive} from 'vue';
import type {GlobalResource} from '@/types/model/standalone';

// eslint-disable-next-line @typescript-eslint/typedef
export const useGlobalResourceStore
  = defineStore('globalResourceStore', () => {

  const map: Map<number, GlobalResource> = reactive(new Map());

  function getAll(): GlobalResource[] {
    return [...map.values()];
  }

  function getById(globalResourceId: number | undefined): GlobalResource | undefined {
    return !globalResourceId ? undefined : map.get(globalResourceId);
  }

  function getBySaveId(saveId: number | undefined): GlobalResource[] {
    if (!saveId) return [];
    return getAll().filter((globalResource: GlobalResource): boolean => globalResource.saveId === saveId);
  }

  return { map, getAll, getById, getBySaveId };
});

export type GlobalResourceStore = ReturnType<typeof useGlobalResourceStore>;