import type { Machine } from '@/types/model/standalone';

import { AbstractBulkCrudEntityApi } from '@/api/model/abstractBulkCrudEntityApi';

export class MachineApi extends AbstractBulkCrudEntityApi<Machine> {
  constructor() {
    super('Machine');
  }

  protected callPost(machines: Partial<Machine>[]): Promise<void> {
    return this.api.post('/api/game/machines', machines, { gameId: machines[0].gameId });
  }

  protected callPatch(machines: Partial<Machine>[]): Promise<void> {
    return this.api.patch('/api/machines', machines);
  }

  protected callDelete(machineIds: number[]): Promise<void> {
    return this.api.delete('/api/machines', { machineIds: machineIds.join(',') });
  }
}

let instance: MachineApi | undefined = undefined;

export function useMachineApi(): MachineApi {
  if (!instance) instance = new MachineApi();
  return instance;
}