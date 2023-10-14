<script setup>
import {ref} from "vue";

const versionInfo = ref({version: "1.0-SNAPSHOT"});
</script>

<template>
  <div id="header">
    <div id="headerLeft">
      <img alt="Banner" src="img/banner.png"/>
    </div>

    <div id="headerRight">
      <div id="appTitle">
        Factorial!
      </div>
      <div id="madeBy">
        Made by Izruo and Yggdrasil128
      </div>
      <div id="versionInfo">
        Version: {{ versionInfo.version }}
      </div>
    </div>
  </div>
</template>

<style scoped>
#header {
  height: 128px;
  width: 100%;
}

#header #headerLeft {
  float: left;
  width: 578px;
}

#header #headerLeft img {
  padding: 5px;
  height: 118px;
}

#header #headerRight {
  float: right;
  width: calc(100% - 1150px);
  text-align: center;
  padding-top: 16px;
}

#appTitle {
  font-size: 30px;
}

#madeBy {
  font-size: 20px
}

#versionInfo {
  font-size: 16px;
  margin-top: 10px;
}
</style>