import { defineStore } from 'pinia';
import { ref, type Ref } from 'vue';

export const useCurrentGameAndSaveStore
  = defineStore('currentGameAndSaveStore', () => {

  const currentGameId: Ref<number> = ref(0);
  const currentSaveId: Ref<number> = ref(0);

  const editingGameId: Ref<number> = ref(0);

  return { currentGameId, currentSaveId, editingGameId };
});