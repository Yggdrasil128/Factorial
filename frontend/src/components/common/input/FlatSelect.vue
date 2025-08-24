<script setup lang="ts">
import {computed, type ComputedRef, type Ref} from 'vue';
import {useVModel} from '@vueuse/core';
import IconImg from '@/components/common/IconImg.vue';
import {type RecipeIconService, useRecipeIconService} from '@/services/useRecipeIconService';

export interface FlatSelectProps {
  modelValue: number;
  options: Entity[];
  isIconEntity?: boolean;
  isRecipeEntity?: boolean;
}

export interface Entity {
  id: number;
  name: string;
  iconId?: number;
}

const props: FlatSelectProps = defineProps<FlatSelectProps>();
const emit: (event: string, ...args: any[]) => void = defineEmits(['update:modelValue']);
const model: Ref<number> = useVModel(props, 'modelValue', emit);

const recipeIconService: RecipeIconService = useRecipeIconService();

const selectModel: ComputedRef<number | undefined> = computed({
  get() {
    return model.value || undefined;
  },
  set(value: number | undefined) {
    model.value = value ?? 0;
  },
});

const modelIconId: ComputedRef<number> = computed(() => {
  if (props.isIconEntity) return model.value;
  if (props.isRecipeEntity) return recipeIconService.getRecipeIconId(model.value);
  return props.options.filter((element: Entity) => element.id === model.value)[0]?.iconId ?? 0;
});
</script>

<template>
  <div class="row items-center">
    <icon-img :icon="modelIconId" :size="32" />

    <el-select v-model="selectModel" v-bind="$attrs" filterable>
      <el-option v-for="option in options"
                 :key="option.id"
                 :value="option.id"
                 :label="option.name">
        <div class="row items-center">
          <icon-img v-if="isIconEntity" :icon="option.id" :size="24" />
          <icon-img v-else-if="isRecipeEntity" :icon="recipeIconService.getRecipeIconId(option.id)" :size="24" />
          <icon-img v-else :icon="option.iconId" :size="24" />

          {{ option.name }}
        </div>
      </el-option>
    </el-select>
  </div>
</template>

<style scoped>

</style>