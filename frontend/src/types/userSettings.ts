export enum ThroughputUnit {
  ItemsPerSecond = 'ItemsPerSecond',
  ItemsPerMinute = 'ItemsPerMinute',
}

export enum VisibleResourceContributors {
  None = 'None',
  Producers = 'Producers',
  Consumers = 'Consumers',
  All = 'All',
}

export enum OptimizeProductionStepMachineCountRounding {
  None = 'None',
  RoundUp = 'RoundUp',
  RoundDown = 'RoundDown',
  RoundToNearest = 'RoundToNearest',
}

export type UserSettings = {
  throughputUnit: ThroughputUnit;
  lastSaveId: number;
  skipUnsavedChangesWarning: boolean;
  visibleLocalResourceContributors: VisibleResourceContributors;
  visibleGlobalResourceContributors: VisibleResourceContributors;
  optimizeProductionStep: {
    machineCountRounding: OptimizeProductionStepMachineCountRounding;
    applyDirectly: boolean;
  }
}