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
  isGlobalResourceRemovedMessage,
  isGlobalResourcesReorderedMessage,
  isGlobalResourceUpdatedMessage,
  isIconRemovedMessage,
  isIconUpdatedMessage,
  isItemRemovedMessage,
  isItemUpdatedMessage,
  isLocalResourceRemovedMessage,
  isLocalResourcesReorderedMessage,
  isLocalResourceUpdatedMessage,
  isMachineRemovedMessage,
  isMachineUpdatedMessage,
  isProductionStepRemovedMessage,
  isProductionStepUpdatedMessage,
  isRecipeModifierRemovedMessage,
  isRecipeModifierUpdatedMessage,
  isRecipeRemovedMessage,
  isRecipeUpdatedMessage,
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
import { useLocalResourceStore } from '@/stores/model/localResourceStore';
import { useIconStore } from '@/stores/model/iconStore';
import { useItemStore } from '@/stores/model/itemStore';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import { useMachineStore } from '@/stores/model/machineStore';
import type { EntityWithOrdinal, GameRelatedEntity, SaveRelatedEntity } from '@/types/model/basic';
import { useSaveStore } from '@/stores/model/saveStore';
import { useGameStore } from '@/stores/model/gameStore';
import { useSaveApi } from '@/api/model/useSaveApi';
import { useGameApi } from '@/api/model/useGameApi';
import type { Game, Save } from '@/types/model/standalone';
import { reactive } from 'vue';
import _ from 'lodash';
import { useUserSettingsStore } from '@/stores/userSettingsStore';
import { useGlobalResourceStore } from '@/stores/model/globalResourceStore';

export interface UseModelSyncService {
  setCurrentSaveIdAndLoad: (saveId: number) => Promise<void>;
  setEditingGameIdAndLoad: (gameId: number) => Promise<void>;
  clearEditingGameId: () => void;
  reload: () => Promise<void>;
}

function useModelSyncService(): UseModelSyncService {
  const currentGameAndSaveStore = useCurrentGameAndSaveStore();
  const userSettingsStore = useUserSettingsStore();

  const storedSaveIds: Set<number> = reactive(new Set());
  const saveStore = useSaveStore();
  const changelistStore = useChangelistStore();
  const factoryStore = useFactoryStore();
  const productionStepStore = useProductionStepStore();
  const localResourceStore = useLocalResourceStore();
  const globalResourceStore = useGlobalResourceStore();

  const storedGameIds: Set<number> = reactive(new Set());
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
    clearStoreBySaveId(localResourceStore, saveId);
    clearStoreBySaveId(globalResourceStore, saveId);
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
      for (const localResource of factorySummary.resources) {
        localResourceStore.map.set(localResource.id, localResource);
      }
    }
    for (const changelist of saveSummary.changelists) {
      changelistStore.map.set(changelist.id, changelist);
    }
    for (const globalResource of saveSummary.resources) {
      globalResourceStore.map.set(globalResource.id, globalResource);
    }
  }

  async function updateStores(fullReload?: boolean): Promise<void> {
    const requiredGameIds: Set<number> = new Set();
    const requiredSaveIds: Set<number> = new Set();

    if (currentGameAndSaveStore.currentSaveId) {
      requiredSaveIds.add(currentGameAndSaveStore.currentSaveId);
    }
    if (currentGameAndSaveStore.currentGameId) {
      requiredGameIds.add(currentGameAndSaveStore.currentGameId);
    }
    if (currentGameAndSaveStore.editingGameId) {
      requiredGameIds.add(currentGameAndSaveStore.editingGameId);

      const saves: Save[] = saveStore.getByGameId(currentGameAndSaveStore.editingGameId);
      for (const save of saves) {
        requiredSaveIds.add(save.id);
      }
    }

    // determine excess/missing games/saves

    const excessGameIds: number[] = _.difference([...storedGameIds], [...requiredGameIds]);
    const excessSaveIds: number[] = _.difference([...storedSaveIds], [...requiredSaveIds]);

    let missingGameIds: number[];
    let missingSaveIds: number[];

    if (fullReload) {
      missingGameIds = [...requiredGameIds];
      missingSaveIds = [...requiredSaveIds];
    } else {
      missingGameIds = _.difference([...requiredGameIds], [...storedGameIds]);
      missingSaveIds = _.difference([...requiredSaveIds], [...storedSaveIds]);
    }

    // load missing summaries

    const gameSummaries: GameSummary[] = [];
    for (const gameId of missingGameIds) {
      gameSummaries.push(await summaryApi.getGameSummary(gameId));
    }

    const saveSummaries: SaveSummary[] = [];
    for (const saveId of missingSaveIds) {
      saveSummaries.push(await summaryApi.getSaveSummary(saveId));
    }

    // apply changes

    for (const saveId of excessSaveIds) {
      clearStoresBySaveId(saveId);
      storedSaveIds.delete(saveId);
    }
    for (const gameId of excessGameIds) {
      clearStoresByGameId(gameId);
      storedGameIds.delete(gameId);
    }

    for (const gameSummary of gameSummaries) {
      applyGameSummary(gameSummary);
      storedGameIds.add(gameSummary.game.id);
    }
    for (const saveSummary of saveSummaries) {
      applySaveSummary(saveSummary);
      storedSaveIds.add(saveSummary.save.id);
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
    const save: Save | undefined = saveStore.getById(saveId);
    if (!save) {
      console.warn('Save with id ' + saveId + ' not found.');
      return;
    }

    currentGameAndSaveStore.currentSaveId = saveId;
    currentGameAndSaveStore.currentGameId = save.gameId;

    userSettingsStore.lastSaveId = saveId;

    await updateStores();
  }

  async function setEditingGameIdAndLoad(gameId: number): Promise<void> {
    currentGameAndSaveStore.editingGameId = gameId;

    await updateStores();
  }

  function clearEditingGameId(): void {
    currentGameAndSaveStore.editingGameId = 0;

    void updateStores();
  }

  async function reload(): Promise<void> {
    await updateStores(true);
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
    if (isSaveUpdatedMessage(message)) {
      saveStore.map.set(message.save.id, message.save);
    } else if (isSavesReorderedMessage(message)) {
      applyOrder(saveStore, message.order);
    } else if (isSaveRemovedMessage(message)) {
      saveStore.map.delete(message.saveId);

      if (currentGameAndSaveStore.currentSaveId === message.saveId) {
        currentGameAndSaveStore.currentSaveId = 0;
        currentGameAndSaveStore.currentGameId = 0;
        void updateStores();
      }
    } else if (isGameUpdatedMessage(message)) {
      gameStore.map.set(message.game.id, message.game);
    } else if (isGamesReorderedMessage(message)) {
      applyOrder(gameStore, message.order);
    } else if (isGameRemovedMessage(message)) {
      gameStore.map.delete(message.gameId);

      if (currentGameAndSaveStore.currentGameId === message.gameId) {
        currentGameAndSaveStore.currentSaveId = 0;
        currentGameAndSaveStore.currentGameId = 0;
        void updateStores();
      }

    } else if (isSaveRelatedModelChangedMessage(message) && storedSaveIds.has(message.saveId)) {

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
      } else if (isLocalResourceUpdatedMessage(message)) {
        localResourceStore.map.set(message.localResource.id, message.localResource);
      } else if (isLocalResourceRemovedMessage(message)) {
        localResourceStore.map.delete(message.localResourceId);
      } else if (isLocalResourcesReorderedMessage(message)) {
        applyOrder(localResourceStore, message.order);
      } else if (isGlobalResourceUpdatedMessage(message)) {
        globalResourceStore.map.set(message.globalResource.id, message.globalResource);
      } else if (isGlobalResourceRemovedMessage(message)) {
        globalResourceStore.map.delete(message.globalResourceId);
      } else if (isGlobalResourcesReorderedMessage(message)) {
        applyOrder(globalResourceStore, message.order);
      }

    } else if (isGameRelatedModelChangedMessage(message) && storedGameIds.has(message.gameId)) {

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

  async function init(): Promise<void> {
    modelSyncWebsocket.connect();

    await reloadSavesAndGames();

    if (userSettingsStore.lastSaveId && saveStore.getById(userSettingsStore.lastSaveId)) {
      await setCurrentSaveIdAndLoad(userSettingsStore.lastSaveId);
    } else {
      await updateStores();
    }
  }

  void init();

  return {
    setCurrentSaveIdAndLoad,
    setEditingGameIdAndLoad,
    clearEditingGameId,
    reload,
  };
}

let modelSyncService: UseModelSyncService | null = null;
export const getModelSyncService: () => UseModelSyncService = () => {
  if (!modelSyncService) modelSyncService = useModelSyncService();
  return modelSyncService;
};