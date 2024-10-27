<script setup lang="ts">
import { useCurrentSaveStore } from '@/stores/currentSaveStore';
import { useChangelistStore } from '@/stores/model/changelistStore';
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useProductionStepStore } from '@/stores/model/productionStepStore';
import { useResourceStore } from '@/stores/model/resourceStore';

const currentSaveStore = useCurrentSaveStore();
const changelistStore = useChangelistStore();
const factoryStore = useFactoryStore();
const productionStepStore = useProductionStepStore();
const resourceStore = useResourceStore();
</script>

<template>
  <h2>Model Stores:</h2>

  <h3>Current save</h3>
  <span v-if="!currentSaveStore.save">(none)</span>
  <pre v-else>{{ JSON.stringify(currentSaveStore.save, null, 2) }}</pre>

  <h3>Changelists</h3>
  <pre v-for="changelist in changelistStore.map.values()" :key="changelist.id">{{
      JSON.stringify(changelist, null, 2)
    }}</pre>

  <h3>Factories</h3>
  <pre v-for="factory in factoryStore.map.values()" :key="factory.id">{{
      JSON.stringify(factory, null, 2)
    }}</pre>

  <h3>Production steps</h3>
  <pre v-for="productionStep in productionStepStore.map.values()" :key="productionStep.id">{{
      JSON.stringify(productionStep, null, 2)
    }}</pre>

  <h3>Resources</h3>
  <pre v-for="resource in resourceStore.map.values()" :key="resource.id">{{
      JSON.stringify(resource, null, 2)
    }}</pre>
</template>

<style scoped>

</style>