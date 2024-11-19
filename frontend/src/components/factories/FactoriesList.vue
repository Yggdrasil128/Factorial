<script setup lang="ts">
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { computed, type ComputedRef, watch } from 'vue';
import { type RouteLocationAsRelativeGeneric, useRoute, useRouter } from 'vue-router';
import type { Factory } from '@/types/model/standalone';
import { Delete, Edit, Plus, Switch } from '@element-plus/icons-vue';
import { ElButton, ElButtonGroup, ElPopconfirm, ElTooltip } from 'element-plus';
import { VueDraggableNext } from 'vue-draggable-next';
import IconImg from '@/components/common/IconImg.vue';
import { type FactoryApi, useFactoryApi } from '@/api/model/useFactoryApi';
import { type DraggableSupport, useDraggableSupport } from '@/utils/useDraggableSupport';
import type { EntityWithOrdinal } from '@/types/model/basic';
import BgcElButton from '@/components/common/input/BgcElButton.vue';
import { useGlobalResourceStore } from '@/stores/model/globalResourceStore';

const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const factoryStore = useFactoryStore();
const factoryApi: FactoryApi = useFactoryApi();
const globalResourceStore = useGlobalResourceStore();

const router = useRouter();
const route = useRoute();

const currentFactoryId: ComputedRef<number | undefined> = computed(() =>
  route.params.factoryId ? Number(route.params.factoryId) : undefined,
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

watch(computed(() => factories.value.length), () => {
  if (factories.value.length > 0 && !currentFactoryId.value) {
    viewFactory(factories.value[0].id, true);
  }
}, { immediate: true });

function deleteFactory(factoryId: number) {
  factoryApi.delete(factoryId);
}

const hasGlobalResources: ComputedRef<boolean> = computed(() =>
  globalResourceStore.getBySaveId(currentGameAndSaveStore.currentSaveId).length > 0,
);

</script>

<template>
  <div class="factoryList">
    <div v-if="hasGlobalResources || true" class="card exportImportOverview">
      <el-icon class="icon" :size="32" style="margin: 4px;">
        <Switch />
      </el-icon>
      <div class="name">
        Export / Import overview
      </div>
    </div>

    <div class="row items-center">
      <h2 style="flex: 1 1 auto;">Factories</h2>
      <div class="createFactory">
        <el-button type="primary" :icon="Plus" @click="newFactory()">New factory</el-button>
      </div>
    </div>

    <vue-draggable-next :model-value="factories" @end="draggableSupport.onDragEnd">
      <div
        v-for="factory in factories"
        :key="factory.id"
        class="card"
        :class="{ active: factory.id === currentFactoryId, hasIcon: !!factory.iconId }"
      >
        <icon-img class="icon" :icon="factory.iconId" :size="40" />
        <div class="name" @click="viewFactory(factory.id)">
          {{ factory.name }}
        </div>
        <div class="buttons">
          <el-button-group>
            <el-tooltip
              effect="dark"
              placement="top-start"
              transition="none"
              :hide-after="0"
              content="Edit"
            >
              <bgc-el-button :icon="Edit" @click="editFactory(factory.id)" />
            </el-tooltip>

            <el-popconfirm
              title="Delete this factory?"
              width="200px"
              @confirm="deleteFactory(factory.id)"
            >
              <template #reference>
                  <span class="row center tooltipHelperSpan">
                    <el-tooltip
                      effect="dark"
                      placement="top-start"
                      transition="none"
                      :hide-after="0"
                      content="Delete"
                    >
                      <el-button type="danger" :icon="Delete" :disabled="factories.length === 1" />
                    </el-tooltip>
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
  width: calc(100% - 2 * var(--padding-left-right));
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #4b4b4b;
  border-radius: 8px;
  padding: 2px var(--padding-left-right);
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