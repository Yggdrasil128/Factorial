import type { Save } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';
import type { EntityWithOrdinal } from '@/types/model/basic';

export interface SaveApi {
  retrieveAll(): Promise<Save[]>;
  reorder(input: EntityWithOrdinal[]): Promise<void>;
}

export function useSaveApi(): SaveApi {
  const api: Api = useApi();

  async function retrieveAll(): Promise<Save[]> {
    return api.get('/api/saves');
  }

  async function reorder(input: EntityWithOrdinal[]): Promise<void> {
    return api.patch('/api/saves/order', input);
  }

  return {
    retrieveAll,
    reorder,
  };
}