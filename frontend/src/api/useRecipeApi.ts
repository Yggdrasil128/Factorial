import type { Recipe } from '@/types/model/standalone';
import type { Api, BulkCrudEntityApi } from '@/api/useApi';
import { useApi } from '@/api/useApi';
import { ElMessage } from 'element-plus';

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
export interface RecipeApi extends BulkCrudEntityApi<Recipe> {
}

export function useRecipeApi(): RecipeApi {
  const api: Api = useApi();

  async function create(recipe: Partial<Recipe>): Promise<void> {
    return api.post('/api/game/recipes', recipe, { gameId: recipe.gameId })
      .then(() => {
        ElMessage.success({ message: 'Recipe created.' });
      });
  }

  async function edit(recipe: Partial<Recipe>): Promise<void> {
    return api.patch('/api/recipe', recipe, { recipeId: recipe.id })
      .then(() => {
        ElMessage.success({ message: 'Recipe updated.' });
      });
  }

  async function delete_(recipeId: number): Promise<void> {
    return api.delete('/api/recipe', { recipeId: recipeId })
      .then(() => {
        ElMessage.success({ message: 'Recipe deleted.' });
      });
  }

  async function bulkEdit(recipes: Partial<Recipe>[]): Promise<void> {
    // TODO replace this implementation with new endpoint
    for (const recipe of recipes) {
      await edit(recipe);
    }
  }

  async function bulkDelete(recipeIds: number[]): Promise<void> {
    // TODO replace this implementation with new endpoint
    for (const recipeId of recipeIds) {
      await delete_(recipeId);
    }
  }

  return {
    create,
    edit,
    delete: delete_,

    bulkEdit,
    bulkDelete,
  };
}