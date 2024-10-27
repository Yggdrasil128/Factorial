import { defineStore } from 'pinia';
import type { Machine } from '@/types/model/standalone';
import { reactive } from 'vue';

export const useMachineStore
  = defineStore('machineStore', () => {
  const map: Map<number, Machine> = reactive(new Map());
  return { map };
});