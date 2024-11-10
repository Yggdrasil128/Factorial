import type { Item } from '@/types/model/standalone';
import { type Api, type BulkCrudEntityApi, useApi } from '@/api/useApi';
import { ElMessage } from 'element-plus';

export interface ItemApi extends BulkCrudEntityApi<Item> {
}

export function useItemApi(): ItemApi {
  const api: Api = useApi();

  async function create(item: Partial<Item>): Promise<void> {
    return api.post('/api/game/items', item, { gameId: item.gameId })
      .then(() => {
        ElMessage.success({ message: 'Item created.' });
      });
  }

  async function edit(item: Partial<Item>): Promise<void> {
    return api.patch('/api/item', item, { itemId: item.id })
      .then(() => {
        ElMessage.success({ message: 'Item updated.' });
      });
  }

  async function delete_(itemId: number): Promise<void> {
    return api.delete('/api/item', { itemId: itemId })
      .then(() => {
        ElMessage.success({ message: 'Item deleted.' });
      });
  }

  return {
    create,
    edit,
    delete: delete_,

    // TODO
    bulkEdit: () => Promise.resolve(),
    bulkDelete: () => Promise.resolve(),
  };
}