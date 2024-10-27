import type { SaveSummary } from '@/types/model/summary';
import { inject } from 'vue';
import type { AxiosInstance } from 'axios';

export interface SaveSummaryApi {
  retrieveSummary: (saveId: number) => Promise<SaveSummary>;
}

export function useSaveSummaryApi(): SaveSummaryApi {
  const axios: AxiosInstance = inject('axios') as AxiosInstance;

  async function retrieveSummary(saveId: number): Promise<SaveSummary> {
    const response = await axios.get('/api/save/summary', {
      params: {
        saveId: saveId
      }
    });
    return await Promise.resolve(response.data);
  }

  return {
    retrieveSummary
  };
}