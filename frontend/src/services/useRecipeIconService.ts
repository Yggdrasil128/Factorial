import type {Item, Recipe} from '@/types/model/standalone';
import {type RecipeStore, useRecipeStore} from '@/stores/model/recipeStore';
import {type ItemStore, useItemStore} from '@/stores/model/itemStore';

export interface RecipeIconService {
  getRecipeIconId: (recipe: Recipe | number | undefined) => number;
}

export function useRecipeIconService(): RecipeIconService {
  const recipeStore: RecipeStore = useRecipeStore();
  const itemStore: ItemStore = useItemStore();

  function getRecipeIconId(recipe: Recipe | number | undefined): number {
    if (!recipe) return 0;
    if (typeof recipe === 'number') {
      recipe = recipeStore.getById(recipe);
    }
    if (!recipe) return 0;
    if (recipe.iconId) return recipe.iconId;
    if (recipe.products.length === 0) return 0;
    const item: Item | undefined = itemStore.getById(recipe.products[0].itemId);
    if (!item) return 0;
    return item.iconId;
  }

  return { getRecipeIconId };
}