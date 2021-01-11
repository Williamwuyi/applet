<template>
  <a-modal
    :visible="rewardApvals"
    title="审批"
    :width= "794"
    :footer="null"
    style="height: 800px; overflow: auto;"
    @cancel="onClose"
  >
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :pagination="false"
    >
      <a slot="content" slot-scope="text,record" style="color:#6290FF" @click="look(record)">{{ text.slice(0,30) }}</a>
      <template slot="idMoney" slot-scope="text, record, index">
        <a-input type="number" v-model="moneyData[index]" @blur="e => test(e.target.value, record, index)"/>
      </template>
      <template slot="status" slot-scope="text, record">
        <a-tag v-if="record.status === 3" color="#DEE1E6">未审批</a-tag>
        <a-tag v-else-if="record.status === 5 && ranks===2" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===2" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===2" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===4" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===4" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 5 && ranks===1" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===0" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===0" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 8" color="#87d068" >已完结</a-tag>
        <a-tag v-else-if="record.status === 2" color="#FF0033">被驳回</a-tag>
      </template>
      <template slot="operation" slot-scope="text, record, index">
        <a-icon type="close-circle"  style="color:#FF0033;margin:0"  @click="deletes(index)" title="删除" />
      </template>
    </a-table>
    <a-form :form="form" style="padding-bottom: 50px">
<!--如果是市级是上报到公安处或者派出所上报到公安处的需要选择提交的公安处-->
      <a-row v-if="ranks===1 || userParRank === 4">
        <a-col :span="24">
          <a-form-item
            :label-col="{span: 4}"
            :wrapper-col="{span: 18}"
            label="选择抄送的单位:"
          >
            <a-select
              v-decorator="[
                'deptName',
                { rules: [{ required: true, message: '请选择抄送的单位' }] },
              ]"
            >
              <a-select-option v-for="item in deptNames" :key="item.deptId" :value="item.deptId">
                  {{item.deptName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <!-- 如果是公安处提交给市级的需要选择市级 -->
      <a-row v-else-if="ranks===4">
        <a-col :span="24">
          <a-form-item
            :label-col="{span: 4}"
            :wrapper-col="{span: 18}"
            label="选择提交市级单位:"
          >
            <a-select
              v-decorator="[
                'deptName',
                { rules: [{ required: true, message: '请选择提交的市级单位!' }] },
              ]"
            >
              <a-select-option v-for="item in deptNames" :key="item.deptId" :value="item.deptId">
                {{item.deptName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
<!--      填写审批意见-->
      <a-row>
        <a-col :span="24">
          <a-form-item
            :label-col="{span: 4}"
            :wrapper-col="{span: 18}"
            label="审批意见:"
          >
            <a-textarea
              allow-clear
              v-decorator="[
                  'auditOpinion',
                  { rules: [{ required: true, message: '请输入审批意见' }] },
                ]"
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃审批？" @confirm="onClose"  okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="submitApval" type="primary" :loading="loading">提交</a-button>
    </div>
    <!--   查看-->
    <rewardLook
      :rewardlookVisiable="rewardlookVisiable"
      @close="hanleedLoclose"
      ref="nolook"
    ></rewardLook>
  </a-modal>
</template>
<script>
import rewardLook from './rewardLook'
export default {
  name: 'rewardapvals',
  components: {rewardLook},
  props: {
    rewardApvals: {
      default: false
    }
  },
  data () {
    return {
      form: this.$form.createForm(this),
      dataSource: [],
      loading: false,
      dataSources: [],
      prizeIds: [],
      ranks: 5,
      deptNames: {},
      rewardlookVisiable: false,
      moneyData: [],
      moneys: [],
      userParRank: 0
    }
  },
  mounted () {
    // 获取当前用户的rank
    this.$get('/dept/findRank').then(res => {
      this.ranks = res.data.data.rank
    })
    // 因派出所rank值与街道办事处相同，需要选择公安处，根据父级单位rank判断是否为派出所
    this.$get('/dept/findRankIfFour').then(res => {
      this.userParRank = res.data.data
    })
  },
  // 初始表格信息
  computed: {
    columns () {
      return [
        {
          title: '编号',
          dataIndex: 'newNumber',
          width: '100px',
          scopedSlots: { customRender: 'number' },
          align: 'center'
        },
        {
          title: '事迹简介',
          dataIndex: 'content',
          width: '30%',
          ellipsis: true,
          scopedSlots: { customRender: 'content' }
        },
        {
          title: '当前状态',
          dataIndex: 'state',
          scopedSlots: { customRender: 'status' },
          align: 'center'
        },
        {
          title: '奖励金额',
          dataIndex: 'idMoney',
          scopedSlots: { customRender: 'idMoney' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          align: 'center',
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  methods: {
    setTableValues (user) {
      if (user instanceof Array) {
        this.dataSource = user
      } else {
        this.dataSource = [user]
      }
      if (this.ranks === 1) {
        // 获取所有公安处
        this.$get('/dept/findRankFour').then(res => {
          this.deptNames = res.data.data
        })
      } else if (this.ranks === 4) {
        // 如果是公安处提交到市级
        this.$get('/dept/queryDeptChileByPrize').then((r) => {
          this.deptNames = r.data.data
        })
      }
    },
    deletes (index) {
      this.dataSource.splice(index, 1)
      this.moneyData.splice(index, 1)
    },
    test (value, key, index) {
      this.moneyData[index] = value
    },
    // 提交审批意见
    submitApval () {
      let apvalStus = []
      this.dataSource.forEach((key, index) => {
        apvalStus[index] = key.status
        this.prizeIds[index] = key.id
      })
      if ((apvalStus.includes(6) && this.ranks === 1) ||
          (apvalStus.includes(7) && this.ranks === 1) ||
          (apvalStus.includes(8) && this.ranks === 1) ||
          (apvalStus.includes(7) && this.ranks === 4) ||
          (apvalStus.includes(8) && this.ranks === 4) ||
          (apvalStus.includes(5) && this.ranks === 2) ||
          (apvalStus.includes(6) && this.ranks === 2) ||
          (apvalStus.includes(7) && this.ranks === 2) ||
          (apvalStus.includes(8) && this.ranks === 2) ||
          (apvalStus.includes(8) && this.ranks === 0)) {
        this.$message.error('存在已审批数据,请移除后再提交')
      } else if (apvalStus.includes(2)) {
        this.$message.error('存在被驳回数据,请移除后再提交')
      } else {
        if (this.prizeIds.length !== this.moneyData.length) {
          this.$message.error('存在金额缺失,请核对后审批')
        } else {
          this.prizeIds.forEach((key, index) => {
            let money = {}
            money.prizeId = this.prizeIds[index]
            money.opinionMoney = this.moneyData[index]
            this.moneys.push(money)
          })
          this.form.validateFields((err, values) => {
            let sendDeptId = ''
            let sendCityId = ''
            if (this.ranks === 1 || this.userParRank === 4) {
              // 如果是市级单位或者派出所审批需要提交选择公安处
              sendDeptId = values.deptName
            } else if (this.ranks === 4) {
              // 如果是公安处需要审批提交选择市级单位
              sendCityId = values.deptName
            }
            this.dataSource.forEach((key, index) => {
              apvalStus[index] = key.status
            })
            let param = {
              auditOpinion: values.auditOpinion,
              moneys: JSON.stringify(this.moneys),
              sendDeptId: sendDeptId,
              prizeIds: this.prizeIds,
              sendCityId: sendCityId
            }
            if (!err) {
              this.loading = true
              this.$post('prize/report', param).then(() => {
                let status = {prizeIds: this.prizeIds, value: 1}
                if (this.ranks === 1) {
                  this.$put('prize/updateCityStatus', status).then(() => {})
                } else if (this.ranks === 4) {
                  this.$put('prize/updateGongStatus', status).then(() => {})
                }
                this.reset()
                this.$emit('success')
              }).catch(() => {
                this.loading = false
              })
            }
          })
        }
      }
    },
    look (record) {
      this.rewardlookVisiable = true
      this.$refs.nolook.setFormValues(record)
    },
    // 关闭查看页面
    hanleedLoclose () {
      this.rewardlookVisiable = false
    },
    // 重置
    reset () {
      this.loading = false
      this.form.resetFields()
      this.prizeIds = []
      this.moneyData = []
      this.dataSource = []
    },
    // 关闭
    onClose () {
      this.reset()
      this.$emit('close')
    }
  }
}
</script>
<style scoped>
  textarea{height: 32px}
</style>
