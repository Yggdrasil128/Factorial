<script setup>
import {computed} from "vue";

const props = defineProps({
  'quantity': Object,
  'color': {
    type: String,
    default: "none",
    validator: value => ['none', 'green', 'red', 'auto'].includes(value)
  },
  'showUnit': Boolean,
});

const parsedQuantity = computed(() => {
  return {
    current: parseFraction(props.quantity.current),
    withPrimaryChangelist: parseFraction(props.quantity.withPrimaryChangelist),
    withActiveChangelists: parseFraction(props.quantity.withActiveChangelists),
  };
})

function parseFraction(s) {
  let i = s.indexOf("/");
  if (i < 0) {
    return Number(s);
  }
  return Number(s.substring(0, i)) / Number(s.substring(i + 1));
}

function formatNumber(value) {
  if (value < 1) {
    return +value.toFixed(3);
  }
  if (value < 10) {
    return +value.toFixed(2);
  }
  if (value < 100) {
    return +value.toFixed(1);
  }
  return Math.round(value);
}

</script>

<template>
  <span :class="color">
    <template v-if="quantity">
      <span :class="{number: 1, positive: parsedQuantity.current > 0, negative: parsedQuantity.current < 0}">
        {{ formatNumber(parsedQuantity.current) }}
      </span>
      <span
          v-if="!(parsedQuantity.current === parsedQuantity.withPrimaryChangelist && parsedQuantity.withPrimaryChangelist === parsedQuantity.withActiveChangelists)">
        |
        <span
            :class="{number: 1, positive: parsedQuantity.withPrimaryChangelist > 0, negative: parsedQuantity.withPrimaryChangelist < 0}">
          {{ formatNumber(parsedQuantity.withPrimaryChangelist) }}
        </span>
      </span>
      <span v-if="parsedQuantity.withPrimaryChangelist !== parsedQuantity.withActiveChangelists">
        |
        <span
            :class="{number: 1, positive: parsedQuantity.withActiveChangelists > 0, negative: parsedQuantity.withActiveChangelists < 0}">
          {{ formatNumber(parsedQuantity.withActiveChangelists) }}
        </span>
      </span>
    </template>
    <span class="unit" v-if="showUnit"> / sec</span>
  </span>
</template>

<!--suppress CssUnusedSymbol -->
<style scoped>
.green .number, .auto .number.positive {
  color: #89ff89;
}

.red .number, .auto .number.negative {
  color: #ff6666;
}

.unit {
  color: #a0a0a0;
}
</style>