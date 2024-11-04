import { type Api, useApi } from '@/api/useApi';
import type { Fraction } from '@/types/model/basic';
import type { ProductionStep } from '@/types/model/standalone';
import { ElMessage } from 'element-plus';

export interface ProductionStepApi {
  createProductionStep(productionStep: Partial<ProductionStep>): Promise<void>;

  editProductionStep(productionStep: Partial<ProductionStep>): Promise<void>;

  deleteProductionStep(productionStepId: number): Promise<void>;

  applyPrimaryChangelist(productionStepId: number): Promise<void>;

  updateMachineCount(productionStepId: number, machineCount: Fraction): Promise<void>;
}

export function useProductionStepApi(): ProductionStepApi {
  const api: Api = useApi();

  async function createProductionStep(productionStep: Partial<ProductionStep>): Promise<void> {
    return api.post('/api/factory/productionSteps', productionStep, { factoryId: productionStep.factoryId })
      .then(() => {
        ElMessage.success({ message: 'Production step created.' });
      });
  }

  async function editProductionStep(productionStep: Partial<ProductionStep>): Promise<void> {
    return api.patch('/api/productionStep', productionStep, { productionStepId: productionStep.id })
      .then(() => {
        ElMessage.success({ message: 'Production step updated.' });
      });
  }

  async function deleteProductionStep(productionStepId: number): Promise<void> {
    return api.delete('/api/productionStep', { productionStepId: productionStepId })
      .then(() => {
        ElMessage.success({ message: 'Production step deleted.' });
      });
  }

  async function applyPrimaryChangelist(productionStepId: number): Promise<void> {
    return api.patch('/api/productionStep/applyPrimaryChangelist', undefined, { productionStepId });
  }

  async function updateMachineCount(productionStepId: number, machineCount: Fraction): Promise<void> {
    return api.patch('/api/productionStep/machineCount', undefined, { productionStepId, machineCount });
  }

  return {
    createProductionStep,
    editProductionStep,
    deleteProductionStep,
    applyPrimaryChangelist,
    updateMachineCount
  };
}