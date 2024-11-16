<script setup lang="ts">
import FactoriesList from '@/components/factories/FactoriesList.vue';
import ChangelistsList from '@/components/factories/ChangelistsList.vue';
import FactoryResources from '@/components/factories/resources/FactoryResources.vue';
</script>

<template>
  <div class="main">
    <div class="left">
      <factories-list />
      <changelists-list />
    </div>
    <div class="right">
      <factory-resources />
    </div>
  </div>

  <router-view name="modal" />
</template>

<style scoped>
.main {
  display: flex;
  gap: 20px;
}

.left {
  flex: 0 0 calc(30% - 20px);
}

.left > * {
  min-height: 300px;
}

.right {
  flex: 0 0 70%;
}
</style>