<script setup lang="ts">
import FactoriesList from '@/components/factories/FactoriesList.vue';
import ChangelistsList from '@/components/factories/ChangelistsList.vue';
import FactoryResources from '@/components/factories/resources/FactoryResources.vue';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import NoSaveLoaded from '@/components/common/NoSaveLoaded.vue';
import { useRoute } from 'vue-router';
import { computed, type ComputedRef } from 'vue';
import ExportImportOverview from '@/components/factories/exportImportOverview/ExportImportOverview.vue';

const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const route = useRoute();

const currentFactoryId: ComputedRef<number | undefined> = computed(() =>
  route.params.factoryId ? Number(route.params.factoryId) : undefined,
);

const showingExportImportOverview: ComputedRef<boolean> = computed(() =>
  route.params.factoryId === 'exportImportOverview',
);

</script>

<template>
  <NoSaveLoaded v-if="!currentGameAndSaveStore.currentSaveId" />

  <div v-else class="main">
    <div class="left">
      <factories-list />
      <changelists-list />
    </div>
    <div class="right">
      <export-import-overview v-if="showingExportImportOverview" />
      <factory-resources v-else-if="currentFactoryId" :factory-id="currentFactoryId" />
    </div>
  </div>

  <router-view name="modal" />
</template>

<style scoped>
.main {
  display: flex;
  gap: 20px;
}

.left {
  width: calc(30% - 20px);
  flex: 0 0 auto;
}

.left > * {
  min-height: 300px;
}

.right {
  width: 70%;
  flex: 0 0 auto;
}
</style>