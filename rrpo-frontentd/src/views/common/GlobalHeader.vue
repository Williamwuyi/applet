<template>
  <a-layout-header :class="[fixHeader && 'ant-header-fixedHeader', layout === 'side' ? (sidebarOpened ? 'ant-header-side-opened' : 'ant-header-side-closed') : null, theme, 'global-header' ]">
    <div :class="['global-header-wide', layout]">
      <router-link v-if="isMobile || layout === 'head'" to="/" :class="['logo', isMobile ? null : 'pc', theme]">
        <img width="32" src="static/img/logo.png"  :alt="systemName"/>
      </router-link>
      <a-divider v-if="isMobile" type="vertical" />
      <a-icon v-if="layout === 'side'" class="trigger" :type="collapsed ? 'menu-unfold' : 'menu-fold'" @click="toggleCollapse"/>
      <div v-if="layout === 'head'" class="global-header-menu">
        <i-menu style="height: 64px; line-height: 64px;" class="system-top-menu" :theme="theme" mode="horizontal" :menuData="menuData" @select="onSelect"/>
      </div>
      <div :class="['global-header-right', theme]">
          <header-avatar class="header-item"/>
      </div>
      <!-- 我的消息 -->
<!--      <div id="components-badge-demo-title" style="float:right; margin-top:3px">-->
<!--    <a-dropdown>-->
<!--    <a class="ant-dropdown-link" @click="e => e.preventDefault()">-->
<!--      <a-badge :count="count" style="font-size: 24px; margin-top: 18px;">-->
<!--      <a-icon type="mail" style="font-size:24px"/>-->
<!--    </a-badge>-->
<!--    </a>-->
<!--    <a-menu slot="overlay" @click="onClick">-->
<!--      <a-menu-item  class="dropdownMsg" v-for="(item,i) in list" :key="i">-->
<!--        <a-tag color="pink">小程序通知</a-tag>-->
<!--        你的消费券还要7天过期，到期不处理则没有了-->
<!--      </a-menu-item><a-menu-divider/>-->
<!--      <a-menu-item key="5" style="text-align: right;">-->
<!--        查看更多>>-->
<!--      </a-menu-item>-->
<!--    </a-menu>-->
<!--  </a-dropdown>-->
<!--  </div>-->
      <!--帮助文档-->
      <div style="float:right; margin-top:2px; font-size: 24px;">
<!--        <a v-if="loading"><a-icon style="font-size:22px;" @click="download" type="question-circle" title="帮助文档"/></a>-->
<!--        <a-spin v-if="!loading" :indicator="indicator" />-->
        <a href="http://114.115.147.159:19527/jbx/cdn/image/2020-11-04/湖南铁护办系统平台使用手册.pdf" target="_blank"><a-icon style="font-size:22px;" type="question-circle" title="帮助文档"/></a>
      </div>
    </div>
<!-- 查看 -->
<!--<noticelook-->
<!--  :NoticelookVisiable="NoticelookVisiable"-->
<!--  @close="hanleNoticelookclose"-->
<!--  ref="nolook"-->
<!--/>-->
  </a-layout-header>
</template>

<script>
import HeaderAvatar from './HeaderAvatar'
import IMenu from '@/components/menu/menu'
import { mapState } from 'vuex'
// import Noticelook from './Noticelook'
// import axios from 'axios'
// import store from '../../store'
// import {notification} from 'ant-design-vue'

export default {
  name: 'GlobalHeader',
  components: {IMenu, HeaderAvatar},
  props: ['collapsed', 'menuData'],
  data () {
    return {
      loading: true,
      indicator: '<a-icon type="loading" style="font-size: 24px" spin />',
      NoticelookVisiable: false,
      count: 1,
      list: []
    }
  },
  computed: {
    ...mapState({
      isMobile: state => state.setting.isMobile,
      layout: state => state.setting.layout,
      systemName: state => state.setting.systemName,
      sidebarOpened: state => state.setting.sidebar.opened,
      fixHeader: state => state.setting.fixHeader
    }),
    theme () {
      return this.layout === 'side' ? 'light' : this.$store.state.setting.theme
    }
  },
  mounted () {
    this.fach()
  },
  methods: {
    toggleCollapse () {
      this.$emit('toggleCollapse')
    },
    onSelect (obj) {
      this.$emit('menuSelect', obj)
    },
    // 数据请求
    fach () {
      // 请求过来数据赋值给数组和count
      this.count = 5
      this.list = [1, 2, 3, 4]
    },
    // 点击直接显示公告
    onClick (key) {
      if (key.key === '5') {
        // 查看更多跳转到收件箱
        this.$router.push('/oa/messageMutual/lnBox')
      } else {
        console.log(`Click on item ${key}`)
        var user = {
          createTime: '202020-222',
          createUser: 'lll',
          title: '不识妻美刘XX',
          content: '大家好，我是XX辉'
        }
        this.NoticelookVisiable = true
        this.$refs.nolook.setFormValues(user)
      }
    },
    // 查看
    hanleNoticelookclose () {
      this.NoticelookVisiable = false
    }
    // 文件下载
    // download () {
    //   this.loading = false
    //   let record = {
    //     file: {
    //       fileId: '8ba8403e99102d79528eaba5fcd3020e',
    //       oldName: '湖南铁护办系统平台使用手册.pdf'
    //     }
    //   }
    //   axios({
    //     method: 'post',
    //     url: this.$constURL + 'file/downloadFile',
    //     headers: {
    //       'Authentication': store.state.account.token
    //     },
    //     params: {
    //       fileId: record.file.fileId
    //     },
    //     responseType: 'blob'
    //   }).then(res => {
    //     this.loading = true
    //     const content = res.data
    //     const blob = new Blob([content])
    //     if ('download' in document.createElement('a')) {
    //       const link = document.createElement('a')
    //       link.download = record.file.oldName
    //       link.style.display = 'none'
    //       link.href = URL.createObjectURL(blob)
    //       document.body.appendChild(link)
    //       link.click()
    //       URL.revokeObjectURL(link.href)
    //       document.body.removeChild(link)
    //     } else {
    //       navigator.msSaveBlob(blob, record.file.oldName)
    //     }
    //     notification.success({
    //       message: '系统提示',
    //       description: '文件下载成功！',
    //       duration: 4
    //     })
    //   }).catch(() => {
    //     notification.error({
    //       message: '系统提示',
    //       description: '文件传输失败！',
    //       duration: 4
    //     })
    //   })
    // }
  }
}
</script>

<style lang="less" scoped>
  .trigger {
    font-size: 20px;
    line-height: 64px;
    padding: 0 24px;
    cursor: pointer;
    transition: color .3s;
  }
  .header-item{
    padding: 0 19px;
    display: inline-block;
    height: 100%;
    cursor: pointer;
    vertical-align: middle;
    i{
      font-size: 16px;
      color: rgba(0,0,0,.65);
    }
  }
  .global-header{
    padding: 0 12px 0 0;
    -webkit-box-shadow: 0 1px 4px rgba(0,21,41,.08);
    box-shadow: 0 1px 4px rgba(0,21,41,.08);
    position: relative;
    &.light{
      background: #fff;
    }
    &.dark{
      background: #393e46;
    }
    .global-header-wide{
      &.head{
        padding: 0 24px;
      }
      &.side{
      }
      .logo {
        height: 64px;
        line-height: 58px;
        vertical-align: top;
        display: inline-block;
        padding: 0 12px 0 24px;
        cursor: pointer;
        font-size: 20px;
        &.pc{
          padding: 0 12px 0 0;
        }
        img {
          display: inline-block;
          vertical-align: middle;
        }
        h1{
          display: inline-block;
          font-size: 16px;
        }
        &.dark h1{
          color: #fff;
        }
      }
      .global-header-menu{
        display: inline-block;
      }
      .global-header-right{
        float: right;
        &.dark{
          color: #fff;
          i{
            color: #fff;
          }
        }
      }
    }
  }
  .ant-header-fixedHeader {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 15;
    width: 100%;
    transition: width .2s;

    &.ant-header-side-opened {
      width: 100%;
      padding-left: 254px;
    }

    &.ant-header-side-closed {
      width: 100%;
      padding-left: 80px;
    }
  }
  #components-badge-demo-title .ant-badge:not(.ant-badge-status) {
  margin-right: 20px;
}
.ant-dropdown-link {
   font-size: 16px;
}
.dropdownMsg {
  width: 300px;
    overflow: hidden;
  text-overflow:ellipsis;
    white-space: nowrap;
}
.head-example {
  width: 42px;
  height: 42px;
  border-radius: 4px;
  background: #eee;
  display: inline-block;
}
</style>
