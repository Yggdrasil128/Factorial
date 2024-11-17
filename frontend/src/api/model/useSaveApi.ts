import type { Save } from '@/types/model/standalone';
import type { EntityWithOrdinal } from '@/types/model/basic';
import { AbstractBulkCrudEntityApi } from '@/api/model/abstractBulkCrudEntityApi';

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

  public migrateToGameId(saveId: number, gameId: number): Promise<void> {
    console.log('migrateToGameId', saveId, gameId);
    return Promise.resolve();
  }
}

let instance: SaveApi | undefined = undefined;

export function useSaveApi(): SaveApi {
  if (!instance) instance = new SaveApi();
  return instance;
}