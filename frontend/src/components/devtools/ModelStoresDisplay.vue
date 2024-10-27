<script setup lang="ts">
import { useCurrentSaveStore } from '@/stores/currentSaveStore';
import { useChangelistStore } from '@/stores/model/changelistStore';
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useProductionStepStore } from '@/stores/model/productionStepStore';
import { useResourceStore } from '@/stores/model/resourceStore';
import { useCurrentGameVersionStore } from '@/stores/currentGameVersionStore';
import { useIconStore } from '@/stores/model/iconStore';
import { useItemStore } from '@/stores/model/itemStore';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import { useMachineStore } from '@/stores/model/machineStore';

const currentSaveStore = useCurrentSaveStore();
const currentGameVersionStore = useCurrentGameVersionStore();

const changelistStore = useChangelistStore();
const factoryStore = useFactoryStore();
const productionStepStore = useProductionStepStore();
const resourceStore = useResourceStore();
const iconStore = useIconStore();
const itemStore = useItemStore();
const recipeStore = useRecipeStore();
const recipeModifierStore = useRecipeModifierStore();
const machineStore = useMachineStore();
</script>

<template>
  <h2>Model Stores:</h2>

  <h3>Current save</h3>
  <span v-if="!currentSaveStore.save">(none)</span>
  <pre v-else>{{ JSON.stringify(currentSaveStore.save, null, 2) }}</pre>

  <h3>Current game version</h3>
  <span v-if="!currentGameVersionStore.gameVersion">(none)</span>
  <pre v-else>{{ JSON.stringify(currentGameVersionStore.gameVersion, null, 2) }}</pre>

  <h3>Changelists</h3>
  <pre v-for="changelist in changelistStore.map.values()" :key="changelist.id">{{
      JSON.stringify(changelist, null, 2)
    }}</pre>

  <h3>Factories</h3>
  <pre v-for="factory in factoryStore.map.values()" :key="factory.id">{{
      JSON.stringify(factory, null, 2)
    }}</pre>

  <h3>Production steps</h3>
  <pre v-for="productionStep in productionStepStore.map.values()" :key="productionStep.id">{{
      JSON.stringify(productionStep, null, 2)
    }}</pre>

  <h3>Resources</h3>
  <pre v-for="resource in resourceStore.map.values()" :key="resource.id">{{
      JSON.stringify(resource, null, 2)
    }}</pre>

  <h3>Icons</h3>
  <pre v-for="icon in iconStore.map.values()" :key="icon.id">{{
      JSON.stringify(icon, null, 2)
    }}</pre>

  <h3>Items</h3>
  <pre v-for="item in itemStore.map.values()" :key="item.id">{{
      JSON.stringify(item, null, 2)
    }}</pre>

  <h3>Recipes</h3>
  <pre v-for="recipe in recipeStore.map.values()" :key="recipe.id">{{
      JSON.stringify(recipe, null, 2)
    }}</pre>

  <h3>Recipe modifiers</h3>
  <pre v-for="recipeModifier in recipeModifierStore.map.values()" :key="recipeModifier.id">{{
      JSON.stringify(recipeModifier, null, 2)
    }}</pre>

  <h3>Machines</h3>
  <pre v-for="machine in machineStore.map.values()" :key="machine.id">{{
      JSON.stringify(machine, null, 2)
    }}</pre>
</template>

<style scoped>

</style>