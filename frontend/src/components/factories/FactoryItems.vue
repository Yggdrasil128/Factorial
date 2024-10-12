<script setup lang="js">
import { computed, inject, onMounted, onUnmounted, ref, watch } from 'vue';
import draggable from 'vuedraggable';
import FactoryItem from '@/components/factories/FactoryItem.vue';
import { useRoute, useRouter } from 'vue-router';
import { Plus } from '@element-plus/icons-vue';

const globalEventBus = inject('globalEventBus');
const axios = inject('axios');
const route = useRoute();
const router = useRouter();

const currentFactoryId = computed(() => route.params.factoryId);

const factory = ref(null);
const itemMap = ref({});
const loading = ref(true);

async function reloadFactoryData() {
  if (!currentFactoryId.value) {
    return;
  }
  loading.value = true;

  const response = await axios.get('api/factoryItemList', {
    params: { factoryId: currentFactoryId.value }
  });
  applyFactoryData(response.data);

  loading.value = false;
}

function applyFactoryData(data) {
  factory.value = data;
  const newItemMap = {};
  for (const item of factory.value.items) {
    newItemMap[String(item.id)] = item;
  }
  itemMap.value = newItemMap;
}

onMounted(() => {
  globalEventBus.on('reloadFactoryData', reloadFactoryData);
  globalEventBus.on('applyFactoryData', applyFactoryData);
});
onUnmounted(() => {
  globalEventBus.off('reloadFactoryData', reloadFactoryData);
  globalEventBus.off('applyFactoryData', applyFactoryData);
});

reloadFactoryData();
watch(currentFactoryId, reloadFactoryData);

function getRelevantProductionSteps(item) {
  // noinspection JSUnresolvedReference
  return factory.value.productionSteps.filter(
    (step) => step.output.filter((output) => output.itemId === item.id).length > 0
  );
}

function newProductionStep() {
  router.push({ name: 'newProductionStep', params: { factoryId: route.params.factoryId } });
}

function newTransportLink() {
}

function newIngress() {
}

function newEgress() {
}
</script>

<template>
  <div
    v-loading="loading"
    element-loading-background="rgba(65, 65, 65, 0.6)"
    style="min-height: 400px"
  >
    <template v-if="factory">
      <div style="overflow: auto">
        <div style="float: left">
          <h2>Factory name: {{ factory.name }}</h2>
        </div>
        <div style="float: right; margin-top: 16px">
          <el-dropdown split-button type="primary" @click="newProductionStep">
            <el-icon class="el-icon--left">
              <plus />
            </el-icon>
            New production step
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="newTransportLink">New transport link</el-dropdown-item>
                <el-dropdown-item @click="newIngress">New ingress</el-dropdown-item>
                <el-dropdown-item @click="newEgress">New egress</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <draggable :list="factory.items" item-key="id">
        <!--suppress VueUnrecognizedSlot -->
        <template #item="{ element }">
          <factory-item
            :item="element"
            :production-steps="getRelevantProductionSteps(element)"
            :item-map="itemMap"
          />
        </template>
      </draggable>
    </template>
  </div>
</template>

<style scoped></style>
