<script setup lang="ts">
import { computed, type ComputedRef, type Ref } from 'vue';
import { useVModel } from '@vueuse/core';
import IconImg from '@/components/common/IconImg.vue';
import { convertToTreeByCategory, type EntityWithCategory, type TreeNode } from '@/utils/treeUtils';

export interface CascaderSelectProps {
  modelValue: number;
  options: EntityWithCategory[];
  clearable?: boolean;
  isIconEntity?: boolean;
  disabled?: boolean;
  placeholder?: string;
}

const props: CascaderSelectProps = defineProps<CascaderSelectProps>();
const emit = defineEmits(['update:modelValue']);
const model: Ref<number> = useVModel(props, 'modelValue', emit);
const modelEntity: Ref<EntityWithCategory | undefined> = computed({
  get: () => {
    if (!model.value) {
      return undefined;
    }
    return props.options.filter(element => element.id === model.value)[0];
  },
  set(value: EntityWithCategory | undefined) {
    model.value = value ? value.id : 0;
  }
});

const cascaderModel: Ref<any> = computed({
  get: () => model.value,
  set(value: any) {
    if (!value) {
      model.value = 0;
    } else {
      const array = value as (number | undefined)[];
      model.value = array[array.length - 1] ?? 0;
    }
  }
});
const options: ComputedRef<TreeNode[]> = computed(() => convertToTreeByCategory(props.options));

function filterMethod(node: TreeNode, keyword: string) {
  return node.label.toLowerCase().includes(keyword.toLowerCase());
}

function getLeafCount(node: any) {
  if (node.isLeaf) {
    return 1;
  }
  let c = 0;
  for (const child of node.children) {
    c += getLeafCount(child);
  }
  return c;
}

</script>

<template>
  <div style="display: flex; width: 214px;">
    <IconImg :icon="isIconEntity ? model : modelEntity?.iconId" :size="32" style="margin-right: 8px;" />

    <el-cascader
      v-model="cascaderModel"
      :options="options"
      style="width: 100%;"
      :show-all-levels="false"
      :clearable="props.clearable"
      :filterable="true"
      :filter-method="filterMethod"
      :disabled="disabled"
      :placeholder="placeholder">
      <template #default="{ node, data }">
        <template v-if="node.isLeaf">
          <div style="height: 36px; display: flex;">
        <span style="margin-right: 8px;">
          <IconImg :icon="isIconEntity ? data.id : data.iconId" :size="32" />
        </span>
            <span>{{ data.label }}</span>
          </div>
        </template>
        <template v-else>
          <span>{{ data.label }} ({{ getLeafCount(node) }})</span>
        </template>
      </template>
    </el-cascader>
  </div>
</template>

<style scoped>

</style>