<script setup>
import {computed, inject, onMounted, onUnmounted, ref, watch} from "vue";
import Item from "@/components/factories/Item.vue";
import {exampleFactoryData} from "./exampleFactoryData";
import {useRoute} from "vue-router";

const globalEventBus = inject("globalEventBus");
const route = useRoute();

const currentFactoryId = computed(() => route.params.factoryId);

const factory = ref(null);
const loading = ref(true);

async function reloadFactoryData() {
  if (currentFactoryId.value === null) {
    return;
  }
  loading.value = true;

  // simulate a delay
  await new Promise(r => setTimeout(r, 500));

  let data;
  if (currentFactoryId.value === '1') {
    data = exampleFactoryData;
  } else {
    data = {
      name: "Steel production north",
      icon: null,
      items: [],
      productionSteps: []
    }
  }
  applyFactoryData(data);
  loading.value = false;
}

function applyFactoryData(data) {
  factory.value = data;
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
  return factory.value.productionSteps.filter(e => e.throughput.output.filter(ee => ee.item.id === item.id).length > 0);
}

</script>

<template>
  <div v-loading="loading" element-loading-background="rgba(65, 65, 65, 0.6)" style="min-height: 400px;">
    <template v-if="factory">
      <h2>{{ factory.name }}</h2>
      <Item v-for="item in factory.items" :item="item" :production-steps="getRelevantProductionSteps(item)"/>
    </template>
  </div>
</template>

<style scoped>

</style>