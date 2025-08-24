<script setup lang="ts">
import {UploadFilled} from '@element-plus/icons-vue';
import {genFileId, type UploadInstance, type UploadProps, type UploadRawFile, type UploadUserFile,} from 'element-plus';
import {type Ref, ref, watch} from 'vue';
import {useVModel} from '@vueuse/core';

export interface FileUploadProps {
  modelValue: string | null;
}

const props: FileUploadProps = defineProps<FileUploadProps>();
const emit: (event: string, ...args: any[]) => void = defineEmits(['update:modelValue']);
const model: Ref<string | null> = useVModel(props, 'modelValue', emit);

const fileList: Ref<UploadUserFile[]> = ref([]);
const upload: Ref<UploadInstance | undefined> = ref();

const handleExceed: UploadProps['onExceed'] = (files: File[]) => {
  const file: UploadRawFile = {
    ...files[0],
    uid: genFileId(),
  };
  upload.value!.clearFiles();
  upload.value!.handleStart(file);
};

watch(
  () => props.modelValue,
  () => {
    if (props.modelValue === null) {
      upload.value!.clearFiles();
    }
  },
);

watch(fileList, () => {
  if (!fileList.value || fileList.value.length === 0) {
    model.value = null;
    return;
  }
  const file: UploadRawFile = fileList.value[0].raw as UploadRawFile;

  const reader: FileReader = new FileReader();

  reader.onload = (): void => {
    model.value = reader.result?.toString() || '';
  };

  reader.readAsText(file, 'utf-8');
});
</script>

<template>
  <el-upload
    class="jsonFileUpload full-width"
    :class="{ fileSelected: fileList.length > 0 }"
    ref="upload"
    v-model:file-list="fileList"
    drag
    :limit="1"
    :on-exceed="handleExceed"
    :auto-upload="false"
    accept=".json"
  >
    <div class="row items-center" style="justify-content: center; gap: 20px;">
      <el-icon :size="48">
        <upload-filled />
      </el-icon>
      <div class="el-upload__text" style="line-height: 20px;">
        Drop file here or<br /><em>click to upload</em>
      </div>
    </div>
  </el-upload>
</template>

<!--suppress CssUnusedSymbol -->
<style>
.jsonFileUpload.fileSelected .el-upload {
  display: none;
}

.jsonFileUpload .el-upload-list {
  margin-top: 0;
}

.jsonFileUpload .el-upload-list__item .el-icon--close {
  display: inline-flex;
}
</style>
