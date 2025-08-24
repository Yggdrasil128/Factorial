<script setup lang="ts">
import type {Factory, Item, ProductionEntry} from '@/types/model/standalone';
import IconImg from '@/components/common/IconImg.vue';
import {computed, type ComputedRef, onMounted, type Ref, ref} from 'vue';
import {onBeforeRouteUpdate, type RouteLocationNormalizedGeneric, useRoute, useRouter} from 'vue-router';
import QuantityByChangelistDisplay from "@/components/factories/resources/QuantityByChangelistDisplay.vue";
import FractionDisplay from "@/components/common/FractionDisplay.vue";

export interface SaveResourceFactoryProps {
  factory: Factory;
  item: Item;
}

const props: SaveResourceFactoryProps = defineProps<SaveResourceFactoryProps>();

const route = useRoute();
const router = useRouter();

const imports: ComputedRef<ProductionEntry[]> = computed(() =>
  props.factory.inputs.filter(entry => entry.itemId === props.item.id),
);

const exports: ComputedRef<ProductionEntry[]> = computed(() =>
  props.factory.outputs.filter(entry => entry.itemId === props.item.id),
);

function goToFactory(): void {
  router.push({
    name: 'factories',
    params: { factoryId: props.factory.id },
  });
}

function goToFactoryItem(): void {
  router.push({
    name: 'factories',
    params: { factoryId: props.factory.id },
    hash: '#item' + props.item.id,
  });
}

const mainDiv = ref<HTMLDivElement>();
const id: ComputedRef<string> = computed(() =>
  'item' + props.item.id + '-factory' + props.factory.id,
);
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
  <div class="factory row" :class="{highlight: highlight}" :id="id" ref="mainDiv">
    <IconImg :icon="factory.iconId" :size="40" />
    <div style="margin-left: 4px;">
      <div style="margin-bottom: 4px;">
        <span class="factoryName link" @click="goToFactory">{{ factory.name }}</span>
      </div>
      <div v-for="(input, index) in imports" :key="index" class="row items-center">
        Imports
        <quantity-by-changelist-display :quantity="input.quantity" color="red" is-throughput hide-throughput-unit/>
        <icon-img :icon="item.iconId" :size="24" />
        <span class="link" @click="goToFactoryItem">{{ item.name }}</span>
        <fraction-display :fraction="undefined" is-throughput/>
      </div>
      <div v-for="(output, index) in exports" :key="index" class="row items-center">
        Exports
        <quantity-by-changelist-display :quantity="output.quantity" color="green" is-throughput hide-throughput-unit/>
        <icon-img :icon="item.iconId" :size="24" />
        <span class="link" @click="goToFactoryItem">{{ item.name }}</span>
        <fraction-display :fraction="undefined" is-throughput/>
      </div>
    </div>
  </div>
</template>

<style scoped>
.factory {
  background-color: #555555;
  border-radius: 16px;
  padding: 8px;
  transition: background-color 0.3s;
}

.factory.highlight {
  background-color: #556e55;
}

.factoryName {
  font-weight: bold;
}

.link:hover {
  text-decoration: underline;
  cursor: pointer;
}
</style>