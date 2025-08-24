<script setup lang="ts">
import {type Ref, ref} from 'vue';
import {onBeforeRouteLeave, type Router, useRouter} from 'vue-router';
import {ElForm, ElMessageBox, type FormInstance, type FormRules} from 'element-plus';
import {Check, Close} from '@element-plus/icons-vue';
import {type UserSettingsStore, useUserSettingsStore} from '@/stores/userSettingsStore';
import {sleep} from '@/utils/utils';

export interface EditModalProps {
  title: string;
  formLabelWidth?: string;
  formModel: any;
  formLoading?: boolean;
  formRules: FormRules;
  hasChanges: boolean;
  isSaving: boolean;
}

const props: EditModalProps = defineProps<EditModalProps>();
const emit: (event: string, ...args: any[]) => void = defineEmits(['submit']);

const router: Router = useRouter();
const userSettingsStore: UserSettingsStore = useUserSettingsStore();

const visible: Ref<boolean> = ref(true);

// handle navigation

onBeforeRouteLeave(async () => {
  if (visible.value && !(await checkLeave())) {
    return false;
  }
});

async function beforeClose(): Promise<void> {
  if (await checkLeave()) {
    router.back();
  }
}

async function checkLeave(): Promise<boolean> {
  if (props.isSaving) {
    return true;
  }
  if (!userSettingsStore.skipUnsavedChangesWarning && props.hasChanges) {
    const answer: boolean = await new Promise((r: (value: (PromiseLike<boolean> | boolean)) => void) =>
      ElMessageBox({
        title: 'Warning',
        message: 'Do you really want to leave? You have unsaved changes!',
        confirmButtonText: 'Discard changes',
        cancelButtonText: 'Cancel',
        showCancelButton: true,
        type: 'warning'
      })
        .then(() => r(true))
        .catch(() => r(false))
    );
    if (!answer) return false;
  }
  visible.value = false;
  await sleep(200);
  return true;
}

// form validation

const form: Ref<FormInstance | undefined> = ref();

async function validate(): Promise<boolean> {
  if (!form.value) return false;
  return await form.value.validate(() => {
  });
}

defineExpose({ validate });
</script>

<template>
  <el-dialog
    :model-value="visible"
    :before-close="beforeClose"
    width="500px"
    :title="title"
    :close-on-click-modal="false"
  >
    <p style="margin-top: 0; margin-bottom: 30px;">
      <slot name="description">Insert description here...</slot>
    </p>

    <el-form
      :label-width="formLabelWidth ?? '120px'"
      style="width: 100%; overflow: auto;"
      v-loading="formLoading"
      element-loading-background="rgba(20, 20, 20, 0.8)"
      :model="formModel"
      ref="form"
      :rules="formRules"
    >
      <slot name="form" />

      <div style="margin-top: 10px; float: right;">
        <el-button :icon="Close" @click="beforeClose">
          Cancel
        </el-button>
        <el-button type="primary" :icon="Check" @click="emit('submit')" :loading="isSaving">
          Save
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<style scoped></style>
