import type { RecipeModifier } from '@/types/model/standalone';

import { AbstractBulkCrudEntityApi } from '@/api/model/abstractBulkCrudEntityApi';

export class RecipeModifierApi extends AbstractBulkCrudEntityApi<RecipeModifier> {
  constructor() {
    super('Recipe modifier');
  }

  protected callPost(recipeModifiers: Partial<RecipeModifier>[]): Promise<void> {
    return this.api.post('/api/game/recipeModifiers', recipeModifiers, { gameId: recipeModifiers[0].gameId });
  }

  protected callPatch(recipeModifiers: Partial<RecipeModifier>[]): Promise<void> {
    return this.api.patch('/api/recipeModifiers', recipeModifiers);
  }

  protected callDelete(recipeModifierIds: number[]): Promise<void> {
    return this.api.delete('/api/recipeModifiers', { recipeModifierIds: recipeModifierIds.join(',') });
  }
}

let instance: RecipeModifierApi | undefined = undefined;

export function useRecipeModifierApi(): RecipeModifierApi {
  if (!instance) instance = new RecipeModifierApi();
  return instance;
}