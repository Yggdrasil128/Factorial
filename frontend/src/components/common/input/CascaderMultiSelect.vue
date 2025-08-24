<script setup lang="ts">
import {computed, type ComputedRef, type Ref} from 'vue';
import {useVModel} from '@vueuse/core';
import IconImg from '@/components/common/IconImg.vue';
import {convertToTreeByCategory, type EntityWithCategory, type TreeNode} from '@/utils/treeUtils';
import type {CascaderProps} from 'element-plus';
import type Node from 'element-plus/es/components/tree/src/model/node';

export interface CascaderSelectProps {
  modelValue: number[];
  options: EntityWithCategory[];
  clearable?: boolean;
  isIconEntity?: boolean;
  disabled?: boolean;
  placeholder?: string;
}

const props: CascaderSelectProps = defineProps<CascaderSelectProps>();
const emit: (event: string, ...args: any[]) => void = defineEmits(['update:modelValue']);
const model: Ref<number[]> = useVModel(props, 'modelValue', emit);

const cascaderModel: Ref = computed({
  get: () => model.value,
  set(value: any) {
    if (!value) {
      model.value = [];
    } else {
      const array: (number | undefined)[][] = value as (number | undefined)[][];
      model.value = array.map((element: (number | undefined)[]) => element[element.length - 1] as number);
    }
  },
});
const options: ComputedRef<TreeNode[]> = computed(() =>
  convertToTreeByCategory(props.options, props.isIconEntity),
);

function filterMethod(node: TreeNode, keyword: string): boolean {
  return node.label.toLowerCase().indexOf(keyword.toLowerCase()) !== -1;
}

function getLeafCount(node: any): number {
  if (node.isLeaf) {
    return 1;
  }
  let c: number = 0;
  for (const child of node.children) {
    c += getLeafCount(child);
  }
  return c;
}

const cascaderProps: CascaderProps = { multiple: true };

function onLeafNodeClick(node: Node): void {
  const id: number = node.data.id;
  const index: number = model.value.indexOf(id);
  if (index >= 0) {
    model.value.splice(index, 1);
  } else {
    model.value.push(id);
  }
}

</script>

<template>
  <el-cascader
    class="cascaderMultiSelect"
    v-model="cascaderModel"
    :options="options"
    :show-all-levels="false"
    :clearable="props.clearable"
    :filterable="true"
    :filter-method="filterMethod"
    :disabled="disabled"
    :placeholder="placeholder"
    :props="cascaderProps">
    <template #default="{ node, data }">
      <template v-if="node.isLeaf">
        <div style="height: 36px; display: flex;" @click="onLeafNodeClick(node)">
          <span style="margin-right: 8px;">
            <IconImg :icon="data.iconId" :size="32" />
          </span>
          <span>{{ data.label }}</span>
        </div>
      </template>
      <template v-else>
        <span>{{ data.label }} ({{ getLeafCount(node) }})</span>
      </template>
    </template>
  </el-cascader>
</template>

<style>
.cascaderMultiSelect {
  --el-cascader-tag-background: var(--el-color-info-light-9);
}
</style>