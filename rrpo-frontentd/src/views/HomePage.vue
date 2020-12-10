<template>
  <div :class="[multipage === true ? 'multi-page':'single-page', 'home-page']" style="height: 100%">
    <a-row :gutter="7" class="head-info">
      <a-card class="head-info-card" style="border-radius: 10px;">
        <a-col :span="12">
          <div class="head-info-avatar">
            <img alt="头像" :src="avatar">
          </div>
          <div class="head-info-count">
            <div class="head-info-welcome">
              {{welcomeMessage}}
            </div>
            <div class="head-info-desc">
              <p>{{user.deptName ? user.deptName : '暂无部门'}} | {{user.roleName ? user.roleName : '暂无角色'}}</p>
            </div>
            <div class="head-info-time">上次登录时间：{{user.lastLoginTime ? user.lastLoginTime : '第一次访问系统'}}</div>
          </div>
        </a-col>
        <a-col :span="12">
          <div>
            <a-row class="more-info">
              <a-col :span="4"></a-col>
              <a-col :span="4"></a-col>
              <a-col :span="4"></a-col>
              <a-col :span="4">
                <head-info title="今日IP" :content="todayIp" :center="false" :bordered="false"/>
              </a-col>
              <a-col :span="4">
                <head-info title="今日访问" :content="todayVisitCount" :center="false" :bordered="false"/>
              </a-col>
              <a-col :span="4">
                <head-info title="总访问量" :content="totalVisitCount" :center="false" />
              </a-col>
            </a-row>
          </div>
        </a-col>
      </a-card>
    </a-row>
    <a-row :gutter="8" class="count-info">
      <a-col :span="12" class="visit-count-wrapper">
        <a-card class="visit-count-wrapper" style="height: 365px;border-radius: 10px;">
          <div style="font-size: 17px;">
            <a-row>
              <a-col :span="5" style="width: 125px"><b style="color: orangered;" title="未读消息"><a-icon type="sound" style="margin-right: 10px;margin-bottom: 10px"/>待办事项 >></b></a-col>
              <a-col :span="16" style="margin-top: 3px;width: 500px;white-space: nowrap;overflow: hidden; text-overflow:ellipsis;">
<!--                滚动文字 -->
                <a-carousel autoplay value="left">
                  <span style="font-style:oblique" v-for="(n,index) in rollData" :key="index" :value="n.id" :loading="loading"><a @click="lookStatus(n.id)" style="color: orangered">{{n.title}}</a></span>
                </a-carousel>
              </a-col>
            </a-row>
          </div>
          <div>
            <div style="background-color: #ECF8FF;box-shadow: rgba(0, 0, 0, 0.3) 1px 0px 2px;border-radius: 5px">
              <b style="color: blueviolet" title="未读消息"><a-icon type="mail" style="margin:8px 10px;"/>信息互递</b>
              <a @click="moreInfo" style="float: right;margin:5px 5px;">更多信息 >></a>
            </div>
            <div style="margin-top: 5px;height: 260px;overflow: auto;">
              <a-empty v-if="xxhdData.length === 0" style="margin-top: 40px"/>
              <ul v-for="(n,index) in xxhdData" :key="index" :value="n.id" style="padding: 0;margin-bottom: 0;">
                <li class="bottom_li">
                  <span style="width: 350px;white-space: nowrap;overflow: hidden;text-overflow:ellipsis; display: inline-block">
                    <a @click="lookStatus(n.id)">{{n.title}}</a>
                  </span>
                  <span class="bottom_time">{{n.releaseTime}}</span></li>
              </ul>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="12" class="project-wrapper">
        <a-card class="visit-count" style="border-radius: 10px;">
          <apexchart ref="count" type=bar height=300 :options="chartOptions" :series="series" />
        </a-card>
      </a-col>
    </a-row>
<!--    -->
    <a-row :gutter="8" class="count-info" style="margin: 7px 0;">
      <a-col :span="24" class="visit-count-wrapper">
        <a-row style="width: 100%;">
          <a-col span="8">
            <a-card title="护路简报" style="border-radius: 10px;height: 360px">
              <a slot="extra" @click="hljbInfo">更多信息>></a>
              <a-empty v-if="hljbData.length === 0" style="margin-top: 10px"/>
              <div style="height: 260px;overflow: auto;">
                <ul v-for="(n,index) in hljbData" :key="index" :value="n.id" style="padding: 0;margin-bottom: 0;text-align: left;">
                  <li class="bottom_li">
                    <span class="bottom_tilte"><a @click="lookRoad(n.id)">{{n.title}}</a></span>
                    <span class="bottom_time">{{n.releaseTime}}</span>
                  </li>
                </ul>
              </div>
            </a-card>
          </a-col>
          <a-col span="8">
<!--            style="border-radius: 10px;margin: 0 10px;border: 1px solid #c3c3c3;height: 230px;display: flex;flex-wrap: nowrap;-->
          <a-card title="一事一奖" style="border-radius: 10px;height: 360px">
            <a slot="extra" @click="ysyjInfo">更多信息>></a>
            <div style="height: 260px;overflow: auto;">
              <a-empty v-if="ysyjData.length === 0" style="margin-top: 10px"/>
              <ul v-for="(n,index) in ysyjData" :key="index" :value="n.id" style="padding: 0;margin-bottom: 0;text-align: left;">
                <li class="bottom_li">
                  <span class="bottom_tilte"><a @click="lookReward(n.id)">{{n.content}}</a></span>
                  <span class="bottom_time">{{n.releaseTime}}</span>
                </li>
              </ul>
            </div>
          </a-card>
          </a-col>
          <a-col span="8">
            <a-card title="通知公告" style="border-radius: 10px;height: 360px">
              <a slot="extra" @click="tzggInfo">更多信息>></a>
              <div style="height: 260px;overflow: auto;">
                <a-empty v-if="tzggData.length === 0" style="margin-top: 10px" />
                <ul v-for="(n,index) in tzggData" :key="index" :value="n.id" style="padding: 0;margin-bottom: 0;text-align: left;">
                  <li class="bottom_li">
                    <span class="bottom_tilte"><a @click="lookNotice(n.id)">{{n.title}}</a></span>
                    <span class="bottom_time">{{n.releaseTime}}</span>
                  </li>
                </ul>
              </div>
            </a-card>
          </a-col>

        </a-row>
      </a-col>
    </a-row>
    <ln-box ref="oldInBox" hidden />
    <in-shield-road ref="oldRoad" hidden />
    <notice ref="oldNotice" hidden/>
    <reward-in ref="oldReward" hidden/>
  </div>
</template>
<script>
import HeadInfo from '@/views/common/HeadInfo'
import LnBox from './oa/messageMutual/lnBox/LnBox'
import {mapState} from 'vuex'
import moment from 'moment'
import InShieldRoad from './briefReport/inShieldRoad/InShieldRoad'
import Notice from './notice/produce/Notice'
import rewardIn from './oa/reward/rewardIn'
moment.locale('zh-cn')

export default {
  name: 'HomePage',
  components: {InShieldRoad, HeadInfo, LnBox, Notice, rewardIn},
  data () {
    return {
      series: [],
      rollData: [], // 滚动数据
      xxhdData: [], // 信息互递数据
      ysyjData: [], // 一事一奖数据
      tzggData: [], // 通知公告数据
      hljbData: [], // 护路简报数据
      ranks: 0,
      chartOptions: {
        chart: {
          toolbar: {
            show: false
          }
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '35%'
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: []
        },
        fill: {
          opacity: 1

        }
      },
      projects: [
        {
          name: 'JIEBAO-Shiro',
          des: 'Spring Boot 2.0.4 & Shiro1.4.0 权限管理系统。',
          avatar: 'F'
        }
      ],
      todayIp: '',
      todayVisitCount: '',
      totalVisitCount: '',
      userRole: '',
      userDept: '',
      lastLoginTime: '',
      welcomeMessage: ''
    }
  },
  computed: {
    ...mapState({
      multipage: state => state.setting.multipage,
      user: state => state.account.user
    }),
    avatar () {
      return `static/avatar/${this.user.avatar}`
    }
  },
  methods: {
    welcome () {
      const date = new Date()
      const hour = date.getHours()
      let time = hour < 6 ? '早上好' : (hour <= 11 ? '上午好' : (hour <= 13 ? '中午好' : (hour <= 18 ? '下午好' : '晚上好')))
      // let welcomeArr = [
      //   '喝杯咖啡休息下吧☕',
      //   '要不要和朋友打局LOL',
      //   '要不要和朋友打局王者荣耀',
      //   '几天没见又更好看了呢😍',
      //   '今天又写了几个Bug🐞呢',
      //   '今天在群里吹水了吗',
      //   '今天吃了什么好吃的呢',
      //   '今天您微笑了吗😊',
      //   '今天帮助别人解决问题了吗',
      //   '准备吃些什么呢',
      //   '周末要不要去看电影？'
      // ]
      // let index = Math.floor((Math.random() * welcomeArr.length))
      // return `${time}，${this.user.username}，${welcomeArr[index]}`
      return `${time}！${this.user.username}`
    },
    // 信息互递的跳转
    moreInfo () {
      this.$router.push('/oa/messageMutual/lnBox')
    },
    // 一事一奖的跳转
    ysyjInfo () {
      this.$router.push('/oa/reward/rewardOut')
    },
    // 护路简报的跳转
    hljbInfo () {
      this.$router.push('/briefReport/inShieldRoad')
    },
    // 通知公告
    tzggInfo () {
      this.$router.push('/notice/collect')
    },
    // 获取信息互递的未读信息
    xxhd () {
      this.loading = true
      this.$get('/exchange/inbox').then(res => {
        let data = res.data.data.rows
        let xxhd = []
        for (let key in data) {
          if (data[key].isRead === 0) {
            xxhd.push(data[key])
          }
        }
        this.xxhdData = xxhd
        this.loading = false
      })
    },
    // 获取通知公告信息
    tzgg () {
      this.loading = true
      this.$get('/notice/inbox').then(res => {
        this.tzggData = res.data.data.rows
        this.loading = false
      })
    },
    // 获取护路简报信息
    hljb () {
      this.loading = true
      this.$get('/briefing/inbox').then(res => {
        this.hljbData = res.data.data.rows
        this.loading = false
      })
    },
    // 判断获取查询一事一奖
    ysyj () {
      let param = {}
      if (this.ranks === 4) {
        param.cityStatus = 0
        this.getYsyj(param)
      } else if (this.ranks === 1) {
        param.gongStatus = 0
        this.getYsyj(param)
      } else {
        this.getYsyj(param)
      }
    },
    // 获取一事一奖的未读信息
    getYsyj (param) {
      this.loading = true
      this.$get('/prize/inbox', param).then(res => {
        let data = res.data.data.rows
        let ysyj = []
        for (let key in data) {
          if (data[key].status === 3) {
            ysyj.push(data[key])
          }
          if (data[key].status === 2) {
            ysyj.push(data[key])
          }
          if (data[key].status === 6 && this.ranks === 4) {
            ysyj.push(data[key])
          }
          if (data[key].status === 5 && this.ranks === 1) {
            ysyj.push(data[key])
          }
          if (data[key].status === 7 && this.ranks === 0) {
            ysyj.push(data[key])
          }
          if (data[key].status === 6 && this.ranks === 0) {
            ysyj.push(data[key])
          }
        }
        this.ysyjData = ysyj
        this.loading = false
      })
    },
    // 信息互递文字点击跳转
    lookStatus (dataId) {
      this.$get('/exchange/inbox', {id: dataId}).then(res => {
        this.$refs.oldInBox.viewLook(res.data.data.rows[0])
      })
    },
    // 护路简报文字点击跳转
    lookRoad (dataId) {
      this.$get('/briefing/inbox', {id: dataId}).then(res => {
        this.$refs.oldRoad.viewLook(res.data.data.rows[0])
      })
    },
    // 通知公告文字点击跳转
    lookNotice (dataId) {
      this.$get('/notice/inbox', {id: dataId}).then(res => {
        this.$refs.oldNotice.look(res.data.data.rows[0])
      })
    },
    // 一事一奖文字点击跳转
    lookReward (dataId) {
      this.$get('/prize/inbox', {id: dataId}).then(res => {
        this.$refs.oldReward.look(res.data.data.rows[0])
      })
    },
    // 有限时回复时间的并滚动的数据
    rollFun () {
      this.loading = true
      this.$get('/exchange/getNotReceive').then(res => {
        this.rollData = res.data.data
        this.loading = false
      })
    }
  },
  mounted () {
    // 获取当前用户的rank
    this.$get('/dept/findRank').then(res => {
      this.ranks = res.data.data.rank
      this.ysyj()
    })
    this.xxhd()
    this.tzgg()
    this.hljb()
    this.rollFun()
    this.welcomeMessage = this.welcome()
    this.$get(`index/${this.user.username}`).then((r) => {
      let data = r.data.data
      this.todayIp = data.todayIp
      this.todayVisitCount = data.todayVisitCount
      this.totalVisitCount = data.totalVisitCount
      let sevenVisitCount = []
      let dateArr = []
      for (let i = 6; i >= 0; i--) {
        let time = moment().subtract(i, 'days').format('MM-DD')
        let contain = false
        for (let o of data.lastSevenVisitCount) {
          if (o.days === time) {
            contain = true
            sevenVisitCount.push(o.count)
          }
        }
        if (!contain) {
          sevenVisitCount.push(0)
        }
        dateArr.push(time)
      }
      let sevenUserVistCount = []
      for (let i = 6; i >= 0; i--) {
        let time = moment().subtract(i, 'days').format('MM-DD')
        let contain = false
        for (let o of data.lastSevenUserVisitCount) {
          if (o.days === time) {
            contain = true
            sevenUserVistCount.push(o.count)
          }
        }
        if (!contain) {
          sevenUserVistCount.push(0)
        }
      }
      this.$refs.count.updateSeries([
        {
          name: '您',
          data: sevenUserVistCount
        },
        {
          name: '总数',
          data: sevenVisitCount
        }
      ], true)
      this.$refs.count.updateOptions({
        xaxis: {
          categories: dateArr
        },
        title: {
          text: '近七日系统访问记录',
          align: 'left'
        }
      }, true, true)
    }).catch((r) => {
      this.$message.error('获取首页信息失败')
    })
  }
}
</script>
<style lang="less">
  .home-page {
    .head-info {
      margin-bottom: .5rem;
      .head-info-card {
        padding: .5rem;
        border-color: #f1f1f1;
        .head-info-avatar {
          display: inline-block;
          float: left;
          margin-right: 1rem;
          img {
            width: 5rem;
            border-radius: 2px;
          }
        }
        .head-info-count {
          display: inline-block;
          float: left;
          .head-info-welcome {
            font-size: 1.05rem;
            margin-bottom: .1rem;
          }
          .head-info-desc {
            color: rgba(0, 0, 0, 0.45);
            font-size: .8rem;
            padding: .2rem 0;
            p {
              margin-bottom: 0;
            }
          }
          .head-info-time {
            color: rgba(0, 0, 0, 0.45);
            font-size: .8rem;
            padding: .2rem 0;
          }
        }
      }
    }
    .count-info {
      .visit-count-wrapper {
        padding-left: 0 !important;
        .visit-count {
          padding: .5rem;
          border-color: #f1f1f1;
          .ant-card-body {
            padding: .5rem 1rem !important;
          }
        }
      }
      .project-wrapper {
        padding-right: 0 !important;
        .project-card {
          border: none !important;
          .ant-card-head {
            border-left: 1px solid #f1f1f1 !important;
            border-top: 1px solid #f1f1f1 !important;
            border-right: 1px solid #f1f1f1 !important;
          }
          .ant-card-body {
            padding: 0 !important;
            table {
              width: 100%;
              td {
                width: 50%;
                border: 1px solid #f1f1f1;
                padding: .6rem;
                .project-avatar-wrapper {
                  display:inline-block;
                  float:left;
                  margin-right:.7rem;
                  .project-avatar {
                    color: #42b983;
                    background-color: #d6f8b8;
                  }
                }
              }
            }
          }
          .project-detail {
            display:inline-block;
            float:left;
            text-align:left;
            width: 78%;
            .project-name {
              font-size:.9rem;
              margin-top:-2px;
              font-weight:600;
            }
            .project-desc {
              color:rgba(0, 0, 0, 0.45);
              p {
                margin-bottom:0;
                font-size:.6rem;
                white-space:normal;
              }
            }
          }
        }
      }
    }

    a {
      color: black;
    }
    .mainbottom {
      height: 230px;
      display: flex;
      flex-wrap: nowrap;
    }
    .divider {
      border-radius: 10px;
      display: flex;
      flex-wrap: nowrap;
      margin: 0 10px;
      border: 1px solid #c3c3c3;
      width: 500px;
    }
    .home-title{
      width: 100%;
      text-align: center;
      margin-top: 10px;
      margin: 10px 20px
    }
    .home-title-p{
      width: 100%;background-color: #ECF8FF;padding: 8px 0;box-shadow: rgba(0, 0, 0, 0.3) 1px 0px 2px;border-radius: 5px;
    }
    .home-title-list{
      height: 150px;overflow: auto
    }
    .bottom_li{
      list-style-type:none;border-bottom: 1px dashed #ccc;padding: 2px 10px
    }
    .bottom_tilte{
      width: 200px;white-space: nowrap;overflow: hidden;text-overflow:ellipsis; display: inline-block
    }
    .bottom_time{
      float: right;width: 85px;white-space: nowrap;overflow: hidden;text-overflow:ellipsis;
    }
  }
</style>
