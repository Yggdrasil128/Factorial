<script setup lang="ts">
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { computed, type ComputedRef, watch } from 'vue';
import { type RouteLocationAsRelativeGeneric, useRoute, useRouter } from 'vue-router';
import type { Factory } from '@/types/model/standalone';
import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import { ElButton, ElButtonGroup, ElPopconfirm, ElTooltip } from 'element-plus';
import { VueDraggableNext } from 'vue-draggable-next';
import IconImg from '@/components/common/IconImg.vue';
import { type FactoryApi, useFactoryApi } from '@/api/useFactoryApi';
import { type DraggableSupport, useDraggableSupport } from '@/utils/useDraggableSupport';
import type { EntityWithOrdinal } from '@/types/model/basic';
import BgcElButton from '@/components/common/input/BgcElButton.vue';

const currentGameAndSaveStore = useCurrentGameAndSaveStore();
const factoryStore = useFactoryStore();
const factoryApi: FactoryApi = useFactoryApi();

const router = useRouter();
const route = useRoute();

const currentFactoryId: ComputedRef<number | undefined> = computed(() =>
  route.params.factoryId ? Number(route.params.factoryId) : undefined
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
    params: { factoryId: route.params.factoryId, editFactoryId: editFactoryId }
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
</script>

<template>
  <div class="factoryList">
    <h2>Factories</h2>
    <vue-draggable-next :model-value="factories" @end="draggableSupport.onDragEnd">
      <div
        v-for="factory in factories"
        :key="factory.id"
        class="list-group-item"
        :class="{ active: factory.id === currentFactoryId, hasIcon: !!factory.iconId }"
      >
        <div class="icon" @click="viewFactory(factory.id)" v-if="factory.iconId">
          <icon-img :icon="factory.iconId" :size="40" />
        </div>
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

    <div class="createFactory">
      <el-button type="primary" :icon="Plus" @click="newFactory()">New factory</el-button>
    </div>
  </div>
</template>

<!--suppress CssUnusedSymbol -->
<style scoped>
.list-group-item {
  width: 100%;
  overflow: hidden;
  font-size: 20px;
  background-color: #4b4b4b;
  border-radius: 8px;
}

.list-group-item.active {
  background-color: #556e55;
}

.list-group-item + .list-group-item {
  margin-top: 10px;
}

.icon,
.name {
  float: left;
  margin-left: 8px;
}

.icon,
.icon img {
  width: 40px;
  height: 40px;
  cursor: grab;
}

.name {
  padding-top: 8px;
  padding-bottom: 8px;
  cursor: pointer;
  width: 280px;
}

.list-group-item.active .name {
  cursor: default;
}

.list-group-item:not(.hasIcon) .name {
  margin-left: 16px;
}

.active .name {
  font-weight: bold;
}

.buttons {
  float: right;
  margin-top: 4px;
  margin-right: 8px;
}

.createFactory {
  width: 100%;
  margin-top: 8px;
  display: flex;
  justify-content: center;
}
</style>