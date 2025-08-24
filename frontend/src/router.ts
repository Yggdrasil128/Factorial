import * as VueRouter from 'vue-router';
import {createRouter} from 'vue-router';

import HomePage from '@/components/HomePage.vue';
import AboutPage from '@/components/AboutPage.vue';
import DevTools from '@/components/devtools/DevTools.vue';

import SavesAndGames from '@/components/savesAndGames/SavesAndGames.vue';

import FactoriesOverview from '@/components/factories/FactoriesOverview.vue';
import EditFactoryModal from '@/components/factories/modal/EditFactoryModal.vue';
import EditChangelistModal from '@/components/factories/modal/EditChangelistModal.vue';
import EditProductionStepModal from '@/components/factories/modal/EditProductionStepModal.vue';
import GameEditor from '@/components/gameEditor/GameEditor.vue';
import SettingsPage from '@/components/settings/SettingsPage.vue';
import EditSaveModal from '@/components/savesAndGames/modal/EditSaveModal.vue';
import MigrateSaveModal from '@/components/savesAndGames/modal/MigrateSaveModal.vue';
import EditGameModal from '@/components/savesAndGames/modal/EditGameModal.vue';
import ImportSaveModal from '@/components/savesAndGames/modal/ImportSaveModal.vue';
import ImportGameModal from '@/components/savesAndGames/modal/ImportGameModal.vue';
import ViewChangelistModal from '@/components/factories/modal/ViewChangelistModal.vue';
import OptimizeProductionStepModal from "@/components/factories/modal/OptimizeProductionStepModal.vue";

const router = createRouter({
  history: VueRouter.createWebHashHistory(),
  routes: [
    { path: '/', component: HomePage },
    { path: '/about', component: AboutPage },
    {
      path: '/savesAndGames',
      name: 'savesAndGames',
      component: SavesAndGames,
      children: [
        {
          path: 'newSave',
          name: 'newSave',
          components: { modal: EditSaveModal },
        },
        {
          path: 'editSave/:saveId',
          name: 'editSave',
          components: { modal: EditSaveModal },
        },
        {
          path: 'migrateSave/:saveId',
          name: 'migrateSave',
          components: { modal: MigrateSaveModal },
        },
        {
          path: 'importSave',
          name: 'importSave',
          components: { modal: ImportSaveModal },
        },
        {
          path: 'newGame',
          name: 'newGame',
          components: { modal: EditGameModal },
        },
        {
          path: 'editGame/:gameId',
          name: 'editGame',
          components: { modal: EditGameModal },
        },
        {
          path: 'importGame',
          name: 'importGame',
          components: { modal: ImportGameModal },
        },
      ],
    },
    {
      path: '/gameEditor/:editGameId/:tab',
      name: 'gameEditor',
      component: GameEditor,
    },
    {
      path: '/factories/:factoryId?',
      name: 'factories',
      component: FactoriesOverview,
      children: [
        {
          path: 'new',
          name: 'newFactory',
          components: { modal: EditFactoryModal },
        },
        {
          path: 'edit/:editFactoryId',
          name: 'editFactory',
          components: { modal: EditFactoryModal },
        },
        {
          path: 'newChangelist',
          name: 'newChangelist',
          components: { modal: EditChangelistModal },
        },
        {
          path: 'editChangelist/:editChangelistId',
          name: 'editChangelist',
          components: { modal: EditChangelistModal },
        },
        {
          path: 'viewChangelist/:changelistId',
          name: 'viewChangelist',
          components: { modal: ViewChangelistModal },
        },
        {
          path: 'newProductionStep',
          name: 'newProductionStep',
          components: { modal: EditProductionStepModal },
        },
        {
          path: 'editProductionStep/:editProductionStepId',
          name: 'editProductionStep',
          components: { modal: EditProductionStepModal },
        },
        {
          path: 'optimizeProductionStep/:productionStepId/resource/:resourceId',
          name: 'optimizeProductionStep',
          components: {modal: OptimizeProductionStepModal},
        },
      ],
    },
    { path: '/settings', component: SettingsPage },
    { path: '/devtools', component: DevTools },
  ],
});

export default router;
