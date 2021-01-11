<template>
  <div class="container" :style="bgImg">
<!--    <vue-particles  color="#87CEFA"-->
<!--                    :particleOpacity="0.7"-->
<!--                    :particlesNumber="80"-->
<!--                    shapeType="circle"-->
<!--                    :particleSize="3"-->
<!--                    linesColor="#87CEFA"-->
<!--                    :linesWidth="1.5"-->
<!--                    :lineLinked="true"-->
<!--                    :lineOpacity="0.5"-->
<!--                    :linesDistance="150"-->
<!--                    :moveSpeed="3"-->
<!--                    :hoverEffect="true"-->
<!--                    hoverMode="grab"-->
<!--                    :clickEffect="true"-->
<!--                    clickMode="push" style="position: absolute;width: 100%;height: auto"></vue-particles>-->
    <div class="content" style="margin-top: 100px;">
      <!--<div class="top">-->
      <div style="text-align:center;">
        <div style="text-align: center;margin-bottom: 30px;
        background-color: white;width: 400px;margin: auto;
        padding: 30px;border-radius: 6px;
        box-shadow: rgba(0, 0, 0, 0.3) 12px 20px 20px 0px">
          <img alt="logo" class="logo" src="static/img/logo.png" />
          <span class="title">{{systemName}}</span>
          <component :is="componentName" @regist="handleRegist" class="main-content" style="margin-top: 40px"></component>
        </div>
        <!--        <div class="desc"></div>-->
        <!--      </div>-->
      </div>
    </div>
    <global-footer :copyright="copyright" />
  </div>
</template>

<script>
import GlobalFooter from '../common/GlobalFooter'
import Login from './Login'
import Regist from './Regist'
import LoginBj from '../../../static/img/login_bj.png'

export default {
  name: 'Common',
  components: {GlobalFooter, Login, Regist},
  data () {
    return {
      bgImg: {
        background: `url(${LoginBj}) 100% 100%`
        // backgroundImage: 'url(" + require("../../../static/img/login_bj.png") + ")'
      },
      componentName: 'Login'
    }
  },
  computed: {
    systemName () {
      return this.$store.state.setting.systemName
    },
    copyright () {
      return this.$store.state.setting.copyright
    }
  },
  methods: {
    handleRegist (val) {
      this.componentName = val
    }
  }
}
</script>
<!--https://gw.alipayobjects.com/zos/rmsportal/TVYTbAXWheQpRcWDaDMu.svg   background: #f0f2f5 url('./bj.jpg') no-repeat center 110px;-->
<style lang="less" scoped>
  .container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    background-size: 100%;

    .content {
      padding: 32px 0;
      flex: 1;
      @media (min-width: 768px) {
        padding: 116px 0 10px;
      }
    }
  }
      .logo {
        width: 40px;
        height: 40px;
        vertical-align: center;
        margin-right: 16px;
      }
      .title {
        font-size: 28px;
        color: rgba(0,0,0,.85);
        font-family: 'Myriad Pro', 'Helvetica Neue', Arial, Helvetica, sans-serif;
        font-weight: 600;
        position: relative;
        top: 6px;
      }
      /*.top {*/
      /*  text-align: center;*/
      /*  background-color: white;*/
      /*  .header {*/
      /*    height: 50px;*/
      /*    line-height: 50px;*/
      /*    a {*/
      /*      text-decoration: none;*/
      /*    }*/
      /*    .logo {*/
      /*      width: 40px;*/
      /*      height: 19px;*/
      /*      vertical-align: center;*/
      /*      margin-right: 16px;*/
      /*    }*/
      /*    .title {*/
      /*      font-size: 28px;*/
      /*      color: rgba(0,0,0,.85);*/
      /*      font-family: 'Myriad Pro', 'Helvetica Neue', Arial, Helvetica, sans-serif;*/
      /*      font-weight: 600;*/
      /*      position: relative;*/
      /*      top: 6px;*/
      /*    }*/
      /*  }*/
      /*  .desc {*/
      /*    font-size: 14px;*/
      /*    color: rgba(0,0,0,.45);*/
      /*    margin-top: 12px;*/
      /*    margin-bottom: 40px;*/
      /*  }*/
      /*}*/
      /*.main-content {*/
      /*  width: 368px;*/
      /*  margin: 0 auto;*/
        /*@media screen and (max-width: 576px) {*/
        /*  width: 95%;*/
        /*}*/
        /*@media screen and (max-width: 320px) {*/
        /*  .captcha-button{*/
        /*    font-size: 14px;*/
        /*  }*/
        /*}*/
      /*}*/
    /*}*/
  /*}*/
</style>
