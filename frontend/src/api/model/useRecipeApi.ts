import type { Recipe } from '@/types/model/standalone';

import { AbstractBulkCrudEntityApi } from '@/api/model/abstractBulkCrudEntityApi';

export class RecipeApi extends AbstractBulkCrudEntityApi<Recipe> {
  constructor() {
    super('Recipe');
  }

  protected callPost(recipes: Partial<Recipe>[]): Promise<void> {
    return this.api.post('/api/game/recipes', recipes, { gameId: recipes[0].gameId });
  }

  protected callPatch(recipes: Partial<Recipe>[]): Promise<void> {
    return this.api.patch('/api/recipes', recipes);
  }

  protected callDelete(recipeIds: number[]): Promise<void> {
    return this.api.delete('/api/recipes', { recipeIds: recipeIds.join(',') });
  }
}

let instance: RecipeApi | undefined = undefined;

export function useRecipeApi(): RecipeApi {
  if (!instance) instance = new RecipeApi();
  return instance;
}