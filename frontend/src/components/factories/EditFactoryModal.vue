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
const factory = ref(emptyFactoryData());
const original = ref(factory.value);
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
  if (!_.isEqual(factory.value, original.value)) {
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

onBeforeRouteUpdate(loadFactoryData);

function emptyFactoryData() {
  return {
    id: null,
    name: "",
    icon: null,
  }
}

async function loadFactoryData(route) {
  if (route.name === 'newFactory') {
    factory.value = emptyFactoryData();
  } else {
    await new Promise(r => setTimeout(r, 1000));
    factory.value = {
      id: 1,
      name: "Main factory",
      icon: {
        id: 2,
        name: "Steel Ingot",
        url: "https://satisfactory.wiki.gg/images/b/bd/Steel_Ingot.png"
      },
    };
  }
  original.value = Object.assign({}, factory.value);
  loading.value = false;
}

loadFactoryData(route);

// form validation

const form = ref();

const rules = reactive({
  name: [
    {required: true, message: 'Please enter a name for the factory', trigger: 'blur'},
  ],
});

async function submitForm() {
  factory.value.name = factory.value.name.trim();

  if (!await form.value.validate(() => ({}))) {
    return;
  }

  console.log("submit");
}

</script>

<template>
  <el-dialog :model-value="visible" :before-close="beforeClose" class="el-dark" width="600px"
             :title="route.name === 'newFactory' ? 'New factory' : 'Edit factory'">
    <p style="margin-top: 0; margin-bottom: 30px;">
      Factories allow you to group production steps. Blablabla, more explanation here...
    </p>

    <el-form label-width="120px" style="width: 500px; overflow: auto;"
             v-loading="loading" element-loading-background="rgba(20, 20, 20, 0.8)"
             :model="factory" ref="form" :rules="rules">
      <el-form-item label="Factory name" prop="name">
        <el-input v-model="factory.name"/>
      </el-form-item>

      <el-form-item label="Icon">
        <icon-select v-model="factory.icon" style="width: 100%;"/>
      </el-form-item>

      <div style="margin-top: 10px; float: right;">
        <el-button :icon="Close" @click="beforeClose">Cancel</el-button>
        <el-button type="primary" :icon="Check" @click="submitForm()">Save</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<style scoped>

</style>