import { defineStore } from 'pinia';
import { ref, type Ref } from 'vue';
import type { Game, Save } from '@/types/model/standalone';

export const useCurrentGameAndSaveStore
  = defineStore('currentGameAndSaveStore', () => {

  const game: Ref<Game | undefined> = ref();
  const save: Ref<Save | undefined> = ref();

  const editingGame: Ref<Game | undefined> = ref();

  return { game, save, editingGame };
});