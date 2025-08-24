import {defineStore} from 'pinia';
import type {RecipeModifier} from '@/types/model/standalone';
import {reactive} from 'vue';

// eslint-disable-next-line @typescript-eslint/typedef
export const useRecipeModifierStore
  = defineStore('recipeModifierStore', () => {

  const map: Map<number, RecipeModifier> = reactive(new Map());

  function getAll(): RecipeModifier[] {
    return [...map.values()];
  }

  function getById(recipeModifierId: number | undefined): RecipeModifier | undefined {
    return !recipeModifierId ? undefined : map.get(recipeModifierId);
  }

  function getByGameId(gameId: number | undefined): RecipeModifier[] {
    if (!gameId) return [];
    return getAll().filter((recipeModifier: RecipeModifier): boolean => recipeModifier.gameId === gameId);
  }

  return { map, getAll, getById, getByGameId };
});

export type RecipeModifierStore = ReturnType<typeof useRecipeModifierStore>;