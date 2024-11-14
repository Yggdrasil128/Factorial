import { ElMessage } from 'element-plus';
import type { Api } from '@/api/useApi';
import { useApi } from '@/api/useApi';

export abstract class AbstractBulkCrudEntityApi<T> {
  protected readonly api: Api = useApi();
  protected readonly entityName: string;

  protected constructor(entityName: string) {
    this.entityName = entityName;
  }

  public async create(entity: Partial<T>): Promise<void> {
    return this.bulkCreate([entity]);
  }

  public async edit(entity: Partial<T>): Promise<void> {
    return this.bulkEdit([entity]);
  }

  public async delete(entityId: number): Promise<void> {
    return this.bulkDelete([entityId]);
  }

  public async bulkCreate(entities: Partial<T>[]): Promise<void> {
    if (entities.length === 0) return;
    this.callPost(entities).then(() => {
      ElMessage.success({
        message: this.entityName + (entities.length > 1 ? 's' : '') + ' created.',
      });
    });
  }

  public async bulkEdit(entities: Partial<T>[]): Promise<void> {
    if (entities.length === 0) return;
    this.callPatch(entities).then(() => {
      ElMessage.success({
        message: this.entityName + (entities.length > 1 ? 's' : '') + ' updated.',
      });
    });
  }

  public async bulkDelete(entityIds: number[]): Promise<void> {
    if (entityIds.length === 0) return;
    this.callDelete(entityIds).then(() => {
      ElMessage.success({
        message: this.entityName + (entityIds.length > 1 ? 's' : '') + ' deleted.',
      });
    });
  }

  protected abstract callPost(entities: Partial<T>[]): Promise<void>;

  protected abstract callPatch(entities: Partial<T>[]): Promise<void>;

  protected abstract callDelete(entityIds: number[]): Promise<void>;
}