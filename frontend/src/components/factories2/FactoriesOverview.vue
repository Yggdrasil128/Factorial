<script setup lang="ts">
import FactoriesList from '@/components/factories2/FactoriesList.vue';
import ChangelistsList from '@/components/factories2/ChangelistsList.vue';
</script>

<template>
  <h1>Factories</h1>
  <div class="left">
    <factories-list />
    <changelists-list />
  </div>
  <div class="right">

  </div>

  <router-view name="modal" />
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