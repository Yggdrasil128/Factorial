<script setup lang="js">
import { computed, inject, onMounted, onUnmounted, ref } from 'vue';
import { VueDraggableNext } from 'vue-draggable-next';
import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import IconImg from '@/components/IconImg.vue';
import { ElButton, ElButtonGroup, ElPopconfirm, ElTooltip } from 'element-plus';

const router = useRouter();
const route = useRoute();
const axios = inject('axios');
const globalEventBus = inject('globalEventBus');

const factories = ref([]);
const currentFactoryId = computed(() => route.params.factoryId);

function newFactory() {
  router.push({ name: 'newFactory', params: { factoryId: route.params.factoryId } });
}

function editFactory(editFactoryId) {
  router.push({
    name: 'editFactory',
    params: { factoryId: route.params.factoryId, editFactoryId: editFactoryId }
  });
}

async function loadFactories() {
  const response = await axios.get('api/save/factories', { params: { saveId: 1 } });
  factories.value = response.data;
  if (factories.value.length > 0 && !currentFactoryId.value) {
    await router.replace({ name: 'factories', params: { factoryId: factories.value[0].id } });
  }
}

async function deleteFactory(factoryId) {
  await axios.delete('api/factory', { params: { factoryId: factoryId } });

  await loadFactories();
}

function viewFactory(id) {
  if (currentFactoryId.value === id) {
    return;
  }
  router.push({ name: 'factories', params: { factoryId: id } });
}

loadFactories();

function onUpdateFactories() {
  loadFactories();
}

onMounted(() => {
  globalEventBus.on('updateFactories', onUpdateFactories);
});
onUnmounted(() => {
  globalEventBus.off('updateFactories', onUpdateFactories);
});
</script>

<template>
  <div class="factoryList">
    <h2>Factory list</h2>
    <vue-draggable-next :list="factories">
      <div
        v-for="factory in factories"
        :key="factory.id"
        class="list-group-item"
        :class="{ active: String(factory.id) === currentFactoryId, hasIcon: !!factory.icon }"
      >
        <div class="icon" @click="viewFactory(factory.id)" v-if="factory.icon">
          <icon-img :icon="factory.icon" :size="40" />
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
              <el-button :icon="Edit" @click="editFactory(factory.id)" />
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
      <el-button type="primary" :icon="Plus" @click="newFactory()">Create factory</el-button>
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
