<script setup lang="ts">
import { computed, type ComputedRef } from 'vue';
import { ElTooltip } from 'element-plus';
import type { Fraction, QuantityByChangelist } from '@/types/model/basic';
import { fractionToNumber, ParsedFraction } from '@/utils/fractionUtils';
import { useUserSettingsStore } from '@/stores/userSettingsStore';
import { ThroughputUnit } from '@/types/userSettings';

export interface QuantityDisplayProps {
  quantity?: QuantityByChangelist;
  color?: 'none' | 'auto' | 'green' | 'red';
  showUnit?: boolean;
  convertUnit?: boolean;
}

const props: QuantityDisplayProps = withDefaults(defineProps<QuantityDisplayProps>(), {
  color: 'none'
});

const userSettingsStore = useUserSettingsStore();

const conversionFactor: ComputedRef<ParsedFraction> = computed(() => {
  if (!props.convertUnit) return ParsedFraction.ONE;

  if (userSettingsStore.throughputUnit === ThroughputUnit.ItemsPerSecond) return ParsedFraction.ONE;
  if (userSettingsStore.throughputUnit === ThroughputUnit.ItemsPerMinute) return new ParsedFraction(60n);

  console.warn('Unknown throughput unit \'' + userSettingsStore.throughputUnit + '\'');
  return ParsedFraction.ONE;
});

const convertedQuantity: ComputedRef<QuantityByChangelist | undefined> = computed(() => {
  if (!props.quantity) return undefined;
  if (conversionFactor.value.equals(ParsedFraction.ONE)) return props.quantity;

  function convert(value: Fraction): Fraction {
    return ParsedFraction.of(value).multiply(conversionFactor.value).toFraction();
  }

  return {
    current: convert(props.quantity.current),
    withPrimaryChangelist: convert(props.quantity.withPrimaryChangelist),
    withActiveChangelists: convert(props.quantity.withActiveChangelists),
  };
});

type NumberQuantityByChangelist = {
  [key in keyof QuantityByChangelist]: number;
}

const parsedQuantity: ComputedRef<NumberQuantityByChangelist | undefined> = computed(() => {
  if (!convertedQuantity.value) return undefined;
  return {
    current: fractionToNumber(convertedQuantity.value.current),
    withPrimaryChangelist: fractionToNumber(convertedQuantity.value.withPrimaryChangelist),
    withActiveChangelists: fractionToNumber(convertedQuantity.value.withActiveChangelists),
  };
});

const numberFormat: Intl.NumberFormat = new Intl.NumberFormat('en-US', { maximumSignificantDigits: 3 });
const intNumberFormat: Intl.NumberFormat = new Intl.NumberFormat('en-US');

function formatNumber(value: number): string {
  if (Math.abs(value) >= 1000) {
    return intNumberFormat.format(Math.round(value));
  } else {
    return numberFormat.format(value);
  }
}
</script>

<template>
  <span :class="color">
    <template v-if="convertedQuantity">
      <el-tooltip
        effect="dark"
        placement="top-start"
        transition="none"
        :hide-after="0"
        :content="convertedQuantity.current.replace('/', ' / ')"
        :disabled="convertedQuantity.current.indexOf('/') < 0"
      >
        <span
          :class="{
            number: true,
            positive: parsedQuantity?.current! > 0,
            negative: parsedQuantity?.current! < 0
          }"
        >
          {{ formatNumber(parsedQuantity?.current!) }}
        </span>
      </el-tooltip>
      <span
        v-if="parsedQuantity?.current !== parsedQuantity?.withPrimaryChangelist ||
            parsedQuantity?.current !== parsedQuantity?.withActiveChangelists"
      >
        |
        <el-tooltip
          effect="dark"
          placement="top-start"
          transition="none"
          :hide-after="0"
          :content="convertedQuantity.withPrimaryChangelist.replace('/', ' / ')"
          :disabled="convertedQuantity.withPrimaryChangelist.indexOf('/') < 0"
        >
          <span
            :class="{
              number: true,
              positive: parsedQuantity?.withPrimaryChangelist! > 0,
              negative: parsedQuantity?.withPrimaryChangelist! < 0
            }"
          >
            {{ formatNumber(parsedQuantity?.withPrimaryChangelist!) }}
          </span>
        </el-tooltip>
      </span>
      <span v-if="parsedQuantity?.withPrimaryChangelist !== parsedQuantity?.withActiveChangelists">
        |
        <el-tooltip
          effect="dark"
          placement="top-start"
          transition="none"
          :hide-after="0"
          :content="convertedQuantity.withActiveChangelists.replace('/', ' / ')"
          :disabled="convertedQuantity.withActiveChangelists.indexOf('/') < 0"
        >
          <span
            :class="{
              number: true,
              positive: parsedQuantity?.withActiveChangelists! > 0,
              negative: parsedQuantity?.withActiveChangelists! < 0
            }"
          >
            {{ formatNumber(parsedQuantity?.withActiveChangelists!) }}
          </span>
        </el-tooltip>
      </span>
    </template>
    <span v-if="showUnit" class="unit">
      <template v-if="!convertUnit || userSettingsStore.throughputUnit === ThroughputUnit.ItemsPerSecond">
        / sec
      </template>
      <template v-else-if="userSettingsStore.throughputUnit === ThroughputUnit.ItemsPerMinute">
        / min
      </template>
    </span>
  </span>
</template>

<!--suppress CssUnusedSymbol -->
<style scoped>
.green .number,
.auto .number.positive {
  color: #89ff89;
}

.red .number,
.auto .number.negative {
  color: #ff6666;
}

.unit {
  color: #a0a0a0;
}
</style>
