import type { Factory } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';
import type { EntityWithOrdinal } from '@/types/model/basic';

export interface FactoryApi {
  createFactory(factory: Partial<Factory>): Promise<void>;

  editFactory(factory: Partial<Factory>): Promise<void>;

  deleteFactory(factoryId: number): Promise<void>;

  reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void>;
}

export function useFactoryApi(): FactoryApi {
  const api: Api = useApi();

  async function createFactory(factory: Partial<Factory>): Promise<void> {
    return api.post('/api/save/factories', factory, { saveId: factory.saveId });
  }

  async function editFactory(factory: Partial<Factory>): Promise<void> {
    return api.patch('/api/factory', factory, { factoryId: factory.id });
  }

  async function deleteFactory(factoryId: number): Promise<void> {
    return api.delete('/api/factory', { factoryId: factoryId });
  }

  async function reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void> {
    return api.patch('/api/save/factories/order', input, { saveId: saveId });
  }

  return {
    createFactory,
    editFactory,
    deleteFactory,
    reorder
  };
}