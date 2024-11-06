import { defineStore } from 'pinia';
import type { Game } from '@/types/model/standalone';
import { reactive } from 'vue';

export const useGameStore
  = defineStore('gameStore', () => {

  const map: Map<number, Game> = reactive(new Map());

  function getAll(): Game[] {
    return [...map.values()];
  }

  function getById(gameId: number | undefined): Game | undefined {
    return !gameId ? undefined : map.get(gameId);
  }

  return { map, getAll, getById };
});