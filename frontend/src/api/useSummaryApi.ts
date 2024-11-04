import type { GameSummary, SaveSummary } from '@/types/model/summary';
import { type Api, useApi } from '@/api/useApi';

export interface SummaryApi {
  getSaveSummary: (saveId: number) => Promise<SaveSummary>;
  getGameSummary: (gameId: number) => Promise<GameSummary>;
}

export function useSummaryApi(): SummaryApi {
  const api: Api = useApi();

  async function getSaveSummary(saveId: number): Promise<SaveSummary> {
    return api.get('/api/save/summary', { saveId: saveId });
  }

  async function getGameSummary(gameId: number): Promise<GameSummary> {
    return api.get('/api/game/summary', { gameId: gameId });
  }

  return {
    getSaveSummary,
    getGameSummary
  };
}