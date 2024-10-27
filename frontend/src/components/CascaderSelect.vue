<script setup lang="ts">
import { computed, type ComputedRef, type Ref } from 'vue';
import { useVModel } from '@vueuse/core';
import IconImg from '@/components/IconImg.vue';

export interface EntityWithCategory {
  id: number;
  name: string;
  iconId?: number;
  category: string[];
}

export interface IconCascaderSelectProps {
  modelValue: number | undefined;
  options: EntityWithCategory[];
  clearable?: boolean;
}

type TreeNode = {
  id?: number;
  iconId?: number;
  label: string;
  children?: TreeNode[];
  value?: number;
}

const props: IconCascaderSelectProps = defineProps<IconCascaderSelectProps>();
const emit = defineEmits(['update:modelValue']);
const model: Ref<number | undefined> = useVModel(props, 'modelValue', emit);

const cascaderModel: Ref<any> = computed({
  get: () => model.value,
  set(value: any) {
    if (!value) {
      model.value = undefined;
    } else {
      const array = value as (number | undefined)[];
      model.value = array[array.length - 1];
    }
  }
});
const options: ComputedRef<TreeNode[]> = computed(() => convertToTreeByCategory(props.options));

function filterMethod(node: TreeNode, keyword: string) {
  return node.label.toLowerCase().indexOf(keyword.toLowerCase()) !== -1;
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

function convertToTreeByCategory<T extends EntityWithCategory>(elements: T[]): TreeNode[] {
  const tree: TreeNode[] = [];

  function insert(nodes: TreeNode[], element: T, categoryPath: string[]) {
    if (categoryPath.length > 0) {
      const category: string = categoryPath.shift() as string;
      for (const node of nodes) {
        if (node.children && node.label === category) {
          insert(node.children, element, categoryPath);
          return;
        }
      }
      const node: TreeNode = { label: category, children: [] };
      nodes.push(node);
      insert(node.children as TreeNode[], element, categoryPath);
      return;
    }
    const node: TreeNode = { id: element.id, value: element.id, iconId: element.iconId, label: element.name };
    nodes.push(node);
  }

  for (let element of elements) {
    insert(tree, element, [...element.category]);
  }

  function sort(nodes: TreeNode[]) {
    nodes.sort((a: TreeNode, b: TreeNode): number => {
      if (!a.children !== !b.children) {
        return !a.children ? 1 : -1;
      }
      return a.label.localeCompare(b.label);
    });

    for (const node of nodes) {
      if (node.children) {
        sort(node.children);
      }
    }
  }

  sort(tree);

  return tree;
}

</script>

<template>
  <div style="display: flex;">
    <IconImg :icon-id="model" :size="32" style="margin-right: 8px;" />

    <el-cascader
      v-model="cascaderModel"
      :options="options"
      popper-class="el-dark"
      :show-all-levels="false"
      :clearable="props.clearable"
      :filterable="true"
      :filter-method="filterMethod">
      <template #default="{ node, data }">
        <template v-if="node.isLeaf">
          <div style="height: 36px; display: flex;">
        <span style="margin-right: 8px;">
          <IconImg :icon-id="data.id" :size="32" />
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