<script setup lang="ts">
import {type ChangelistStore, useChangelistStore} from '@/stores/model/changelistStore';
import {type FactoryStore, useFactoryStore} from '@/stores/model/factoryStore';
import {type ProductionStepStore, useProductionStepStore} from '@/stores/model/productionStepStore';
import {type LocalResourceStore, useLocalResourceStore} from '@/stores/model/localResourceStore';
import {type CurrentGameAndSaveStore, useCurrentGameAndSaveStore} from '@/stores/currentGameAndSaveStore';
import {type IconStore, useIconStore} from '@/stores/model/iconStore';
import {type ItemStore, useItemStore} from '@/stores/model/itemStore';
import {type RecipeStore, useRecipeStore} from '@/stores/model/recipeStore';
import {type RecipeModifierStore, useRecipeModifierStore} from '@/stores/model/recipeModifierStore';
import {type MachineStore, useMachineStore} from '@/stores/model/machineStore';
import {type GlobalResourceStore, useGlobalResourceStore} from '@/stores/model/globalResourceStore';

const currentGameAndSaveStore: CurrentGameAndSaveStore = useCurrentGameAndSaveStore();

const changelistStore: ChangelistStore = useChangelistStore();
const factoryStore: FactoryStore = useFactoryStore();
const productionStepStore: ProductionStepStore = useProductionStepStore();
const localResourceStore: LocalResourceStore = useLocalResourceStore();
const globalResourceStore: GlobalResourceStore = useGlobalResourceStore();
const iconStore: IconStore = useIconStore();
const itemStore: ItemStore = useItemStore();
const recipeStore: RecipeStore = useRecipeStore();
const recipeModifierStore: RecipeModifierStore = useRecipeModifierStore();
const machineStore: MachineStore = useMachineStore();
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

  <h3>Local resources</h3>
  <pre v-for="resource in localResourceStore.getAll()" :key="resource.id">{{
      JSON.stringify(resource, null, 2)
    }}</pre>

  <h3>Global resources</h3>
  <pre v-for="resource in globalResourceStore.getAll()" :key="resource.id">{{
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