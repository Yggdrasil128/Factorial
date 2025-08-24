import {defineStore} from 'pinia';
import type {Recipe} from '@/types/model/standalone';
import {reactive} from 'vue';

// eslint-disable-next-line @typescript-eslint/typedef
export const useRecipeStore
  = defineStore('recipeStore', () => {

  const map: Map<number, Recipe> = reactive(new Map());

  function getAll(): Recipe[] {
    return [...map.values()];
  }

  function getById(recipeId: number | undefined): Recipe | undefined {
    return !recipeId ? undefined : map.get(recipeId);
  }

  function getByGameId(gameId: number | undefined): Recipe[] {
    if (!gameId) return [];
    return getAll().filter((recipe: Recipe): boolean => recipe.gameId === gameId);
  }

  return { map, getAll, getById, getByGameId };
});

export type RecipeStore = ReturnType<typeof useRecipeStore>;