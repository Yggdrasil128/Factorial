import type { Changelist } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';
import type { EntityWithOrdinal } from '@/types/model/basic';
import { ElMessage } from 'element-plus';

export interface ChangelistApi {
  create(changelist: Partial<Changelist>): Promise<void>;
  edit(changelist: Partial<Changelist>): Promise<void>;
  delete(changelistId: number): Promise<void>;
  setPrimary(changelistId: number): Promise<void>;
  setActive(changelistId: number, active: boolean): Promise<void>;
  apply(changelistId: number): Promise<void>;
  reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void>;
}

export function useChangelistApi(): ChangelistApi {
  const api: Api = useApi();

  async function create(changelist: Partial<Changelist>): Promise<void> {
    return api.post('/api/save/changelists', [ changelist ], { saveId: changelist.saveId })
      .then(() => {
        ElMessage.success({ message: 'Changelist created.' });
      });
  }

  async function edit(changelist: Partial<Changelist>): Promise<void> {
    return api.patch('/api/changelists', [ changelist ])
      .then(() => {
        ElMessage.success({ message: 'Changelist updated.' });
      });
  }

  async function delete_(changelistId: number): Promise<void> {
    return api.delete('/api/changelists', { changelistIds: [ changelistId ] })
      .then(() => {
        ElMessage.success({ message: 'Changelist deleted.' });
      });
  }

  async function setPrimary(changelistId: number): Promise<void> {
    await edit({
      id: changelistId,
      primary: true,
      active: true,
    });
  }

  async function setActive(changelistId: number, active: boolean): Promise<void> {
    await edit({
      id: changelistId,
      active: active,
    });
  }

  async function apply(changelistId: number): Promise<void> {
    return api.post('/api/changelist/apply', undefined, { changelistId: changelistId });
  }

  async function reorder(saveId: number, input: EntityWithOrdinal[]): Promise<void> {
    return api.patch('/api/save/changelists/order', input, { saveId: saveId });
  }

  return {
    create,
    edit,
    delete: delete_,
    setPrimary,
    setActive,
    apply,
    reorder,
  };
}