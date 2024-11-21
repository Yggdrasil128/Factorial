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
import PlaceholderHelpBox from '@/components/common/PlaceholderHelpBox.vue';
import { ordinalComparator } from '@/utils/utils';

const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const globalResourceStore = useGlobalResourceStore();
const globalResourceApi = useGlobalResourceApi();

const resources: ComputedRef<GlobalResource[]> = computed(() =>
  globalResourceStore.getBySaveId(currentGameAndSaveStore.currentSaveId).sort(ordinalComparator),
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

  <PlaceholderHelpBox v-if="resources.length === 0" show-only-when-ready
                      title="You don't have any globally exported or imported resources at the moment.">
    <p>
      To enable automatic export or import of a resource, go to a factory and toggle the
      <el-switch size="large" :active-icon="Switch" :inactive-icon="Switch" inline-prompt :model-value="false" />
      switch next to a resource.
    </p>
    <p>
      That resource will then show up here in this list. If the factory has any surplus of that resource
      (i.e. if more of the resource is produced than consumed), the resource will count as exported,
      and the surplus will show up here. Otherwise, the resource will count as imported, and the deficit
      will show up here instead.
    </p>
    <p>
      Factories will always export or import their full surplus or deficit of a resource when activated.
      You can visit this page to see if the needs of your factories are satisfied.
    </p>
  </PlaceholderHelpBox>

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