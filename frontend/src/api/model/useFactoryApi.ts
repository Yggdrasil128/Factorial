import type { Factory } from '@/types/model/standalone';
import type { EntityWithOrdinal } from '@/types/model/basic';
import { AbstractBulkCrudEntityApi } from '@/api/model/abstractBulkCrudEntityApi';

export class FactoryApi extends AbstractBulkCrudEntityApi<Factory> {
  constructor() {
    super('Factory');
  }

  protected callPost(factories: Partial<Factory>[]): Promise<void> {
    return this.api.post('/api/save/factories', factories, { saveId: factories[0].saveId });
  }

  protected callPatch(factories: Partial<Factory>[]): Promise<void> {
    return this.api.patch('/api/factories', factories);
  }

  protected callDelete(factoryIds: number[]): Promise<void> {
    return this.api.delete('/api/factories', { factoryIds: factoryIds.join(',') });
  }


  public async reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void> {
    return this.api.patch('/api/save/factories/order', input, { saveId: saveId });
  }
}

let instance: FactoryApi | undefined = undefined;

export function useFactoryApi(): FactoryApi {
  if (!instance) instance = new FactoryApi();
  return instance;
}