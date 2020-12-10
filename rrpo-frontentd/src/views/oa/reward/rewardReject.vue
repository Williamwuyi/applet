<template>
  <a-modal
    :visible="rewardReject"
    title="驳回"
    :width= "794"
    :footer="null"
    @cancel="() => { onClose() }"
  >
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :pagination="false"
    >
      <a slot="content" slot-scope="text,record" style="color:#6290FF" @click="look(record)">{{ text }}</a>
      <template slot="status" slot-scope="text, record">
        <a-tag v-if="record.status === 3" color="#DEE1E6">未审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 8 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 8 && ranks===0" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===4" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 8 && ranks===4" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 5 && ranks===2" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===2" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===2" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 8 && ranks===2" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 5 && ranks===1" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===4" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===0" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===0" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 8" color="#87d068" >已完结</a-tag>
        <a-tag v-else-if="record.status === 2" color="#FF0033">被驳回</a-tag>
      </template>
      <template slot="operation" slot-scope="text, record, index">
        <a-icon type="delete"  style="color:#FF0033;margin:0"  @click="deletes(index)" title="删除" />
      </template>
    </a-table>
    <a-form :form="form" style="padding-bottom: 50px;margin-top: 30px">
      <!--      填写驳回意见-->
      <a-row>
        <a-col :span="24">
          <a-form-item
            :label-col="{span: 8}"
            :wrapper-col="{span: 12}"
            label="驳回意见:"
          >
            <a-textarea
              allow-clear
              v-decorator="[
                  'additionalPro',
                  { rules: [{ required: true, message: '请输入驳回意见' }] },
                ]"
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃驳回？" @confirm="onClose"  okText="确定" cancelText="取消">
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
    rewardReject: {
      default: false
    }
  },
  data () {
    return {
      form: this.$form.createForm(this),
      dataSource: [],
      loading: false,
      prizeIds: [],
      ranks: 5,
      rewardlookVisiable: false
    }
  },
  mounted () {
    // 获取当前用户的rank
    this.$get('/dept/findRank').then(res => {
      this.ranks = res.data.data.rank
    })
  },
  // 初始表格信息
  computed: {
    columns () {
      return [
        {
          title: '编号',
          dataIndex: 'newNumber',
          width: '10%',
          scopedSlots: { customRender: 'number' },
          align: 'center'
        },
        {
          title: '事迹简介',
          dataIndex: 'content',
          ellipsis: true,
          scopedSlots: { customRender: 'content' }
        },
        {
          title: '当前状态',
          dataIndex: 'state',
          width: '15%',
          scopedSlots: { customRender: 'status' },
          align: 'center'
        },
        {
          title: '操作',
          width: '10%',
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
    },
    deletes (index) {
      this.dataSource.splice(index, 1)
    },
    // 提交驳回意见
    submitApval () {
      let apvalStus = []
      this.dataSource.forEach((key, index) => {
        apvalStus[index] = key.status
      })
      if ((apvalStus.includes(6) && this.ranks === 1) ||
        (apvalStus.includes(7) && this.ranks === 1) ||
        (apvalStus.includes(8) && this.ranks === 1) ||
        (apvalStus.includes(7) && this.ranks === 4) ||
        (apvalStus.includes(5) && this.ranks === 2) ||
        (apvalStus.includes(6) && this.ranks === 2) ||
        (apvalStus.includes(7) && this.ranks === 2) ||
        (apvalStus.includes(8) && this.ranks === 2) ||
        (apvalStus.includes(8) && this.ranks === 4) ||
        (apvalStus.includes(8) && this.ranks === 0)) {
        this.$message.error('存在已审批数据,请移除后再提交')
      } else if (apvalStus.includes(2)) {
        this.$message.error('存在已驳回数据,请移除后再提交')
      } else {
        this.dataSource.forEach((key, index) => {
          this.prizeIds[index] = key.id
        })
        this.form.validateFields((err, values) => {
          let Opinion = values.additionalPro
          let param = {
            prizeIds: this.prizeIds,
            rejectAuditOpinion: Opinion
          }
          if (!err) {
            this.loading = true
            this.$get('/prize/reject/list', param).then(() => {
              this.reset()
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          }
        })
      }
    },
    // 重置
    reset () {
      this.loading = false
      this.form.resetFields()
      this.dataSource = []
      this.prizeIds = []
    },
    // 关闭
    onClose () {
      this.reset()
      this.$emit('close')
    },
    // 查看详情
    look (record) {
      this.rewardlookVisiable = true
      this.$refs.nolook.setFormValues(record)
    },
    // 关闭查看页面
    hanleedLoclose () {
      this.rewardlookVisiable = false
    }
  }
}
</script>

<style scoped></style>
