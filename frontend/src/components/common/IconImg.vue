<script setup lang="ts">
import { computed, type ComputedRef } from 'vue';
import type { Icon } from '@/types/model/standalone';
import { useIconStore } from '@/stores/model/iconStore';
import { ElTooltip } from 'element-plus';

export interface IconImgProps {
  icon: Icon | number | undefined;
  size: number;
}

const props: IconImgProps = defineProps<IconImgProps>();

const iconStore = useIconStore();

const icon: ComputedRef<Icon | undefined> = computed(() => {
  if (!props.icon) return undefined;
  if (typeof props.icon === 'number') {
    return iconStore.getById(props.icon);
  } else {
    return props.icon;
  }
});

const src: ComputedRef<string> = computed(() => {
  if (!icon.value) {
    return '';
  }
  return 'http://localhost:8080/api/icon/raw' +
    '?id=' + icon.value.id +
    '&lastUpdated=' + Math.round(icon.value.lastUpdated);
});

</script>

<template>
  <el-tooltip
    v-if="icon"
    effect="dark"
    placement="top-start"
    transition="none"
    :hide-after="0"
    :content="icon.name"
  >
    <img
      :src="src"
      :alt="icon.name"
      :style="{ width: size + 'px', height: size + 'px' }"
      v-bind="$attrs"
    />
  </el-tooltip>
</template>

<style scoped></style>
