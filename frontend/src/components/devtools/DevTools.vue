<script setup>
import {inject} from "vue";
import * as satisfactoryUpdate7 from "./json/SatisfactoryUpdate7.json";
import * as exampleWithSteelProductionNorth from "./json/ExampleWithSteelProductionNorth.json";

const axios = inject('axios');

async function setupTestData() {
  await axios.post('api/migration/game', satisfactoryUpdate7.default);
  await axios.post('api/migration/save', exampleWithSteelProductionNorth.default);
}

</script>

<template>
  <h1>Dev Tools</h1>
  <p>Hello World</p>
  <el-button @click="setupTestData">Setup test data</el-button>
</template>

<style scoped>

</style>