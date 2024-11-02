<script setup lang="ts">
import { ref } from 'vue';
import { getModelSyncService, type ModelSyncService } from '@/services/model/modelSyncService';

const localTime = ref('');

function updateLocalTime() {
  const now = new Date();
  localTime.value = now.toLocaleTimeString();
}

updateLocalTime();
setInterval(updateLocalTime, 1000);

const modelSyncService: ModelSyncService = getModelSyncService();
</script>

<template>
  <div id="navbar">
    <div id="navList">
      <router-link to="/">Home</router-link>
      <router-link to="/about">About</router-link>
      <router-link :to="{ name: 'factories' }">Factories</router-link>
      <router-link :to="{ name: 'factories2' }">Factories 2</router-link>
      <router-link to="/devtools">Dev Tools</router-link>
    </div>
    <div id="navExtra">
      <div style="display: flex; gap: 10px; margin-right: 10px;">
        <div>
          <button style="margin-top: 7px;"
                  @click="modelSyncService.reload()">
            Reload model
          </button>
        </div>
        <div>
          Local time:<br /><span id="localTime">{{ localTime }}</span>
        </div>
      </div>

    </div>
  </div>
</template>

<!--suppress CssUnusedSymbol -->
<style scoped>
#navbar {
  border-top: 2px solid black;
  border-bottom: 2px solid black;
  padding: 4px 0;
  height: 40px;
  width: 100%;
  display: flex;
}

#navList {
  padding-top: 6px;
  float: left;
  flex-grow: 1;
}

#navExtra {
  float: right;
  flex-grow: 0;
  text-align: center;
  padding-top: 3px;
}

#localTime {
  font-family: monospace;
}

a {
  font-size: 25px;
  margin: 0 12px;
  text-decoration: none;
}

a.router-link-active {
  border: 1px solid white;
  cursor: default;
  color: white;
  padding: 2px 8px;
}

a:not(.router-link-active) {
  cursor: pointer;
  color: #c0d8ff;
  padding: 3px 9px;
}
</style>
