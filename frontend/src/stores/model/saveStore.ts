import {defineStore} from 'pinia';
import type {Save} from '@/types/model/standalone';
import {reactive} from 'vue';

// eslint-disable-next-line @typescript-eslint/typedef
export const useSaveStore
  = defineStore('saveStore', () => {

  const map: Map<number, Save> = reactive(new Map());

  function getAll(): Save[] {
    return [...map.values()];
  }

  function getById(saveId: number | undefined): Save | undefined {
    return !saveId ? undefined : map.get(saveId);
  }

  function getByGameId(gameId: number | undefined): Save[] {
    if (!gameId) return [];
    return getAll().filter((save: Save): boolean => save.gameId === gameId);
  }

  return { map, getAll, getById, getByGameId };
});

export type SaveStore = ReturnType<typeof useSaveStore>;