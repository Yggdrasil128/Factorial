<script setup lang="ts">
import type {Item, LocalResource, ProductionStep} from '@/types/model/standalone';
import IconImg from '@/components/common/IconImg.vue';
import {useItemStore} from '@/stores/model/itemStore';
import {computed, type ComputedRef, onMounted, type Ref, ref} from 'vue';
import _ from 'lodash';
import {useProductionStepStore} from '@/stores/model/productionStepStore';
import ResourceProductionStep from '@/components/factories/resources/ResourceProductionStep.vue';
import {useUserSettingsStore} from '@/stores/userSettingsStore';
import {VisibleResourceContributors} from '@/types/userSettings';
import CustomElTooltip from '@/components/common/CustomElTooltip.vue';
import {Switch} from '@element-plus/icons-vue';
import HelpPopover from '@/components/common/HelpPopover.vue';
import {useLocalResourceApi} from '@/api/model/useLocalResourceApi';
import {until} from '@vueuse/core';
import {onBeforeRouteUpdate, type RouteLocationNormalizedGeneric, useRoute, useRouter} from 'vue-router';
import QuantityByChangelistDisplay from "@/components/factories/resources/QuantityByChangelistDisplay.vue";

export interface FactoryResourceProps {
  resource: LocalResource;
}

const props: FactoryResourceProps = defineProps<FactoryResourceProps>();

const route = useRoute();
const router = useRouter();
const itemStore = useItemStore();
const productionStepStore = useProductionStepStore();
const userSettingsStore = useUserSettingsStore();
const localResourceApi = useLocalResourceApi();

const item: ComputedRef<Item> = computed(() =>
  itemStore.getById(props.resource.itemId)!,
);

const productionSteps: ComputedRef<ProductionStep[]> = computed(() => {
  let productionStepIds: number[];
  switch (userSettingsStore.visibleLocalResourceContributors) {
    case VisibleResourceContributors.None:
      productionStepIds = [];
      break;
    case VisibleResourceContributors.Producers:
      productionStepIds = _.uniq([...props.resource.producerIds]);
      break;
    case VisibleResourceContributors.Consumers:
      productionStepIds = _.uniq([...props.resource.consumerIds]);
      break;
    case VisibleResourceContributors.All:
      productionStepIds = _.uniq([...props.resource.producerIds, ...props.resource.consumerIds]);
      break;
  }

  return productionStepIds
    .map(productionStepId => productionStepStore.getById(productionStepId))
    .filter(productionStep => !!productionStep) as ProductionStep[];
});

const importExportSwitchLoading: Ref<boolean> = ref(false);

async function setResourceImportExport(value: boolean): Promise<void> {
  importExportSwitchLoading.value = true;
  try {
    await localResourceApi.edit({ id: props.resource.id, importExport: value });
    await until(computed(() => props.resource.importExport)).toBe(value, { timeout: 5000 });
  } finally {
    importExportSwitchLoading.value = false;
  }
}

function goToExportImportOverview(): void {
  const hash: string = '#item' + props.resource.itemId + '-factory' + props.resource.factoryId;
  router.push({
    name: 'factories',
    params: { factoryId: 'exportImportOverview' },
    hash: hash,
  });
}

const mainDiv = ref<HTMLDivElement>();
const id: ComputedRef<string> = computed(() => 'item' + props.resource.itemId);
const highlight: Ref<boolean> = ref(false);

function onUpdateRoute(to: RouteLocationNormalizedGeneric): void {
  if (to.hash !== ('#' + id.value)) return;

  setTimeout(() => {
    mainDiv.value?.scrollIntoView({ block: 'center', behavior: 'smooth' });
  }, 100);

  highlight.value = true;
  setTimeout(() => highlight.value = false, 800);
}

onBeforeRouteUpdate(onUpdateRoute);

onMounted(() => onUpdateRoute(route));

</script>

<template>
  <div v-if="item" class="item" :class="{highlight: highlight}" ref="mainDiv" :id="id">
    <div style="overflow: auto;">
      <div class="itemIcon">
        <icon-img :icon="item.iconId" :size="64" />
      </div>
      <div class="itemInfo">
        <div class="itemName">
          {{ item.name }}
        </div>
        <div class="itemBalance row items-center" style="gap: 16px;">
          <div class="row items-center" style="gap: 4px; margin-right: 8px;">
            <el-switch size="large" :active-icon="Switch" :inactive-icon="Switch" inline-prompt
                       :loading="importExportSwitchLoading"
                       :model-value="resource.importExport" @update:model-value="setResourceImportExport" />

            <HelpPopover>
              <b>Automatic export/import</b><br>
              When enabled, any surplus or deficit of this item is automatically exported or imported to/from other
              factories.<br>
              Go to the 'Export/Import overview' page (above the factories list) for an overview of global supply and
              demand.
              <div style="margin-top: 8px;">
                Alternatively, you can set up transport links between factories as a more fine-tuned approach.
              </div>
            </HelpPopover>
          </div>

          <div>
            <custom-el-tooltip content="The production surplus (if positive), or deficit (if negative)">
              Net:
            </custom-el-tooltip>
            <quantity-by-changelist-display :quantity="resource.overProduced"
                                            :color="resource.importExport ? undefined : 'auto'"
                                            is-throughput/>
            <span v-if="resource.importExport" class="link" @click="goToExportImportOverview">
              <template v-if="resource.overProduced.current === '0'">
                (no import/export)
              </template>
              <template v-else-if="resource.overProduced.current.startsWith('-')">
                (imported)
              </template>
              <template v-else>
                (exported)
              </template>
            </span>
          </div>

          <div>
            <custom-el-tooltip content="The amount being produced">
              Produced:
            </custom-el-tooltip>
            <quantity-by-changelist-display :quantity="resource.produced"
                                            color="green"
                                            is-throughput/>
          </div>

          <div>
            <custom-el-tooltip content="The amount being consumed">
              Consumed:
            </custom-el-tooltip>
            <quantity-by-changelist-display :quantity="resource.consumed"
                                            color="red"
                                            is-throughput/>
          </div>
        </div>
      </div>
    </div>

    <div class="column" style="margin-left: 80px;">
      <resource-production-step
        v-for="productionStep in productionSteps"
        :key="productionStep.id"
        :production-step="productionStep"
        :resource="resource"
      />
    </div>
  </div>
</template>

<style scoped>
.item {
  background-color: #4b4b4b;
  border-radius: 24px;
  padding: 8px;
  margin-bottom: 24px;
  transition: background-color 0.3s;
}

.item.highlight {
  background-color: #556e55;
}

.itemIcon {
  float: left;
}

.itemInfo {
  float: left;
  margin-left: 16px;
  margin-top: 5px;
  vertical-align: top;
  overflow: auto;
}

.itemName {
  font-size: 24px;
}

.itemBalance {
  margin-top: 4px;
}

.link:hover {
  text-decoration: underline;
  cursor: pointer;
}
</style>