<script setup>
import {reactive, ref} from "vue";
import {onBeforeRouteLeave, onBeforeRouteUpdate, useRoute, useRouter} from "vue-router";
import IconSelect from "@/components/IconSelect.vue";
import {Check, Close} from "@element-plus/icons-vue";
import _ from 'lodash';
import {ElMessageBox} from "element-plus";

const router = useRouter();
const route = useRoute();

const visible = ref(true);
const changelist = ref(emptyChangelistData());
const original = ref(changelist.value);
const loading = ref(true);

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
    icon: null,
    primary: true,
    active: true,
  }
}

async function loadChangelistData(route) {
  if (route.name === 'newChangelist') {
    changelist.value = emptyChangelistData();
  } else {
    await new Promise(r => setTimeout(r, 1000));
    changelist.value = {
      id: 2,
      name: "Upgrade steel production",
      icon: {
        id: 2,
        name: "Steel Ingot",
        url: "https://satisfactory.wiki.gg/images/b/bd/Steel_Ingot.png"
      },
      primary: true,
      active: true,
    };
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

  console.log("submit");
}

</script>

<template>
  <el-dialog :model-value="visible" :before-close="beforeClose" class="el-dark" width="600px"
             :title="route.name === 'newChangelist' ? 'New changelist' : 'Edit changelist'">
    <p style="margin-top: 0; margin-bottom: 30px;">
      Insert changelist explanation here...
    </p>

    <el-form label-width="120px" style="width: 500px; overflow: auto;"
             v-loading="loading" element-loading-background="rgba(20, 20, 20, 0.8)"
             :model="changelist" ref="form" :rules="rules">
      <el-form-item label="Factory name" prop="name">
        <el-input v-model="changelist.name"/>
      </el-form-item>

      <el-form-item label="Icon">
        <icon-select v-model="changelist.icon" style="width: 100%;"/>
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
        <el-button type="primary" :icon="Check" @click="submitForm()">Save</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<style scoped>

</style>