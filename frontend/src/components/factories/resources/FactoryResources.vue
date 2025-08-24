<script setup lang="ts">
import {type RouteLocationNormalizedLoadedGeneric, type Router, useRoute, useRouter} from 'vue-router';
import {type FactoryStore, useFactoryStore} from '@/stores/model/factoryStore';
import {computed, type ComputedRef} from 'vue';
import type {Factory, LocalResource} from '@/types/model/standalone';
import {type LocalResourceStore, useLocalResourceStore} from '@/stores/model/localResourceStore';
import {ElDropdown, ElDropdownItem, ElDropdownMenu, ElIcon} from 'element-plus';
import {VueDraggableNext} from 'vue-draggable-next';
import {Plus} from '@element-plus/icons-vue';
import FactoryResource from '@/components/factories/resources/FactoryResource.vue';
import {type LocalResourceApi, useLocalResourceApi} from '@/api/model/useLocalResourceApi';
import {type DraggableSupport, useDraggableSupport} from '@/utils/useDraggableSupport';
import type {EntityWithOrdinal} from '@/types/model/basic';
import ProductionsStepsDisplayChooser from '@/components/factories/resources/ResourceContributorsDisplayToggle.vue';
import IconImg from '@/components/common/IconImg.vue';
import PlaceholderHelpBox from '@/components/common/PlaceholderHelpBox.vue';

export interface FactoryResourcesProps {
  factoryId: number;
}

const props: FactoryResourcesProps = defineProps<FactoryResourcesProps>();

const router: Router = useRouter();
const route: RouteLocationNormalizedLoadedGeneric = useRoute();

const factoryStore: FactoryStore = useFactoryStore();
const localResourceStore: LocalResourceStore = useLocalResourceStore();
const localResourceApi: LocalResourceApi = useLocalResourceApi();

const rr: string = 'aHR0cHM6Ly93d3cueW91dHViZS5jb20vd2F0Y2g/dj1kUXc0dzlXZ1hjUQ==';

const factory: ComputedRef<Factory> = computed(() =>
  factoryStore.getById(props.factoryId)!,
);

const resources: ComputedRef<LocalResource[]> = computed(() =>
  localResourceStore.getByFactoryId(props.factoryId)
      .sort((a: LocalResource, b: LocalResource) => a.ordinal - b.ordinal),
);

const draggableSupport: DraggableSupport = useDraggableSupport(resources,
  (input: EntityWithOrdinal[]) => localResourceApi.reorder(props.factoryId, input),
);

function newProductionStep(): void {
  router.push({ name: 'newProductionStep', params: { factoryId: route.params.factoryId } });
}

function newTransportLink(): void {
  window.open(atob(rr), '_blank')!.focus();
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
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <PlaceholderHelpBox v-if="resources.length === 0" show-only-when-ready
                        title="This factory is empty.">
      <p>
        <el-link type="primary" @click="newProductionStep">
          Create a production step
        </el-link>
        to get started.
      </p>
      <p>
        Factories allow you to group production steps. They also accumulate their inputs and outputs,
        showing you how much of each item is produced and consumed.
      </p>
      <p>
        A production step is a group of identical machines that are all processing the same recipe.
      </p>
    </PlaceholderHelpBox>

    <vue-draggable-next v-else :model-value="resources" @end="draggableSupport.onDragEnd">
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