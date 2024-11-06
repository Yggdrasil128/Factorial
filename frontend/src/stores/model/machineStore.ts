import { defineStore } from 'pinia';
import type { Machine } from '@/types/model/standalone';
import { reactive } from 'vue';

export const useMachineStore
  = defineStore('machineStore', () => {

  const map: Map<number, Machine> = reactive(new Map());

  function getAll(): Machine[] {
    return [...map.values()];
  }

  function getById(machineId: number | undefined): Machine | undefined {
    return !machineId ? undefined : map.get(machineId);
  }

  function getByGameId(gameId: number | undefined): Machine[] {
    if (!gameId) return [];
    return getAll().filter(machine => machine.gameId === gameId);
  }

  return { map, getAll, getById, getByGameId };
});