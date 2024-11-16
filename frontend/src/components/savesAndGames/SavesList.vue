<script setup lang="ts">
import { useSaveStore } from '@/stores/model/saveStore';
import { Plus, Upload } from '@element-plus/icons-vue';
import { ElButton } from 'element-plus';
import { computed, type ComputedRef } from 'vue';
import type { Save } from '@/types/model/standalone';
import { ordinalComparator } from '@/utils/utils';
import { type DraggableSupport, useDraggableSupport } from '@/utils/useDraggableSupport';
import { useSaveApi } from '@/api/model/useSaveApi';
import { VueDraggableNext } from 'vue-draggable-next';
import SaveCard from '@/components/savesAndGames/SaveCard.vue';

const saveStore = useSaveStore();
const saveApi = useSaveApi();

const saves: ComputedRef<Save[]> = computed(() => saveStore.getAll().sort(ordinalComparator));

const draggableSupport: DraggableSupport = useDraggableSupport(saves, saveApi.reorder);

function newSave(): void {

}

function importSave(): void {

}

</script>

<template>
  <div style="display: flex; align-items: center;">
    <h1 style="">Saves ({{ saves.length }})</h1>
    <div style="flex-grow: 1;"></div>
    <div style="float: right;">
      <el-button type="primary" :icon="Plus" @click="newSave">New save</el-button>
      <el-button :icon="Upload" @click="importSave">Import save</el-button>
    </div>
  </div>

  <vue-draggable-next :model-value="saves" @end="draggableSupport.onDragEnd">
    <SaveCard v-for="save in saves" :key="save.id" :save="save" />
  </vue-draggable-next>
</template>

<style scoped>

</style>