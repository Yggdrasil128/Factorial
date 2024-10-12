<script setup lang="js">
import { onMounted, onUnmounted, ref } from 'vue';
import { onBeforeRouteLeave, useRouter } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import { Check, Close } from '@element-plus/icons-vue';

const emit = defineEmits(['submit']);

const props = defineProps([
  'title',
  'formLabelWidth',
  'formModel',
  'formLoading',
  'formRules',
  'hasChanges',
  'isSaving'
]);

const router = useRouter();

const visible = ref(true);

onMounted(() => {
  document.body.classList.add('el-dark-popper');
});

onUnmounted(() => {
  document.body.classList.remove('el-dark-popper');
});

// handle navigation

onBeforeRouteLeave(async () => {
  if (visible.value && !(await checkLeave())) {
    return false;
  }
});

async function beforeClose() {
  if (await checkLeave()) {
    router.back();
  }
}

async function checkLeave() {
  if (props.isSaving) {
    return true;
  }
  if (props.hasChanges) {
    const answer = await new Promise((r) =>
      ElMessageBox({
        title: 'Warning',
        message: 'Do you really want to leave? You have unsaved changes!',
        confirmButtonText: 'Discard changes',
        cancelButtonText: 'Cancel',
        showCancelButton: true,
        type: 'warning',
        customClass: 'el-dark'
      })
        .then(() => r(true))
        .catch(() => r(false))
    );
    if (!answer) return false;
  }
  visible.value = false;
  await new Promise((r) => setTimeout(r, 200));
  return true;
}

// form validation

const form = ref();

async function validate() {
  return await form.value.validate(() => ({}));
}

defineExpose({ validate });
</script>

<template>
  <el-dialog
    :model-value="visible"
    :before-close="beforeClose"
    class="el-dark"
    width="1000px"
    :title="title"
  >
    <p style="margin-top: 0; margin-bottom: 30px">
      <slot name="description"> Insert description here...</slot>
      New version! Hello from EditModal.vue
    </p>

    <el-form
      :label-width="formLabelWidth ?? '120px'"
      style="width: 900px; overflow: auto"
      v-loading="formLoading"
      element-loading-background="rgba(20, 20, 20, 0.8)"
      :model="formModel"
      ref="form"
      :rules="formRules"
    >
      <slot name="form" />

      <div style="margin-top: 10px; float: right">
        <el-button :icon="Close" @click="beforeClose">Cancel</el-button>
        <el-button type="primary" :icon="Check" @click="emit('submit')" :loading="isSaving"
        >Save
        </el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>

<style scoped></style>
