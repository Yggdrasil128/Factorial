import type { Save } from '@/types/model/standalone';
import type { EntityWithOrdinal } from '@/types/model/basic';
import { AbstractBulkCrudEntityApi } from '@/api/model/abstractBulkCrudEntityApi';
import type { SaveSummary } from '@/types/model/summary';
import { ElMessage } from 'element-plus';

export class SaveApi extends AbstractBulkCrudEntityApi<Save> {
  constructor() {
    super('Save');
  }

  protected callPost(saves: Partial<Save>[]): Promise<void> {
    return this.api.post('/api/saves', saves);
  }

  protected callPatch(saves: Partial<Save>[]): Promise<void> {
    return this.api.patch('/api/saves', saves);
  }

  protected callDelete(saveIds: number[]): Promise<void> {
    return this.api.delete('/api/saves', { saveIds: saveIds.join(',') });
  }


  public retrieveAll(): Promise<Save[]> {
    return this.api.get('/api/saves');
  }

  public reorder(input: EntityWithOrdinal[]): Promise<void> {
    return this.api.patch('/api/saves/order', input);
  }

  public async clone(saveId: number, newName: string): Promise<void> {
    return this.api.post('api/migration/save/clone', undefined, { saveId, newName })
      .then(() => {
        ElMessage.success({ message: 'Save cloned.' });
      });
  }

  public async migrateToGameId(saveId: number, newName: string, gameId: number): Promise<void> {
    return this.api.post('api/migration/save/migrate', undefined, { saveId, newName, gameId })
      .then(() => {
        ElMessage.success({ message: 'Save migrated.' });
      });
  }

  public async import(saveSummary: SaveSummary): Promise<void> {
    return this.api.post('api/migration/save', saveSummary)
      .then(() => {
        ElMessage.success({ message: 'Save imported.' });
      });
  }

  public export(saveId: number): Promise<SaveSummary> {
    return this.api.get('api/migration/save', { saveId });
  }
}

let instance: SaveApi | undefined = undefined;

export function useSaveApi(): SaveApi {
  if (!instance) instance = new SaveApi();
  return instance;
}