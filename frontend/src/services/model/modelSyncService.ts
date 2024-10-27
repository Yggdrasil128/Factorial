import { type ModelStoresUpdateService, useModelStoresUpdateService } from '@/services/model/modelStoresUpdateService';
import { useSaveSummaryApi } from '@/api/useSaveSummaryApi';
import type { GameVersionSummary, SaveSummary } from '@/types/model/summary';
import { useCurrentSaveStore } from '@/stores/currentSaveStore';
import { type ModelSyncWebsocket, useModelSyncWebsocket } from '@/api/useModelSyncWebsocket';
import {
  isChangelistRemovedMessage,
  isChangelistUpdatedMessage,
  isModelChangedMessage,
  isProductionStepRemovedMessage,
  isProductionStepUpdatedMessage,
  isResourceRemovedMessage,
  isResourceUpdatedMessage,
  type WebsocketMessage
} from '@/types/websocketMessages/modelChangedEvents';
import { useGameVersionSummaryApi } from '@/api/useGameVersionSummaryApi';

export interface ModelSyncService {
  setSaveIdAndReload: (saveId: number) => void;
  reload: () => void;
}

function useModelSyncService(): ModelSyncService {
  const modelStoresUpdateService: ModelStoresUpdateService = useModelStoresUpdateService();
  const currentSaveStore = useCurrentSaveStore();

  const saveSummaryApi = useSaveSummaryApi();
  const gameVersionSummaryApi = useGameVersionSummaryApi();

  const modelSyncWebsocket: ModelSyncWebsocket = useModelSyncWebsocket(onWebsocketMessage, reload);

  function setSaveIdAndReload(saveId: number) {
    saveSummaryApi.retrieveSummary(saveId)
      .then((saveSummary: SaveSummary) => {
        const isInitialLoad: boolean = !currentSaveStore.save;
        modelStoresUpdateService.applySaveSummary(saveSummary);
        if (isInitialLoad) {
          modelSyncWebsocket.connect();
        }

        gameVersionSummaryApi.retrieveSummary(saveSummary.save.gameVersionId)
          .then((gameVersionSummary: GameVersionSummary) => {
            modelStoresUpdateService.applyGameVersionSummary(gameVersionSummary);
          });
      });
  }

  function reload() {
    if (!currentSaveStore.save) {
      console.error('Cannot reload model without saveId');
      return;
    }
    setSaveIdAndReload(currentSaveStore.save.id);
  }

  function onWebsocketMessage(message: WebsocketMessage) {
    if (!currentSaveStore.save) return;
    if (!isModelChangedMessage(message)) return;
    if (currentSaveStore.save.id !== message.saveId) return;

    if (isChangelistUpdatedMessage(message)) {
      modelStoresUpdateService.updateChangelist(message.changelist);
    } else if (isChangelistRemovedMessage(message)) {
      modelStoresUpdateService.deleteChangelist(message.changelistId);
    } else if (isProductionStepUpdatedMessage(message)) {
      modelStoresUpdateService.updateProductionStep(message.productionStep);
    } else if (isProductionStepRemovedMessage(message)) {
      modelStoresUpdateService.deleteProductionStep(message.productionStepId);
    } else if (isResourceUpdatedMessage(message)) {
      modelStoresUpdateService.updateResource(message.resource);
    } else if (isResourceRemovedMessage(message)) {
      modelStoresUpdateService.deleteResource(message.resourceId);
    }
  }

  return {
    setSaveIdAndReload,
    reload
  };
}

let modelSyncService: ModelSyncService | null = null;
export const getModelSyncService: () => ModelSyncService = () => {
  if (!modelSyncService) modelSyncService = useModelSyncService();
  return modelSyncService;
};