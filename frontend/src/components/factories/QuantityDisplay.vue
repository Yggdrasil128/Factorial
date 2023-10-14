<script setup>
const props = defineProps({
  'quantity': Object,
  'color': {
    type: String,
    default: "none",
    validator: value => ['none', 'green', 'red', 'auto'].includes(value)
  },
  'showUnit': Boolean,
});

function formatFraction(s) {
  let i = s.indexOf("/");
  if (i < 0) {
    return Number(s);
  }
  let v = Number(s.substring(0, i)) / Number(s.substring(i + 1));
  if (v < 1) {
    return +v.toFixed(3);
  }
  if (v < 10) {
    return +v.toFixed(2);
  }
  if (v < 100) {
    return +v.toFixed(1);
  }
  return Math.round(v);
}

</script>

<template>
  <span :class="color">
    <span :class="{number: 1, positive: quantity.actual > 0, negative: quantity.actual < 0}">
      {{ formatFraction(quantity.actual) }}
    </span>
    <span
        v-if="!(props.quantity.actual === props.quantity.desiredCurrentChangelist && props.quantity.desiredCurrentChangelist === props.quantity.desiredAllChangelists)">
      /
      <span
          :class="{number: 1, positive: quantity.desiredCurrentChangelist > 0, negative: quantity.desiredCurrentChangelist < 0}">
        {{ formatFraction(quantity.desiredCurrentChangelist) }}
      </span>
    </span>
    <span v-if="props.quantity.desiredCurrentChangelist !== props.quantity.desiredAllChangelists">
      /
      <span
          :class="{number: 1, positive: quantity.desiredAllChangelists > 0, negative: quantity.desiredAllChangelists < 0}">
        {{ formatFraction(quantity.desiredAllChangelists) }}
      </span>
    </span>
    <span class="unit" v-if="showUnit"> / min</span>
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
  color: #808080;
}
</style>