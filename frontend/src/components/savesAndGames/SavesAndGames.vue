<script setup lang="ts">
import SavesList from '@/components/savesAndGames/SavesList.vue';
import GamesList from '@/components/savesAndGames/GamesList.vue';
</script>

<template>
  <div class="container">
    <div class="saves">
      <SavesList />
    </div>
    <div class="games">
      <GamesList />
    </div>
  </div>

  <router-view name="modal" />
</template>

<style scoped>
.container {
  display: flex;
  gap: 4%;
}

.saves, .games {
  width: 48%;
  flex: 0 0 auto;
}
</style>