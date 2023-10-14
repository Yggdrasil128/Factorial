<script setup>
import FactoryItems from "@/components/factories/FactoryItems.vue";
import Factories from "@/components/factories/Factories.vue";
import Changelists from "@/components/factories/Changelists.vue";
</script>

<template>
  <h1>Factories</h1>
  <div class="left">
    <factories/>
    <changelists/>
  </div>
  <div class="right">
    <factory-items/>
  </div>

  <router-view name="modal"/>
</template>

<style scoped>
.left {
  width: 30%;
  float: left;
}

.left > * {
  min-height: 300px;
  margin-right: 20px;
}

.right {
  width: 70%;
  float: right;
}
</style>