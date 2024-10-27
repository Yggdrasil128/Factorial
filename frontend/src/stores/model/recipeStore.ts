import { defineStore } from 'pinia';
import type { Recipe } from '@/types/model/standalone';
import { reactive } from 'vue';

export const useRecipeStore
  = defineStore('recipeStore', () => {
  const map: Map<number, Recipe> = reactive(new Map());
  return { map };
});