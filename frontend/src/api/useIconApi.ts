import type { Icon } from '@/types/model/standalone';
import { type Api, useApi } from '@/api/useApi';
import { ElMessage } from 'element-plus';

export interface IconApi {
  createIcon(icon: Partial<Icon>): Promise<void>;
  editIcon(icon: Partial<Icon>): Promise<void>;
  deleteIcon(iconId: number): Promise<void>;
}

export function useIconApi(): IconApi {
  const api: Api = useApi();

  async function createIcon(icon: Partial<Icon>): Promise<void> {
    return api.post('/api/game/icons', icon, { gameId: icon.gameId })
      .then(() => {
        ElMessage.success({ message: 'Icon created.' });
      });
  }

  async function editIcon(icon: Partial<Icon>): Promise<void> {
    return api.patch('/api/icon', icon, { iconId: icon.id })
      .then(() => {
        ElMessage.success({ message: 'Icon updated.' });
      });
  }

  async function deleteIcon(iconId: number): Promise<void> {
    return api.delete('/api/icon', { iconId: iconId })
      .then(() => {
        ElMessage.success({ message: 'Icon deleted.' });
      });
  }

  return {
    createIcon,
    editIcon,
    deleteIcon,
  };
}