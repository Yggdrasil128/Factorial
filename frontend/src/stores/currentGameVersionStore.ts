import { defineStore } from 'pinia';
import { ref, type Ref } from 'vue';
import type { GameVersion } from '@/types/model/standalone';

export const useCurrentGameVersionStore
  = defineStore('currentGameVersionStore', () => {
  const gameVersion: Ref<GameVersion | undefined> = ref();
  return { gameVersion };
});