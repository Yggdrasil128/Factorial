<script setup lang="ts">
import FactoriesList from '@/components/factories/FactoriesList.vue';
import ChangelistsList from '@/components/factories/ChangelistsList.vue';
import FactoryResources from '@/components/factories/resources/FactoryResources.vue';
</script>

<template>
  <h1>Factories</h1>
  <div class="left">
    <factories-list />
    <changelists-list />
  </div>
  <div class="right">
    <factory-resources />
  </div>

  <router-view name="modal" />
</template>

<style scoped>
.left {
  width: 30%;
  float: left;
}

.left > * {
  min-height: 300px;
  margin-right: 20px;
}

.right {
  width: 70%;
  float: right;
}
</style>