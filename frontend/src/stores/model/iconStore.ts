import { defineStore } from 'pinia';
import type { Icon } from '@/types/model/standalone';
import { reactive } from 'vue';

export const useIconStore
  = defineStore('iconStore', () => {
  const map: Map<number, Icon> = reactive(new Map());
  return { map };
});