import { defineStore } from 'pinia';
import { type Ref, ref } from 'vue';
import type { Save } from '@/types/model/standalone';

export const useCurrentSaveStore
  = defineStore('currentSaveStore', () => {
  const save: Ref<Save | undefined> = ref();
  return { save };
});