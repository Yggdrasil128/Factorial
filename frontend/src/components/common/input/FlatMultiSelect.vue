<script setup lang="ts">
import { type Ref } from 'vue';
import { useVModel } from '@vueuse/core';
import { useRecipeIconService } from '@/services/useRecipeIconService';
import IconImg from '@/components/common/IconImg.vue';

export interface FlatMultiSelectProps {
  modelValue: number[];
  options: Entity[];
  isIconEntity?: boolean;
  isRecipeEntity?: boolean;
}

export interface Entity {
  id: number;
  name: string;
  iconId?: number;
}

const props: FlatMultiSelectProps = defineProps<FlatMultiSelectProps>();
const emit = defineEmits(['update:modelValue']);
const model: Ref<number[]> = useVModel(props, 'modelValue', emit);

const recipeIconService = useRecipeIconService();
</script>

<template>
  <el-select v-model="model" multiple v-bind="$attrs" filterable>
    <el-option v-for="option in options"
               :key="option.id"
               :value="option.id"
               :label="option.name">
      <div class="option">
        <icon-img v-if="isIconEntity" :icon="option.id" :size="24" />
        <icon-img v-else-if="isRecipeEntity" :icon="recipeIconService.getRecipeIconId(option.id)" :size="24" />
        <icon-img v-else :icon="option.iconId" :size="24" />

        {{ option.name }}
      </div>
    </el-option>
  </el-select>
</template>

<style scoped>
.option {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>