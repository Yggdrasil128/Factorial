import {defineStore} from 'pinia';
import type {Icon} from '@/types/model/standalone';
import {reactive} from 'vue';

export const useIconStore
  = defineStore('iconStore', () => {

  const map: Map<number, Icon> = reactive(new Map());

  function getAll(): Icon[] {
    return [...map.values()];
  }

  function getById(iconId: number | undefined): Icon | undefined {
    return !iconId ? undefined : map.get(iconId);
  }

  function getByGameId(gameId: number | undefined): Icon[] {
    if (!gameId) return [];
    return getAll().filter(icon => icon.gameId === gameId);
  }

  return { map, getAll, getById, getByGameId };
});

export type IconStore = ReturnType<typeof useIconStore>;