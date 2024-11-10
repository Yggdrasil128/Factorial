import type { Game } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';
import type { EntityWithOrdinal } from '@/types/model/basic';

export interface GameApi {
  retrieveAll(): Promise<Game[]>;
  reorder(input: EntityWithOrdinal[]): Promise<void>;
}

export function useGameApi(): GameApi {
  const api: Api = useApi();

  async function retrieveAll(): Promise<Game[]> {
    return api.get('/api/games');
  }

  async function reorder(input: EntityWithOrdinal[]): Promise<void> {
    return api.patch('/api/games/order', input);
  }

  return {
    retrieveAll,
    reorder,
  };
}