<script setup lang="ts">
import type {QuantityByChangelist} from "@/types/model/basic";
import FractionDisplay, {type FractionDisplayProps} from "@/components/common/FractionDisplay.vue";
import {computed, type ComputedRef} from "vue";

export interface QuantityByChangelistDisplayProps {
  quantity?: QuantityByChangelist;
  color?: FractionDisplayProps['color'];
  isThroughput?: boolean;
  hideThroughputUnit?: boolean;
  forceSign?: boolean;
}

const props: QuantityByChangelistDisplayProps = defineProps<QuantityByChangelistDisplayProps>();

const detailLevel: ComputedRef<1 | 2 | 3> = computed(() => {
  if (!props.quantity) return 1;
  if (props.quantity.withPrimaryChangelist !== props.quantity.withActiveChangelists) return 3;
  if (props.quantity.current !== props.quantity.withPrimaryChangelist) return 2;
  return 1;
});

const fractionDisplayProps: ComputedRef<Partial<FractionDisplayProps>> = computed(() => {
  return {
    color: props.color,
    isThroughput: props.isThroughput,
    forceSign: props.forceSign,
  };
});

</script>

<template>
  <template v-if="quantity">
    <fraction-display :fraction="quantity.current"
                      v-bind="fractionDisplayProps"
                      :hide-throughput-unit="detailLevel >= 2 || props.hideThroughputUnit"/>
    <template v-if="detailLevel >= 2">
      |
      <fraction-display :fraction="quantity.withPrimaryChangelist"
                        v-bind="fractionDisplayProps"
                        :hide-throughput-unit="detailLevel >= 3 || props.hideThroughputUnit"/>
    </template>
    <template v-if="detailLevel >= 3">
      |
      <fraction-display :fraction="quantity.withActiveChangelists" v-bind="fractionDisplayProps"/>
    </template>
  </template>
</template>

<style scoped>

</style>