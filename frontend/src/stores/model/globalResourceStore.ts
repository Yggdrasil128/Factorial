import { defineStore } from 'pinia';
import { reactive } from 'vue';
import type { GlobalResource } from '@/types/model/standalone';

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
    return getAll().filter(globalResource => globalResource.saveId === saveId);
  }

  return { map, getAll, getById, getBySaveId };
});