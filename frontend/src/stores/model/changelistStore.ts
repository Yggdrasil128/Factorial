import { defineStore } from 'pinia';
import { reactive } from 'vue';
import type { Changelist } from '@/types/model/standalone';

export const useChangelistStore
  = defineStore('changelistStore', () => {

  const map: Map<number, Changelist> = reactive(new Map());

  function getAll(): Changelist[] {
    return [...map.values()];
  }

  function getById(changelistId: number | undefined): Changelist | undefined {
    return !changelistId ? undefined : map.get(changelistId);
  }

  function getBySaveId(saveId: number | undefined): Changelist[] {
    if (!saveId) return [];
    return getAll().filter(changelist => changelist.saveId === saveId);
  }

  return { map, getAll, getById, getBySaveId };
});