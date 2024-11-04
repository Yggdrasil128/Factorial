import type { Changelist } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';
import type { EntityWithOrdinal } from '@/types/model/basic';
import { ElMessage } from 'element-plus';

export interface ChangelistApi {
  createChangelist(changelist: Partial<Changelist>): Promise<void>;

  editChangelist(changelist: Partial<Changelist>): Promise<void>;

  deleteChangelist(changelistId: number): Promise<void>;

  setPrimary(changelistId: number): Promise<void>;

  setActive(changelistId: number, active: boolean): Promise<void>;

  apply(changelistId: number): Promise<void>;

  reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void>;
}

export function useChangelistApi(): ChangelistApi {
  const api: Api = useApi();

  async function createChangelist(changelist: Partial<Changelist>): Promise<void> {
    return api.post('/api/save/changelists', changelist, { saveId: changelist.saveId })
      .then(() => {
        ElMessage.success({ message: 'Changelist created.' });
      });
  }

  async function editChangelist(changelist: Partial<Changelist>): Promise<void> {
    return api.patch('/api/changelist', changelist, { changelistId: changelist.id })
      .then(() => {
        ElMessage.success({ message: 'Changelist updated.' });
      });
  }

  async function deleteChangelist(changelistId: number): Promise<void> {
    return api.delete('/api/changelist', { changelistId: changelistId })
      .then(() => {
        ElMessage.success({ message: 'Changelist deleted.' });
      });
  }

  async function setPrimary(changelistId: number): Promise<void> {
    await editChangelist({
      id: changelistId,
      primary: true,
      active: true
    });
  }

  async function setActive(changelistId: number, active: boolean): Promise<void> {
    await editChangelist({
      id: changelistId,
      active: active
    });
  }

  async function apply(changelistId: number): Promise<void> {
    return api.post('/api/changelist/apply', undefined, { changelistId: changelistId });
  }

  async function reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void> {
    return api.patch('/api/save/changelists/order', input, { saveId: saveId });
  }

  return {
    createChangelist,
    editChangelist,
    deleteChangelist,
    setPrimary,
    setActive,
    apply,
    reorder
  };
}