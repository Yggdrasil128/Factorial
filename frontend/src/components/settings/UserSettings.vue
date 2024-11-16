<script setup lang="ts">
import { useUserSettingsStore } from '@/stores/userSettingsStore';
import { ThroughputUnit } from '@/types/userSettings';

const userSettingsStore = useUserSettingsStore();
</script>

<template>
  <h1>User settings</h1>

  <el-form class="userSettingsForm" @submit.prevent>
    <el-form-item label="Throughput unit">
      <el-select v-model="userSettingsStore.throughputUnit">
        <el-option :value="ThroughputUnit.ItemsPerSecond" label="Items per second" />
        <el-option :value="ThroughputUnit.ItemsPerMinute" label="Items per minute" />
      </el-select>
    </el-form-item>
  </el-form>
</template>

<style scoped>
.userSettingsForm {
  width: 350px;
}
</style>