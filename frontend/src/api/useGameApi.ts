import type { Game } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';

export interface GameApi {
  retrieveAll(): Promise<Game[]>;
}

export function useGameApi(): GameApi {
  const api: Api = useApi();

  async function retrieveAll(): Promise<Game[]> {
    return api.get('/api/games');
  }

  return {
    retrieveAll
  };
}