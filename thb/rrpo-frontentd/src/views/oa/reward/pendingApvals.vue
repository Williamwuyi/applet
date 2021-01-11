<template>
  <a-modal
    :visible="rewardApvals"
    title="联签"
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
      <template slot="status" slot-scope="text, record">
        <a-tag v-if="record.status === 3" color="#DEE1E6">未签署</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===1" color="#87d068" >已签署</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===4" color="#87d068" >已签署</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===4" color="#DEE1E6" >未签署</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===1" color="#DEE1E6" >未签署</a-tag>
        <a-tag v-else-if="record.status === 8" color="#87d068" >已完结</a-tag>
        <a-tag v-else-if="record.status === 2" color="#FF0033">被驳回</a-tag>
      </template>
      <template slot="operation" slot-scope="text, record, index">
        <a-icon type="close-circle"  style="color:#FF0033;margin:0"  @click="deletes(index)" title="删除" />
      </template>
    </a-table>
    <a-form :form="form" style="padding:50px 0">
      <!--      填写审批意见-->
      <a-row>
        <a-col :span="10">
          <a-form-item
            :label-col="{span: 6}"
            :wrapper-col="{span: 16}"
            label="签署意见:"
          >
            <a-radio-group name="radioGroup" @change="radio" v-model="radiom" :default-value="1">
              <a-radio value="同意">
                同意
              </a-radio>
              <a-radio value="1">
                其他
              </a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>
        <a-col :span="14" v-if='isTrue'>
          <a-form-item>
            <a-textarea
              allow-clear
              v-decorator="[
                  'additionalPro',
                  { rules: [{ required: true, message: '请输入签署意见' }] },
                ]"
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃签署？" @confirm="onClose"  okText="确定" cancelText="取消">
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
      userParRank: 0,
      radiom: '同意',
      isTrue: false
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
      if (this.ranks !== 4) {
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
    },
    radio () {
      console.log(this.radiom)
      if (this.radiom === '1') {
        this.isTrue = true
      } else {
        this.isTrue = false
      }
    },
    test (value, key, index) {
      if ((this.ranks === 1 && (key.status === 6 || key.status === 8)) || key.status === 2 || (this.ranks === 4 && (key.status === 8 || key.status === 7))) {
        this.$message.error('已签署/驳回数据不可再次审批')
      } else {
        this.moneyData[index] = value
      }
    },
    // 提交审批意见
    submitApval () {
      let apvalStus = []
      this.dataSource.forEach((key, index) => {
        apvalStus[index] = key.status // 获取审批数据的状态
        this.prizeIds[index] = key.id // 获取审批数据的ID
      })
      console.log('apvalStus', apvalStus)
      if ((apvalStus.includes(6) && this.ranks === 1) || (apvalStus.includes(8) && this.ranks === 1) || (apvalStus.includes(7) && this.ranks === 4) || (apvalStus.includes(7) && this.ranks === 4)) {
        this.$message.error('存在已签署数据,请移除后再提交')
      } else if (apvalStus.includes(2)) {
        this.$message.error('存在被驳回数据,请移除后再提交')
      } else {
        this.dataSource.forEach((key, index) => {
          apvalStus[index] = key.status
        })

        this.form.validateFields((err, values) => {
          let Opinion = values.additionalPro
          let param = {
            auditOpinion: this.radiom,
            prizeIds: this.prizeIds
          }
          if (this.radiom === '1') {
            param.auditOpinion = Opinion
          }
          if (!err) {
            console.log('提交到后台的数据', param)
            this.loading = true
            this.$get('/prize/reject/list', param).then(() => {
              this.reset()
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          }
        })
        // let param = {
        //   auditOpinion: this.radiom,
        //   prizeIds: this.prizeIds
        // }
        // this.loading = true
        // console.log('提交给到后台的数据', param)
        // this.$post('prize/report', param).then(() => {
        //   this.reset()
        //   this.$emit('success')
        // }).catch(() => {
        //   this.loading = false
        // })
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
