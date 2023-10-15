<script setup>
import {inject, reactive, ref} from "vue";
import {onBeforeRouteLeave, onBeforeRouteUpdate, useRoute, useRouter} from "vue-router";
import IconSelect from "@/components/iconselect/IconSelect.vue";
import {Check, Close} from "@element-plus/icons-vue";
import _ from 'lodash';
import {ElMessageBox} from "element-plus";

const router = useRouter();
const route = useRoute();

const axios = inject('axios');
const globalEventBus = inject('globalEventBus');

const visible = ref(true);
const changelist = ref(emptyChangelistData());
const original = ref(changelist.value);
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
  if (!_.isEqual(changelist.value, original.value)) {
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

onBeforeRouteUpdate(loadChangelistData);

function emptyChangelistData() {
  return {
    id: null,
    name: '',
    iconId: 0,
    primary: true,
    active: true,
  }
}

async function loadChangelistData(route) {
  if (route.name === 'newChangelist') {
    changelist.value = emptyChangelistData();
  } else {
    let response = await axios.get('/api/changelist', {params: {changelistId: route.params.editChangelistId}});
    changelist.value = response.data;
    changelist.value.iconId = changelist.value.icon ? changelist.value.icon.id : 0;
  }
  original.value = Object.assign({}, changelist.value);
  loading.value = false;
}

loadChangelistData(route);

// form validation

const form = ref();
const rules = reactive({
  name: [
    {required: true, message: 'Please enter a name for the changelist', trigger: 'blur'},
  ],
});

async function submitForm() {
  changelist.value.name = changelist.value.name.trim();

  if (!await form.value.validate(() => ({}))) {
    return;
  }

  saving.value = true;

  if (route.name === 'newChangelist') {
    await axios.post('/api/save/changelists', changelist.value, {params: {saveId: 1}});
  } else {
    await axios.patch('/api/changelist', changelist.value, {params: {changelistId: route.params.editChangelistId}});
  }

  globalEventBus.emit('updateChangelists');

  await router.push({name: 'factories', params: {factoryId: route.params.factoryId}});
}

</script>

<template>
  <el-dialog :model-value="visible" :before-close="beforeClose" class="el-dark" width="1000px"
             :title="route.name === 'newChangelist' ? 'New changelist' : 'Edit changelist'">
    <p style="margin-top: 0; margin-bottom: 30px;">
      Insert changelist explanation here...
    </p>

    <el-form label-width="150px" style="width: 900px; overflow: auto;"
             v-loading="loading" element-loading-background="rgba(20, 20, 20, 0.8)"
             :model="changelist" ref="form" :rules="rules">

      <el-form-item label="Changelist name" prop="name">
        <el-input v-model="changelist.name"/>
      </el-form-item>

      <el-form-item label="Icon">
        <icon-select v-model="changelist.iconId"/>
      </el-form-item>

      <template v-if="route.name === 'newChangelist'">
        <el-form-item label="Primary">
          <el-switch v-model="changelist.primary"
                     @update:model-value="changelist.primary ? changelist.active = true : ''"/>
        </el-form-item>

        <el-form-item label="Active">
          <el-switch v-model="changelist.active" :disabled="changelist.primary"/>
        </el-form-item>
      </template>

      <div style="margin-top: 10px; float: right;">
        <el-button :icon="Close" @click="beforeClose">Cancel</el-button>
        <el-button type="primary" :icon="Check" @click="submitForm()" :loading="saving">Save</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<style scoped>

</style>