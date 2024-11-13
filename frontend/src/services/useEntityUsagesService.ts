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
  Save,
} from '@/types/model/standalone';
import { useSaveStore } from '@/stores/model/saveStore';
import { useChangelistStore } from '@/stores/model/changelistStore';
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useProductionStepStore } from '@/stores/model/productionStepStore';
import { useResourceStore } from '@/stores/model/resourceStore';
import { useGameStore } from '@/stores/model/gameStore';
import { useItemStore } from '@/stores/model/itemStore';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import { useMachineStore } from '@/stores/model/machineStore';
import { ElMessageBox } from 'element-plus';
import { h } from 'vue';
import EntityUsagesList from '@/components/common/EntityUsagesList.vue';

export interface EntityUsagesService {
  findGameUsages(gameId: number): EntityUsages;
  findItemUsages(itemId: number): EntityUsages;
  findMachineUsages(machineId: number): EntityUsages;
  findRecipeUsages(recipeId: number): EntityUsages;
  findRecipeModifierUsages(recipeModifierId: number): EntityUsages;
  findIconUsages(iconId: number): EntityUsages;
}

export class EntityUsages {
  games: Game[] = [];
  items: Item[] = [];
  machines: Machine[] = [];
  recipes: Recipe[] = [];
  recipeModifiers: RecipeModifier[] = [];
  icons: Icon[] = [];

  saves: Save[] = [];
  factories: Factory[] = [];
  changelists: Changelist[] = [];
  productionSteps: ProductionStep[] = [];
  resources: Resource[] = [];

  public hasAnyUsages(): boolean {
    return this.games.length > 0
      || this.items.length > 0
      || this.machines.length > 0
      || this.recipes.length > 0
      || this.recipeModifiers.length > 0
      || this.icons.length > 0

      || this.saves.length > 0
      || this.factories.length > 0
      || this.changelists.length > 0
      || this.productionSteps.length > 0
      || this.resources.length > 0;
  }

  public showMessageBox(title: string, message: string): void {
    void ElMessageBox({
      title: title,
      type: 'warning',
      message: () => h('div', null, [
        h('p', null, message),
        h(EntityUsagesList, { entityUsages: this }),
      ]),
    });
  }
}

export function useEntityUsagesService(): EntityUsagesService {
  const gameStore = useGameStore();
  const itemStore = useItemStore();
  const machineStore = useMachineStore();
  const recipeStore = useRecipeStore();
  const recipeModifierStore = useRecipeModifierStore();
  // const iconStore = useIconStore();

  const saveStore = useSaveStore();
  const factoryStore = useFactoryStore();
  const changelistStore = useChangelistStore();
  const productionStepStore = useProductionStepStore();
  const resourceStore = useResourceStore();


  function findGameUsages(gameId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.saves = saveStore.getByGameId(gameId);

    return usages;
  }

  function findItemUsages(itemId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.recipes = recipeStore.getAll().filter(recipe => {
      for (const ingredient of recipe.ingredients) {
        if (ingredient.itemId === itemId) {
          return true;
        }
      }
      for (const product of recipe.products) {
        if (product.itemId === itemId) {
          return true;
        }
      }
      return false;
    });

    usages.productionSteps = productionStepStore.getAll().filter(productionStep => {
      for (const input of productionStep.inputs) {
        if (input.itemId === itemId) {
          return true;
        }
      }
      for (const output of productionStep.outputs) {
        if (output.itemId === itemId) {
          return true;
        }
      }
      return false;
    });

    usages.resources = resourceStore.getAll()
      .filter(resource => resource.itemId === itemId);

    return usages;
  }

  function findMachineUsages(machineId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.recipes = recipeStore.getAll()
      .filter(recipe => recipe.applicableMachineIds.includes(machineId));
    usages.productionSteps = productionStepStore.getAll()
      .filter(productionStep => productionStep.machineId === machineId);

    return usages;
  }

  function findRecipeUsages(recipeId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.productionSteps = productionStepStore.getAll()
      .filter(productionStep => productionStep.recipeId === recipeId);

    return usages;
  }

  function findRecipeModifierUsages(recipeModifierId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.machines = machineStore.getAll()
      .filter(machine => machine.machineModifierIds.includes(recipeModifierId));
    usages.recipes = recipeStore.getAll()
      .filter(recipe => recipe.applicableModifierIds.includes(recipeModifierId));
    usages.productionSteps = productionStepStore.getAll()
      .filter(productionStep => productionStep.modifierIds.includes(recipeModifierId));

    return usages;
  }

  function findIconUsages(iconId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.games = gameStore.getAll()
      .filter(game => game.iconId === iconId);
    usages.items = itemStore.getAll()
      .filter(item => item.iconId === iconId);
    usages.machines = machineStore.getAll()
      .filter(machine => machine.iconId === iconId);
    usages.recipes = recipeStore.getAll()
      .filter(recipe => recipe.iconId === iconId);
    usages.recipeModifiers = recipeModifierStore.getAll()
      .filter(recipeModifier => recipeModifier.iconId === iconId);

    usages.saves = saveStore.getAll()
      .filter(save => save.iconId === iconId);
    usages.factories = factoryStore.getAll()
      .filter(factory => factory.iconId === iconId);
    usages.changelists = changelistStore.getAll()
      .filter(changelist => changelist.iconId === iconId);

    return usages;
  }

  return {
    findGameUsages,
    findItemUsages,
    findMachineUsages,
    findRecipeUsages,
    findRecipeModifierUsages,
    findIconUsages,
  };
}