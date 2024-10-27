import type { Fraction, QuantityByChangelist } from '@/types/model/basic';

export type Save = {
  id: number;
  gameVersionId: number;
  name: string;
}

export type Factory = {
  id: number;
  saveId: number;
  ordinal: number;
  name: string;
  description: string;
  iconId: number | null;
  productionStepIds: number[];
  resourceIds: number[];
}

export type ProductionStep = {
  id: number;
  factoryId: number;
  machineId: number;
  recipeId: number;
  modifierIds: number[];
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
  iconId: number | null;
  productionStepChanges: ProductionStepChange[];
}

export type ProductionStepChange = {
  productionStepId: number;
  change: Fraction;
}

export type ProductionEntry = {
  itemId: number;
  quantity: QuantityByChangelist;
}

export type Resource = {
  id: number;
  factoryId: number;
  ordinal: number;
  itemId: number;
  imported: boolean;
  exported: boolean;
  /**
   * ProductionStep IDs
   */
  producerIds: number[];
  produced: QuantityByChangelist;
  /**
   * ProductionStep IDs
   */
  consumerIds: number[];
  consumed: QuantityByChangelist;
}

export type GameVersion = {
  id: number;
  name: string;
  iconId: number;
}

export type Icon = {
  id: number;
  gameVersionId: number;
  name: string;
  mimeType: string;
  category: string[];
}

export type Item = {
  id: number;
  gameVersionId: number;
  name: string;
  description: string;
  iconId: number;
  category: string[];
}

export type Recipe = {
  id: number;
  gameVersionId: number;
  name: string;
  iconId: number;
  ingredients: ItemQuantity[];
  products: ItemQuantity[];
  duration: Fraction;
  applicableModifierIds: number[];
  applicableMachineIds: number[];
  category: string[];
}

export type RecipeModifier = {
  id: number;
  gameVersionId: number;
  name: string;
  description: string;
  iconId: number;
  durationMultiplier: Fraction;
  inputQuantityMultiplier: Fraction;
  outputQuantityMultiplier: Fraction;
}

export type Machine = {
  id: number;
  gameVersionId: number;
  name: string;
  iconId: number;
  machineModifierId: number[];
  category: string[];
}

export type ItemQuantity = {
  itemId: number;
  quantity: Fraction;
}