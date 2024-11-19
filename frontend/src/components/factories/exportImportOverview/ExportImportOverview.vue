<script setup lang="ts">
import ProductionsStepsDisplayChooser from '@/components/factories/resources/ResourceContributorsDisplayToggle.vue';
import { ElIcon } from 'element-plus';
import { Switch } from '@element-plus/icons-vue';
import { computed, type ComputedRef } from 'vue';
import type { GlobalResource } from '@/types/model/standalone';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { useGlobalResourceStore } from '@/stores/model/globalResourceStore';
import { useGlobalResourceApi } from '@/api/model/useGlobalResourceApi';
import { type DraggableSupport, useDraggableSupport } from '@/utils/useDraggableSupport';
import type { EntityWithOrdinal } from '@/types/model/basic';
import { VueDraggableNext } from 'vue-draggable-next';
import SaveResource from '@/components/factories/exportImportOverview/SaveResource.vue';

const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const globalResourceStore = useGlobalResourceStore();
const globalResourceApi = useGlobalResourceApi();

const resources: ComputedRef<GlobalResource[]> = computed(() =>
  globalResourceStore.getBySaveId(currentGameAndSaveStore.currentSaveId),
);

const draggableSupport: DraggableSupport = useDraggableSupport(resources,
  (input: EntityWithOrdinal[]) => globalResourceApi.reorder(currentGameAndSaveStore.currentSaveId, input),
);

</script>

<template>
  <div class="row items-center full-width" style="gap: 24px; margin-bottom: 12px;">
    <el-icon :size="32" style="margin-right: -16px;">
      <Switch />
    </el-icon>
    <div class="" style="min-width: 0; flex: 1 1 auto;">
      <h2 class="ellipsis">Export / Import overview</h2>
    </div>
    <div style="flex: 0 0 auto;">
      <ProductionsStepsDisplayChooser global />
    </div>
  </div>

  <vue-draggable-next :model-value="resources" @end="draggableSupport.onDragEnd">
    <save-resource
      v-for="resource in resources"
      :key="resource.id"
      :resource="resource"
    />
  </vue-draggable-next>
</template>

<style scoped>

</style>