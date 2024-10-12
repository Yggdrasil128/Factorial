<script setup lang="js">
import { computed, inject, reactive, ref } from 'vue';
import { onBeforeRouteUpdate, useRoute, useRouter } from 'vue-router';
import _ from 'lodash';
import MachineSelect from '@/components/iconselect/MachineSelect.vue';
import RecipeSelect from '@/components/iconselect/RecipeSelect.vue';
import { ElNotification } from 'element-plus';

const router = useRouter();
const route = useRoute();

const axios = inject('axios');

const loading = ref(true);
const saving = ref(false);
const productionStep = ref(emptyProductionStepData());
const original = ref(null);
const editModal = ref();

const hasChanges = computed(() => !_.isEqual(productionStep.value, original.value));

const formRules = reactive({
  recipeId: [{ validator: checkGreaterZero, params: { entityName: 'recipe' } }],
  machineId: [{ validator: checkGreaterZero, trigger: 'blur', params: { entityName: 'machine' } }]
});

function checkGreaterZero(rule, value, callback) {
  if (!value || value <= 0) {
    return callback(new Error('Please select a ' + rule.params.entityName));
  } else {
    callback();
  }
}

function emptyProductionStepData() {
  return {
    id: null,
    recipeId: 0,
    machineId: 0,
    machineCount: '1',
    changelistDeltas: {}
  };
}

async function loadProductionStepData(route) {
  if (route.name === 'newProductionStep') {
    productionStep.value = emptyProductionStepData();
  } else {
    const response = await axios.get('/api/productionStep', {
      params: { productionStepId: route.params.editProductionStepId }
    });
    productionStep.value = response.data;
  }
  original.value = Object.assign({}, productionStep.value);
  loading.value = false;
}

loadProductionStepData(route);
onBeforeRouteUpdate(loadProductionStepData);

async function submitForm() {
  if (!(await editModal.value.validate())) {
    ElNotification.error({
      title: 'Validation error',
      customClass: 'el-dark'
    });
    return;
  }
  ElNotification.success({
    title: 'Validation success',
    customClass: 'el-dark'
  });

  // eslint-disable-next-line no-constant-condition
  if (true) {
    return;
  }

  // noinspection UnreachableCodeJS
  saving.value = true;

  if (route.name === 'newProductionStep') {
    await axios.post('/api/factory/productionSteps', productionStep.value, {
      params: { factoryId: route.params.factoryId }
    });
  } else {
    await axios.patch('/api/productionStep', productionStep.value, {
      params: { productionStepId: route.params.editProductionStepId }
    });
  }

  await router.push({ name: 'factories', params: { factoryId: route.params.factoryId } });
}
</script>

<template>
  <edit-modal
    :title="route.name === 'newProductionStep' ? 'New production step' : 'Edit production step'"
    form-label-width="120px"
    :form-model="productionStep"
    :form-loading="loading"
    :form-rules="formRules"
    :has-changes="hasChanges"
    :is-saving="saving"
    @submit="submitForm"
    ref="editModal"
  >
    <template #description> Insert production step explanation here...</template>

    <template #form>
      <el-form-item label="Recipe" prop="recipeId" required>
        <recipe-select v-model="productionStep.recipeId" />
      </el-form-item>

      <el-form-item label="Machine" prop="machineId" required>
        <machine-select v-model="productionStep.machineId" />
      </el-form-item>
    </template>
  </edit-modal>
</template>

<style scoped></style>
