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

export type UserSettings = {
  throughputUnit: ThroughputUnit;
  lastSaveId: number;
  skipUnsavedChangesWarning: boolean;
  visibleLocalResourceContributors: VisibleResourceContributors;
  visibleGlobalResourceContributors: VisibleResourceContributors;
}