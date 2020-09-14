import Vue from 'vue'
import Jiebao from './JIEBAO'
import router from './router'
import Antd from 'ant-design-vue'
import store from './store'
import request from 'utils/request'
import db from 'utils/localstorage'
import VueApexCharts from 'vue-apexcharts'
import echarts from 'echarts'
import VueParticles from 'vue-particles'

import 'ant-design-vue/dist/antd.css'

import 'utils/install'

import {
  // layout
  Container,
  Header,
  Main,
  Footer,
  Button,
  Input,
  Form,
  FormItem
} from 'element-ui'
import { ElementTiptapPlugin } from 'element-tiptap'
// import ElementUI's styles
import 'element-ui/lib/theme-chalk/index.css'
// import this package's styles
import 'element-tiptap/lib/index.css'
// use this package's plugin
Vue.use(ElementTiptapPlugin, { lang: 'zh' })
Vue.use(Container)
Vue.use(Header)
Vue.use(Main)
Vue.use(VueParticles)
Vue.use(Footer)
Vue.use(Button)
Vue.use(Input)
Vue.use(Form)
Vue.use(FormItem)

Vue.config.productionTip = false
Vue.use(Antd)
Vue.use(db)
Vue.use(VueApexCharts)

Vue.component('apexchart', VueApexCharts)

Vue.use({
  install (Vue) {
    Vue.prototype.$db = db
  }
})

Vue.prototype.$echarts = echarts
Vue.prototype.$post = request.post
Vue.prototype.$constURL = request.constURL
Vue.prototype.$get = request.get
Vue.prototype.$put = request.put
Vue.prototype.$delete = request.delete
Vue.prototype.$export = request.export
Vue.prototype.$download = request.download
Vue.prototype.$upload = request.upload

/* eslint-disable no-new */
new Vue({
  router,
  store,
  render: h => h(Jiebao)
}).$mount('#jbx')
