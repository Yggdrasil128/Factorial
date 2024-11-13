import type { Icon } from '@/types/model/standalone';
import { type Api, type BulkCrudEntityApi, useApi } from '@/api/useApi';
import { ElMessage } from 'element-plus';

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
export interface IconApi extends BulkCrudEntityApi<Icon> {
}

export function useIconApi(): IconApi {
  const api: Api = useApi();

  async function create(icon: Partial<Icon>): Promise<void> {
    return api.post('/api/game/icons', [ icon ], { gameId: icon.gameId })
      .then(() => {
        ElMessage.success({ message: 'Icon created.' });
      });
  }

  async function edit(icon: Partial<Icon>): Promise<void> {
    return api.patch('/api/icons', [ icon ])
      .then(() => {
        ElMessage.success({ message: 'Icon updated.' });
      });
  }

  async function delete_(iconId: number): Promise<void> {
    return api.delete('/api/icons', { iconIds: [ iconId ] })
      .then(() => {
        ElMessage.success({ message: 'Icon deleted.' });
      });
  }

  async function bulkEdit(icons: Partial<Icon>[]): Promise<void> {
    return api.patch('/api/icons', icons)
      .then(() => {
        ElMessage.success({ message: 'Icons updated.' });
      });
  }

  async function bulkDelete(iconIds: number[]): Promise<void> {
    return api.delete('/api/icons', { iconIds: iconIds })
      .then(() => {
        ElMessage.success({ message: 'Icons deleted.' });
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