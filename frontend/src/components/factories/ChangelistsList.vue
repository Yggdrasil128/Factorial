<script setup lang="ts">
import { useCurrentSaveStore } from '@/stores/currentSaveStore';
import { useChangelistStore } from '@/stores/model/changelistStore';
import { useChangelistApi } from '@/api/useChangelistApi';
import { useRoute, useRouter } from 'vue-router';
import { computed, type ComputedRef, h, type Ref, ref } from 'vue';
import type { Changelist } from '@/types/model/standalone';
import { Check, Delete, Edit, Plus, Star } from '@element-plus/icons-vue';
import IconImg from '@/components/IconImg.vue';
import { ElButton, ElButtonGroup, ElMessageBox, ElPopconfirm, ElSwitch, ElTooltip } from 'element-plus';
import { VueDraggableNext } from 'vue-draggable-next';
import { type DraggableSupport, useDraggableSupport } from '@/utils/useDraggableSupport';
import type { EntityWithOrdinal } from '@/types/model/basic';
import BgcElButton from '@/components/input/BgcElButton.vue';

const currentSaveStore = useCurrentSaveStore();
const changelistStore = useChangelistStore();
const changelistApi = useChangelistApi();

const router = useRouter();
const route = useRoute();

const changelists: ComputedRef<Changelist[]> = computed(() => {
  return [...changelistStore.map.values()]
    .filter(changelist => changelist.saveId === currentSaveStore.save?.id)
    .sort((a, b) => a.ordinal - b.ordinal);
});

const draggableSupport: DraggableSupport = useDraggableSupport(changelists,
  (input: EntityWithOrdinal[]) => changelistApi.reorder(currentSaveStore.save!.id, input)
);

function newChangelist(): void {
  router.push({ name: 'newChangelist', params: { factoryId: route.params.factoryId } });
}

function editChangelist(changelistId: number): void {
  router.push({
    name: 'editChangelist',
    params: { factoryId: route.params.factoryId, editChangelistId: changelistId }
  });
}

function deleteChangelist(changelistId: number): void {
  changelistApi.deleteChangelist(changelistId);
}

function setPrimary(changelistId: number): void {
  changelistApi.setPrimary(changelistId);
}

function setActive(changelistId: number, active: boolean): void {
  changelistApi.setActive(changelistId, active);
}

async function askApplyChangelist(changelistId: number, isPrimary: boolean): Promise<void> {
  const checked: Ref<boolean> = ref(false);
  const confirm = await new Promise((r) =>
    ElMessageBox({
      title: 'Apply all changes from this changelist?',
      confirmButtonText: 'Yes',
      cancelButtonText: 'No',
      showCancelButton: true,
      message: isPrimary ? undefined : () =>
        h('div', null, [
          h(ElSwitch, {
            disabled: isPrimary,
            modelValue: checked.value,
            'onUpdate:modelValue': (val) => (checked.value = Boolean(val))
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
  await changelistApi.apply(changelistId);
  if (!isPrimary && checked.value) {
    await changelistApi.deleteChangelist(changelistId);
  }
}
</script>

<template>
  <div class="changelists">
    <h2>Changelists</h2>
    <vue-draggable-next :model-value="changelists" @end="draggableSupport.onDragEnd">
      <div
        v-for="changelist in changelists"
        :key="changelist.id"
        class="list-group-item"
        :class="{ active: changelist.active, primary: changelist.primary, hasIcon: !!changelist.iconId }"
      >
        <div class="left">
          <div class="icon" v-if="changelist.iconId">
            <icon-img :icon="changelist.iconId" :size="80" v-if="changelist.iconId" />
          </div>
          <div class="name" v-if="!changelist.iconId">
            {{ changelist.name }}
          </div>
        </div>
        <div class="right">
          <div class="name" v-if="changelist.iconId">
            {{ changelist.name }}
          </div>
          <div class="buttons">
            <div style="float: right;">
              <el-tooltip
                effect="dark"
                placement="top-start"
                transition="none"
                :hide-after="0"
                content="Set active"
              >
                <el-switch
                  v-model="changelist.active"
                  :disabled="changelist.primary"
                  style="margin-right: 8px;"
                  @update:model-value="(val) => setActive(changelist.id, Boolean(val))"
                />
              </el-tooltip>

              <el-button-group>
                <el-tooltip
                  effect="dark"
                  placement="top-start"
                  transition="none"
                  :hide-after="0"
                  content="Set primary"
                  v-if="!changelist.primary"
                >
                  <bgc-el-button :icon="Star" @click="setPrimary(changelist.id)" />
                </el-tooltip>
                <el-tooltip
                  effect="dark"
                  placement="top-start"
                  transition="none"
                  :hide-after="0"
                  content="Apply all changes"
                >
                  <bgc-el-button
                    :icon="Check"
                    @click="askApplyChangelist(changelist.id, changelist.primary)"
                  />
                </el-tooltip>
                <el-tooltip
                  effect="dark"
                  placement="top-start"
                  transition="none"
                  :hide-after="0"
                  content="Edit"
                >
                  <bgc-el-button :icon="Edit" @click="editChangelist(changelist.id)" />
                </el-tooltip>
                <el-popconfirm
                  title="Delete this changelist?"
                  width="200px"
                  @confirm="deleteChangelist(changelist.id)"
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
                          :disabled="changelists.length === 1 || changelist.primary"
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
    </vue-draggable-next>

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