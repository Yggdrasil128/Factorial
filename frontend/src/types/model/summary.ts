import type { Changelist, Factory, ProductionStep, Resource, Save } from '@/types/model/standalone';

export type Summary = {
  save: Save;
  factories: FactorySummary[];
  changelists: Changelist[];
}

export type FactorySummary = {
  factory: Factory;
  productionSteps: ProductionStep[];
  resources: Resource[];
}