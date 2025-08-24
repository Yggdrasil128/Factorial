import {defineStore} from 'pinia';
import type {Item} from '@/types/model/standalone';
import {reactive} from 'vue';

// eslint-disable-next-line @typescript-eslint/typedef
export const useItemStore
  = defineStore('itemStore', () => {

  const map: Map<number, Item> = reactive(new Map());

  function getAll(): Item[] {
    return [...map.values()];
  }

  function getById(itemId: number | undefined): Item | undefined {
    return !itemId ? undefined : map.get(itemId);
  }

  function getByGameId(gameId: number | undefined): Item[] {
    if (!gameId) return [];
    return getAll().filter((item: Item): boolean => item.gameId === gameId);
  }

  return { map, getAll, getById, getByGameId };
});

export type ItemStore = ReturnType<typeof useItemStore>;