<script setup lang="ts">
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { computed, type ComputedRef, watch } from 'vue';
import { type RouteLocationAsRelativeGeneric, useRoute, useRouter } from 'vue-router';
import type { Factory } from '@/types/model/standalone';
import { Delete, Edit, Plus, Switch } from '@element-plus/icons-vue';
import { ElButton, ElButtonGroup, ElPopconfirm } from 'element-plus';
import { VueDraggableNext } from 'vue-draggable-next';
import IconImg from '@/components/common/IconImg.vue';
import { type FactoryApi, useFactoryApi } from '@/api/model/useFactoryApi';
import { type DraggableSupport, useDraggableSupport } from '@/utils/useDraggableSupport';
import type { EntityWithOrdinal } from '@/types/model/basic';
import BgcElButton from '@/components/common/input/BgcElButton.vue';
import CustomElTooltip from '@/components/common/CustomElTooltip.vue';
import PlaceholderHelpBox from '@/components/common/PlaceholderHelpBox.vue';

const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const factoryStore = useFactoryStore();
const factoryApi: FactoryApi = useFactoryApi();

const router = useRouter();
const route = useRoute();

const currentFactoryId: ComputedRef<number | undefined> = computed(() =>
  route.params.factoryId ? Number(route.params.factoryId) : undefined,
);

const showingExportImportOverview: ComputedRef<boolean> = computed(() =>
  route.params.factoryId === 'exportImportOverview',
);

const factories: ComputedRef<Factory[]> = computed(() => {
  return factoryStore.getBySaveId(currentGameAndSaveStore.currentSaveId)
    .sort((a, b) => a.ordinal - b.ordinal);
});

const draggableSupport: DraggableSupport = useDraggableSupport(factories,
  (input: EntityWithOrdinal[]) => factoryApi.reorder(currentGameAndSaveStore.currentSaveId, input),
);

function newFactory(): void {
  router.push({ name: 'newFactory', params: { factoryId: route.params.factoryId } });
}

function editFactory(editFactoryId: number): void {
  router.push({
    name: 'editFactory',
    params: { factoryId: route.params.factoryId, editFactoryId: editFactoryId },
  });
}

function viewFactory(factoryId: number, replace?: boolean): void {
  if (currentFactoryId.value === factoryId) {
    return;
  }

  const toRoute: RouteLocationAsRelativeGeneric =
    { name: 'factories', params: { factoryId: factoryId } };

  if (replace) {
    router.replace(toRoute);
  } else {
    router.push(toRoute);
  }
}

function viewExportImportOverview(): void {
  router.push({ name: 'factories', params: { factoryId: 'exportImportOverview' } });
}

watch(computed(() => factories.value.length), () => {
  if (factories.value.length > 0 && !currentFactoryId.value && !showingExportImportOverview.value) {
    viewFactory(factories.value[0].id, true);
  }
}, { immediate: true });

function deleteFactory(factoryId: number) {
  factoryApi.delete(factoryId);
}

</script>

<template>
  <div class="factoryList">
    <div class="card exportImportOverview" :class="{active: showingExportImportOverview}"
         @click="viewExportImportOverview">
      <el-icon class="icon" :size="32" style="margin: 4px;">
        <Switch />
      </el-icon>
      <div class="name">
        Export / Import overview
      </div>
    </div>

    <div class="row items-center">
      <h2 style="flex: 1 1 auto;">Factories ({{ factories.length }})</h2>
      <div class="createFactory">
        <el-button type="primary" :icon="Plus" @click="newFactory()">New factory</el-button>
      </div>
    </div>

    <PlaceholderHelpBox v-if="factories.length === 0" show-only-when-ready
                        title="You don't have any factories at the moment.">
      <p>
        <el-link type="primary" @click="newFactory">
          Create your first factory
        </el-link>
        to get started.
      </p>
    </PlaceholderHelpBox>

    <vue-draggable-next v-else :model-value="factories" @end="draggableSupport.onDragEnd">
      <div v-for="factory in factories"
           :key="factory.id"
           class="card" :class="{ active: factory.id === currentFactoryId, hasIcon: !!factory.iconId }"
           @click="viewFactory(factory.id)">
        <icon-img class="icon" :icon="factory.iconId" :size="40" />

        <div class="name">
          {{ factory.name }}
        </div>

        <div class="buttons" @click.stop>
          <el-button-group>
            <custom-el-tooltip content="Edit">
              <bgc-el-button :icon="Edit" @click.stop="editFactory(factory.id)" />
            </custom-el-tooltip>

            <el-popconfirm title="Delete this factory?"
                           width="200px"
                           @confirm="deleteFactory(factory.id)">
              <template #reference>
                  <span class="row center tooltipHelperSpan">
                    <custom-el-tooltip content="Delete">
                      <el-button type="danger" :icon="Delete" :disabled="factories.length === 1" />
                    </custom-el-tooltip>
                  </span>
              </template>
            </el-popconfirm>
          </el-button-group>
        </div>
      </div>
    </vue-draggable-next>
  </div>
</template>

<style scoped>
.card {
  --padding-left-right: 8px;
  padding: 2px var(--padding-left-right);
  width: calc(100% - 2 * var(--padding-left-right));
  min-height: 40px;
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #4b4b4b;
  border-radius: 8px;
}

.card:not(.active) {
  cursor: pointer;
}

.card + .card {
  margin-top: 10px;
}

.card.active {
  background-color: #556e55;
}

.card .name {
  font-size: 20px;
  min-width: 0;
  flex: 1 1 auto;
  overflow: auto;
  margin: 2px 0;
}

.card.active .name {
  font-weight: bold;
}

.card .buttons {
  flex: 0 0 auto;
  opacity: 0;
}

.card:hover .buttons {
  opacity: 1;
}

.exportImportOverview {
  margin-top: 12px;
  margin-bottom: 12px;
}
</style>