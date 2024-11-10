import type { Machine } from '@/types/model/standalone';
import type { Api, BulkCrudEntityApi } from '@/api/useApi';
import { useApi } from '@/api/useApi';
import { ElMessage } from 'element-plus';

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
export interface MachineApi extends BulkCrudEntityApi<Machine> {
}

export function useMachineApi(): MachineApi {
  const api: Api = useApi();

  async function create(machine: Partial<Machine>): Promise<void> {
    return api.post('/api/game/machines', machine, { gameId: machine.gameId })
      .then(() => {
        ElMessage.success({ message: 'Machine created.' });
      });
  }

  async function edit(machine: Partial<Machine>): Promise<void> {
    return api.patch('/api/machine', machine, { machineId: machine.id })
      .then(() => {
        ElMessage.success({ message: 'Machine updated.' });
      });
  }

  async function delete_(machineId: number): Promise<void> {
    return api.delete('/api/machine', { itemId: machineId })
      .then(() => {
        ElMessage.success({ message: 'Machine deleted.' });
      });
  }

  async function bulkEdit(machines: Partial<Machine>[]): Promise<void> {
    // TODO replace this implementation with new endpoint
    for (const machine of machines) {
      await edit(machine);
    }
  }

  async function bulkDelete(machineIds: number[]): Promise<void> {
    // TODO replace this implementation with new endpoint
    for (const machineId of machineIds) {
      await delete_(machineId);
    }
  }

  return {
    create,
    edit,
    delete: delete_,

    bulkEdit,
    bulkDelete,
  };
}