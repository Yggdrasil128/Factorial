import { defineStore } from 'pinia';
import { ref, type Ref } from 'vue';
import type { Game } from '@/types/model/standalone';

export const useCurrentGameStore
  = defineStore('currentGameStore', () => {
  const game: Ref<Game | undefined> = ref();
  return { game: game };
});