<script setup lang="ts">
import { type Ref, ref, watch } from 'vue';
import { Check, Minus, Plus } from '@element-plus/icons-vue';
import { ElButtonGroup, ElInput } from 'element-plus';
import type { Fraction } from '@/types/model/basic';
import { useVModel } from '@vueuse/core';
import { useProductionStepApi } from '@/api/model/useProductionStepApi';
import BgcElButton from '@/components/common/input/BgcElButton.vue';
import { modifyFraction } from '@/utils/fractionUtils';

export interface MachineCountInputProps {
  modelValue: Fraction;
  productionStepId: number;
}

const props: MachineCountInputProps = defineProps<MachineCountInputProps>();
const emit = defineEmits(['update:modelValue']);
const model: Ref<Fraction> = useVModel(props, 'modelValue', emit);
const modelCache: Ref<Fraction> = ref(model.value);
watch(model, () => modelCache.value = model.value);

const productionStepApi = useProductionStepApi();

const plusButtonLoading: Ref<boolean> = ref(false);
const minusButtonLoading: Ref<boolean> = ref(false);
const checkButtonLoading: Ref<boolean> = ref(false);
const buttonsDisabled: Ref<boolean> = ref(false);

let submitTimeoutHandle: NodeJS.Timeout | null = null;
const inputSubmitDelayMs: number = 1000;

function clearSubmitTimeout(): void {
  if (submitTimeoutHandle != null) {
    clearTimeout(submitTimeoutHandle);
    submitTimeoutHandle = null;
  }
}

function setSubmitTimeout(): void {
  clearSubmitTimeout();
  submitTimeoutHandle = setTimeout(onSubmitTimeout, inputSubmitDelayMs);
}

function onSubmitTimeout(): void {
  submitTimeoutHandle = null;
  updateMachineCount();
}

function onInputUpdate(): void {
  setSubmitTimeout();
}

async function updateMachineCount(): Promise<void> {
  buttonsDisabled.value = true;
  await productionStepApi.updateMachineCount(props.productionStepId, modelCache.value)
    .finally(() => {
      buttonsDisabled.value = false;
    });
}

async function plusOne(): Promise<void> {
  modelCache.value = modifyFraction(modelCache.value, 1);
  clearSubmitTimeout();
  plusButtonLoading.value = true;
  updateMachineCount()
    .finally(() => plusButtonLoading.value = false);
}

async function minusOne(): Promise<void> {
  modelCache.value = modifyFraction(modelCache.value, -1);
  clearSubmitTimeout();
  minusButtonLoading.value = true;
  updateMachineCount()
    .finally(() => minusButtonLoading.value = false);
}

async function apply(): Promise<void> {
  checkButtonLoading.value = true;
  if (submitTimeoutHandle != null) {
    clearSubmitTimeout();
    await updateMachineCount();
  }
  buttonsDisabled.value = true;
  await productionStepApi.applyPrimaryChangelist(props.productionStepId)
    .finally(() => buttonsDisabled.value = checkButtonLoading.value = false);
}
</script>

<template>
  <el-input
    v-model="modelCache"
    style="width: 80px;"
    class="mciInput"
    @update:model-value="onInputUpdate"
  >
  </el-input>
  <el-button-group class="mciButtonGroup">
    <bgc-el-button
      :icon="Plus"
      @click="plusOne"
      :loading="plusButtonLoading"
      :disabled="buttonsDisabled"
    />
    <bgc-el-button
      :icon="Minus"
      @click="minusOne"
      :loading="minusButtonLoading"
      :disabled="buttonsDisabled || modelCache === '0'"
    />
    <bgc-el-button
      :icon="Check"
      @click="apply"
      :loading="checkButtonLoading"
      :disabled="buttonsDisabled"
    />
  </el-button-group>
</template>

<!--suppress CssUnusedSymbol -->
<style>
.mciInput .el-input__wrapper {
  border-top-right-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
  box-shadow: 0 0 0 1px #454545 inset;
  background-color: #80808080;
}

.mciInput .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #a0a0a0 inset;
}

.mciInput .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px var(--el-input-focus-border-color) inset;
}

.mciButtonGroup button:nth-child(1) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
</style>
