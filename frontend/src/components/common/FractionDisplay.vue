<script setup lang="ts">
import type {Fraction} from "@/types/model/basic";
import {ParsedFraction} from "@/utils/fractionUtils";
import {computed, type ComputedRef} from "vue";
import CustomElTooltip from "@/components/common/CustomElTooltip.vue";
import {useUserSettingsStore} from "@/stores/userSettingsStore";
import {ThroughputUnit} from "@/types/userSettings";

export interface FractionDisplayProps {
  fraction: Fraction | ParsedFraction;
  color?: undefined | 'auto' | 'green' | 'red';
  isThroughput?: boolean;
  hideUnit?: boolean;
}

const props: FractionDisplayProps = defineProps<FractionDisplayProps>();

const userSettingsStore = useUserSettingsStore();

const conversionFactor: ComputedRef<ParsedFraction> = computed(() => {
  if (!props.isThroughput) return ParsedFraction.ONE;

  if (userSettingsStore.throughputUnit === ThroughputUnit.ItemsPerSecond) return ParsedFraction.ONE;
  if (userSettingsStore.throughputUnit === ThroughputUnit.ItemsPerMinute) return new ParsedFraction(60n);

  console.warn('Unknown throughput unit \'' + userSettingsStore.throughputUnit + '\'');
  return ParsedFraction.ONE;
});

const parsedFraction: ComputedRef<ParsedFraction> = computed(() => {
  let pf: ParsedFraction;
  if (typeof props.fraction === 'string') {
    pf = ParsedFraction.of(props.fraction);
  } else {
    pf = props.fraction;
  }
  return pf.multiply(conversionFactor.value);
});

const numberFormat: Intl.NumberFormat = new Intl.NumberFormat('en-US', {maximumSignificantDigits: 3});
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
  <custom-el-tooltip
      :content="parsedFraction.toFraction().replace('/', ' / ')"
  >
    <span :class="{
      green: props.color === 'green' || props.color === 'auto' && parsedFraction.isPositive(),
      red: props.color === 'red' || props.color === 'auto' && parsedFraction.isNegative(),
    }">
      {{ formatNumber(parsedFraction.toNumber()) }}
    </span>
  </custom-el-tooltip>
  <span v-if="isThroughput && !hideUnit" class="unit">
    <template v-if="userSettingsStore.throughputUnit === ThroughputUnit.ItemsPerSecond">
        / sec
      </template>
      <template v-else-if="userSettingsStore.throughputUnit === ThroughputUnit.ItemsPerMinute">
        / min
      </template>
  </span>
</template>

<style scoped>
.green {
  color: #89ff89;
}

.red {
  color: #ff6666;
}

.unit {
  color: #a0a0a0;
}
</style>