<template>
  <a-modal
    v-if="arr"
    :visible="yearExamVisible"
    width= "100%"
    :dialogStyle="{top: 0}"
    @cancel="() => { onClose() }"
    :footer="null"
  >
    <a-affix :offset-top="0">
      <fuchuang :numArr="numArr"/>
    </a-affix>
    <div v-if="arr" style="text-align: center; max-height: calc(100vh - 35px);overflow: auto" ref="container">
      <a-affix :offset-top="top" :target="() => this.$refs.container">
    <div style="background-color: white">
      <h2 style="text-align: center; font-size: 31px; line-height: 31px">年度考核详细信息</h2>
      <hr />
    </div>
      </a-affix>
      <a-spin :spinning="spinning">
    <table  width="100%" v-if="wachws.list.length > 0"  v-for="(wachws, index) in arr"  :key="wachws.name"  border="1" class="t1" align="center" cellpadding="0" cellspacing="0" style="font-size: 14px">
      <tr v-if="index === 0">
        <th style="width: 130px;">一级指标</th>
        <th style="width: 70px;">序号</th>
        <th>评估内容</th>
        <th style="width: 70px;">加减分</th>
        <th style="width: 50px;">佐证材料</th>
        <th style="width: 40px;">自评得分</th>
        <th style="width: 250px;">自评描述</th>
        <th style="width: 40px;">复评得分</th>
        <th style="width: 250px;">复评描述</th>
        <th style="width: 70px;">状态</th>
        <th style="width: 40px;">终评得分</th>
        <th style="width: 250px;">终评描述</th>
      </tr>
      <tr>
        <td style="width: 130px;" :rowspan="wachws.list.length + 1">{{wachws.name}}</td>
      </tr>
      <tr v-for="(item) in wachws.list" :key="item.gradeId" v-if="item.menusYear">
        <td style="width: 70px;">{{item.bbc}}</td>
        <td  style="text-align: left">{{item.menusYear.content}}</td>
        <td style="width: 70px;">
          <a-icon type="plus-square" v-if="item.menusYear.saveOrDelete === '加分项'" style="color: #52c41a"/>
          <a-icon type="minus-square" v-if="item.menusYear.saveOrDelete === '减分项'" style="color: red"/>
        </td>
        <td style="width: 50px;">
          <a-button  @click="condProof(item)">查看</a-button>
        </td>
        <td style="width: 40px;">{{item.num}}</td>
        <td style="width: 250px;">{{item.message}}</td>
        <td style="width: 40px;" ><a-input-number :id="item.gradeId" :disabled="InputShow" v-model="item.num2" @blur="onChange(item.gradeId, $event)" :default-value="item.num2"/></td>
        <td style="width: 250px;"><a-textarea
          :autoSize=true
          @change="onChangeMsg(item.gradeId, $event)" :default-value="item.message2" :read-only="InputShow"
          placeholder="请输入简要情况" allow-clear /></td>
<!--        <a-input @change="onChangeMsg(item.gradeId, $event)" :default-value="item.message2" :read-only="InputShow"></a-input>-->
        <td style="width: 70px;">
          <a-tag v-if="item.status === 0" color="#87d068">无异议</a-tag>
          <a-tag v-else-if="item.status === 1" color="#f50">有异议</a-tag>
        </td>
        <td style="width: 40px;"><span>{{item.fpNum}}</span></td>
        <td style="width: 250px;"><span>{{item.fpMessage}}</span></td>
      </tr>
    </table>
      </a-spin>
    <!--  佐证材料-->
    <year-exlook-proof
      :prooflookVisibt="prooflookVisibt"
      @close = 'proofClose'
      @success="proofSuccess"
      ref="looKclick"
    />
      <a-affix :offset-bottom="bottom" :target="() => this.$refs.container">
    <div class="footer">
      <hr />
      <a-button @click="onClose">取消</a-button>
      <a-button style="background-color: #00C853; color: #fff" @click="handleSubmit(2)" v-if="TJshow">重新提交</a-button>
    </div>
      </a-affix>
    </div>
  </a-modal>
</template>

<script>
import fuchuang from './fuchuang'
import yearExlookProof from './yearExlookProof'
export default {
  name: 'yearExaminationlook',
  components: {yearExlookProof, fuchuang},
  props: ['yearExamVisible'],
  data () {
    return {
      top: 0,
      bottom: 0,
      arr: [],
      numArr: {
        gzxg: 0,
        jcgzX: 0,
        jcgzY: 0
      },
      yearId: '',
      deptId: '',
      TJshow: true,
      InputShow: false,
      spinning: false,
      prooflookVisibt: false
    }
  },
  computed: {
    twoNum () {
      return 1
    }
  },
  methods: {
    fach (key) {
      let TimeStatus = new Date().getTime() > new Date(key.deadDate).getTime()
      // 当状态为终审时和已经重新提交,超过规定提交时间后 不可重新提交  输入框不可输入
      if (key.status === 3 || key.status === 2 || TimeStatus) {
        this.TJshow = false
        this.InputShow = true
      }
      let prames = {}
      prames.deptId = key.dept.deptId
      prames.yearId = key.yearId
      this.deptId = key.dept.deptId
      this.yearId = key.yearId
      this.spinning = true
      this.$get('/check/grade/selectByUserIdOrDateYear', prames).then(res => {
        this.arr = res.data.data
        this.spinning = false
        this.statistical()
        this.getNum()
      })
    },
    getNum () {
      for (var k = 0; k < this.arr.length; k++) {
        let index = k + 1
        let zf = this.arr[k].list[0].menusYear.saveOrDelete
        let number = 1
        let number1 = 1
        for (var i = 0; i < this.arr[k].list.length; i++) {
          if (zf !== this.arr[k].list[i].menusYear.saveOrDelete) {
            number++
            number1 = 1
            zf = this.arr[k].list[i].menusYear.saveOrDelete
          }
          let str1 = index + '-' + number + '-' + number1
          number1++
          this.arr[k].list[i].bbc = str1
        }
      }
    },
    onClose () {
      this.rest()
      this.$emit('close')
    },
    rest () {
      this.TJshow = true
      this.numArr = {
        gzxg: 0,
        jcgzX: 0,
        jcgzY: 0
      }
    },
    // 统计分数
    statistical () {
      this.arr.forEach(t => {
        // 判断当前有题
        if (t.list.length > 0) {
          // 判断基础工作
          if (t.id === '76b942fb606de4b0ba62e68cc4ecf69c') {
            t.list.forEach(i => {
              if (i.menusYear.saveOrDelete === '加分项') {
                this.numArr.jcgzX += i.num2
              }
              if (i.menusYear.saveOrDelete === '减分项') {
                this.numArr.jcgzY += i.num2
              }
            })
          }
          // 判断工作效果
          if (t.id === '93d0a833e9fb2e7cd7d6a6d82ec16b86') {
            t.list.forEach(i => {
              this.numArr.gzxg += i.num2
            })
          }
        }
      })
    },
    // 查看
    condProof (item) {
      this.prooflookVisibt = true
      item.yearId = this.yearId
      item.deptId = this.deptId
      this.$refs.looKclick.getyearDate(item)
    },
    proofClose () {
      this.prooflookVisibt = false
    },
    proofSuccess () {
      this.prooflookVisibt = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    // 重新自评分
    onChange (id, event) {
      this.numArr.gzxg = 0
      this.numArr.jcgzY = 0
      this.numArr.jcgzX = 0
      this.statistical()
      let pramase = {}
      pramase.gradeId = id
      pramase.type = 2
      pramase.number = event.target.value
      this.$post('/check/grade/addGrade', pramase).then(res => {
      })
    },
    // 新自评描述
    onChangeMsg (id, event) {
      let pramase = {}
      pramase.gradeId = id
      pramase.type = 2
      pramase.message = event.target.value
      this.$post('/check/grade/addGrade', pramase).then(res => {
      })
    },
    // 提交
    handleSubmit (key) {
      this.$get('/check/grade/commit', {deptId: this.deptId, yearId: this.yearId, status: key}).then(res => {
        this.$emit('success')
      })
    }
  }
}
</script>

<style scoped>
  .t1
  {
    clear: both;
    border: 1px solid #c9dae4;
  }
  .t1 tr th
  {
    color: #0d487b;
    background: #f2f4f8;
    line-height: 28px;
    border-bottom: 1px solid #9cb6cf;
    border-top: 1px solid #e9edf3;
    font-weight: normal;
    text-shadow: #e6ecf3 1px 1px 0px;
    padding-left: 5px;
    padding-right: 5px;
  }
  .t1 tr td
  {
    border-bottom: 1px solid #e9e9e9;
    padding-bottom: 5px;
    padding-top: 5px;
    color: #444;
    border-top: 1px solid #FFFFFF;
    padding-left: 5px;
    padding-right: 5px;
    word-break: break-all;
  }
  #rcDialogTitle0 {
    font-size: 34px;
  }
  .footer {
    text-align: right;
    margin-top: 5px;
    background-color: white;
  }
</style>
