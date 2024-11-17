import type { EntityWithOrdinal } from '@/types/model/basic';
import { type Api, useApi } from '@/api/useApi';
import type { LocalResource } from '@/types/model/standalone';
import { ElMessage } from 'element-plus';

export interface LocalResourceApi {
  edit(resource: Partial<LocalResource>): Promise<void>;
  bulkEdit(resources: Partial<LocalResource>[]): Promise<void>;
  reorder(factoryId: number, input: EntityWithOrdinal[]): Promise<void>;
}

export function useLocalResourceApi(): LocalResourceApi {
  const api: Api = useApi();

  async function edit(localResource: Partial<LocalResource>): Promise<void> {
    return bulkEdit([localResource]);
  }

  async function bulkEdit(localResources: Partial<LocalResource>[]): Promise<void> {
    if (localResources.length === 0) return;
    api.patch('/api/resources', localResources).then(() => {
      ElMessage.success({
        message: 'Resource' + (localResources.length > 1 ? 's' : '') + ' updated.',
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