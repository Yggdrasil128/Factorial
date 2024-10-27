import type { Summary } from '@/types/model/summary';
import { useChangelistStore } from '@/stores/model/changelistStore';
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useProductionStepStore } from '@/stores/model/productionStepStore';
import { useCurrentSaveStore } from '@/stores/currentSaveStore';
import type { Changelist, Factory, ProductionStep, Resource, Save } from '@/types/model/standalone';
import { useResourceStore } from '@/stores/model/resourceStore';

export interface ModelStoresUpdateService {
  applyCurrentSave: (save: Save) => void;

  updateFactory: (factory: Factory) => void;
  updateProductionStep: (productionStep: ProductionStep) => void;
  updateChangelist: (changelist: Changelist) => void;
  updateResource: (resource: Resource) => void;

  deleteFactory: (id: number) => void;
  deleteProductionStep: (id: number) => void;
  deleteChangelist: (id: number) => void;
  deleteResource: (id: number) => void;

  applySaveSummary: (saveSummary: Summary) => void;
}

export function useModelStoresUpdateService(): ModelStoresUpdateService {
  const currentSaveStore = useCurrentSaveStore();

  const changelistStore = useChangelistStore();
  const factoryStore = useFactoryStore();
  const productionStepStore = useProductionStepStore();
  const resourceStore = useResourceStore();

  function clearStores(): void {
    currentSaveStore.save = undefined;

    changelistStore.map.clear();
    factoryStore.map.clear();
    productionStepStore.map.clear();
    resourceStore.map.clear();
  }

  function applyCurrentSave(save: Save) {
    currentSaveStore.save = save;
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


  function applySaveSummary(saveSummary: Summary): void {
    clearStores();

    applyCurrentSave(saveSummary.save);

    for (const factorySummary of saveSummary.factories) {
      updateFactory(factorySummary.factory);
      for (const productionStep of factorySummary.productionSteps) {
        updateProductionStep(productionStep);
      }
      for (const resource of factorySummary.resources) {
        updateResource(resource);
      }
    }
    for (const changelist of saveSummary.changelists) {
      updateChangelist(changelist);
    }
  }

  return {
    applyCurrentSave,

    updateFactory,
    updateChangelist,
    updateProductionStep,
    updateResource,

    deleteFactory,
    deleteChangelist,
    deleteProductionStep,
    deleteResource,

    applySaveSummary
  };
}