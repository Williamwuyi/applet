<template>
  <a-modal
    :visible="rewardReject"
    title="驳回"
    :width= "794"
    :footer="null"
    style="height: 800px; overflow: auto"
    @cancel="() => { onClose() }"
  >
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :pagination="false"
    >
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
  </a-modal>
</template>

<script>
export default {
  name: 'rewardapvals',
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
      ranks: 5
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
          dataIndex: 'number',
          width: '10%',
          scopedSlots: { customRender: 'number' },
          align: 'center'
        },
        {
          title: '事迹简介',
          dataIndex: 'content',
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
      if ((apvalStus.includes(5) && this.ranks === 1) || (apvalStus.includes(6) && this.ranks === 1) || (apvalStus.includes(7) && this.ranks === 1) || (apvalStus.includes(6) && this.ranks === 4) || (apvalStus.includes(7) && this.ranks === 4) || (apvalStus.includes(7) && this.ranks === 0)) {
        console.log('存在已审批数据！')
        this.$message.error('存在已审批数据,请移除后再提交')
      } else if (apvalStus.includes(2)) {
        console.log('存在被驳回数据!')
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
      console.log(11111)
      this.reset()
      this.$emit('close')
    }
  }
}
</script>

<style scoped></style>
