import type {Fraction} from '@/types/model/basic';
import type {ProductionStep} from '@/types/model/standalone';
import {AbstractBulkCrudEntityApi} from '@/api/model/abstractBulkCrudEntityApi';

export class ProductionStepApi extends AbstractBulkCrudEntityApi<ProductionStep> {
  constructor() {
    super('Production step');
  }

  protected callPost(productionSteps: Partial<ProductionStep>[]): Promise<void> {
    return this.api.post('/api/factory/productionSteps', productionSteps, { factoryId: productionSteps[0].factoryId });
  }

  protected callPatch(productionSteps: Partial<ProductionStep>[]): Promise<void> {
    return this.api.patch('/api/productionSteps', productionSteps);
  }

  protected callDelete(productionStepIds: number[]): Promise<void> {
    return this.api.delete('/api/productionSteps', { productionStepIds: productionStepIds.join(',') });
  }


  public updateMachineCount(productionStepId: number, machineCount: Fraction): Promise<void> {
    return this.api.patch('/api/productionStep/machineCount', undefined, { productionStepId, machineCount });
  }

  public findSatisfaction(productionStepId: number, resourceId: number): Promise<Fraction> {
    return this.api.get('/api/productionStep/satisfaction', {productionStepId, resourceId});
  }
}

let instance: ProductionStepApi | undefined = undefined;

export function useProductionStepApi(): ProductionStepApi {
  if (!instance) instance = new ProductionStepApi();
  return instance;
}