import type { Item } from '@/types/model/standalone';

import { AbstractBulkCrudEntityApi } from '@/api/model/abstractBulkCrudEntityApi';

export class ItemApi extends AbstractBulkCrudEntityApi<Item> {
  constructor() {
    super('Item');
  }

  protected callPost(items: Partial<Item>[]): Promise<void> {
    return this.api.post('/api/game/items', items, { gameId: items[0].gameId });
  }

  protected callPatch(items: Partial<Item>[]): Promise<void> {
    return this.api.patch('/api/items', items);
  }

  protected callDelete(itemIds: number[]): Promise<void> {
    return this.api.delete('/api/items', { itemIds: itemIds.join(',') });
  }
}

let instance: ItemApi | undefined = undefined;

export function useItemApi(): ItemApi {
  if (!instance) instance = new ItemApi();
  return instance;
}