<script setup>
import {computed, inject, onMounted, onUnmounted, ref, watch} from "vue";
import draggable from 'vuedraggable';
import Item from "@/components/factories/Item.vue";
import {useRoute} from "vue-router";

const globalEventBus = inject("globalEventBus");
const axios = inject('axios');
const route = useRoute();

const currentFactoryId = computed(() => route.params.factoryId);

const factory = ref(null);
const itemMap = ref({});
const loading = ref(true);

const dummy = computed(() => {
  console.log(factory.value.items);
  return factory.value.items;
});

async function reloadFactoryData() {
  if (!currentFactoryId.value) {
    return;
  }
  loading.value = true;

  let response = await axios.get('api/factoryItemList?factoryId=' + currentFactoryId.value);
  applyFactoryData(response.data);

  loading.value = false;
}

function applyFactoryData(data) {
  factory.value = data;
  let newItemMap = {};
  for (let item of factory.value.items) {
    newItemMap[String(item.id)] = item;
  }
  itemMap.value = newItemMap;
}

onMounted(() => {
  globalEventBus.on("reloadFactoryData", reloadFactoryData);
  globalEventBus.on("applyFactoryData", applyFactoryData);
});
onUnmounted(() => {
  globalEventBus.off("reloadFactoryData", reloadFactoryData);
  globalEventBus.off("applyFactoryData", applyFactoryData);
});

reloadFactoryData();
watch(currentFactoryId, reloadFactoryData);

function getRelevantProductionSteps(item) {
  return factory.value.productionSteps.filter(e => e.output.filter(ee => ee.itemId === item.id).length > 0);
}

</script>

<template>
  <div v-loading="loading" element-loading-background="rgba(65, 65, 65, 0.6)" style="min-height: 400px;">
    <template v-if="factory">
      <h2>{{ factory.name }}</h2>
      <draggable :list="factory.items" item-key="id">
        <!--suppress VueUnrecognizedSlot -->
        <template #item="{ element }">
          <Item :item="element" :production-steps="getRelevantProductionSteps(element)" :item-map="itemMap"/>
        </template>
      </draggable>
    </template>
  </div>
</template>

<style scoped>

</style>