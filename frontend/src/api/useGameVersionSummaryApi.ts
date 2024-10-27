import type { GameVersionSummary } from '@/types/model/summary';
import type { AxiosInstance } from 'axios';
import { inject } from 'vue';

export interface GameVersionSummaryApi {
  retrieveSummary: (gameVersionId: number) => Promise<GameVersionSummary>;
}

export function useGameVersionSummaryApi(): GameVersionSummaryApi {
  const axios: AxiosInstance = inject('axios') as AxiosInstance;

  async function retrieveSummary(gameVersionId: number): Promise<GameVersionSummary> {
    const response = await axios.get('/api/gameVersion/summary', {
      params: {
        gameVersionId: gameVersionId
      }
    });
    return await Promise.resolve(response.data);
  }

  return {
    retrieveSummary
  };
}