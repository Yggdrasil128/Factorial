<script setup lang="ts">
import { WarningFilled } from '@element-plus/icons-vue';
import { getModelSyncService } from '@/services/useModelSyncService';

export interface PlaceholderHelpBoxProps {
  title?: string;
  width?: number;
  showOnlyWhenReady?: boolean;
  backgroundColor?: string;
}

defineProps<PlaceholderHelpBoxProps>();

const modelSyncService = getModelSyncService();

</script>

<template>
  <div class="main" v-if="!showOnlyWhenReady || !modelSyncService.state.loading.value">
    <div class="box" :style="{width: (width ?? 500) + 'px', backgroundColor}">
      <div class="headline">
        <slot name="title">
          <el-icon :size="32">
            <WarningFilled />
          </el-icon>
          <h3>{{ title }}</h3>
        </slot>
      </div>

      <slot />
    </div>
  </div>
</template>

<style scoped>
.main {
  width: 100%;
  display: flex;
  justify-content: center;
}

.box {
  width: 500px;
  background-color: #4b4b4b;
  border-radius: 8px;
  padding: 0 16px;
}

.headline {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: -10px;
}
</style>