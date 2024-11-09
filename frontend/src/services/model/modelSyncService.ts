import type { GameSummary, SaveSummary } from '@/types/model/summary';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { type ModelSyncWebsocket, useModelSyncWebsocket } from '@/api/useModelSyncWebsocket';
import {
  isChangelistRemovedMessage,
  isChangelistsReorderedMessage,
  isChangelistUpdatedMessage,
  isFactoriesReorderedMessage,
  isFactoryRemovedMessage,
  isFactoryUpdatedMessage,
  isGameRelatedModelChangedMessage,
  isGameRemovedMessage,
  isGamesReorderedMessage,
  isGameUpdatedMessage,
  isIconRemovedMessage,
  isIconUpdatedMessage,
  isItemRemovedMessage,
  isItemUpdatedMessage,
  isMachineRemovedMessage,
  isMachineUpdatedMessage,
  isProductionStepRemovedMessage,
  isProductionStepUpdatedMessage,
  isRecipeModifierRemovedMessage,
  isRecipeModifierUpdatedMessage,
  isRecipeRemovedMessage,
  isRecipeUpdatedMessage,
  isResourceRemovedMessage,
  isResourcesReorderedMessage,
  isResourceUpdatedMessage,
  isSaveRelatedModelChangedMessage,
  isSaveRemovedMessage,
  isSavesReorderedMessage,
  isSaveUpdatedMessage,
  type WebsocketMessage,
} from '@/types/websocketMessages/modelChangedMessages';
import { useSummaryApi } from '@/api/useSummaryApi';
import { useChangelistStore } from '@/stores/model/changelistStore';
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useProductionStepStore } from '@/stores/model/productionStepStore';
import { useResourceStore } from '@/stores/model/resourceStore';
import { useIconStore } from '@/stores/model/iconStore';
import { useItemStore } from '@/stores/model/itemStore';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import { useMachineStore } from '@/stores/model/machineStore';
import type { EntityWithOrdinal, GameRelatedEntity, SaveRelatedEntity } from '@/types/model/basic';
import { useSaveStore } from '@/stores/model/saveStore';
import { useGameStore } from '@/stores/model/gameStore';
import { useSaveApi } from '@/api/useSaveApi';
import { useGameApi } from '@/api/useGameApi';
import type { Game, Save } from '@/types/model/standalone';

export interface ModelSyncService {
  setCurrentSaveIdAndLoad: (saveId: number) => Promise<void>;
  setEditingGameIdAndLoad: (gameId: number) => Promise<void>;
  clearEditingGameId: () => void;
  reload: () => Promise<void>;
}

function useModelSyncService(): ModelSyncService {
  const currentGameAndSaveStore = useCurrentGameAndSaveStore();

  const saveStore = useSaveStore();
  const changelistStore = useChangelistStore();
  const factoryStore = useFactoryStore();
  const productionStepStore = useProductionStepStore();
  const resourceStore = useResourceStore();

  const gameStore = useGameStore();
  const iconStore = useIconStore();
  const itemStore = useItemStore();
  const recipeStore = useRecipeStore();
  const recipeModifierStore = useRecipeModifierStore();
  const machineStore = useMachineStore();

  const summaryApi = useSummaryApi();
  const saveApi = useSaveApi();
  const gameApi = useGameApi();

  const modelSyncWebsocket: ModelSyncWebsocket = useModelSyncWebsocket(onWebsocketMessage, reload);

  modelSyncWebsocket.connect();

  interface GameRelatedEntityStore {
    map: Map<number, GameRelatedEntity>;

    getByGameId(gameId: number): GameRelatedEntity[];
  }

  function clearStoreByGameId(store: GameRelatedEntityStore, gameId: number): void {
    const entities: GameRelatedEntity[] = store.getByGameId(gameId);
    for (const entity of entities) {
      store.map.delete(entity.id);
    }
  }

  function clearStoresByGameId(gameId: number): void {
    clearStoreByGameId(iconStore, gameId);
    clearStoreByGameId(itemStore, gameId);
    clearStoreByGameId(recipeStore, gameId);
    clearStoreByGameId(recipeModifierStore, gameId);
    clearStoreByGameId(machineStore, gameId);
  }

  interface SaveRelatedEntityStore {
    map: Map<number, SaveRelatedEntity>;

    getBySaveId(gameId: number): SaveRelatedEntity[];
  }

  function clearStoreBySaveId(store: SaveRelatedEntityStore, saveId: number): void {
    const entities: SaveRelatedEntity[] = store.getBySaveId(saveId);
    for (const entity of entities) {
      store.map.delete(entity.id);
    }
  }

  function clearStoresBySaveId(saveId: number): void {
    clearStoreBySaveId(changelistStore, saveId);
    clearStoreBySaveId(factoryStore, saveId);
    clearStoreBySaveId(productionStepStore, saveId);
    clearStoreBySaveId(resourceStore, saveId);
  }

  function applyGameSummary(gameSummary: GameSummary): void {
    clearStoresByGameId(gameSummary.game.id);

    for (const icon of gameSummary.icons) {
      iconStore.map.set(icon.id, icon);
    }
    for (const item of gameSummary.items) {
      itemStore.map.set(item.id, item);
    }
    for (const recipe of gameSummary.recipes) {
      recipeStore.map.set(recipe.id, recipe);
    }
    for (const recipeModifier of gameSummary.recipeModifiers) {
      recipeModifierStore.map.set(recipeModifier.id, recipeModifier);
    }
    for (const machine of gameSummary.machines) {
      machineStore.map.set(machine.id, machine);
    }
  }

  function applySaveSummary(saveSummary: SaveSummary): void {
    clearStoresBySaveId(saveSummary.save.id);

    for (const factorySummary of saveSummary.factories) {
      factoryStore.map.set(factorySummary.factory.id, factorySummary.factory);

      for (const productionStep of factorySummary.productionSteps) {
        productionStepStore.map.set(productionStep.id, productionStep);
      }
      for (const resource of factorySummary.resources) {
        resourceStore.map.set(resource.id, resource);
      }
    }
    for (const changelist of saveSummary.changelists) {
      changelistStore.map.set(changelist.id, changelist);
    }
  }

  interface EntityWithOrdinalStore {
    map: Map<number, EntityWithOrdinal>;
  }

  function applyOrder(store: EntityWithOrdinalStore, order: EntityWithOrdinal[]) {
    for (const entry of order) {
      const entity: EntityWithOrdinal | undefined = store.map.get(entry.id);
      if (entity) {
        entity.ordinal = entry.ordinal;
      }
    }
  }

  async function setCurrentSaveIdAndLoad(saveId: number): Promise<void> {
    const previousSaveId: number | undefined = currentGameAndSaveStore.save?.id;
    const previousGameId: number | undefined = currentGameAndSaveStore.game?.id;

    const saveSummary: SaveSummary = await summaryApi.getSaveSummary(saveId);
    const gameSummary: GameSummary = await summaryApi.getGameSummary(saveSummary.save.gameId);

    currentGameAndSaveStore.save = saveSummary.save;
    currentGameAndSaveStore.game = gameSummary.game;
    applySaveSummary(saveSummary);
    applyGameSummary(gameSummary);

    if (previousSaveId !== undefined && previousSaveId !== saveSummary.save.id) {
      console.log('Current save ID changed, clearing entities from previous save');
      clearStoresBySaveId(previousSaveId);
    }

    if (previousGameId !== undefined
      && previousGameId !== gameSummary.game.id
      && previousGameId !== currentGameAndSaveStore.editingGame?.id
    ) {
      console.log('Current game ID changed, clearing entities from previous game');
      clearStoresByGameId(previousGameId);
    }
  }

  async function setEditingGameIdAndLoad(gameId: number): Promise<void> {
    const previousEditingGameId: number | undefined = currentGameAndSaveStore.editingGame?.id;

    const gameSummary: GameSummary = await summaryApi.getGameSummary(gameId);

    currentGameAndSaveStore.editingGame = gameSummary.game;
    applyGameSummary(gameSummary);

    if (previousEditingGameId !== undefined && previousEditingGameId !== gameSummary.game.id && previousEditingGameId !== currentGameAndSaveStore.game?.id) {
      console.log('Current editing game ID changed, clearing entities from previous game');
      clearStoresByGameId(previousEditingGameId);
    }
  }

  function clearEditingGameId(): void {
    if (currentGameAndSaveStore.editingGame === undefined) return;

    if (currentGameAndSaveStore.editingGame.id !== currentGameAndSaveStore.game?.id) {
      console.log('Current editing game ID changed, clearing entities from previous game');
      clearStoresByGameId(currentGameAndSaveStore.editingGame.id);
    }

    currentGameAndSaveStore.editingGame = undefined;
  }

  async function reload(): Promise<void> {
    let saveSummary: SaveSummary | undefined = undefined;
    if (currentGameAndSaveStore.save) {
      saveSummary = await summaryApi.getSaveSummary(currentGameAndSaveStore.save.id);
    }

    let gameSummary: GameSummary | undefined = undefined;
    if (currentGameAndSaveStore.game) {
      gameSummary = await summaryApi.getGameSummary(currentGameAndSaveStore.game.id);
    }

    let editingGameSummary: GameSummary | undefined = undefined;
    if (currentGameAndSaveStore.editingGame
      && currentGameAndSaveStore.editingGame.id !== currentGameAndSaveStore.game?.id
    ) {
      editingGameSummary = await summaryApi.getGameSummary(currentGameAndSaveStore.editingGame.id);
    }

    await reloadSavesAndGames();

    changelistStore.map.clear();
    factoryStore.map.clear();
    productionStepStore.map.clear();
    resourceStore.map.clear();

    iconStore.map.clear();
    itemStore.map.clear();
    recipeStore.map.clear();
    recipeModifierStore.map.clear();
    machineStore.map.clear();

    if (saveSummary) {
      currentGameAndSaveStore.save = saveSummary.save;
      applySaveSummary(saveSummary);
    }

    if (gameSummary) {
      currentGameAndSaveStore.game = gameSummary.game;
      applyGameSummary(gameSummary);
    }

    if (editingGameSummary) {
      currentGameAndSaveStore.editingGame = editingGameSummary.game;
      applyGameSummary(editingGameSummary);
    }
  }

  async function reloadSavesAndGames(): Promise<void> {
    const saves: Save[] = await saveApi.retrieveAll();
    const games: Game[] = await gameApi.retrieveAll();

    saveStore.map.clear();
    for (const save of saves) {
      saveStore.map.set(save.id, save);
    }

    gameStore.map.clear();
    for (const game of games) {
      gameStore.map.set(game.id, game);
    }
  }

  function onWebsocketMessage(message: WebsocketMessage) {
    console.log(message);
    if (isSaveUpdatedMessage(message)) {
      saveStore.map.set(message.save.id, message.save);
    } else if (isSaveRemovedMessage(message)) {
      saveStore.map.delete(message.saveId);
    } else if (isSavesReorderedMessage(message)) {
      applyOrder(saveStore, message.order);
    } else if (isGameUpdatedMessage(message)) {
      gameStore.map.set(message.game.id, message.game);
    } else if (isGameRemovedMessage(message)) {
      gameStore.map.delete(message.gameId);
    } else if (isGamesReorderedMessage(message)) {
      applyOrder(gameStore, message.order);
    } else if (isSaveRelatedModelChangedMessage(message)) {
      if (message.saveId !== currentGameAndSaveStore.save?.id) {
        return;
      }

      if (isFactoryUpdatedMessage(message)) {
        factoryStore.map.set(message.factory.id, message.factory);
      } else if (isFactoryRemovedMessage(message)) {
        factoryStore.map.delete(message.factoryId);
      } else if (isFactoriesReorderedMessage(message)) {
        applyOrder(factoryStore, message.order);
      } else if (isChangelistUpdatedMessage(message)) {
        changelistStore.map.set(message.changelist.id, message.changelist);
      } else if (isChangelistRemovedMessage(message)) {
        changelistStore.map.delete(message.changelistId);
      } else if (isChangelistsReorderedMessage(message)) {
        applyOrder(changelistStore, message.order);
      } else if (isProductionStepUpdatedMessage(message)) {
        productionStepStore.map.set(message.productionStep.id, message.productionStep);
      } else if (isProductionStepRemovedMessage(message)) {
        productionStepStore.map.delete(message.productionStepId);
      } else if (isResourceUpdatedMessage(message)) {
        resourceStore.map.set(message.resource.id, message.resource);
      } else if (isResourceRemovedMessage(message)) {
        resourceStore.map.delete(message.resourceId);
      } else if (isResourcesReorderedMessage(message)) {
        applyOrder(resourceStore, message.order);
      }

    } else if (isGameRelatedModelChangedMessage(message)) {
      if (message.gameId !== currentGameAndSaveStore.game?.id && message.gameId !== currentGameAndSaveStore.editingGame?.id) {
        return;
      }

      if (isIconUpdatedMessage(message)) {
        iconStore.map.set(message.icon.id, message.icon);
      } else if (isIconRemovedMessage(message)) {
        iconStore.map.delete(message.iconId);
      } else if (isItemUpdatedMessage(message)) {
        itemStore.map.set(message.item.id, message.item);
      } else if (isItemRemovedMessage(message)) {
        itemStore.map.delete(message.itemId);
      } else if (isRecipeUpdatedMessage(message)) {
        recipeStore.map.set(message.recipe.id, message.recipe);
      } else if (isRecipeRemovedMessage(message)) {
        recipeStore.map.delete(message.recipeId);
      } else if (isRecipeModifierUpdatedMessage(message)) {
        recipeModifierStore.map.set(message.recipeModifier.id, message.recipeModifier);
      } else if (isRecipeModifierRemovedMessage(message)) {
        recipeModifierStore.map.delete(message.recipeModifierId);
      } else if (isMachineUpdatedMessage(message)) {
        machineStore.map.set(message.machine.id, message.machine);
      } else if (isMachineRemovedMessage(message)) {
        machineStore.map.delete(message.machineId);
      }
    }
  }

  void reloadSavesAndGames();

  return {
    setCurrentSaveIdAndLoad,
    setEditingGameIdAndLoad,
    clearEditingGameId,
    reload
  };
}

let modelSyncService: ModelSyncService | null = null;
export const getModelSyncService: () => ModelSyncService = () => {
  if (!modelSyncService) modelSyncService = useModelSyncService();
  return modelSyncService;
};