import type { Factory } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';
import type { EntityWithOrdinal } from '@/types/model/basic';
import { ElMessage } from 'element-plus';

export interface FactoryApi {
  create(factory: Partial<Factory>): Promise<void>;
  edit(factory: Partial<Factory>): Promise<void>;
  delete(factoryId: number): Promise<void>;
  reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void>;
}

export function useFactoryApi(): FactoryApi {
  const api: Api = useApi();

  async function create(factory: Partial<Factory>): Promise<void> {
    return api.post('/api/save/factories', factory, { saveId: factory.saveId })
      .then(() => {
        ElMessage.success({ message: 'Factory created.' });
      });
  }

  async function edit(factory: Partial<Factory>): Promise<void> {
    return api.patch('/api/factory', factory, { factoryId: factory.id })
      .then(() => {
        ElMessage.success({ message: 'Factory updated.' });
      });
  }

  async function delete_(factoryId: number): Promise<void> {
    return api.delete('/api/factory', { factoryId: factoryId })
      .then(() => {
        ElMessage.success({ message: 'Factory deleted.' });
      });
  }

  async function reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void> {
    return api.patch('/api/save/factories/order', input, { saveId: saveId });
  }

  return {
    create,
    edit,
    delete: delete_,
    reorder,
  };
}