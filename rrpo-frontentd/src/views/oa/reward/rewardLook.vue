<template>
  <a-modal
    :visible="rewardlookVisiable"
    @cancel="() => { rewclone() }"
    :width= "794"
    :footer="null"
    title="查看详情"
  >
    <div class="looktype">
      <h1>湖南省铁路护路联防工作’一事一奖‘详情</h1>
      <div class="lookborder">
        <ul class="lookName clearboth">
          <li class="title">姓名</li>
          <li>{{lookList.name}}</li>
          <li class="title">年龄</li>
          <li>{{lookList.age}}</li>
          <li class="title">性别</li>
          <li>{{lookList.sex}}</li>
        </ul>
        <ul class="lookCard clearboth">
          <li class="title">身份</li>
          <li>{{lookList.identity}}</li>
        </ul>
        <ul class="lookState clearboth">
          <li class="title">该事迹在乡镇铁路护路<br/>微信平台发布情况</li>
          <li><u>{{lookList.creatTime}}</u>在<u>{{lookList.place}}</u><br/>铁路护路微信平台由<u>{{lookList.releaseWho}}</u>发布</li>
        </ul>
        <div class="lookDescribe">
          <p class="title">事迹简要描述：</p>
          <p class="content">{{happenTimeyear}}年{{happenTimemon}}月{{happenTimedate}}日，{{lookList.content}}</p>
        </div>
        <ul class="lookOpinion clearboth">
          <li>
            <p class="title">推荐单位意见</p>
            <p class="content">{{lookList.ownOpinion}}</p>
            <span class="money">建议奖励金额<u>{{lookList.ownMoney}}</u>元</span>
            <span class="time">{{lookList.creatTime}}</span>
          </li>
          <li>
            <p class="title">所在铁路公安机关意见</p>
            <div v-if="opiData.length>1">
              <p class="content">{{auditOpinion.stateaud}}</p>
              <span class="money" v-if="ranks == 1 || ranks == 4 || ranks == 0">建议奖励金额<u>{{money.statemoney}}</u>元</span>
              <span class="time">2020年3月19日</span>
            </div>
            <div v-else-if="rejectOpinion.rejectRank==4">
              <p class="content">{{rejectOpinion.rejectContent}}</p>
              <span class="money" v-if="ranks==4">驳回人:<u>{{rejectOpinion.rejectName}}</u></span>
              <span class="time"> 驳回时间:{{rejectOpinion.rejectTime}}</span>
            </div>
          </li>
        </ul>
        <ul class="lookOpinion clearboth">
          <li>
            <p class="title">市州护路办意见</p>
            <div v-if="opiData.length>0">
              <p class="content">{{auditOpinion.ctiyaud}}</p>
              <span class="money" v-if="ranks == 4 || ranks == 0">建议奖励金额<u>{{money.ctiymoney}}</u>元</span>
              <span class="time">2020年3月19日</span>
            </div>
            <div v-else-if="rejectOpinion.rejectRank===1">
              <p class="content">{{rejectOpinion.rejectContent}}</p>
              <span class="money" v-if="ranks==1">驳回人:<u>{{rejectOpinion.rejectName}}</u></span>
              <span class="time"> 驳回时间:{{rejectOpinion.rejectTime}}</span>
            </div>
          </li>
          <li>
            <p class="title">省护路办意见</p>
            <div v-if="opiData.length>2">
              <p class="content">{{auditOpinion.proaud}}</p>
              <span class="money" v-if="ranks == 0">奖励金额<u>{{money.promoney}}</u>元</span>
              <span class="time">2020年3月19日</span>
            </div>
            <div v-else-if="rejectOpinion.rejectRank==0">
              <p class="content">{{rejectOpinion.rejectContent}}</p>
              <span class="money" v-if="ranks==0">驳回人:<u>{{rejectOpinion.rejectName}}</u></span>
              <span class="time"> 驳回时间:{{rejectOpinion.rejectTime}}</span>
            </div>
          </li>
        </ul>
      </div>
      <appendix style="width: 755px" :refId="this.ids" refType="2" :is-upload="false" ref="appendix" :unique="this.ids"></appendix>
    </div>
    <div class="drawer-bootom-button">
      <a-button @click="rewclone" type="primary" :loading="loading">关闭</a-button>
    </div>
  </a-modal>
</template>
<script>
import Appendix from '../../../components/appendix/Appendix'
export default {
  name: '',
  components: {Appendix},
  data () {
    return {
      money: {
        ctiymoney: '',
        statemoney: '',
        promoney: ''
      },
      auditOpinion: {
        ctiyaud: '',
        stateaud: '',
        proaud: ''
      },
      lookList: {},
      loading: false,
      ids: '',
      state: 0,
      opiData: {},
      rejectOpinion: {
        rejectRank: 0,
        rejectContent: '',
        rejectName: '',
        rejectTime: ''
      },
      ranks: 0,
      idenList: {},
      happenTimeyear: '',
      happenTimemon: '',
      happenTimedate: ''
    }
  },
  // 预设窗口弹出关闭状态
  props: {
    rewardlookVisiable: {
      default: false
    }
  },
  mounted () {
    //  获取身份信息
    this.$get('/prizeIdentity/getInformListByMapper').then((r) => {
      this.idenList = r.data
    })
  },
  methods: {
    // 获取标题数据
    setFormValues (user) {
      this.lookList = user
      this.happenTimeyear = this.lookList.happenTime.substring(0, 4)
      this.happenTimemon = this.lookList.happenTime.substring(5, 7)
      this.happenTimedate = this.lookList.happenTime.substring(8, 10)
      this.idenList.forEach((key) => {
        if (this.lookList.identity === key.id) {
          this.lookList.identity = key.identity
        }
      })
      this.ids = user.id
      this.state = user.status
      // 获取当前文档意见
      this.$get('/prize/findOpinion', {prizeId: user.id}).then(res => {
        this.opiData = res.data.data
        this.opiData.forEach((key) => {
          if (key.rank === 1) {
            this.money.ctiymoney = key.money
            this.auditOpinion.ctiyaud = key.auditOpinion
          } else if (key.rank === 4) {
            this.money.statemoney = key.money
            this.auditOpinion.stateaud = key.auditOpinion
          } else if (key.rank === 0) {
            this.money.promoney = key.money
            this.auditOpinion.proaud = key.auditOpinion
          }
        })
      })
      // 获取当前用户rank
      this.$get('/dept/findRank').then(res => {
        this.ranks = res.data.data.rank
      })
      // 获取驳回数据
      this.$get('/prize/findRejectOpinion', {prizeId: user.id}).then(res => {
        this.rejectOpinion.rejectRank = res.data.data.rank
        this.rejectOpinion.rejectContent = res.data.data.auditOpinion
        this.rejectOpinion.rejectName = res.data.data.userName
        this.rejectOpinion.rejectTime = res.data.data.auditTime
      })
    },
    rest () {
      // 清空数据
      this.lookList = {}
      this.money.ctiymoney = ''
      this.auditOpinion.ctiyaud = ''
      this.auditOpinion.stateaud = ''
      this.money.statemoney = ''
      this.auditOpinion.proaud = ''
      this.money.promoney = ''
    },
    // 关闭
    rewclone () {
      this.$emit('close')
      this.rest()
    }
  }
}
</script>
<style scoped>
  /** rest **/
  ul,li{list-style: none;margin: 0;padding: 0;}
  .title{font-weight: bold;}
  .clearboth:after,.clearboth:before{display:block;content: '';clear: both;}
  .looktype{width:765px;padding: 20px 0 50px 0;max-height: 500px;overflow: scroll}
  .looktype .lookborder{width: 750px;margin:0 auto;border: 1px solid #4a4a48;}
  .looktype h1{height: 80px;line-height: 80px; text-align: center;color: red;}
  .clearboth li{float: left;}
  .lookName,.lookCard,.lookState,.lookDescribe{border-bottom: 1px solid #4a4a48;}
  .lookName li{width: 124px; height: 46px;line-height: 46px; text-align: center;}
  .lookName li+li{border-left: 1px solid #4a4a48;}
  .lookCard li{width: 622px; height: 46px;line-height: 46px;text-align: center;}
  .lookCard li.title{width: 125px;border-right: 1px solid #4a4a48;}
  .lookState li{width: 374px;height: 58px;line-height: 26px;text-align: center;}
  .lookState li.title{border-right: 1px solid #4a4a48;}
  .lookDescribe{padding: 20px 20px 10px 20px;}
  .lookDescribe .content{text-indent:25px;}
  .lookOpinion{border-bottom: 1px solid #4a4a48;}
  .lookOpinion li{width: 374px;height: 120px;position: relative;padding: 14px;}
  .lookOpinion li:first-child{border-right: 1px solid #4a4a48;}
  .lookOpinion .content{text-indent:25px;}
  .lookOpinion .money{position: absolute;bottom: 20px;right: 14px;}
  .lookOpinion .time{position: absolute;bottom: 0;right: 14px;}
</style>
