import { defineStore } from 'pinia';
import type { UserSettings } from '@/types/userSettings';
import { ThroughputUnit, VisibleResourceContributors } from '@/types/userSettings';

export const useUserSettingsStore
  = defineStore('userSettingsStore', {
  state: () => {
    return {
      throughputUnit: ThroughputUnit.ItemsPerSecond,
      lastSaveId: 0,
      skipUnsavedChangesWarning: false,
      visibleLocalResourceContributors: VisibleResourceContributors.Producers,
      visibleGlobalResourceContributors: VisibleResourceContributors.All,
    } as UserSettings;
  },
  persist: {
    storage: localStorage,
  },
});