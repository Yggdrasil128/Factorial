<script setup lang="ts">
import type { EntityUsages } from '@/services/useEntityUsagesService';
import { useGameStore } from '@/stores/model/gameStore';
import { useSaveStore } from '@/stores/model/saveStore';
import { useFactoryStore } from '@/stores/model/factoryStore';

export interface EntityUsagesListProps {
  entityUsages: EntityUsages;
}

defineProps<EntityUsagesListProps>();

const gameStore = useGameStore();
const saveStore = useSaveStore();
const factoryStore = useFactoryStore();
</script>

<template>
  <ul>
    <li v-for="game in entityUsages.games" :key="game.id">
      Game '{{ game.name }}'
    </li>
    <li v-for="item in entityUsages.items" :key="item.id">
      Item '{{ item.name }}',
      in game '{{ gameStore.getById(item.gameId)?.name }}'
    </li>
    <li v-for="machine in entityUsages.machines" :key="machine.id">
      Machine '{{ machine.name }}',
      in game '{{ gameStore.getById(machine.gameId)?.name }}'
    </li>
    <li v-for="recipe in entityUsages.recipes" :key="recipe.id">
      Recipe '{{ recipe.name }}',
      in game '{{ gameStore.getById(recipe.gameId)?.name }}'
    </li>
    <li v-for="recipeModifier in entityUsages.recipeModifiers" :key="recipeModifier.id">
      Recipe modifier '{{ recipeModifier.name }}',
      in game '{{ gameStore.getById(recipeModifier.gameId)?.name }}'
    </li>
    <li v-for="icon in entityUsages.icons" :key="icon.id">
      Icon '{{ icon.name }}',
      in game '{{ gameStore.getById(icon.gameId)?.name }}'
    </li>

    <li v-for="save in entityUsages.saves" :key="save.id">
      Save '{{ save.name }}'
    </li>
    <li v-for="factory in entityUsages.factories" :key="factory.id">
      Factory '{{ factory.name }}',
      in save '{{ saveStore.getById(factory.saveId)?.name }}'
    </li>
    <li v-for="changelist in entityUsages.changelists" :key="changelist.id">
      Changelist '{{ changelist.name }}',
      in save '{{ saveStore.getById(changelist.saveId)?.name }}'
    </li>
    <li v-for="productionStep in entityUsages.productionSteps" :key="productionStep.id">
      Production step,
      in factory '{{ factoryStore.getById(productionStep.factoryId)?.name }}}',
      in save '{{ saveStore.getById(productionStep.saveId)?.name }}'
    </li>
    <li v-for="resource in entityUsages.resources" :key="resource.id">
      Resource,
      in factory '{{ factoryStore.getById(resource.factoryId)?.name }}}',
      in save '{{ saveStore.getById(resource.saveId)?.name }}'
    </li>
  </ul>
</template>

<style scoped>

</style>