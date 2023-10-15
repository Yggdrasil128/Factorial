<script setup>
import {inject, reactive, ref} from "vue";
import {onBeforeRouteLeave, onBeforeRouteUpdate, useRoute, useRouter} from "vue-router";
import {Check, Close} from "@element-plus/icons-vue";
import _ from 'lodash';
import {ElMessageBox} from "element-plus";
import RecipeSelect from "@/components/iconselect/RecipeSelect.vue";
import MachineSelect from "@/components/iconselect/MachineSelect.vue";

const router = useRouter();
const route = useRoute();

const axios = inject('axios');
const globalEventBus = inject('globalEventBus');

const visible = ref(true);
const productionStep = ref(emptyProductionStepData());
const original = ref(productionStep.value);
const loading = ref(true);
const saving = ref(false);

document.body.classList.add('el-dark-popper');

// handle navigation

onBeforeRouteLeave(async () => {
  if (visible.value && !(await checkLeave())) {
    return false;
  }
  document.body.classList.remove('el-dark-popper');
});

async function beforeClose() {
  if (await checkLeave()) {
    router.back();
  }
}

async function checkLeave() {
  if (saving.value) {
    return true;
  }
  if (!_.isEqual(productionStep.value, original.value)) {
    const answer = await new Promise(r => ElMessageBox({
      title: 'Warning',
      message: 'Do you really want to leave? You have unsaved changes!',
      confirmButtonText: 'Discard changes',
      cancelButtonText: 'Cancel',
      showCancelButton: true,
      type: 'warning',
      customClass: 'el-dark'
    }).then(() => r(true)).catch(() => r(false)));
    if (!answer) return false;
  }
  visible.value = false;
  await new Promise(r => setTimeout(r, 200));
  return true;
}

onBeforeRouteUpdate(loadProductionStepData);

function emptyProductionStepData() {
  return {
    id: null,
    recipeId: 0,
    machineId: 0,
    machineCount: "1",
    changelistDeltas: {},
  }
}

async function loadProductionStepData(route) {
  if (route.name === 'newProductionStep') {
    productionStep.value = emptyProductionStepData();
  } else {
    let response = await axios.get('/api/productionStep',
        {params: {productionStepId: route.params.editProductionStepId}});
    productionStep.value = response.data;
  }
  original.value = Object.assign({}, productionStep.value);
  loading.value = false;
}

loadProductionStepData(route);

// form validation

const form = ref();
const rules = reactive({
  recipeId: [
    {validator: checkGreaterZero, trigger: 'blur', params: {entityName: 'recipe'}}
  ]
});

function checkGreaterZero(rule, value, callback) {
  if (!value || value <= 0) {
    return callback(new Error('Please select a ' + rule.params.entityName));
  }
}

async function submitForm() {
  if (!await form.value.validate(() => ({}))) {
    return;
  }

  if (true) {
    return;
  }

  saving.value = true;

  if (route.name === 'newProductionStep') {
    await axios.post('/api/factory/productionSteps', productionStep.value,
        {params: {factoryId: route.params.factoryId}});
  } else {
    await axios.patch('/api/productionStep', productionStep.value,
        {params: {productionStepId: route.params.editProductionStepId}});
  }

  globalEventBus.emit('updateChangelists');

  await router.push({name: 'factories', params: {factoryId: route.params.factoryId}});
}

</script>

<template>
  <el-dialog :model-value="visible" :before-close="beforeClose" class="el-dark" width="1000px"
             :title="route.name === 'newChangelist' ? 'New changelist' : 'Edit changelist'">
    <p style="margin-top: 0; margin-bottom: 30px;">
      Insert production step explanation here...
    </p>

    <el-form label-width="150px" style="width: 900px; overflow: auto;"
             v-loading="loading" element-loading-background="rgba(20, 20, 20, 0.8)"
             :model="productionStep" ref="form" :rules="rules">

      <el-form-item label="Recipe">
        <recipe-select v-model="productionStep.recipeId"/>
      </el-form-item>

      <el-form-item label="Machine">
        <machine-select v-model="productionStep.machineId"/>
      </el-form-item>

      <div style="margin-top: 10px; float: right;">
        <el-button :icon="Close" @click="beforeClose">Cancel</el-button>
        <el-button type="primary" :icon="Check" @click="submitForm()" :loading="saving">Save</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<style scoped>

</style>