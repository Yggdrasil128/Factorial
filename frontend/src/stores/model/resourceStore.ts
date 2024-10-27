import { defineStore } from 'pinia';
import { reactive } from 'vue';
import type { Resource } from '@/types/model/standalone';

export const useResourceStore
  = defineStore('resourceStore', () => {
  const map: Map<number, Resource> = reactive(new Map());
  return { map };
});