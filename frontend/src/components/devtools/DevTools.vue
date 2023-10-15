<script setup>
import {inject, ref} from "vue";
import * as satisfactoryUpdate7 from "./json/SatisfactoryUpdate7.json";
import * as exampleWithSteelProductionNorth from "./json/ExampleWithSteelProductionNorth.json";
import IconSelect from "@/components/iconselect/IconSelect.vue";
import {Check} from "@element-plus/icons-vue";

const axios = inject('axios');

const setupTestDataButtonState = ref(0);

async function setupTestData() {
  setupTestDataButtonState.value = 1;

  await axios.post('api/migration/game', satisfactoryUpdate7.default);
  await axios.post('api/migration/save', exampleWithSteelProductionNorth.default);

  setupTestDataButtonState.value = 2;
}

</script>

<template>
  <h1>Dev Tools</h1>
  <p>
    <el-button :loading="setupTestDataButtonState === 1" :disabled="setupTestDataButtonState > 0"
               @click="setupTestData" :icon="setupTestDataButtonState === 2 ? Check : null">
      Setup test data
    </el-button>
  </p>
  <p>
    <icon-select :model-value="null" @update:model-value="console.log"/>
  </p>
</template>

<style scoped>

</style>