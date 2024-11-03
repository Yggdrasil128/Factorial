import type { EntityWithOrdinal } from '@/types/model/basic';
import { type Api, useApi } from '@/api/useApi';

export interface ResourceApi {
  reorder(factoryId: number, input: EntityWithOrdinal[]): Promise<void>;
}

export function useResourceApi(): ResourceApi {
  const api: Api = useApi();

  async function reorder(factoryId: number, input: EntityWithOrdinal[]): Promise<void> {
    return api.patch('/api/factory/resources/order', input, { factoryId: factoryId });
  }

  return {
    reorder
  };
}