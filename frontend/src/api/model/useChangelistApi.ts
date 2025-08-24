import type {Changelist} from '@/types/model/standalone';
import type {EntityWithOrdinal, Fraction} from '@/types/model/basic';
import {AbstractBulkCrudEntityApi} from '@/api/model/abstractBulkCrudEntityApi';

export class ChangelistApi extends AbstractBulkCrudEntityApi<Changelist> {
  constructor() {
    super('Changelist');
  }

  protected callPost(changelists: Partial<Changelist>[]): Promise<void> {
    return this.api.post('/api/save/changelists', changelists, {saveId: changelists[0].saveId});
  }

  protected callPatch(changelists: Partial<Changelist>[]): Promise<void> {
    return this.api.patch('/api/changelists', changelists);
  }

  protected callDelete(changelistIds: number[]): Promise<void> {
    return this.api.delete('/api/changelists', {changelistIds: changelistIds.join(',')});
  }


  public async setPrimary(changelistId: number): Promise<void> {
    await this.edit({
      id: changelistId,
      primary: true,
      active: true,
    });
  }

  public async setActive(changelistId: number, active: boolean): Promise<void> {
    await this.edit({
      id: changelistId,
      active: active,
    });
  }

  public async reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void> {
    return this.api.patch('/api/save/changelists/order', input, {saveId});
  }

  public async apply(changelistId: number): Promise<void> {
    return this.api.post('/api/changelist/apply', undefined, {changelistId});
  }

  public async applyChange(changelistId: number, productionStepId: number): Promise<void> {
    return this.api.patch('/api/changelist/change/apply', undefined, {changelistId, productionStepId});
  }

  public async updateMachineCountChange(changelistId: number, productionStepId: number, machineCountChange: Fraction): Promise<void> {
    return this.api.patch('/api/changelist/change/machineCount', undefined,
      {changelistId, productionStepId, machineCountChange});
  }

  public async applyPrimaryChange(productionStepId: number): Promise<void> {
    return this.api.patch('/api/changelist/primary/change/apply', undefined, {productionStepId});
  }

  public async updatePrimaryMachineCountChange(productionStepId: number, machineCountChange: Fraction): Promise<void> {
    return this.api.patch('/api/changelist/primary/change/machineCount', undefined,
      {productionStepId, machineCountChange});
  }

  public async deleteChange(changelistId: number, productionStepId: number): Promise<void> {
    return this.updateMachineCountChange(changelistId, productionStepId, '0');
  }

  public async clear(changelistId: number): Promise<void> {
    return this.api.patch('/api/changelists', [{id: changelistId, productionStepChanges: []}] as Partial<Changelist>[]);
  }
}

let instance: ChangelistApi | undefined = undefined;

export function useChangelistApi(): ChangelistApi {
  if (!instance) instance = new ChangelistApi();
  return instance;
}