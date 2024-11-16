<script setup lang="ts">
import FactoriesList from '@/components/factories/FactoriesList.vue';
import ChangelistsList from '@/components/factories/ChangelistsList.vue';
import FactoryResources from '@/components/factories/resources/FactoryResources.vue';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import NoSaveLoaded from '@/components/common/NoSaveLoaded.vue';

const currentGameAndSaveStore = useCurrentGameAndSaveStore();

</script>

<template>
  <NoSaveLoaded v-if="!currentGameAndSaveStore.currentSaveId" />

  <div v-else class="main">
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