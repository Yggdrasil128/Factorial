import type {
  Changelist,
  Factory,
  Game,
  GlobalResource,
  Icon,
  Item,
  LocalResource,
  Machine,
  ProductionStep,
  Recipe,
  RecipeModifier,
  Save,
} from '@/types/model/standalone';

export type SaveSummary = {
  save: Save;
  factories: FactorySummary[];
  changelists: Changelist[];
  resources: GlobalResource[];
}

export type FactorySummary = {
  factory: Factory;
  productionSteps: ProductionStep[];
  resources: LocalResource[];
}

export type GameSummary = {
  game: Game;
  icons: Icon[];
  items: Item[];
  recipes: Recipe[];
  recipeModifiers: RecipeModifier[];
  machines: Machine[];
}