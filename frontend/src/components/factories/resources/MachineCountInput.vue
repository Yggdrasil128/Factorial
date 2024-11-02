<script setup lang="ts">
import { type Ref, ref, watch } from 'vue';
import { Check, Minus, Plus } from '@element-plus/icons-vue';
import { ElButton, ElButtonGroup, ElInput } from 'element-plus';
import type { Fraction } from '@/types/model/basic';
import { useVModel } from '@vueuse/core';
import { useProductionStepApi } from '@/api/useProductionStepApi';

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
  modelCache.value = editFraction(modelCache.value, 1);
  clearSubmitTimeout();
  plusButtonLoading.value = true;
  updateMachineCount()
    .finally(() => plusButtonLoading.value = false);
}

async function minusOne(): Promise<void> {
  modelCache.value = editFraction(modelCache.value, -1);
  clearSubmitTimeout();
  minusButtonLoading.value = true;
  updateMachineCount()
    .finally(() => minusButtonLoading.value = false);
}

function editFraction(fraction: Fraction, delta: number): Fraction {
  const i = fraction.indexOf('/');
  if (i < 0) {
    return String(Math.max(Number(fraction) + delta, 0));
  } else {
    let n = Number(fraction.substring(0, i));
    const d = Number(fraction.substring(i + 1));
    n += d * delta;
    if (n <= 0) {
      return '0';
    }
    return String(n) + '/' + String(d);
  }
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
    <el-button
      :icon="Plus"
      @click="plusOne"
      :loading="plusButtonLoading"
      :disabled="buttonsDisabled"
    />
    <el-button
      :icon="Minus"
      @click="minusOne"
      :loading="minusButtonLoading"
      :disabled="buttonsDisabled || modelCache === '0'"
    />
    <el-button
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
}

.mciButtonGroup button:nth-child(1) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-left: none;
}
</style>
