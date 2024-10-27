<script setup lang="js">
import { inject, ref } from 'vue';
import { Check, WarnTriangleFilled } from '@element-plus/icons-vue';
import { ElButton, ElOption, ElSelect } from 'element-plus';
import ModelStoresDisplay from '@/components/devtools/ModelStoresDisplay.vue';
import TestComponent from '@/components/devtools/TestComponent.vue';

const axios = inject('axios');

const wipeDatabaseButtonState = ref(0);
const setupTestDataButtonState = ref(0);
const selectedTestDataSetup = ref('');

const availableTestDataSetups = ref({
  'SatisfactoryUpdate7 + ExampleWithSteelProductionNorth': {
    gameVersionFile: 'SatisfactoryUpdate7',
    saveFile: 'ExampleWithSteelProductionNorth'
  },
  'SatisfactoryUpdate8_withIcons + ExampleWithSteelProductionNorth': {
    gameVersionFile: 'SatisfactoryUpdate8_withIcons',
    saveFile: 'ExampleWithSteelProductionNorthV8'
  }
});

async function wipeDatabaseAndRestart() {
  wipeDatabaseButtonState.value = 1;

  await axios.get('api/devtools/wipeDatabaseAndRestart');

  wipeDatabaseButtonState.value = 2;

  setTimeout(() => {
    wipeDatabaseButtonState.value = 0;
  }, 2000);
}

async function setupTestData() {
  if (!selectedTestDataSetup.value) {
    return;
  }

  setupTestDataButtonState.value = 1;

  const setup = availableTestDataSetups.value[selectedTestDataSetup.value];

  await importJsonFile('gameVersion', setup.gameVersionFile);
  await importJsonFile('save', setup.saveFile);

  setupTestDataButtonState.value = 2;

  setTimeout(() => {
    setupTestDataButtonState.value = 0;
  }, 2000);
}

async function importJsonFile(kind, filename) {
  const data = await import('./json/' + filename + '.json');
  await axios.post('api/migration/' + kind, data.default);
}
</script>

<template>
  <h1>Dev Tools</h1>
  <p>
    <el-button
      :loading="wipeDatabaseButtonState === 1"
      :disabled="wipeDatabaseButtonState > 0"
      type="danger"
      @click="wipeDatabaseAndRestart"
      :icon="wipeDatabaseButtonState === 2 ? Check : WarnTriangleFilled"
    >
      Wipe database and restart Factorial
    </el-button>
  </p>
  <p>
    <el-select
      v-model="selectedTestDataSetup"
      placeholder="Select test data file"
      style="width: 400px"
    >
      <el-option
        v-for="option in Object.keys(availableTestDataSetups)"
        :key="option"
        :value="option"
        :label="option"
      ></el-option>
    </el-select>
    &ensp;
    <el-button
      :loading="setupTestDataButtonState === 1"
      :disabled="setupTestDataButtonState > 0 || !selectedTestDataSetup"
      @click="setupTestData"
      :icon="setupTestDataButtonState === 2 ? Check : null"
    >
      Setup test data
    </el-button>
  </p>
  <p>
    <TestComponent />
  </p>
  <p>
    <ModelStoresDisplay />
  </p>
</template>

<style scoped></style>
