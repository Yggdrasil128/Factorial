export enum ThroughputUnit {
  ItemsPerSecond = 'ItemsPerSecond',
  ItemsPerMinute = 'ItemsPerMinute',
}

export type UserSettings = {
  throughputUnit: ThroughputUnit;
  lastSaveId: number;
  skipUnsavedChangesWarning: boolean;
}