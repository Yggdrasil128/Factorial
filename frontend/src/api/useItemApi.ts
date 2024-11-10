import type { Item } from '@/types/model/standalone';
import { type Api, type BulkCrudEntityApi, useApi } from '@/api/useApi';
import { ElMessage } from 'element-plus';

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
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

  async function bulkEdit(items: Partial<Item>[]): Promise<void> {
    // TODO replace this implementation with new endpoint
    for (const item of items) {
      await edit(item);
    }
  }

  async function bulkDelete(itemIds: number[]): Promise<void> {
    // TODO replace this implementation with new endpoint
    for (const itemId of itemIds) {
      await delete_(itemId);
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