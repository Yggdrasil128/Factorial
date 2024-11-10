<script setup lang="ts">
import { UploadFilled } from '@element-plus/icons-vue';
import { type Ref, ref, watch } from 'vue';
import {
  genFileId,
  type UploadInstance,
  type UploadProps,
  type UploadRawFile,
  type UploadUserFile,
} from 'element-plus';
import { useVModel } from '@vueuse/core';

export interface IconUploadProps {
  dataBase64: string;
}

const props: IconUploadProps = defineProps<IconUploadProps>();
const emit = defineEmits(['update:dataBase64']);
const dataBase64Model: Ref<string> = useVModel(props, 'dataBase64', emit);

const fileList: Ref<UploadUserFile[]> = ref([]);
const upload = ref<UploadInstance>();

const handleExceed: UploadProps['onExceed'] = (files) => {
  const file = files[0] as UploadRawFile;
  file.uid = genFileId();
  upload.value!.clearFiles();
  upload.value!.handleStart(file);
};

watch(() => props.dataBase64, () => {
  if (!props.dataBase64) {
    upload.value!.clearFiles();
  }
});

watch(fileList, () => {
  if (!fileList.value || fileList.value.length === 0) {
    dataBase64Model.value = '';
    return;
  }
  const file: UploadRawFile = fileList.value[0].raw as UploadRawFile;

  const reader = new FileReader();

  reader.onload = () => {
    dataBase64Model.value = reader.result?.toString() || '';
  };

  reader.readAsDataURL(file);
});
</script>

<template>
  <el-upload class="iconUpload"
             ref="upload"
             v-model:file-list="fileList"
             drag
             :limit="1"
             :on-exceed="handleExceed"
             :auto-upload="false"
             accept=".jpg,.jpeg,.png,.webp,.svg"
             list-type="picture-card">
    <el-icon class="el-icon--upload">
      <upload-filled />
    </el-icon>
    <div class="el-upload__text">
      Drop file here or <em>click to upload</em>
    </div>
  </el-upload>
</template>

<style scoped>
.iconUpload .el-icon--upload {
  margin-bottom: 0;
}

.iconUpload .el-upload__text {
  line-height: 20px;
}
</style>

<!--suppress CssUnusedSymbol -->
<style>
.iconUpload .el-upload .el-upload-dragger {
  background: none;
  --el-upload-dragger-padding-horizontal: 13px;
}

.iconUpload .el-upload .el-upload-dragger:not(.is-dragover) {
  border: none;
  --el-upload-dragger-padding-horizontal: 14px;
}

.iconUpload .el-upload-list__item-preview {
  display: none !important;
}

.iconUpload .el-upload-list--picture-card .el-upload-list__item-actions span + span {
  margin-left: 0;
}
</style>