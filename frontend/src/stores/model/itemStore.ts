import { defineStore } from 'pinia';
import type { Item } from '@/types/model/standalone';
import { reactive } from 'vue';

export const useItemStore
  = defineStore('itemStore', () => {
  const map: Map<number, Item> = reactive(new Map());
  return { map };
});