import type { Fraction, ItemQuantity, QuantityByChangelist } from '@/types/model/basic';

export type Save = {
  readonly id: number;
  gameId: number;
  iconId: number;
  name: string;
  description: string;
  ordinal: number;
}

export type Factory = {
  readonly id: number;
  readonly saveId: number;
  ordinal: number;
  name: string;
  description: string;
  iconId: number;
  readonly productionStepIds: number[];
  readonly resourceIds: number[];
  readonly inputs: ProductionEntry[];
  readonly outputs: ProductionEntry[];
}

export type ProductionStep = {
  readonly id: number;
  readonly saveId: number;
  readonly factoryId: number;
  machineId: number;
  recipeId: number;
  modifierIds: number[];
  machineCount: Fraction;
  machineCounts: QuantityByChangelist;
  readonly inputs: ProductionEntry[];
  readonly outputs: ProductionEntry[];
}

export type Changelist = {
  readonly id: number;
  readonly saveId: number;
  ordinal: number;
  name: string;
  primary: boolean;
  active: boolean;
  iconId: number;
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
  readonly id: number;
  readonly saveId: number;
  readonly factoryId: number;
  ordinal: number;
  readonly itemId: number;
  imported: boolean;
  exported: boolean;
  /**
   * ProductionStep IDs
   */
  readonly producerIds: number[];
  readonly produced: QuantityByChangelist;
  /**
   * ProductionStep IDs
   */
  readonly consumerIds: number[];
  readonly consumed: QuantityByChangelist;
  readonly overProduced: QuantityByChangelist;
}

export type Game = {
  readonly id: number;
  name: string;
  iconId: number;
  ordinal: number;
}

export type Icon = {
  readonly id: number;
  readonly gameId: number;
  name: string;
  mimeType: string;
  imageData: string;
  category: string[];
}

export type Item = {
  readonly id: number;
  readonly gameId: number;
  name: string;
  description: string;
  iconId: number;
  category: string[];
}

export type Recipe = {
  readonly id: number;
  readonly gameId: number;
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
  readonly id: number;
  readonly gameId: number;
  name: string;
  description: string;
  iconId: number;
  durationMultiplier: Fraction;
  inputQuantityMultiplier: Fraction;
  outputQuantityMultiplier: Fraction;
}

export type Machine = {
  readonly id: number;
  readonly gameId: number;
  name: string;
  iconId: number;
  machineModifierId: number[];
  category: string[];
}