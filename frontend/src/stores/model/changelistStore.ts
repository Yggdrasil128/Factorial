import { defineStore } from 'pinia';
import { reactive } from 'vue';
import type { Changelist } from '@/types/model/standalone';

export const useChangelistStore
  = defineStore('changelistStore', () => {
  const map: Map<number, Changelist> = reactive(new Map());
  return { map };
});