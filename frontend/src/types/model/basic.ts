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
