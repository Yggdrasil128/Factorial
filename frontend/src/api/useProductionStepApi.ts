import { type Api, useApi } from '@/api/useApi';
import type { Fraction } from '@/types/model/basic';

export interface ProductionStepApi {
  applyPrimaryChangelist(productionStepId: number): Promise<void>;

  updateMachineCount(productionStepId: number, machineCount: Fraction): Promise<void>;
}

export function useProductionStepApi(): ProductionStepApi {
  const api: Api = useApi();

  async function applyPrimaryChangelist(productionStepId: number): Promise<void> {
    return api.patch('/api/productionStep/applyPrimaryChangelist', undefined, { productionStepId });
  }

  async function updateMachineCount(productionStepId: number, machineCount: Fraction): Promise<void> {
    return api.patch('/api/productionStep/machineCount', undefined, { productionStepId, machineCount });
  }

  return {
    applyPrimaryChangelist,
    updateMachineCount
  };
}