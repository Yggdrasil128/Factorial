<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps(['icon', 'size']);

const src = computed(() => {
  if (props.icon.url.startsWith('http')) {
    return props.icon.url;
  }
  return 'http://localhost:8080' + props.icon.url;
});
</script>

<template>
  <img
    v-if="icon"
    :src="src"
    :alt="icon.name"
    :style="{ width: size + 'px', height: size + 'px' }"
  />
</template>

<style scoped></style>
