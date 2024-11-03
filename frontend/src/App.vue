<script setup lang="ts">
import AppHeader from '@/components/AppHeader.vue';
import NavBar from '@/components/NavBar.vue';
import { getModelSyncService, type ModelSyncService } from '@/services/model/modelSyncService';

const modelSyncService: ModelSyncService = getModelSyncService();
modelSyncService.setSaveIdAndReload(1);
</script>

<template>
  <AppHeader />
  <NavBar />

  <div style="margin: 10px 20px;">
    <router-view />
  </div>
</template>

<style scoped></style>

<style>
html {
  overflow-y: scroll;
  background-color: #373737;
}

body {
  width: 1600px !important;
  background-color: #414141;
  margin: 0 auto;
  color: #dddddd;
  font-family: Arial, Helvetica, sans-serif;
  font-size: 16px;
  min-height: 100vh;
  overflow: auto;
}
</style>
