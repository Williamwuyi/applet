import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const router=new Router({
  mode: 'history',
  routes: [
    {
      path: '/login',
      name: 'login',
      component: ()=>import('@/components/Login/login')
    },
    { path: "*", component: () => import("@/components/404page/404") }
  ]
});

router.$addRoutes = (params) => {
  router.matcher = new Router().matcher;
  router.addRoutes(params)
}

export default router
