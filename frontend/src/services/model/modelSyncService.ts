import { type ModelStoresUpdateService, useModelStoresUpdateService } from '@/services/model/modelStoresUpdateService';
import type { GameSummary, SaveSummary } from '@/types/model/summary';
import { useCurrentSaveStore } from '@/stores/currentSaveStore';
import { type ModelSyncWebsocket, useModelSyncWebsocket } from '@/api/useModelSyncWebsocket';
import {
  isChangelistRemovedMessage,
  isChangelistUpdatedMessage,
  isFactoryRemovedMessage,
  isFactoryUpdatedMessage,
  isModelChangedMessage,
  isProductionStepRemovedMessage,
  isProductionStepUpdatedMessage,
  isResourceRemovedMessage,
  isResourceUpdatedMessage,
  type WebsocketMessage
} from '@/types/websocketMessages/modelChangedMessages';
import { useSummaryApi } from '@/api/useSummaryApi';

export interface ModelSyncService {
  setSaveIdAndReload: (saveId: number) => void;
  reload: () => void;
}

function useModelSyncService(): ModelSyncService {
  const modelStoresUpdateService: ModelStoresUpdateService = useModelStoresUpdateService();
  const currentSaveStore = useCurrentSaveStore();

  const summaryApi = useSummaryApi();

  const modelSyncWebsocket: ModelSyncWebsocket = useModelSyncWebsocket(onWebsocketMessage, reload);

  function setSaveIdAndReload(saveId: number) {
    summaryApi.getSaveSummary(saveId)
      .then((saveSummary: SaveSummary) => {
        const isInitialLoad: boolean = !currentSaveStore.save;
        modelStoresUpdateService.applySaveSummary(saveSummary);
        if (isInitialLoad) {
          modelSyncWebsocket.connect();
        }

        summaryApi.getGameSummary(saveSummary.save.gameId)
          .then((gameSummary: GameSummary) => {
            modelStoresUpdateService.applyGameSummary(gameSummary);
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

    if (isFactoryUpdatedMessage(message)) {
      modelStoresUpdateService.updateFactory(message.factory);
    } else if (isFactoryRemovedMessage(message)) {
      modelStoresUpdateService.deleteFactory(message.factoryId);
    } else if (isChangelistUpdatedMessage(message)) {
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