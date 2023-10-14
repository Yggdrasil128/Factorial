<script setup>
import {computed} from "vue";

const props = defineProps(['icon', 'size']);

const src = computed(() => {
  return "http://25.53.80.171:8080" + props.icon.url;
});
</script>

<template>
  <img :src="src" :alt="icon.name" :style="{width: size + 'px', height: size + 'px'}"/>
</template>

<style scoped>

</style>