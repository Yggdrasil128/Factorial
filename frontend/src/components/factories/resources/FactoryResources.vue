<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { useFactoryStore } from '@/stores/model/factoryStore';
import { computed, type ComputedRef } from 'vue';
import type { Factory, LocalResource } from '@/types/model/standalone';
import { useLocalResourceStore } from '@/stores/model/localResourceStore';
import { ElDropdown, ElDropdownItem, ElDropdownMenu, ElIcon } from 'element-plus';
import { VueDraggableNext } from 'vue-draggable-next';
import { Plus } from '@element-plus/icons-vue';
import FactoryResource from '@/components/factories/resources/FactoryResource.vue';
import { useLocalResourceApi } from '@/api/model/useLocalResourceApi';
import { type DraggableSupport, useDraggableSupport } from '@/utils/useDraggableSupport';
import type { EntityWithOrdinal } from '@/types/model/basic';
import ProductionsStepsDisplayChooser from '@/components/factories/resources/ResourceContributorsDisplayToggle.vue';
import IconImg from '@/components/common/IconImg.vue';

export interface FactoryResourcesProps {
  factoryId: number;
}

const props: FactoryResourcesProps = defineProps<FactoryResourcesProps>();

const route = useRoute();
const router = useRouter();

const factoryStore = useFactoryStore();
const localResourceStore = useLocalResourceStore();
const localResourceApi = useLocalResourceApi();

const factory: ComputedRef<Factory> = computed(() =>
  factoryStore.getById(props.factoryId)!
);

const resources: ComputedRef<LocalResource[]> = computed(() =>
  localResourceStore.getByFactoryId(props.factoryId)
    .sort((a, b) => a.ordinal - b.ordinal)
);

const draggableSupport: DraggableSupport = useDraggableSupport(resources,
  (input: EntityWithOrdinal[]) => localResourceApi.reorder(props.factoryId, input),
);

function newProductionStep() {
  router.push({ name: 'newProductionStep', params: { factoryId: route.params.factoryId } });
}

function newTransportLink() {
}

function newIngress() {
}

function newEgress() {
}

</script>

<template>
  <template v-if="factory">
    <div class="row items-center full-width" style="gap: 24px; margin-bottom: 12px;">
      <IconImg :icon="factory.iconId" :size="48" style="margin-right: -16px;" />
      <div class="" style="min-width: 0; flex: 1 1 auto;">
        <h2 class="ellipsis">{{ factory.name }}</h2>
      </div>
      <div style="flex: 0 0 auto;">
        <ProductionsStepsDisplayChooser />
      </div>
      <div style="flex: 0 0 auto;">
        <el-dropdown split-button type="primary" @click="newProductionStep">
          <el-icon class="el-icon--left">
            <plus />
          </el-icon>
          New production step
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="newTransportLink">New transport link</el-dropdown-item>
              <el-dropdown-item @click="newIngress">New ingress</el-dropdown-item>
              <el-dropdown-item @click="newEgress">New egress</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <vue-draggable-next :model-value="resources" @end="draggableSupport.onDragEnd">
      <factory-resource
        v-for="resource in resources"
        :key="resource.id"
        :resource="resource"
      />
    </vue-draggable-next>
  </template>
</template>

<style scoped>

</style>