import type { Item } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';
import { ElMessage } from 'element-plus';

export interface ItemApi {
  createItem(item: Partial<Item>): Promise<void>;
  editItem(item: Partial<Item>): Promise<void>;
  deleteItem(itemId: number): Promise<void>;
}

export function useItemApi(): ItemApi {
  const api: Api = useApi();

  async function createItem(item: Partial<Item>): Promise<void> {
    return api.post('/api/game/items', item, { gameId: item.gameId })
      .then(() => {
        ElMessage.success({ message: 'Item created.' });
      });
  }

  async function editItem(item: Partial<Item>): Promise<void> {
    return api.patch('/api/item', item, { itemId: item.id })
      .then(() => {
        ElMessage.success({ message: 'Item updated.' });
      });
  }

  async function deleteItem(itemId: number): Promise<void> {
    return api.delete('/api/item', { itemId: itemId })
      .then(() => {
        ElMessage.success({ message: 'Item deleted.' });
      });
  }

  return {
    createItem,
    editItem,
    deleteItem,
  };
}