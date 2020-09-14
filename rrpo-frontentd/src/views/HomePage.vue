<template>
  <div :class="[multipage === true ? 'multi-page':'single-page', 'not-menu-page', 'home-page']">
    <a-row :gutter="8" class="count-info">
      <a-col :span="16">
<!--        -->
        <a-card class="visit-count-wrapper">
          <div class="head_info">
            <div class="head-info-avatar">
              <img alt="头像" :src="avatar" style="width: 5rem;border-radius: 50px;margin-right: 20px">
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
          </div>
          <div style="margin-top: 5px">
            <a-card class="visit-count">
            <apexchart ref="count" type=bar height=300 :options="chartOptions" :series="series" />
            </a-card>
          </div>
        </a-card>
      </a-col>
<!--    业务提醒  -->
      <a-col :span="8">
        <a-card class="visit-count-wrapper">
          <div style="border-bottom:1px solid #f0f2f5;font-size: 15px;">
            <b style="color: orangered"><a-icon type="sound" style="margin-right: 10px"/>业务提醒</b>
          </div>
          <div>
            <b style="color: blueviolet"><a-icon type="mail" style="margin-right: 10px;margin-top: 10px"/>信息互递</b>
            <div style="margin-top: 5px;background-color: #f2f2f2;height: 102px;">
              <ul v-for="(site,index) in xxhdData" :key="index" style="padding: 0;margin-bottom: 0;">
                <li style="list-style-type:none;border-bottom: 1px dashed #ccc;padding: 2px 10px">
                  <span style="width: 300px;white-space: nowrap;overflow: hidden;text-overflow:ellipsis; display: inline-block">{{site.title}}</span>
                  <span style="float: right">{{site.releaseTime}}</span></li>
              </ul>
            </div>
<!---->
            <b style="color: darkorange"><a-icon type="hdd" style="margin-right: 10px;margin-top: 10px"/>一事一奖</b>
            <div style="margin-top: 5px;background-color: #f2f2f2;height: 102px;">
              <ul v-for="(site,index) in xxhdData" :key="index" style="padding: 0;margin-bottom: 0;">
                <li style="list-style-type:none;border-bottom: 1px dashed #ccc;padding: 2px 10px">
                  <span style="width: 300px;white-space: nowrap;overflow: hidden;text-overflow:ellipsis; display: inline-block">{{site.title}}</span>
                  <span style="float: right">{{site.releaseTime}}</span></li>
              </ul>
            </div>
<!--          -->
          <b style="color: lightgreen"><a-icon type="read" style="margin-right: 10px;margin-top: 10px"/>其他</b>
          <div style="margin-top: 5px;background-color: #f2f2f2;height: 102px;">
            <ul v-for="(site,index) in xxhdData" :key="index" style="padding: 0;margin-bottom: 0;">
              <li style="list-style-type:none;border-bottom: 1px dashed #ccc;padding: 2px 10px">
                <span style="width: 300px;white-space: nowrap;overflow: hidden;text-overflow:ellipsis; display: inline-block">{{site.title}}</span>
                <span style="float: right">{{site.releaseTime}}</span></li>
            </ul>
          </div>
<!--              -->
          </div>
        </a-card>
      </a-col>
<!--      -->
    </a-row>
<!--    -->
    <a-row :gutter="8" class="count-info" style="margin-top: 7px">
      <a-col :span="24" class="visit-count-wrapper">
        <a-card class="visit-count">
          <div class="mainbottom">
            <div class="divider">
              <a-card hoverable>
                <img
                  slot="cover"
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
                <template slot="actions" class="ant-card-actions">
                  <a-icon key="setting" type="setting" />
                  <a-icon key="edit" type="edit" />
                  <a-icon key="ellipsis" type="ellipsis" />
                </template>
                <a-card-meta title="Card title" description="This is the description">
                  <a-avatar
                    slot="avatar"
                    src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"
                  />
                </a-card-meta>
              </a-card>
            </div>
            <div class="divider">
              <a-card hoverable>
                <img
                  slot="cover"
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
                <template slot="actions" class="ant-card-actions">
                  <a-icon key="setting" type="setting" />
                  <a-icon key="edit" type="edit" />
                  <a-icon key="ellipsis" type="ellipsis" />
                </template>
                <a-card-meta title="Card title" description="This is the description">
                  <a-avatar
                    slot="avatar"
                    src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"
                  />
                </a-card-meta>
              </a-card>
            </div>
            <div class="divider">
              <a-card hoverable>
                <img
                  slot="cover"
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
                <template slot="actions" class="ant-card-actions">
                  <a-icon key="setting" type="setting" />
                  <a-icon key="edit" type="edit" />
                  <a-icon key="ellipsis" type="ellipsis" />
                </template>
                <a-card-meta title="Card title" description="This is the description">
                  <a-avatar
                    slot="avatar"
                    src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"
                  />
                </a-card-meta>
              </a-card>
            </div>
            <div class="divider">
              <a-card hoverable>
                <img
                  slot="cover"
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
                <template slot="actions" class="ant-card-actions">
                  <a-icon key="setting" type="setting" />
                  <a-icon key="edit" type="edit" />
                  <a-icon key="ellipsis" type="ellipsis" />
                </template>
                <a-card-meta title="Card title" description="This is the description">
                  <a-avatar
                    slot="avatar"
                    src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"
                  />
                </a-card-meta>
              </a-card>
            </div>
            <div class="divider">
              <a-card hoverable>
                <img
                  slot="cover"
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
                <template slot="actions" class="ant-card-actions">
                  <a-icon key="setting" type="setting" />
                  <a-icon key="edit" type="edit" />
                  <a-icon key="ellipsis" type="ellipsis" />
                </template>
                <a-card-meta title="Card title" description="This is the description">
                  <a-avatar
                    slot="avatar"
                    src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"
                  />
                </a-card-meta>
              </a-card>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
    <!--    -->
  </div>
</template>
<script>
import HeadInfo from '@/views/common/HeadInfo'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'HomePage',
  components: {HeadInfo},
  data () {
    return {
      series: [],
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
      xxhdData: [],
      ysyjData: [],
      hljbData: [],
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
    },
    columns () {
      return [{
        title: '标题',
        dataIndex: 'title',
        width: '30%',
        scopedSlots: { customRender: 'name' }
      }, {
        title: '发布时间',
        dataIndex: 'createTime'
      }, {
        title: '当前状态',
        dataIndex: 'state',
        scopedSlots: { customRender: 'status' }
      }, {
        title: '发布人',
        dataIndex: 'createUser'
      }]
    }
  },
  methods: {
    welcome () {
      const date = new Date()
      const hour = date.getHours()
      let time = hour < 6 ? '早上好' : (hour <= 11 ? '上午好' : (hour <= 13 ? '中午好' : (hour <= 18 ? '下午好' : '晚上好')))
      let welcomeArr = [
        '喝杯咖啡休息下吧☕',
        '要不要和朋友打局LOL',
        '要不要和朋友打局王者荣耀',
        '几天没见又更好看了呢😍',
        '今天又写了几个Bug🐞呢',
        '今天在群里吹水了吗',
        '今天吃了什么好吃的呢',
        '今天您微笑了吗😊',
        '今天帮助别人解决问题了吗',
        '准备吃些什么呢',
        '周末要不要去看电影？'
      ]
      let index = Math.floor((Math.random() * welcomeArr.length))
      return `${time}，${this.user.username}，${welcomeArr[index]}`
    },
    callback (key) {
      console.log(key)
    }
  },
  mounted () {
    this.$get('/exchange/inbox').then(res => {
      this.xxhdData = res.data.data.rows
      this.loading = false
    })
    this.$get('/briefing/inbox').then(res => {
      this.hljbData = res.data.data.rows
      this.loading = false
    })
    this.$get('/prize/inbox').then(res => {
      this.ysyjData = res.data.data.rows
      this.loading = false
    })
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
      console.error(r)
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
  }
  .head_info{
    display: flex;
    flex-wrap: nowrap;
  }
  .tableStyle{
    margin-top: 15px;
    height:200px;
    overflow:auto;
  }
  .mainbottom {
    height: 300px;
    display: flex;
    flex-wrap: nowrap;
  }

  .divider {
    margin: 0 10px;
    border: 1px solid #c3c3c3;
    width: 400px;
  }
  .headerDiv{
    border-radius: 10px;
    background-color: antiquewhite;
    height: 80px;
    width: 100px;
    margin-left: 15px;
    padding-right: 4px;
    line-height: 80px;
    text-align: center;
  }

</style>
