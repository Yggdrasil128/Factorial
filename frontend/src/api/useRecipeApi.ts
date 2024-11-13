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
    return api.post('/api/game/recipes', [ recipe ], { gameId: recipe.gameId })
      .then(() => {
        ElMessage.success({ message: 'Recipe created.' });
      });
  }

  async function edit(recipe: Partial<Recipe>): Promise<void> {
    return api.patch('/api/recipes', recipe, { recipeId: recipe.id })
      .then(() => {
        ElMessage.success({ message: 'Recipe updated.' });
      });
  }

  async function delete_(recipeId: number): Promise<void> {
    return api.delete('/api/recipes', { recipeId: recipeId })
      .then(() => {
        ElMessage.success({ message: 'Recipe deleted.' });
      });
  }

  async function bulkEdit(recipes: Partial<Recipe>[]): Promise<void> {
    return api.patch('/api/recipes', recipes)
      .then(() => {
        ElMessage.success({ message: 'Recipes updated.' });
      });
  }

  async function bulkDelete(recipeIds: number[]): Promise<void> {
    return api.delete('/api/recipes', { recipeIds: recipeIds })
      .then(() => {
        ElMessage.success({ message: 'Recipes deleted.' });
      });
  }

  return {
    create,
    edit,
    delete: delete_,

    bulkEdit,
    bulkDelete,
  };
}