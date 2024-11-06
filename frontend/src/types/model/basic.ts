export type ItemQuantity = {
  itemId: number;
  quantity: Fraction;
}

export type QuantityByChangelist = {
  current: Fraction;
  withPrimaryChangelist: Fraction;
  withActiveChangelists: Fraction;
}

export type Fraction = string;

export interface EntityWithOrdinal {
  id: number;
  ordinal: number;
}

export interface GameRelatedEntity {
  id: number;
  gameId: number;
}

export interface SaveRelatedEntity {
  id: number;
  saveId: number;
}
