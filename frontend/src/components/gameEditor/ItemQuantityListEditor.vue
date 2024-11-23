<script setup lang="ts">
import type { ItemQuantity } from '@/types/model/basic';
import { computed, type ComputedRef, nextTick, ref, type Ref } from 'vue';
import { useVModel } from '@vueuse/core';
import CascaderSelect from '@/components/common/input/CascaderSelect.vue';
import { useItemStore } from '@/stores/model/itemStore';
import type { Item } from '@/types/model/standalone';
import { Delete, Plus } from '@element-plus/icons-vue';
import type { RuleItem } from 'async-validator/dist-types/interface';
import { elFormFractionValidator } from '@/utils/fractionUtils';
import type { FormItemInstance } from 'element-plus';
import FractionInput from '@/components/common/input/FractionInput.vue';

export interface ItemQuantityListEditorProps {
  modelValue: ItemQuantity[];
  type: 'ingredients' | 'products';
  gameId: number;
}

const props: ItemQuantityListEditorProps = defineProps<ItemQuantityListEditorProps>();
const emit = defineEmits(['update:modelValue']);
const model: Ref<ItemQuantity[]> = useVModel(props, 'modelValue', emit);

const itemStore = useItemStore();

const formItemsForItemId: Ref<FormItemInstance[]> = ref([]);

const items: ComputedRef<Item[]> = computed(() => itemStore.getByGameId(props.gameId));

const duplicateItemIds: ComputedRef<number[]> = computed(() => {
  const itemIds: number[] = model.value
    .map(itemQuantity => itemQuantity.itemId)
    .filter(itemId => !!itemId);
  nextTick(() => {
    formItemsForItemId.value.forEach(formItem => formItem.validate('change'));
  });
  return itemIds.filter((itemId, index) => itemIds.includes(itemId, index + 1));
});

function add(): void {
  model.value.push({ itemId: 0, quantity: '' });
}

function remove(index: number): void {
  model.value.splice(index, 1);
}

const quantityRules: RuleItem[] = [
  {
    required: true, message: 'Invalid quantity.', trigger: 'blur',
    validator: elFormFractionValidator, allowZero: false, allowNegative: false,
  } as RuleItem,
];
const itemRules: RuleItem[] = [
  {
    required: true, message: 'Please select an item.', trigger: 'change',
    validator: (_: any, value: any, callback: any) => {
      if (!value) {
        callback(new Error());
      }
      callback();
    },
  } as RuleItem,
  {
    message: 'Duplicate item selected.', trigger: 'change',
    validator: (_: any, value: any, callback: any) => {
      if (duplicateItemIds.value.includes(value)) {
        callback(new Error());
      }
      callback();
    },
  } as RuleItem,
];

</script>

<template>
  <div class="main">
    <div v-for="(itemQuantity, index) in model" :key="index" class="row">
      <el-form-item class="quantity" :prop="type + '.' + index + '.quantity'" :rules="quantityRules">
        <fraction-input v-model:fraction="itemQuantity.quantity" placeholder="Quantity" />
      </el-form-item>
      <el-form-item class="item" :prop="type + '.' + index + '.itemId'" :rules="itemRules" ref="formItemsForItemId">
        <CascaderSelect style="width: 100%;" v-model="itemQuantity.itemId" :options="items" placeholder="Item" />
      </el-form-item>
      <el-button class="remove" type="danger" :icon="Delete" @click="remove(index)" />
    </div>

    <div class="add">
      <el-button type="primary" :icon="Plus" @click="add()">
        Add {{ type === 'ingredients' ? 'ingredient' : 'product' }}
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.main {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.row {
  width: 100%;
  display: flex;
  gap: 8px;
}

.quantity {
  flex: 0 0 100px;
  width: auto;
}

.item {
  flex: 1 1 auto;
}

.remove {
  flex: 0 0 auto;
}
</style>