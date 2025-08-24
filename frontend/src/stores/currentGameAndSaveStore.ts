import {defineStore} from 'pinia';
import {ref, type Ref} from 'vue';

// eslint-disable-next-line @typescript-eslint/typedef
export const useCurrentGameAndSaveStore
  = defineStore('currentGameAndSaveStore', () => {

  const currentGameId: Ref<number> = ref(0);
  const currentSaveId: Ref<number> = ref(0);

  const editingGameId: Ref<number> = ref(0);

  return { currentGameId, currentSaveId, editingGameId };
});

export type CurrentGameAndSaveStore = ReturnType<typeof useCurrentGameAndSaveStore>;