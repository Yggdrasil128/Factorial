import * as VueRouter from 'vue-router';
import { createRouter } from 'vue-router';

import HomePage from '@/components/HomePage.vue';
import AboutPage from '@/components/AboutPage.vue';
import FactoriesOverview from '@/components/factories/FactoriesOverview.vue';
import DevTools from '@/components/devtools/DevTools.vue';
// @ts-expect-error TS7016
import EditFactoryModal from '@/components/factories/EditFactoryModal.vue';
// @ts-expect-error TS7016
import EditChangelistModal from '@/components/factories/EditChangelistModal.vue';
// @ts-expect-error TS7016
import EditProductionStepModal from '@/components/factories/EditProductionStepModal.vue';

import FactoriesOverview2 from '@/components/factories2/FactoriesOverview.vue';
import EditFactoryModal2 from '@/components/factories2/EditFactoryModal.vue';
import EditChangelistModal2 from '@/components/factories2/EditChangelistModal.vue';

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
    {
      path: '/factories2/:factoryId?',
      name: 'factories2',
      component: FactoriesOverview2,
      children: [
        {
          path: 'new',
          name: 'newFactory2',
          components: { modal: EditFactoryModal2 }
        },
        {
          path: 'edit/:editFactoryId',
          name: 'editFactory2',
          components: { modal: EditFactoryModal2 }
        },
        {
          path: 'newChangelist',
          name: 'newChangelist2',
          components: { modal: EditChangelistModal2 }
        },
        {
          path: 'editChangelist/:editChangelistId',
          name: 'editChangelist2',
          components: { modal: EditChangelistModal2 }
        }
      ]
    },
    { path: '/devtools', component: DevTools }
  ]
});

export default router;
