<script setup lang="ts">
import type {Fraction} from '@/types/model/basic';
import {ParsedFraction} from '@/utils/fractionUtils';
import {useVModel} from '@vueuse/core';
import {computed, type ComputedRef, onMounted, ref, type Ref, watch} from 'vue';
import {parseTerm} from '@/utils/termParser';

export interface FractionInputProps {
  fraction?: Fraction;
  parsedFraction?: ParsedFraction;
}

const props: FractionInputProps = defineProps<FractionInputProps>();
const emit: (event: (string), ...args: any[]) => void = defineEmits(['update:fraction', 'update:parsedFraction']);
const fractionProp: Ref<Fraction | undefined> = useVModel(props, 'fraction', emit);
const parsedFractionProp: Ref<ParsedFraction | undefined> = useVModel(props, 'parsedFraction', emit);

const parsedFractionModel: Ref<ParsedFraction | undefined> = ref();

watch(() => props.fraction, () => {
  if (props.fraction) {
    const parsedFraction: ParsedFraction = ParsedFraction.of(props.fraction);
    if (!parsedFractionProp.value || !parsedFractionProp.value.equals(parsedFraction)) {
      parsedFractionProp.value = parsedFraction;
    }
    if (!parsedFractionModel.value || !parsedFractionModel.value.equals(parsedFraction)) {
      parsedFractionModel.value = parsedFraction;
    }
  } else {
    if (parsedFractionProp.value) {
      parsedFractionProp.value = undefined;
    }
    if (parsedFractionModel.value) {
      parsedFractionProp.value = undefined;
    }
  }
});

watch(() => props.parsedFraction, () => {
  if (props.parsedFraction) {
    const fraction: Fraction = props.parsedFraction.toFraction();
    if (!fractionProp.value || fractionProp.value !== fraction) {
      fractionProp.value = fraction;
    }
    if (!parsedFractionModel.value || !parsedFractionModel.value.equals(props.parsedFraction)) {
      parsedFractionModel.value = props.parsedFraction;
    }
  } else {
    if (fractionProp.value) {
      fractionProp.value = '';
    }
    if (parsedFractionModel.value) {
      parsedFractionModel.value = undefined;
    }
  }
});

watch(parsedFractionModel, () => {
  if (parsedFractionModel.value) {
    const fraction: Fraction = parsedFractionModel.value.toFraction();
    if (!fractionProp.value || fractionProp.value !== fraction) {
      fractionProp.value = fraction;
    }
    if (!parsedFractionProp.value || !parsedFractionProp.value.equals(parsedFractionModel.value)) {
      parsedFractionProp.value = parsedFractionModel.value;
    }
    if (!inputFraction.value || !inputFraction.value.equals(parsedFractionModel.value)) {
      inputModel.value = fraction;
    }
  } else {
    if (fractionProp.value) {
      fractionProp.value = '';
    }
    if (parsedFractionProp.value) {
      parsedFractionProp.value = undefined;
    }
    if (inputFraction.value) {
      inputModel.value = '';
    }
  }
});

onMounted(() => {
  if (props.fraction) {
    parsedFractionModel.value = ParsedFraction.of(props.fraction);
  } else if (props.parsedFraction) {
    parsedFractionModel.value = props.parsedFraction;
  }
});

const inputModel: Ref<string> = ref('');
const inputFraction: ComputedRef<ParsedFraction | undefined> = computed(() =>
  parseTerm(inputModel.value),
);

const inputDebounceMs: number = 1000;
let inputDebounceTimeout: NodeJS.Timeout | null = null;

function onInputUpdate(): void {
  if (inputDebounceTimeout) {
    clearTimeout(inputDebounceTimeout);
  }
  inputDebounceTimeout = setTimeout(onInputDebounceTimer, inputDebounceMs);
}

function onInputDebounceTimer(): void {
  inputDebounceTimeout = null;

  parsedFractionModel.value = inputFraction.value;
}

function onInputBlur(): void {
  if (inputDebounceTimeout) {
    clearTimeout(inputDebounceTimeout);
    inputDebounceTimeout = null;
  }

  parsedFractionModel.value = inputFraction.value;
  inputModel.value = inputFraction.value?.toFraction() ?? '';
}

</script>

<template>
  <el-input v-model="inputModel" @update:model-value="onInputUpdate" @blur="onInputBlur" />
</template>

<style scoped>

</style>