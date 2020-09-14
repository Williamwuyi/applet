<template>
  <a-modal
    :visible="ShModalVisiable"
    :width= "1200"
    :footer="null"
    @cancel="() => { onClose() }"
    loading
    style="overflow: auto; height: 800px"
  >
        <div style="padding-bottom: 20px">
          <h2>乡镇街铁路护路微信平台建设及运行申报表</h2>
          <div class="middle">
            <table border="1">
              <tr>
                <td style="width: 150px;">群&nbsp;&nbsp; 名</td>
                <td colspan="3">{{Shmo.wxName}}</td>
              </tr>
              <tr>
                <td style="width: 300px;">建群时间</td>
                <td>{{Shmo.date}}</td>
                <td style="width: 300px;">所属县市区</td>
                <td v-if="Shmo.szDept">{{Shmo.szDept.deptName}}{{Shmo.qxDept.deptName}}</td>
              </tr>
              <tr>
                <td>群主姓名及职务</td>
                <td>{{Shmo.wxUserName}}({{Shmo.wxUserZw}})</td>
                <td>目前群员人数</td>
                <td>{{Shmo.wxNumber}}</td>
              </tr>
              <tr>
                <td>护路线路及里程</td>
                <td v-if="Shmo.dictLine">{{Shmo.dictLine.valuee}}({{Shmo.qunKm}})公里</td>
                <td>群活跃程度</td>
                <td>{{Shmo.qunHyd}}</td>
              </tr>
              <tr>
                <td>县市区级联络员是否加入</td>
                <td>{{Shmo.xq === 1 ? '是': '否'}}</td>
                <td>铁路公安是否加入</td>
                <td>{{Shmo.ga === 1 ? '是': '否'}}</td>
              </tr>
              <tr>
                <td>市州级联络员是否加入</td>
                <td>{{Shmo.sz === 1 ? '是': '否'}}</td>
                <td>铁路工务是否加入</td>
                <td>{{Shmo.gw === 1 ? '是': '否'}}</td>
              </tr>
              <tr>
                <td>省考核乡镇专用微信是否加入</td>
                <td>{{Shmo.zy === 1 ? '是': '否'}}</td>
                <td>铁路电务是否加入</td>
                <td>{{Shmo.dw === 1 ? '是': '否'}}</td>
              </tr>
              <tr>
                <td>路地临时施工单位是否加入</td>
                <td>{{Shmo.sg === 1 ? '是': '否'}}</td>
                <td>铁路车务（车站）是否加入</td>
                <td>{{Shmo.cw === 1 ? '是': '否'}}</td>
              </tr>
              <tr>
                <td>铁路护路信息是否占90%以上</td>
                <td colspan="3">{{Shmo.hlxx === 1 ? '是': '否'}}</td>
              </tr>
              <tr>
                <td rowspan="2">执行湘护路护线组[2016]2号文件情况</td>
                <td rowspan="2" colspan="3">
                  {{Shmo.file1}}
                </td>
              </tr>
              <tr>
              </tr>
              <tr>
                <td rowspan="3">解决具体问题个数及解决主要问题简要情况</td>
                <td rowspan="3" colspan="3">{{Shmo.file2}}</td>
              </tr>
              <tr>
              </tr>
              <tr>
              </tr>
              <tr>
                <td rowspan="3">主要经验做法</td>
                <td rowspan="3" colspan="3">{{Shmo.file3}}</td>
              </tr>
              <tr>
              </tr>
              <tr>
              </tr>
              <tr>
                <td>奖励申请</td>
                <td colspan="3">{{Shmo.award}}</td>
              </tr>
              <tr>
                <td>省办审批</td>
                <td colspan="3">{{Shmo.sbyj}}</td>
              </tr>
            </table>
          </div>
          <div class="flooter">
            <div>填报单位：{{Shmo.fillDeptId}}</div>
            <div>填报时间：{{Shmo.fillDate}}</div>
          </div>
        </div>
  </a-modal>
</template>
<script>
export default {
  name: 'ShModal',
  props: ['ShModalVisiable'],
  data () {
    return {
      Shmo: {}
    }
  },
  methods: {
    onClose () {
      this.$emit('error')
    },
    getSh (key) {
      console.log(key)
      this.$get('/wx/qun-js/selectById', {wXId: key}).then(res => {
        console.log(res)
        this.Shmo = res.data.data
      })
    },
    htt (value1, value2) {
      console.log(value1, value2)
    }
  }
}
</script>

<style scoped>
 h2 {
   text-align: center;
   font-weight: 600;
 }
 .flooter div {
   width: 50%;
   float: left;
   text-align: center;
   font-size: 18px;
 }
  .middle {
    width: 100%;
  }
 .middle table {
   width: 800px;
   margin: 0 auto;
 }
 .middle table tr {
   height: 40px;
 }
 .middle table td {
    text-align: center;
   font-size: 16px;
  }
</style>
