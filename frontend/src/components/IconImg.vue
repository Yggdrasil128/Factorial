<script setup lang="ts">
import { computed } from 'vue';
import type { Icon } from '@/types/model/standalone';
import { useIconStore } from '@/stores/model/iconStore';

export interface IconImgProps {
  icon?: Icon | number;
  size: number;
}

const props: IconImgProps = defineProps<IconImgProps>();

const iconStore = useIconStore();

const src = computed(() => {
  if (!props.icon) {
    return '';
  }
  if (typeof props.icon === 'number') {
    return 'http://localhost:8080/api/icon/raw?id=' + props.icon;
  }
  return 'http://localhost:8080/api/icon/raw?id=' + props.icon.id;
});

const alt = computed(() => {
  if (!props.icon) {
    return '';
  }
  if (typeof props.icon === 'number') {
    const icon = iconStore.map.get(props.icon);
    return icon ? icon.name : '';
  }
  return props.icon.name;
});
</script>

<template>
  <img
    v-if="src"
    :src="src"
    :alt="alt"
    :style="{ width: size + 'px', height: size + 'px' }"
  />
</template>

<style scoped></style>
