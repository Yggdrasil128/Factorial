<script setup lang="ts">
import IconImg from '@/components/common/IconImg.vue';
import { Delete, Edit, Folder, Plus } from '@element-plus/icons-vue';
import { type EntityTreeService } from '@/services/useEntityTreeService';
import { ElButtonGroup, ElPopconfirm, ElTooltip } from 'element-plus';

export interface EntityTreeProps {
  service: EntityTreeService<any>;
  entityName: string;
}

const props: EntityTreeProps = defineProps<EntityTreeProps>();

// :allow-drop="(draggingNode, dropNode, type) => {console.log(draggingNode, dropNode, type); return true;}"
</script>

<template>
  <div>
    <el-tree
      class="tree"
      node-key="key"
      :default-expanded-keys="service.state.expandedKeysList.value"
      :data="service.state.tree.value"
      @node-expand="service.onTreeNodeExpanded"
      @node-collapse="service.onTreeNodeCollapsed"
      draggable
      @node-drag-start="console.log"
      @node-drag-end="console.log"
      @node-drop="console.log"
    >
      <!--suppress VueUnrecognizedSlot -->
      <template #default="{ node, data }">
        <div class="node" :class="{parent: data.children !== undefined, leaf: data.children === undefined}">
          <div class="icon">
            <el-icon v-if="data.children !== undefined" :size="28" style="margin: 0 2px;">
              <Folder />
            </el-icon>
            <IconImg v-else-if="data.iconId" :icon="data.iconId" :size="32" />
            <div v-else style="width: 32px;"></div>
          </div>
          <div class="name">
            {{ data.label }}
          </div>
          <div class="buttons">
            <template v-if="data.children !== undefined">
              <el-button-group style="margin-right: 8px;" @click.stop>
                <el-tooltip :content="service.getButtonTooltip('New ' + entityName)"
                            effect="dark" placement="top" transition="none" :hide-after="0">
                  <el-button type="primary" :icon="Plus"
                             :disabled="service.state.editingHasChanges.value"
                             @click="service.createNewEntityAtNode(node)" />
                </el-tooltip>
                <el-tooltip :content="service.getButtonTooltip('New folder')"
                            effect="dark" placement="top" transition="none" :hide-after="0">
                  <el-button :icon="Folder"
                             :disabled="service.state.editingHasChanges.value"
                             @click="service.createNewFolderAtNode(node)" />
                </el-tooltip>
              </el-button-group>

              <el-button-group @click.stop>
                <el-tooltip :content="service.getButtonTooltip('Edit folder name')"
                            effect="dark" placement="top" transition="none" :hide-after="0">
                  <el-button :icon="Edit"
                             :disabled="service.state.editingHasChanges.value"
                             @click="service.editFolder(node)" />
                </el-tooltip>

                <el-popconfirm
                  :title="'Delete this folder and its contents?'"
                  width="200px"
                  @confirm=""
                >
                  <template #reference>
                    <span class="row center tooltipHelperSpan">
                      <el-tooltip :content="service.getButtonTooltip('Delete folder')"
                                  effect="dark" placement="top" transition="none" :hide-after="0">
                        <el-button type="danger" :icon="Delete"
                                   :disabled="service.state.editingHasChanges.value"
                                   @click="service.deleteFolder(node)" />
                      </el-tooltip>
                    </span>
                  </template>
                </el-popconfirm>
              </el-button-group>
            </template>
            <template v-else>
              <el-button-group @click.stop>
                <el-tooltip :content="service.getButtonTooltip('Edit ' + entityName)"
                            effect="dark" placement="top" transition="none" :hide-after="0">
                  <el-button :icon="Edit"
                             :disabled="service.state.editingHasChanges.value"
                             @click="service.editEntity(data.id)" />
                </el-tooltip>

                <el-popconfirm
                  :title="'Delete this ' + entityName + '?'"
                  width="200px"
                  @confirm="service.deleteEntity(data.id)"
                >
                  <template #reference>
                    <span class="row center tooltipHelperSpan">
                      <el-tooltip :content="service.getButtonTooltip('Delete ' + entityName)"
                                  effect="dark" placement="top" transition="none" :hide-after="0">
                        <el-button type="danger" :icon="Delete"
                                   :disabled="service.state.editingHasChanges.value" />
                      </el-tooltip>
                    </span>
                  </template>
                </el-popconfirm>
              </el-button-group>
            </template>
          </div>
        </div>
      </template>
    </el-tree>

    <div class="note">
      <p>
        Drag elements to move them to a different folder.
      </p>
      <p>
        Empty folders won't be saved.
      </p>
    </div>
  </div>
</template>

<style scoped>
.tree.tree {
  background: none;
  font-size: 16px;

  --el-tree-node-content-height: 40px;
  --el-fill-color-light: #4b4b4b;
}

.node {
  height: 40px;
  display: flex;
  align-items: center;
  width: calc(100% - 24px);
  max-width: calc(100% - 24px);
}

.node .icon {
  flex: 0 0 auto;
  margin-right: 8px;
}

.node .name {
  flex: 1 1 auto;
  min-width: 0;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

.node .buttons {
  flex: 0 0 auto;
  margin-right: 8px;
  display: none;
}

.node:hover .buttons {
  display: block;
}

.note {
  margin-top: 48px;
  margin-bottom: 48px;
  font-size: 14px;
}
</style>