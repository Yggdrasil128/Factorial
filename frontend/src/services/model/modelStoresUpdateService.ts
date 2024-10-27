import type { GameVersionSummary, SaveSummary } from '@/types/model/summary';
import { useChangelistStore } from '@/stores/model/changelistStore';
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useProductionStepStore } from '@/stores/model/productionStepStore';
import { useCurrentSaveStore } from '@/stores/currentSaveStore';
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
import { useResourceStore } from '@/stores/model/resourceStore';
import { useCurrentGameVersionStore } from '@/stores/currentGameVersionStore';
import { useIconStore } from '@/stores/model/iconStore';
import { useItemStore } from '@/stores/model/itemStore';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import { useMachineStore } from '@/stores/model/machineStore';

export interface ModelStoresUpdateService {
  applyCurrentSave: (save: Save) => void;
  applyCurrentGameVersion: (gameVersion: GameVersion) => void;

  updateFactory: (factory: Factory) => void;
  updateProductionStep: (productionStep: ProductionStep) => void;
  updateChangelist: (changelist: Changelist) => void;
  updateResource: (resource: Resource) => void;
  updateIcon: (icon: Icon) => void;
  updateItem: (item: Item) => void;
  updateRecipe: (recipe: Recipe) => void;
  updateRecipeModifier: (recipe: RecipeModifier) => void;
  updateMachine: (machine: Machine) => void;

  deleteFactory: (id: number) => void;
  deleteProductionStep: (id: number) => void;
  deleteChangelist: (id: number) => void;
  deleteResource: (id: number) => void;
  deleteIcon: (id: number) => void;
  deleteItem: (id: number) => void;
  deleteRecipe: (id: number) => void;
  deleteRecipeModifier: (id: number) => void;
  deleteMachine: (id: number) => void;

  applySaveSummary: (saveSummary: SaveSummary) => void;
  applyGameVersionSummary: (gameVersionSummary: GameVersionSummary) => void;
}

export function useModelStoresUpdateService(): ModelStoresUpdateService {
  const currentSaveStore = useCurrentSaveStore();
  const currentGameVersionStore = useCurrentGameVersionStore();

  const changelistStore = useChangelistStore();
  const factoryStore = useFactoryStore();
  const productionStepStore = useProductionStepStore();
  const resourceStore = useResourceStore();
  const iconStore = useIconStore();
  const itemStore = useItemStore();
  const recipeStore = useRecipeStore();
  const recipeModifierStore = useRecipeModifierStore();
  const machineStore = useMachineStore();

  function applyCurrentSave(save: Save) {
    currentSaveStore.save = save;
  }

  function applyCurrentGameVersion(gameVersion: GameVersion) {
    currentGameVersionStore.gameVersion = gameVersion;
  }


  function updateFactory(factory: Factory): void {
    factoryStore.map.set(factory.id, factory);
  }

  function updateProductionStep(productionStep: ProductionStep): void {
    productionStepStore.map.set(productionStep.id, productionStep);
  }

  function updateChangelist(changelist: Changelist): void {
    changelistStore.map.set(changelist.id, changelist);
  }

  function updateResource(resource: Resource): void {
    resourceStore.map.set(resource.id, resource);
  }

  function updateIcon(icon: Icon): void {
    iconStore.map.set(icon.id, icon);
  }

  function updateItem(item: Item): void {
    itemStore.map.set(item.id, item);
  }

  function updateRecipe(recipe: Recipe): void {
    recipeStore.map.set(recipe.id, recipe);
  }

  function updateRecipeModifier(recipeModifier: RecipeModifier): void {
    recipeModifierStore.map.set(recipeModifier.id, recipeModifier);
  }

  function updateMachine(machine: Machine): void {
    machineStore.map.set(machine.id, machine);
  }


  function deleteFactory(id: number): void {
    factoryStore.map.delete(id);
  }

  function deleteProductionStep(id: number): void {
    productionStepStore.map.delete(id);
  }

  function deleteChangelist(id: number): void {
    changelistStore.map.delete(id);
  }

  function deleteResource(id: number): void {
    resourceStore.map.delete(id);
  }

  function deleteIcon(id: number): void {
    iconStore.map.delete(id);
  }

  function deleteItem(id: number): void {
    itemStore.map.delete(id);
  }

  function deleteRecipe(id: number): void {
    recipeStore.map.delete(id);
  }

  function deleteRecipeModifier(id: number): void {
    recipeModifierStore.map.delete(id);
  }

  function deleteMachine(id: number): void {
    machineStore.map.delete(id);
  }


  function applySaveSummary(summary: SaveSummary): void {
    changelistStore.map.clear();
    factoryStore.map.clear();
    productionStepStore.map.clear();
    resourceStore.map.clear();

    applyCurrentSave(summary.save);

    for (const factorySummary of summary.factories) {
      updateFactory(factorySummary.factory);
      for (const productionStep of factorySummary.productionSteps) {
        updateProductionStep(productionStep);
      }
      for (const resource of factorySummary.resources) {
        updateResource(resource);
      }
    }
    for (const changelist of summary.changelists) {
      updateChangelist(changelist);
    }
  }

  function applyGameVersionSummary(summary: GameVersionSummary): void {
    iconStore.map.clear();
    itemStore.map.clear();
    recipeStore.map.clear();
    recipeModifierStore.map.clear();
    machineStore.map.clear();

    applyCurrentGameVersion(summary.gameVersion);

    for (const icon of summary.icons) {
      updateIcon(icon);
    }
    for (const item of summary.items) {
      updateItem(item);
    }
    for (const recipe of summary.recipes) {
      updateRecipe(recipe);
    }
    for (const recipeModifier of summary.recipeModifiers) {
      updateRecipeModifier(recipeModifier);
    }
    for (const machine of summary.machines) {
      updateMachine(machine);
    }
  }

  return {
    applyCurrentSave,
    applyCurrentGameVersion,

    updateFactory,
    updateChangelist,
    updateProductionStep,
    updateResource,
    updateIcon,
    updateItem,
    updateRecipe,
    updateRecipeModifier,
    updateMachine,

    deleteFactory,
    deleteChangelist,
    deleteProductionStep,
    deleteResource,
    deleteIcon,
    deleteItem,
    deleteRecipe,
    deleteRecipeModifier,
    deleteMachine,

    applySaveSummary,
    applyGameVersionSummary
  };
}