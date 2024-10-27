import type { Fraction, QuantityByChangelist } from '@/types/model/basic';

export type Save = {
  id: number;
  gameVersion: number;
  name: string;
}

export type Factory = {
  id: number;
  saveId: number;
  ordinal: number;
  name: string;
  description: string;
  icon: number | null;
  productionSteps: number[];
  resources: number[];
}

export type ProductionStep = {
  id: number;
  factoryId: number;
  machine: number;
  recipe: number;
  modifiers: number[];
  machineCount: Fraction;
  inputs: ProductionEntry[];
  outputs: ProductionEntry[];
}

export type Changelist = {
  id: number;
  saveId: number;
  ordinal: number;
  name: string;
  primary: boolean;
  active: boolean;
  icon: number | null;
  productionStepChanges: ProductionStepChange[];
}

export type ProductionStepChange = {
  productionStep: number;
  change: Fraction;
}

export type ProductionEntry = {
  item: number;
  quantity: QuantityByChangelist;
}

export type Resource = {
  id: number;
  factoryId: number;
  ordinal: number;
  item: number;
  imported: boolean;
  exported: boolean;
  /**
   * ProductionStep IDs
   */
  producers: number[];
  produced: QuantityByChangelist;
  /**
   * ProductionStep IDs
   */
  consumers: number[];
  consumed: QuantityByChangelist;
}