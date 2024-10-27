import { defineStore } from 'pinia';
import type { RecipeModifier } from '@/types/model/standalone';
import { reactive } from 'vue';

export const useRecipeModifierStore
  = defineStore('recipeModifierStore', () => {
  const map: Map<number, RecipeModifier> = reactive(new Map());
  return { map };
});