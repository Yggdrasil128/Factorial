import type { Summary } from '@/types/model/summary';
import { inject } from 'vue';
import type { AxiosInstance } from 'axios';

export interface SaveSummaryApi {
  retrieveSummary: (saveId: number) => Promise<Summary>;
}

export function useSaveSummaryApi(): SaveSummaryApi {
  const axios: AxiosInstance = inject('axios') as AxiosInstance;

  async function retrieveSummary(saveId: number): Promise<Summary> {
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