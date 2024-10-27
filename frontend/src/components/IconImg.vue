<script setup lang="ts">
import { computed } from 'vue';
import type { Icon } from '@/types/model/standalone';
import { useIconStore } from '@/stores/model/iconStore';

export interface IconImgProps {
  icon?: Icon;
  iconId?: number;
  size: number;
}

const props: IconImgProps = defineProps<IconImgProps>();

const iconStore = useIconStore();

const src = computed(() => {
  if (props.icon) {
    return 'http://localhost:8080/api/icons?id=' + props.icon.id;
  }
  if (props.iconId) {
    return 'http://localhost:8080/api/icons?id=' + props.iconId;
  }
  return '';
});

const alt = computed(() => {
  if (props.icon) {
    return props.icon.name;
  }
  if (props.iconId) {
    const icon = iconStore.map.get(props.iconId);
    if (icon) {
      return icon.name;
    }
  }
  return '';
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
