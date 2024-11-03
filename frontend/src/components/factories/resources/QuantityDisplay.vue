<script setup lang="ts">
import { computed, type ComputedRef } from 'vue';
import { ElTooltip } from 'element-plus';
import type { QuantityByChangelist } from '@/types/model/basic';
import { fractionToNumber } from '@/utils/utils';

export interface QuantityDisplayProps {
  quantity?: QuantityByChangelist;
  color?: 'none' | 'auto' | 'green' | 'red';
  showUnit?: boolean;
}

const props: QuantityDisplayProps = withDefaults(defineProps<QuantityDisplayProps>(), {
  color: 'none'
});

type NumberQuantityByChangelist = {
  [key in keyof QuantityByChangelist]: number;
}

const parsedQuantity: ComputedRef<NumberQuantityByChangelist | undefined> = computed(() => {
  if (!props.quantity) return undefined;
  return {
    current: fractionToNumber(props.quantity.current),
    withPrimaryChangelist: fractionToNumber(props.quantity.withPrimaryChangelist),
    withActiveChangelists: fractionToNumber(props.quantity.withActiveChangelists)
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
    <template v-if="quantity">
      <el-tooltip
        effect="dark"
        placement="top-start"
        transition="none"
        :hide-after="0"
        :content="quantity.current.replace('/', ' / ')"
        :disabled="quantity.current.indexOf('/') < 0"
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
          :content="quantity.withPrimaryChangelist.replace('/', ' / ')"
          :disabled="quantity.withPrimaryChangelist.indexOf('/') < 0"
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
          :content="quantity.withActiveChangelists.replace('/', ' / ')"
          :disabled="quantity.withActiveChangelists.indexOf('/') < 0"
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
    <span v-if="showUnit" class="unit"> / sec</span>
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
