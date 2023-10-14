<script setup>
import {inject, ref} from "vue";
import {Check, Minus, Plus} from "@element-plus/icons-vue";

const inputDelay = 1000;

const plusButtonLoading = ref(false);
const minusButtonLoading = ref(false);
const checkButtonLoading = ref(false);
const requestRunning = ref(false);

const props = defineProps(["quantity", "productionStepId"]);
const inputValue = ref(props.quantity.withPrimaryChangelist);

const eventBus = inject("globalEventBus");
const axios = inject('axios');

let timeoutHandle = null;

function onInputUpdate() {
  if (timeoutHandle != null) {
    clearTimeout(timeoutHandle);
  }
  timeoutHandle = setTimeout(async () => {
    requestRunning.value = true;
    await submit();
    requestRunning.value = false;
  }, inputDelay);
}

async function submit() {
  let response = await axios.patch("api/factoryItemList/reportProductionStepMachineCount?" +
      "productionStepId=" + props.productionStepId +
      "&machineCount=" + inputValue.value)
  eventBus.emit("applyFactoryData", response.data);
}

async function plusOne() {
  inputValue.value = editFraction(inputValue.value, 1);
  plusButtonLoading.value = requestRunning.value = true;
  await submit();
  plusButtonLoading.value = requestRunning.value = false;
}

async function minusOne() {
  inputValue.value = editFraction(inputValue.value, -1);
  minusButtonLoading.value = requestRunning.value = true;
  await submit();
  minusButtonLoading.value = requestRunning.value = false;
}

function editFraction(fraction, delta) {
  let i = fraction.indexOf("/");
  if (i < 0) {
    return String(Math.max(Number(fraction) + delta, 0));
  } else {
    let n = Number(fraction.substring(0, i));
    let d = Number(fraction.substring(i + 1));
    n += d * delta;
    if (n <= 0) {
      return "0";
    }
    return String(n) + "/" + String(d);
  }
}

</script>

<template>
  <el-input v-model="inputValue" style="width: 80px" class="mciInput" @update:model-value="onInputUpdate">
  </el-input>
  <el-button-group class="mciButtonGroup">
    <el-button :icon="Plus" @click="plusOne" :loading="plusButtonLoading" :disabled="requestRunning"/>
    <el-button :icon="Minus" @click="minusOne" :loading="minusButtonLoading"
               :disabled="requestRunning || inputValue === '0'"/>
    <el-button :icon="Check" :loading="checkButtonLoading" :disabled="requestRunning"/>
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
}

.mciButtonGroup {
//margin-left: -1px;
}

.mciButtonGroup button:nth-child(1) {
  border-left: none;
//padding-left: 16px;
}
</style>