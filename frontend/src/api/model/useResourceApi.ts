import type { EntityWithOrdinal } from '@/types/model/basic';
import { type Api, useApi } from '@/api/useApi';
import type { Resource } from '@/types/model/standalone';
import { ElMessage } from 'element-plus';

export interface ResourceApi {
  edit(resource: Partial<Resource>): Promise<void>;
  bulkEdit(resources: Partial<Resource>[]): Promise<void>;
  reorder(factoryId: number, input: EntityWithOrdinal[]): Promise<void>;
}

export function useResourceApi(): ResourceApi {
  const api: Api = useApi();

  async function edit(resource: Partial<Resource>): Promise<void> {
    return bulkEdit([resource]);
  }

  async function bulkEdit(resources: Partial<Resource>[]): Promise<void> {
    if (resources.length === 0) return;
    api.patch('/api/resources', resources).then(() => {
      ElMessage.success({
        message: 'Resource' + (resources.length > 1 ? 's' : '') + ' updated.',
      });
    });
  }

  async function reorder(factoryId: number, input: EntityWithOrdinal[]): Promise<void> {
    return api.patch('/api/factory/resources/order', input, { factoryId: factoryId });
  }

  return {
    edit,
    bulkEdit,
    reorder,
  };
}