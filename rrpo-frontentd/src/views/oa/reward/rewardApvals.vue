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
        <a-input type="number" @blur="e => test(e.target.value, record, index)"/>
      </template>
      <template slot="status" slot-scope="text, record">
        <a-tag v-if="record.status === 3" color="#DEE1E6">未审批</a-tag>
        <a-tag v-else-if="record.status === 5 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 5 && ranks===4" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===4" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===4" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===0" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===0" color="#87d068" >已完结</a-tag>
        <a-tag v-else-if="record.status === 2" color="#FF0033">被驳回</a-tag>
      </template>
      <template slot="operation" slot-scope="text, record, index">
        <a-icon type="delete"  style="color:#FF0033;margin:0"  @click="deletes(index)" title="删除" />
      </template>
    </a-table>
    <a-form :form="form" style="padding-bottom: 50px">
<!--      选择提交的铁护办-->
      <a-row v-if="ranks===1">
        <a-col :span="24">
          <a-form-item
            :label-col="{span: 4}"
            :wrapper-col="{span: 18}"
            label="选择提交单位:"
          >
            <a-select
              v-decorator="[
                'deptName',
                { rules: [{ required: true, message: '请选择提交单位!' }] },
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
      moneys: []
    }
  },
  mounted () {
    // 获取当前用户的rank
    this.$get('/dept/findRank').then(res => {
      this.ranks = res.data.data.rank
    })
    // 获取所有公安处
    this.$get('/dept/findRankFour').then(res => {
      console.log('公安处列表', res)
      this.deptNames = res.data.data
    })
  },
  // 初始表格信息
  computed: {
    columns () {
      return [
        {
          title: '编号',
          dataIndex: 'number',
          width: '10%',
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
        console.log(this.dataSource)
      } else {
        this.dataSource = [user]
        console.log(this.dataSource)
      }
    },
    deletes (index) {
      this.dataSource.splice(index, 1)
    },
    test (value, key, index) {
      console.log(value, key, index)
      if ((this.ranks === 1 && key.status >= 5) || key.status === 2 || (this.ranks === 4 && key.status >= 6) || (this.ranks === 0 && key.status === 7)) {
        this.$message.error('已审批/驳回数据不可再次审批')
      } else if (value === '') {
        this.$message.error('审批金额不得为空')
      } else {
        this.moneyData[index] = value
      }
    },
    // 提交审批意见
    submitApval () {
      let apvalStus = []
      this.dataSource.forEach((key, index) => {
        apvalStus[index] = key.status
        this.prizeIds[index] = key.id
      })
      if ((apvalStus.includes(5) && this.ranks === 1) || (apvalStus.includes(6) && this.ranks === 1) || (apvalStus.includes(7) && this.ranks === 1) || (apvalStus.includes(6) && this.ranks === 4) || (apvalStus.includes(7) && this.ranks === 4) || (apvalStus.includes(7) && this.ranks === 0)) {
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
          console.log(JSON.stringify(this.moneys))
          this.form.validateFields((err, values) => {
            let sendDeptId = ''
            if (this.ranks === 1) {
              sendDeptId = values.deptName
            } else {
              sendDeptId = ''
            }
            this.dataSource.forEach((key, index) => {
              apvalStus[index] = key.status
            })
            let param = {
              auditOpinion: values.auditOpinion,
              moneys: JSON.stringify(this.moneys),
              sendDeptId: sendDeptId,
              prizeIds: this.prizeIds
            }
            if (!err) {
              console.log('提交到后台的数据----', param)
              this.loading = true
              this.$post('prize/report', param).then(() => {
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
