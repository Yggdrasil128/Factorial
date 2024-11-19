<script setup lang="ts">

</script>

<template>
  <el-popover :width="500" :show-after="200">
    <slot />
    <template #reference>
      <img class="helpIcon" src="/img/question.png" alt="Help" />
    </template>
  </el-popover>
</template>

<style scoped>
.helpIcon {
  width: 20px;
  height: 20px;
  filter: invert(0.8);
  cursor: help;
}
</style>