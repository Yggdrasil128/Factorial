<script setup lang="ts">
import {type CurrentGameAndSaveStore, useCurrentGameAndSaveStore} from '@/stores/currentGameAndSaveStore';
import {type ChangelistStore, useChangelistStore} from '@/stores/model/changelistStore';
import {ChangelistApi, useChangelistApi} from '@/api/model/useChangelistApi';
import {type RouteLocationNormalizedLoadedGeneric, type Router, useRoute, useRouter} from 'vue-router';
import {computed, type ComputedRef, h, type Ref, ref, type VNode} from 'vue';
import type {Changelist} from '@/types/model/standalone';
import {ArrowDown, Delete, Edit, Finished, Hide, Plus, Remove, Star, View} from '@element-plus/icons-vue';
import IconImg from '@/components/common/IconImg.vue';
import {
  ElButton,
  ElButtonGroup,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElMessageBox,
  ElSwitch,
} from 'element-plus';
import {VueDraggableNext} from 'vue-draggable-next';
import {type DraggableSupport, useDraggableSupport} from '@/utils/useDraggableSupport';
import type {EntityWithOrdinal} from '@/types/model/basic';
import CustomElTooltip from '@/components/common/CustomElTooltip.vue';

const currentGameAndSaveStore: CurrentGameAndSaveStore = useCurrentGameAndSaveStore();
const changelistStore: ChangelistStore = useChangelistStore();
const changelistApi: ChangelistApi = useChangelistApi();

const router: Router = useRouter();
const route: RouteLocationNormalizedLoadedGeneric = useRoute();

const changelists: ComputedRef<Changelist[]> = computed(() => {
  return changelistStore.getBySaveId(currentGameAndSaveStore.currentSaveId)
      .sort((a: Changelist, b: Changelist) => a.ordinal - b.ordinal);
});

const draggableSupport: DraggableSupport = useDraggableSupport(changelists,
  (input: EntityWithOrdinal[]) => changelistApi.reorder(currentGameAndSaveStore.currentSaveId, input),
);

function newChangelist(): void {
  router.push({ name: 'newChangelist', params: { factoryId: route.params.factoryId } });
}

function editChangelist(changelistId: number): void {
  router.push({
    name: 'editChangelist',
    params: { factoryId: route.params.factoryId, editChangelistId: changelistId },
  });
}

function viewChangelist(changelistId: number): void {
  router.push({
    name: 'viewChangelist',
    params: { factoryId: route.params.factoryId, changelistId: changelistId },
  });
}

function clearChangelist(changelistId: number): void {
  const changelist: Changelist | undefined = changelistStore.getById(changelistId);
  ElMessageBox.confirm(
      'Are you sure you want to clear all changes in changelist \'' + changelist?.name + '\'?',
      'Warning',
      {
        confirmButtonText: 'Clear',
        cancelButtonText: 'Cancel',
        type: 'warning',
      },
  ).then(() => {
    changelistApi.clear(changelistId);
  }).catch(() => {
  });
}

function deleteChangelist(changelistId: number): void {
  const changelist: Changelist | undefined = changelistStore.getById(changelistId);
  ElMessageBox.confirm(
      'Are you sure you want to delete changelist \'' + changelist?.name + '\'?',
      'Warning',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning',
      },
  ).then(() => {
    changelistApi.delete(changelistId);
  }).catch(() => {
  });
}

function setPrimary(changelistId: number): void {
  changelistApi.setPrimary(changelistId);
}

function setActive(changelistId: number, active: boolean): void {
  changelistApi.setActive(changelistId, active);
}

async function askApplyChangelist(changelistId: number, isPrimary: boolean): Promise<void> {
  const checked: Ref<boolean> = ref(false);
  const confirm: boolean = await new Promise((r: (value: (PromiseLike<boolean> | boolean)) => void) =>
    ElMessageBox({
      title: 'Apply all changes from this changelist?',
      confirmButtonText: 'Yes',
      cancelButtonText: 'No',
      showCancelButton: true,
      message: isPrimary ? undefined : (): VNode =>
        h('div', null, [
          h(ElSwitch, {
            disabled: isPrimary,
            modelValue: checked.value,
            'onUpdate:modelValue': (val: string | number | boolean) => (checked.value = Boolean(val)),
          }),
          h('span', { style: 'margin-left: 10px;' }, 'Delete changelist afterwards'),
        ]),
    })
      .then(() => r(true))
      .catch(() => r(false)),
  );
  if (!confirm) {
    return;
  }
  await changelistApi.apply(changelistId);
  if (!isPrimary && checked.value) {
    await changelistApi.delete(changelistId);
  }
}

const dropdownMenuOpenChangelistId: Ref<number> = ref(0);
let dropdownMenuOpenResetTimeout: NodeJS.Timeout | null = null;

function onDropdownVisibleChange(changelistId: number, visible: boolean): void {
  if (dropdownMenuOpenResetTimeout) {
    clearTimeout(dropdownMenuOpenResetTimeout);
    dropdownMenuOpenResetTimeout = null;
  }

  if (visible) {
    dropdownMenuOpenChangelistId.value = changelistId;
  } else {
    dropdownMenuOpenResetTimeout = setTimeout(onDropdownMenuOpenResetTimer, 150);
  }
}

function onDropdownMenuOpenResetTimer(): void {
  dropdownMenuOpenResetTimeout = null;
  dropdownMenuOpenChangelistId.value = 0;
}

</script>

<template>
  <div class="changelists">
    <div class="full-width row items-center">
      <h2 style="flex: 1 1 auto;">Changelists ({{ changelists.length }})</h2>
      <div>
        <el-button type="primary" :icon="Plus" @click="newChangelist()">New changelist</el-button>
      </div>
    </div>

    <vue-draggable-next :model-value="changelists" @end="draggableSupport.onDragEnd">
      <div
        v-for="changelist in changelists"
        :key="changelist.id"
        class="list-group-item"
        :class="{
          active: changelist.active,
          primary: changelist.primary,
          hasIcon: !!changelist.iconId,
          forceShowButtons: dropdownMenuOpenChangelistId === changelist.id,
        }">
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
              <custom-el-tooltip content="Set active">
                <el-switch
                  v-model="changelist.active"
                  :disabled="changelist.primary"
                  :active-icon="View"
                  :inactive-icon="Hide"
                  inline-prompt
                  style="margin-right: 8px;"
                  @update:model-value="(val) => setActive(changelist.id, Boolean(val))"
                />
              </custom-el-tooltip>

              <custom-el-tooltip v-if="!changelist.primary" content="Set primary">
                <el-button style="margin-right: 8px;" :icon="Star" @click="setPrimary(changelist.id)" />
              </custom-el-tooltip>

              <el-button-group>
                <custom-el-tooltip content="Apply all changes">
                  <el-button
                      :icon="Finished"
                    @click="askApplyChangelist(changelist.id, changelist.primary)"
                  />
                </custom-el-tooltip>

                <el-dropdown class="buttonDropdown" trigger="click"
                             @visible-change="(visible: boolean) => onDropdownVisibleChange(changelist.id, visible)">
                  <el-button :icon="ArrowDown" />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :icon="View" @click="viewChangelist(changelist.id)">
                        View changes
                      </el-dropdown-item>
                      <el-dropdown-item :icon="Edit" @click="editChangelist(changelist.id)">
                        Edit
                      </el-dropdown-item>
                      <el-dropdown-item :icon="Remove" @click="clearChangelist(changelist.id)"
                                        class="destructive"
                                        :class="{disabled: changelist.productionStepChanges.length === 0}"
                                        :disabled="changelist.productionStepChanges.length === 0">
                        Clear
                      </el-dropdown-item>
                      <el-dropdown-item :icon="Delete" @click="deleteChangelist(changelist.id)"
                                        class="destructive"
                                        :class="{disabled: changelists.length === 1 || changelist.primary}"
                                        :disabled="changelists.length === 1 || changelist.primary">
                        Delete
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-button-group>
            </div>
          </div>
        </div>
      </div>
    </vue-draggable-next>
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

/*noinspection CssUnusedSymbol*/
.buttonDropdown > .el-button {
  --el-button-divide-border-color: #60606080;
  padding-left: 8px;
  padding-right: 8px;
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

.destructive {
  color: var(--el-color-danger) !important;
}

.destructive.disabled {
  color: var(--el-color-danger-light-5) !important;
}
</style>