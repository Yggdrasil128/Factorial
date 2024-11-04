import type {
  Changelist,
  Factory,
  Game,
  Icon,
  Item,
  Machine,
  ProductionStep,
  Recipe,
  RecipeModifier,
  Resource,
  Save
} from '@/types/model/standalone';

export type SaveSummary = {
  save: Save;
  factories: FactorySummary[];
  changelists: Changelist[];
}

export type FactorySummary = {
  factory: Factory;
  productionSteps: ProductionStep[];
  resources: Resource[];
}

export type GameSummary = {
  game: Game;
  icons: Icon[];
  items: Item[];
  recipes: Recipe[];
  recipeModifiers: RecipeModifier[];
  machines: Machine[];
}