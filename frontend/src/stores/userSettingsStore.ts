import { defineStore } from 'pinia';
import type { UserSettings } from '@/types/userSettings';
import { ThroughputUnit } from '@/types/userSettings';

export const useUserSettingsStore
  = defineStore('userSettingsStore', {
  state: () => {
    return {
      throughputUnit: ThroughputUnit.ItemsPerSecond,
      lastSaveId: 0,
    } as UserSettings;
  },
  persist: {
    storage: localStorage,
  },
});