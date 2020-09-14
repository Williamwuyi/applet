<template>
  <a-config-provider :locale="zh_CN">
    <div id="app">
      <router-view v-if="isRouterAlive" />
    </div>
  </a-config-provider>
</template>

<script>
import zh_CN from "ant-design-vue/lib/locale-provider/zh_CN";
import moment from "moment";
import "moment/locale/zh-cn";

export default {
  name: "App",
  data() {
    return {
      zh_CN,
      isRouterAlive: true
    };
  },
  provide: function() {
    return {
      reload: this.reload
    };
  },
  methods:{
    reload:function(){
        this.isRouterAlive=false;
        this.$nextTick(function(){
            this.isRouterAlive=true
        })
    }
  }
}
</script>

<style>
* {
  padding: 0;
  margin: 0;
}
/* 设置全局的页面最小高度为当前可视高度 */
#components-layout-demo-custom-trigger {
  min-height: 100vh !important;
}
</style>
