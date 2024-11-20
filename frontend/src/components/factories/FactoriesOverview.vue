<script setup lang="ts">
import FactoriesList from '@/components/factories/FactoriesList.vue';
import ChangelistsList from '@/components/factories/ChangelistsList.vue';
import FactoryResources from '@/components/factories/resources/FactoryResources.vue';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { useRoute, useRouter } from 'vue-router';
import { computed, type ComputedRef } from 'vue';
import ExportImportOverview from '@/components/factories/exportImportOverview/ExportImportOverview.vue';
import PlaceholderHelpBox from '@/components/common/PlaceholderHelpBox.vue';

const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const route = useRoute();
const router = useRouter();

const currentFactoryId: ComputedRef<number | undefined> = computed(() =>
  route.params.factoryId ? Number(route.params.factoryId) : undefined,
);

const showingExportImportOverview: ComputedRef<boolean> = computed(() =>
  route.params.factoryId === 'exportImportOverview',
);

</script>

<template>
  <PlaceholderHelpBox v-if="!currentGameAndSaveStore.currentSaveId" show-only-when-ready
                      title="You don't have any save loaded at the moment.">
    <p>
      Go to
      <el-link type="primary" @click="router.push({ name: 'savesAndGames' })">
        Saves / Games
      </el-link>
      and load a save.
    </p>
  </PlaceholderHelpBox>

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