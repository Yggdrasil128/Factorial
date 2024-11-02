import type { Changelist } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';

export interface ChangelistApi {
  createChangelist(changelist: Partial<Changelist>): Promise<void>;

  editChangelist(changelist: Partial<Changelist>): Promise<void>;

  deleteChangelist(changelistId: number): Promise<void>;

  setPrimary(changelistId: number): Promise<void>;

  setActive(changelistId: number, active: boolean): Promise<void>;

  apply(changelistId: number): Promise<void>;
}

export function useChangelistApi(): ChangelistApi {
  const api: Api = useApi();

  async function createChangelist(changelist: Partial<Changelist>): Promise<void> {
    return api.post('/api/save/changelists', changelist, { saveId: changelist.saveId });
  }

  async function editChangelist(changelist: Partial<Changelist>): Promise<void> {
    return api.patch('/api/changelist', changelist, { changelistId: changelist.id });
  }

  async function deleteChangelist(changelistId: number): Promise<void> {
    return api.delete('/api/changelist', { changelistId: changelistId });
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

  return {
    createChangelist,
    editChangelist,
    deleteChangelist,
    setPrimary,
    setActive,
    apply
  };
}