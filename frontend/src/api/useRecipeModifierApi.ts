import type { RecipeModifier } from '@/types/model/standalone';
import type { Api, BulkCrudEntityApi } from '@/api/useApi';
import { useApi } from '@/api/useApi';
import { ElMessage } from 'element-plus';

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
export interface RecipeModifierApi extends BulkCrudEntityApi<RecipeModifier> {
}

export function useRecipeModifierApi(): RecipeModifierApi {
  const api: Api = useApi();

  async function create(recipeModifier: Partial<RecipeModifier>): Promise<void> {
    return api.post('/api/game/recipeModifiers', [ recipeModifier ], { gameId: recipeModifier.gameId })
      .then(() => {
        ElMessage.success({ message: 'Recipe modifier created.' });
      });
  }

  async function edit(recipeModifier: Partial<RecipeModifier>): Promise<void> {
    return api.patch('/api/recipeModifiers', [ recipeModifier ])
      .then(() => {
        ElMessage.success({ message: 'Recipe modifier updated.' });
      });
  }

  async function delete_(recipeModifierId: number): Promise<void> {
    return api.delete('/api/recipeModifiers', { recipeModifierIds: [ recipeModifierId ] })
      .then(() => {
        ElMessage.success({ message: 'Recipe modifier deleted.' });
      });
  }

  async function bulkEdit(recipeModifiers: Partial<RecipeModifier>[]): Promise<void> {
    return api.patch('/api/recipeModifiers', recipeModifiers)
      .then(() => {
        ElMessage.success({ message: 'Recipe modifiers updated.' });
      });
  }

  async function bulkDelete(recipeModifierIds: number[]): Promise<void> {
    return api.delete('/api/recipeModifiers', { recipeModifierIds: recipeModifierIds })
      .then(() => {
        ElMessage.success({ message: 'Recipe modifiers deleted.' });
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