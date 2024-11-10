<script setup lang="ts">
import { useChangelistStore } from '@/stores/model/changelistStore';
import { useFactoryStore } from '@/stores/model/factoryStore';
import { useProductionStepStore } from '@/stores/model/productionStepStore';
import { useResourceStore } from '@/stores/model/resourceStore';
import { useCurrentGameAndSaveStore } from '@/stores/currentGameAndSaveStore';
import { useIconStore } from '@/stores/model/iconStore';
import { useItemStore } from '@/stores/model/itemStore';
import { useRecipeStore } from '@/stores/model/recipeStore';
import { useRecipeModifierStore } from '@/stores/model/recipeModifierStore';
import { useMachineStore } from '@/stores/model/machineStore';

const currentGameAndSaveStore = useCurrentGameAndSaveStore();

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

  <h3>Current save id</h3>
  <pre>{{ currentGameAndSaveStore.currentSaveId }}</pre>

  <h3>Current game id</h3>
  <pre>{{ currentGameAndSaveStore.currentGameId }}</pre>

  <h3>Changelists</h3>
  <pre v-for="changelist in changelistStore.getAll()" :key="changelist.id">{{
      JSON.stringify(changelist, null, 2)
    }}</pre>

  <h3>Factories</h3>
  <pre v-for="factory in factoryStore.getAll()" :key="factory.id">{{
      JSON.stringify(factory, null, 2)
    }}</pre>

  <h3>Production steps</h3>
  <pre v-for="productionStep in productionStepStore.getAll()" :key="productionStep.id">{{
      JSON.stringify(productionStep, null, 2)
    }}</pre>

  <h3>Resources</h3>
  <pre v-for="resource in resourceStore.getAll()" :key="resource.id">{{
      JSON.stringify(resource, null, 2)
    }}</pre>

  <h3>Icons</h3>
  <pre v-for="icon in iconStore.getAll()" :key="icon.id">{{
      JSON.stringify(icon, null, 2)
    }}</pre>

  <h3>Items</h3>
  <pre v-for="item in itemStore.getAll()" :key="item.id">{{
      JSON.stringify(item, null, 2)
    }}</pre>

  <h3>Recipes</h3>
  <pre v-for="recipe in recipeStore.getAll()" :key="recipe.id">{{
      JSON.stringify(recipe, null, 2)
    }}</pre>

  <h3>Recipe modifiers</h3>
  <pre v-for="recipeModifier in recipeModifierStore.getAll()" :key="recipeModifier.id">{{
      JSON.stringify(recipeModifier, null, 2)
    }}</pre>

  <h3>Machines</h3>
  <pre v-for="machine in machineStore.getAll()" :key="machine.id">{{
      JSON.stringify(machine, null, 2)
    }}</pre>
</template>

<style scoped>

</style>