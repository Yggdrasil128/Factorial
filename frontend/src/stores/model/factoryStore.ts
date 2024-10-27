import { defineStore } from 'pinia';
import { reactive } from 'vue';
import type { Factory } from '@/types/model/standalone';

export const useFactoryStore
  = defineStore('factoryStore', () => {
  const map: Map<number, Factory> = reactive(new Map());
  return { map };
});