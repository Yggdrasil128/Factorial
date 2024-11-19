<script setup lang="ts">
import { ElTooltip, type ElTooltipProps } from 'element-plus';

export interface CustomElTooltipProps extends Partial<ElTooltipProps> {
  content?: string;
}

defineProps<CustomElTooltipProps>();

</script>

<template>
  <el-tooltip :content="content" effect="dark" placement="top" transition="none" :hide-after="0" v-bind="$attrs">
    <template #content>
      <slot name="content"></slot>
    </template>
    <slot />
  </el-tooltip>
</template>

<style scoped>

</style>