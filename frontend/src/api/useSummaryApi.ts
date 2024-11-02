import type { GameVersionSummary, SaveSummary } from '@/types/model/summary';
import { type Api, useApi } from '@/api/useApi';

export interface SummaryApi {
  getSaveSummary: (saveId: number) => Promise<SaveSummary>;
  getGameVersionSummary: (gameVersionId: number) => Promise<GameVersionSummary>;
}

export function useSummaryApi(): SummaryApi {
  const api: Api = useApi();

  async function getSaveSummary(saveId: number): Promise<SaveSummary> {
    return api.get('/api/save/summary', { saveId: saveId });
  }

  async function getGameVersionSummary(gameVersionId: number): Promise<GameVersionSummary> {
    return api.get('/api/gameVersion/summary', { gameVersionId: gameVersionId });
  }

  return {
    getSaveSummary,
    getGameVersionSummary
  };
}