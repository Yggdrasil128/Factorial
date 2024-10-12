<script setup lang="js">
import { h, inject, onMounted, onUnmounted, ref } from 'vue';
import draggable from 'vuedraggable';
import { Check, Delete, Edit, Plus, Star } from '@element-plus/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessageBox, ElSwitch } from 'element-plus';
import IconImg from '@/components/IconImg.vue';

const router = useRouter();
const route = useRoute();

const axios = inject('axios');
const globalEventBus = inject('globalEventBus');

const changelists = ref([]);

async function loadChangelists() {
  const response = await axios.get('api/save/changelists', { params: { saveId: 1 } });
  changelists.value = response.data;
}

loadChangelists();

async function setPrimary(changelistId) {
  await axios.patch('/api/changelist/primary', null, { params: { changelistId: changelistId } });

  for (const changelist of changelists.value) {
    if (changelist.id === changelistId) {
      changelist.primary = true;
      changelist.active = true;
    } else {
      changelist.primary = false;
    }
  }

  globalEventBus.emit('reloadFactoryData');
}

async function setActive(changelistId, active) {
  await axios.patch('/api/changelist/active', null, {
    params: { changelistId: changelistId, active: active }
  });

  globalEventBus.emit('reloadFactoryData');
}

async function askApplyChangelist(changelistId, primary) {
  const checked = ref(!primary);
  const confirm = await new Promise((r) =>
    ElMessageBox({
      title: 'Apply all changes from this changelist?',
      confirmButtonText: 'Yes',
      cancelButtonText: 'No',
      showCancelButton: true,
      customClass: 'el-dark',
      message: () =>
        h('div', null, [
          h(ElSwitch, {
            disabled: primary,
            modelValue: checked.value,
            'onUpdate:modelValue': (val) => (checked.value = val)
          }),
          h('span', { style: 'margin-left: 10px;' }, 'Delete changelist afterwards')
        ])
    })
      .then(() => r(true))
      .catch(() => r(false))
  );
  if (!confirm) {
    return;
  }
  console.log('applyChangelist', checked.value, changelistId);
}

async function deleteChangelist(changelistId) {
  await axios.delete('/api/changelist', { params: { changelistId: changelistId } });

  globalEventBus.emit('reloadFactoryData');

  await loadChangelists();
}

function onUpdateChangelists() {
  loadChangelists();
}

onMounted(() => {
  globalEventBus.on('updateChangelists', onUpdateChangelists);
});
onUnmounted(() => {
  globalEventBus.off('updateChangelists', onUpdateChangelists);
});

function newChangelist() {
  router.push({ name: 'newChangelist', params: { factoryId: route.params.factoryId } });
}

function editChangelist(changelistId) {
  router.push({
    name: 'editChangelist',
    params: { factoryId: route.params.factoryId, editChangelistId: changelistId }
  });
}
</script>

<template>
  <div class="changelists">
    <h2>Changelists</h2>
    <draggable :list="changelists" item-key="id">
      <!--suppress VueUnrecognizedSlot -->
      <template #item="{ element }">
        <div
          class="list-group-item"
          :class="{ active: element.active, primary: element.primary, hasIcon: !!element.icon }"
        >
          <div class="left">
            <div class="icon" v-if="element.icon">
              <icon-img :icon="element.icon" :size="80" v-if="element.icon" />
            </div>
            <div class="name" v-if="!element.icon">
              {{ element.name }}
            </div>
          </div>
          <div class="right">
            <div class="name" v-if="element.icon">
              {{ element.name }}
            </div>
            <div class="buttons">
              <div style="float: right">
                <el-tooltip
                  effect="dark"
                  placement="top-start"
                  transition="none"
                  :hide-after="0"
                  content="Set active"
                >
                  <el-switch
                    v-model="element.active"
                    :disabled="element.primary"
                    style="margin-right: 8px"
                    @update:model-value="(val) => setActive(element.id, val)"
                  />
                </el-tooltip>

                <el-button-group>
                  <el-tooltip
                    effect="dark"
                    placement="top-start"
                    transition="none"
                    :hide-after="0"
                    content="Set primary"
                    v-if="!element.primary"
                  >
                    <el-button :icon="Star" @click="setPrimary(element.id)" />
                  </el-tooltip>
                  <el-tooltip
                    effect="dark"
                    placement="top-start"
                    transition="none"
                    :hide-after="0"
                    content="Apply all changes"
                  >
                    <el-button
                      :icon="Check"
                      @click="askApplyChangelist(element.id, element.primary)"
                    />
                  </el-tooltip>
                  <el-tooltip
                    effect="dark"
                    placement="top-start"
                    transition="none"
                    :hide-after="0"
                    content="Edit"
                  >
                    <el-button :icon="Edit" @click="editChangelist(element.id)" />
                  </el-tooltip>
                  <el-popconfirm
                    title="Delete this changelist?"
                    width="200px"
                    @confirm="deleteChangelist(element.id)"
                  >
                    <template #reference>
                      <span class="row center tooltipHelperSpan">
                        <el-tooltip
                          effect="dark"
                          placement="top-start"
                          transition="none"
                          :hide-after="0"
                          content="Delete"
                        >
                          <el-button
                            type="danger"
                            :icon="Delete"
                            :disabled="changelists.length === 1 || element.primary"
                          />
                        </el-tooltip>
                      </span>
                    </template>
                  </el-popconfirm>
                </el-button-group>
              </div>
            </div>
          </div>
        </div>
      </template>
    </draggable>

    <div class="createChangelist">
      <el-button type="primary" :icon="Plus" @click="newChangelist()">Create changelist</el-button>
    </div>
  </div>
</template>

<!--suppress CssUnusedSymbol -->
<style scoped>
.list-group-item {
  width: 100%;
  overflow: hidden;
  font-size: 20px;
  background-color: #4b4b4b;
  border-radius: 8px;
}

.list-group-item.active {
  background-color: #555555;
}

.list-group-item.primary {
  background-color: #556e55;
}

.list-group-item + .list-group-item {
  margin-top: 10px;
}

.left {
  float: left;
}

.right {
  float: right;
}

.hasIcon .left {
  width: 80px;
}

.hasIcon .right {
  width: calc(100% - 80px - 16px);
}

.icon,
.name {
  margin-left: 8px;
}

.list-group-item:not(.hasIcon) .name {
  margin-left: 16px;
}

.icon,
.icon img {
  width: 80px;
  height: 80px;
  cursor: grab;
}

.name {
  padding-top: 8px;
  padding-bottom: 8px;
}

.primary .name {
  font-weight: bold;
}

.buttons {
  margin-top: 4px;
  margin-right: 8px;
  margin-bottom: 4px;
  overflow: auto;
}

.createChangelist {
  width: 100%;
  margin-top: 8px;
  display: flex;
  justify-content: center;
}
</style>

<style>
.tooltipHelperSpan button {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-right-color: var(--el-color-danger) !important;
}

.tooltipHelperSpan button[disabled] {
  border-right-color: var(--el-color-danger-light-5) !important;
}

.tooltipHelperSpan button:hover {
  border-right-color: var(--el-color-danger-light-3) !important;
}

.tooltipHelperSpan button:focus {
  border-right-color: var(--el-color-danger-light-3) !important;
}

.tooltipHelperSpan button:active {
  border-right-color: var(--el-color-danger-dark-2) !important;
}
</style>
