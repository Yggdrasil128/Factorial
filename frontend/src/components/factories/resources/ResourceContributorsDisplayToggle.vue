<script setup lang="ts">
import {Goods, GoodsFilled, Sell, SoldOut} from '@element-plus/icons-vue';
import {type UserSettingsStore, useUserSettingsStore} from '@/stores/userSettingsStore';
import {VisibleResourceContributors} from '@/types/userSettings';
import {type Component, computed, type ComputedRef, type Ref} from 'vue';

export interface ResourceContributorsDisplayToggleProps {
  global?: boolean;
}

const props: ResourceContributorsDisplayToggleProps = defineProps<ResourceContributorsDisplayToggleProps>();

const userSettingsStore: UserSettingsStore = useUserSettingsStore();

const model: Ref<VisibleResourceContributors> = computed({
  get(): VisibleResourceContributors {
    return props.global ? userSettingsStore.visibleGlobalResourceContributors : userSettingsStore.visibleLocalResourceContributors;
  },
  set(value: VisibleResourceContributors): void {
    if (props.global) {
      userSettingsStore.visibleGlobalResourceContributors = value;
    } else {
      userSettingsStore.visibleLocalResourceContributors = value;
    }
  },
});

interface Option {
  label: string;
  value: VisibleResourceContributors;
  icon: Component;
}

const options: ComputedRef<Option[]> = computed(() => [
  {
    label: 'None',
    value: VisibleResourceContributors.None,
    icon: Goods,
  },
  {
    label: props.global ? 'Exporters' : 'Producers',
    value: VisibleResourceContributors.Producers,
    icon: Sell,
  },
  {
    label: props.global ? 'Importers' : 'Consumers',
    value: VisibleResourceContributors.Consumers,
    icon: SoldOut,
  },
  {
    label: 'All',
    value: VisibleResourceContributors.All,
    icon: GoodsFilled,
  },
]);
</script>

<template>
  <div class="column items-center" style="gap: 4px;">
    <span v-if="global">Visible factories:</span>
    <span v-else>Visible production steps:</span>

    <el-segmented v-model="model" :options="options">
      <template #default="{ item }">
        <div>
          <el-icon size="20">
            <component :is="item.icon" />
          </el-icon>
          <div>{{ item.label }}</div>
        </div>
      </template>
    </el-segmented>
  </div>
</template>

<style scoped>

</style>