import {defineStore} from 'pinia';
import {
  OptimizeProductionStepMachineCountRounding,
  ThroughputUnit,
  type UserSettings,
  VisibleResourceContributors
} from '@/types/userSettings';

// eslint-disable-next-line @typescript-eslint/typedef
export const useUserSettingsStore
  = defineStore('userSettingsStore', {
  state: () => {
    return {
      throughputUnit: ThroughputUnit.ItemsPerSecond,
      lastSaveId: 0,
      skipUnsavedChangesWarning: false,
      visibleLocalResourceContributors: VisibleResourceContributors.Producers,
      visibleGlobalResourceContributors: VisibleResourceContributors.All,
      optimizeProductionStep: {
        machineCountRounding: OptimizeProductionStepMachineCountRounding.None,
        applyDirectly: true,
      }
    } as UserSettings;
  },
  persist: {
    storage: localStorage,
  },
});

export type UserSettingsStore = ReturnType<typeof useUserSettingsStore>;