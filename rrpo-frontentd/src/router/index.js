import Vue from 'vue'
import Router from 'vue-router'
import MenuView from '@/views/common/MenuView'
import PageView from '@/views/common/PageView'
import LoginView from '@/views/login/Common'
import EmptyPageView from '@/views/common/EmptyPageView'
import HomePageView from '@/views/HomePage'
import db from 'utils/localstorage'
import request from 'utils/request'

Vue.use(Router)
const originalPush = Router.prototype.push
Router.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(err => err)
}
let constRouter = [
  {
    path: '/login',
    name: '登录页',
    component: LoginView
  },
  {
    path: '/index',
    name: '首页',
    redirect: '/home'
  }
]

let router = new Router({
  routes: constRouter
})

const whiteList = ['/login']

let asyncRouter

// 导航守卫，渲染动态路由
router.beforeEach((to, from, next) => {
  // 如果是登录页就不用权限，直接进行登录
  if (whiteList.indexOf(to.path) !== -1) {
    next()
  }
  let token = db.get('USER_TOKEN')
  let user = db.get('USER')
  let userRouter = get('USER_ROUTER')

  if (token.length && user) {
    // undfind取反为true
    if (!asyncRouter) {
      if (!userRouter) {
        request.get(`menu/${user.username}`).then((res) => {
          asyncRouter = res.data
          save('USER_ROUTER', asyncRouter)
          go(to, next)
        })
      } else {
        asyncRouter = userRouter
        go(to, next)
      }
    } else {
      next()
    }
  } else {
    next('/login')
  }
})
// 动态添加路由
function go (to, next) {
  asyncRouter = filterAsyncRouter(asyncRouter)
  router.addRoutes(asyncRouter)
  next({...to, replace: true})
}
// 把数据里面的名字保存到本地
function save (name, data) {
  localStorage.setItem(name, JSON.stringify(data))
}
// 获取本地数据里面的名字
function get (name) {
  return JSON.parse(localStorage.getItem(name))
}
// 对于数据里面路由的一些操作
function filterAsyncRouter (routes) {
  return routes.filter((route) => {
    let component = route.component
    if (component) {
      switch (route.component) {
        case 'MenuView':
          route.component = MenuView
          break
        case 'PageView':
          route.component = PageView
          break
        case 'EmptyPageView':
          route.component = EmptyPageView
          break
        case 'HomePageView':
          route.component = HomePageView
          break
        default:
          route.component = view(component)
      }
      if (route.children && route.children.length) {
        route.children = filterAsyncRouter(route.children)
      }
      return true
    }
  })
}
// ？
function view (path) {
  return function (resolve) {
    import(`@/views/${path}.vue`).then(mod => {
      resolve(mod)
    })
  }
}

export default router
