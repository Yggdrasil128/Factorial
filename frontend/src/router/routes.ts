import * as VueRouter from 'vue-router';
import { createRouter } from 'vue-router';

import HomePage from '@/components/HomePage.vue';
import AboutPage from '@/components/AboutPage.vue';
import DevTools from '@/components/devtools/DevTools.vue';

import FactoriesOverview from '@/components/factories/FactoriesOverview.vue';
import EditFactoryModal from '@/components/factories/modal/EditFactoryModal.vue';
import EditChangelistModal from '@/components/factories/modal/EditChangelistModal.vue';

const router = createRouter({
  history: VueRouter.createWebHashHistory(),
  routes: [
    { path: '/', component: HomePage },
    { path: '/about', component: AboutPage },
    {
      path: '/factories/:factoryId?',
      name: 'factories',
      component: FactoriesOverview,
      children: [
        {
          path: 'new',
          name: 'newFactory',
          components: { modal: EditFactoryModal }
        },
        {
          path: 'edit/:editFactoryId',
          name: 'editFactory',
          components: { modal: EditFactoryModal }
        },
        {
          path: 'newChangelist',
          name: 'newChangelist',
          components: { modal: EditChangelistModal }
        },
        {
          path: 'editChangelist/:editChangelistId',
          name: 'editChangelist',
          components: { modal: EditChangelistModal }
        }
      ]
    },
    { path: '/devtools', component: DevTools }
  ]
});

export default router;
