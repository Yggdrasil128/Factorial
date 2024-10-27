import * as VueRouter from 'vue-router';
import { createRouter } from 'vue-router';

import HomePage from '@/components/HomePage.vue';
import AboutPage from '@/components/AboutPage.vue';
// @ts-expect-error TS7016
import FactoriesOverview from '@/components/factories/FactoriesOverview.vue';
// @ts-expect-error TS7016
import DevTools from '@/components/devtools/DevTools.vue';
// @ts-expect-error TS7016
import EditFactoryModal from '@/components/factories/EditFactoryModal.vue';
// @ts-expect-error TS7016
import EditChangelistModal from '@/components/factories/EditChangelistModal.vue';
// @ts-expect-error TS7016
import EditProductionStepModal from '@/components/factories/EditProductionStepModal.vue';

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
        },
        {
          path: 'newProductionStep',
          name: 'newProductionStep',
          components: { modal: EditProductionStepModal }
        },
        {
          path: 'editProductionStep/:editProductionStepId',
          name: 'editProductionStep',
          components: { modal: EditProductionStepModal }
        }
      ]
    },
    { path: '/devtools', component: DevTools }
  ]
});

export default router;
