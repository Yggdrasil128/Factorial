<script setup lang="ts">
import {computed, type Ref, ref} from 'vue';
import {Check, Minus, Plus} from '@element-plus/icons-vue';
import {ElButtonGroup} from 'element-plus';
import {until} from '@vueuse/core';
import {useProductionStepApi} from '@/api/model/useProductionStepApi';
import {ParsedFraction} from '@/utils/fractionUtils';
import CustomElTooltip from '@/components/common/CustomElTooltip.vue';
import type {ProductionStep} from '@/types/model/standalone';
import FractionInput from '@/components/common/input/FractionInput.vue';

export interface MachineCountInputProps {
  productionStep: ProductionStep;
}

const props: MachineCountInputProps = defineProps<MachineCountInputProps>();
const parsedFraction: Ref<ParsedFraction> = computed({
  get() {
    return ParsedFraction.of(props.productionStep.machineCounts.withPrimaryChangelist);
  },
  set(value: ParsedFraction) {
    if (!value) {
      return;
    }
    buttonsDisabled.value = true;
    productionStepApi.updateMachineCount(props.productionStep.id, value.toFraction())
        .finally(() => {
          buttonsDisabled.value = false;
        });
  },
});

const productionStepApi = useProductionStepApi();

const plusButtonLoading: Ref<boolean> = ref(false);
const minusButtonLoading: Ref<boolean> = ref(false);
const checkButtonLoading: Ref<boolean> = ref(false);
const buttonsDisabled: Ref<boolean> = ref(false);

async function plusOne(): Promise<void> {
  parsedFraction.value = parsedFraction.value.add(ParsedFraction.ONE);
  plusButtonLoading.value = true;
  until(buttonsDisabled).toBe(false, {timeout: 5000})
      .finally(() => plusButtonLoading.value = false);
}

async function minusOne(): Promise<void> {
  let newValue = parsedFraction.value.subtract(ParsedFraction.ONE);
  if (newValue.isNegative()) {
    newValue = ParsedFraction.ZERO;
  }
  parsedFraction.value = newValue;
  minusButtonLoading.value = true;
  until(buttonsDisabled).toBe(false)
      .then(() => minusButtonLoading.value = false);
}

async function apply(): Promise<void> {
  checkButtonLoading.value = true;
  buttonsDisabled.value = true;
  await productionStepApi.applyPrimaryChangelist(props.productionStep.id)
      .finally(() => buttonsDisabled.value = checkButtonLoading.value = false);
}
</script>

<template>
  <FractionInput
      v-model:parsed-fraction="parsedFraction"
      style="width: 80px;"
      class="mciInput"
  >
  </FractionInput>
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
        :disabled="buttonsDisabled || parsedFraction.isZero()"
    />
    <custom-el-tooltip content="Apply changes">
      <el-button
          :icon="Check"
          @click="apply"
          :loading="checkButtonLoading"
          :disabled="buttonsDisabled"
      />
    </custom-el-tooltip>
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
