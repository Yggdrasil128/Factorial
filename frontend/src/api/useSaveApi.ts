import type { Save } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';

export interface SaveApi {
  retrieveAll(): Promise<Save[]>;
}

export function useSaveApi(): SaveApi {
  const api: Api = useApi();

  async function retrieveAll(): Promise<Save[]> {
    return api.get('/api/saves');
  }

  return {
    retrieveAll
  };
}