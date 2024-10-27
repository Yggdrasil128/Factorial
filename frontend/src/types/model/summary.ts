import type {
  Changelist,
  Factory,
  GameVersion,
  Icon,
  Item,
  Machine,
  ProductionStep,
  Recipe,
  RecipeModifier,
  Resource,
  Save
} from '@/types/model/standalone';

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

export type GameVersionSummary = {
  gameVersion: GameVersion;
  icons: Icon[];
  items: Item[];
  recipes: Recipe[];
  recipeModifiers: RecipeModifier[];
  machines: Machine[];
}