import type {
  Changelist,
  Factory,
  Game,
  Icon,
  Item,
  LocalResource,
  Machine,
  ProductionStep,
  Recipe,
  RecipeModifier,
  Save,
} from '@/types/model/standalone';
import {type SaveStore, useSaveStore} from '@/stores/model/saveStore';
import {type ChangelistStore, useChangelistStore} from '@/stores/model/changelistStore';
import {type FactoryStore, useFactoryStore} from '@/stores/model/factoryStore';
import {type ProductionStepStore, useProductionStepStore} from '@/stores/model/productionStepStore';
import {type LocalResourceStore, useLocalResourceStore} from '@/stores/model/localResourceStore';
import {type GameStore, useGameStore} from '@/stores/model/gameStore';
import {type ItemStore, useItemStore} from '@/stores/model/itemStore';
import {type RecipeStore, useRecipeStore} from '@/stores/model/recipeStore';
import {type RecipeModifierStore, useRecipeModifierStore} from '@/stores/model/recipeModifierStore';
import {type MachineStore, useMachineStore} from '@/stores/model/machineStore';
import {ElMessageBox} from 'element-plus';
import {h} from 'vue';
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
  resources: LocalResource[] = [];

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
      customStyle: { maxWidth: '800px', width: 'auto' },
      message: () => h('div', null, [
        h('p', null, message),
        h(EntityUsagesList, { entityUsages: this, style: 'margin-right: 8px;' }),
      ]),
    });
  }
}

export function useEntityUsagesService(): EntityUsagesService {
  const gameStore: GameStore = useGameStore();
  const itemStore: ItemStore = useItemStore();
  const machineStore: MachineStore = useMachineStore();
  const recipeStore: RecipeStore = useRecipeStore();
  const recipeModifierStore: RecipeModifierStore = useRecipeModifierStore();
  // const iconStore = useIconStore();

  const saveStore: SaveStore = useSaveStore();
  const factoryStore: FactoryStore = useFactoryStore();
  const changelistStore: ChangelistStore = useChangelistStore();
  const productionStepStore: ProductionStepStore = useProductionStepStore();
  const localResourceStore: LocalResourceStore = useLocalResourceStore();


  function findGameUsages(gameId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.saves = saveStore.getByGameId(gameId);

    return usages;
  }

  function findItemUsages(itemId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.recipes = recipeStore.getAll().filter((recipe: Recipe) => {
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

    usages.productionSteps = productionStepStore.getAll().filter((productionStep: ProductionStep): boolean => {
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

    usages.resources = localResourceStore.getAll()
      .filter((resource: LocalResource): boolean => resource.itemId === itemId);

    return usages;
  }

  function findMachineUsages(machineId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.recipes = recipeStore.getAll()
      .filter((recipe: Recipe) => recipe.applicableMachineIds.includes(machineId));
    usages.productionSteps = productionStepStore.getAll()
      .filter((productionStep: ProductionStep): boolean => productionStep.machineId === machineId);

    return usages;
  }

  function findRecipeUsages(recipeId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.productionSteps = productionStepStore.getAll()
      .filter((productionStep: ProductionStep): boolean => productionStep.recipeId === recipeId);

    return usages;
  }

  function findRecipeModifierUsages(recipeModifierId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.machines = machineStore.getAll()
      .filter((machine: Machine) => machine.machineModifierIds.includes(recipeModifierId));
    usages.recipes = recipeStore.getAll()
      .filter((recipe: Recipe) => recipe.applicableModifierIds.includes(recipeModifierId));
    usages.productionSteps = productionStepStore.getAll()
      .filter((productionStep: ProductionStep) => productionStep.modifierIds.includes(recipeModifierId));

    return usages;
  }

  function findIconUsages(iconId: number): EntityUsages {
    const usages: EntityUsages = new EntityUsages();

    usages.games = gameStore.getAll()
      .filter((game: Game): boolean => game.iconId === iconId);
    usages.items = itemStore.getAll()
      .filter((item: Item): boolean => item.iconId === iconId);
    usages.machines = machineStore.getAll()
      .filter((machine: Machine): boolean => machine.iconId === iconId);
    usages.recipes = recipeStore.getAll()
      .filter((recipe: Recipe): boolean => recipe.iconId === iconId);
    usages.recipeModifiers = recipeModifierStore.getAll()
      .filter((recipeModifier: RecipeModifier): boolean => recipeModifier.iconId === iconId);

    usages.saves = saveStore.getAll()
      .filter((save: Save): boolean => save.iconId === iconId);
    usages.factories = factoryStore.getAll()
      .filter((factory: Factory): boolean => factory.iconId === iconId);
    usages.changelists = changelistStore.getAll()
      .filter((changelist: Changelist): boolean => changelist.iconId === iconId);

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