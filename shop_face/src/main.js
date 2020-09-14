// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import 'babel-polyfill'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.less'
import store from './store'
import axios from 'axios'
import echarts from 'echarts'

Vue.config.productionTip = false;

Vue.prototype.$ajax = axios;
Vue.prototype.$echarts = echarts;

Vue.use(Antd);

router.beforeEach((to,from,next)=>{
  store.commit('getUserToken');//判断localstorage有无token
  if(to.path=='/login'){
    next();
  }else{
    if(store.state.token==""||store.state.token==null){//没有登录
      next('/login');
    }else{
      let flag=store.state.menuflag;
      if(flag){//已配置路由
        next();
      }else{//刷新页面导致store变为默认值
        if(store.state.iscasher==0){//非收银账号
          store.commit('getMenus');//重设菜单
          let menu=store.state.menus;
          store.commit('getRootBtn',menu);//重新获取所有权限按钮
          router.$addRoutes(store.state.addRoute);//配置路由
          store.commit('setmMenuflag',true);//设置路由表状态，避免重复添加进入死循环
          next(to);
        }else if(store.state.iscasher==1){//收银账号
          store.commit('setCasherRoute');
          router.$addRoutes(store.state.addRoute);
          store.commit('setmMenuflag',true);
          next(to);
        }
        
      }
    }
  }
})

function startAPP(){
  axios.get('./static/config.json').then(res=>{
    Vue.prototype.root_path=res.data;
    new Vue({
      el: '#app',
      router,
      store,
      components: { App },
      template: '<App/>'
    })
  })
    
}
startAPP();//将全局变量以配置中心的形式展示。

//设置默认请求头
axios.interceptors.request.use(config=>{
  if((config.url).indexOf('login')!=-1){//登录请求不需要携带token
  }else{
    if(localStorage.getItem('token')){
      config.headers.token=localStorage.getItem('token');
    }
  }
  return config;
},error=>{
  return Promise.reject(error);
});

// 异步请求后，判断token是否过期
axios.interceptors.response.use(
  response=>{
    return response;
  },
  error=>{
    if(error.response){
      switch(error.response.status){
        case 401:
          localStorage.removeItem('token');
          router.$addRoutes([//重设路由表
            {path:'/login',name:'login',component: ()=>import('@/components/Login/login')},
            { path: "*", component: () => import("@/components/404page/404") }
          ]);
          router.push({name:'login'}).catch(error=>{});
          console.log("403,登录超时");
          break;
        case 403:
          console.log("401，无权限操作");
          break;
      }
    }
  }
)