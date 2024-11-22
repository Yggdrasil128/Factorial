<script setup lang="ts">
import { useIconStore } from '@/stores/model/iconStore';
import { computed, type ComputedRef, type Ref, ref } from 'vue';
import IconImg from '@/components/common/IconImg.vue';
import CascaderSelect from '@/components/common/input/CascaderSelect.vue';
import { useItemStore } from '@/stores/model/itemStore';
import CascaderMultiSelect from '@/components/common/input/CascaderMultiSelect.vue';
import FlatSelect from '@/components/common/input/FlatSelect.vue';
import FlatMultiSelect from '@/components/common/input/FlatMultiSelect.vue';
import ProductionsStepsDisplayChooser from '@/components/factories/resources/ResourceContributorsDisplayToggle.vue';
import { ParsedFraction } from '@/utils/fractionUtils';
import { parseTerm } from '@/utils/termParser';
import type { Fraction } from '@/types/model/basic';
import FractionInput from '@/components/common/input/FractionInput.vue';

const iconStore = useIconStore();
const itemStore = useItemStore();

const selectedItem: Ref<number> = ref(0);
const selectedItems: Ref<number[]> = ref([]);
const numberParseInput: Ref<string> = ref('2*3*5+2*3*2');
const numberParseOutput: ComputedRef<ParsedFraction | undefined> = computed(() =>
  parseTerm(numberParseInput.value),
);

const fraction: Ref<Fraction> = ref('');
const setFraction: Ref<string> = ref('');

const parsedFraction: Ref<ParsedFraction | undefined> = ref();
const setParsedFraction: Ref<string> = ref('');

</script>

<template>
  <icon-img :icon="iconStore.getById(1)" :size="64" />
  <icon-img :icon="2" :size="64" />

  <div>
    <h2>Number parser</h2>
    <el-input style="width: 250px;" v-model="numberParseInput" />
    <br />
    <br />
    Result:
    <template v-if="!numberParseOutput">
      undefined
    </template>
    <template v-else>
      {{ numberParseOutput.toFraction() }} = {{ numberParseOutput.toNumber() }}
    </template>
  </div>

  <h2>FractionInput</h2>
  <div>
    <h3>With Fraction as model</h3>

    <div>
      <el-input style="width: 250px;" v-model="setFraction" />
      <el-button @click="fraction = setFraction">Set fraction</el-button>
    </div>

    <FractionInput style="width: 250px;" v-model:fraction="fraction" />
    <br>
    Model value: {{ fraction }}
  </div>
  <div>
    <h3>With ParsedFraction as model</h3>

    <div>
      <el-input style="width: 250px;" v-model="setParsedFraction" />
      <el-button @click="parsedFraction = ParsedFraction.of(setParsedFraction)">Set parsed fraction</el-button>
    </div>

    <FractionInput style="width: 250px;" v-model:parsed-fraction="parsedFraction" />
    <br>
    Model value: {{ parsedFraction ? parsedFraction.toFraction() : 'undefined' }}
  </div>

  <div>
    <h2>FlatSelect and CascaderSelect</h2>

    <FlatSelect style="width: 250px;" v-model="selectedItem" :options="[...itemStore.getAll()]" clearable />
    <br>
    <CascaderSelect style="width: 250px;" v-model="selectedItem" :options="[...itemStore.getAll()]" clearable />

    <pre>{{ JSON.stringify(selectedItem) }}</pre>
  </div>

  <div>
    <h2>FlatMultiSelect and CascaderMultiSelect</h2>

    <FlatMultiSelect style="width: 250px;" v-model="selectedItems" :options="[...itemStore.getAll()]" clearable />
    <br><br>
    <CascaderMultiSelect style="width: 250px;" v-model="selectedItems" :options="[...itemStore.getAll()]" clearable />

    <pre>{{ JSON.stringify(selectedItems) }}</pre>
  </div>

  <br />
  <br />
  <div style="width: fit-content;">
    <ProductionsStepsDisplayChooser />
  </div>
  <br />
  <div style="width: fit-content;">
    <ProductionsStepsDisplayChooser global />
  </div>
</template>

<style scoped>

</style>