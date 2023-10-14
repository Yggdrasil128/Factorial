<script setup>
import {ref} from "vue";

const localTime = ref("");

function updateLocalTime() {
  const now = new Date();
  localTime.value = now.toLocaleTimeString();
}

updateLocalTime();
setInterval(updateLocalTime, 1000);

</script>

<template>
  <div id="navbar">
    <div id="navList">
      <router-link to="/">Home</router-link>
      <router-link to="/about">About</router-link>
      <router-link :to="{name: 'factories'}">Factories</router-link>
      <router-link to="/devtools">Dev Tools</router-link>
    </div>
    <div id="navExtra">
      Local time:<br/><span id="localTime">{{ localTime }}</span>
    </div>
  </div>
</template>

<!--suppress CssUnusedSymbol -->
<style scoped>
#navbar {
  border-top: 2px solid black;
  border-bottom: 2px solid black;
  height: 48px;
  width: 100%;
}

#navList {
  padding-top: 8px;
  float: left;
  width: calc(100% - 128px);
}

#navExtra {
  float: right;
  width: 128px;
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