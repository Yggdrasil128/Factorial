import type { EntityWithOrdinal } from '@/types/model/basic';
import { type Api, useApi } from '@/api/useApi';

export interface GlobalResourceApi {
  reorder(factoryId: number, input: EntityWithOrdinal[]): Promise<void>;
}

export function useLocalResourceApi(): GlobalResourceApi {
  const api: Api = useApi();

  async function reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void> {
    return api.patch('/api/save/resources/order', input, { saveId: saveId });
  }

  return {
    reorder,
  };
}