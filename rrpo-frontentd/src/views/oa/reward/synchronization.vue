<template>
  <a-modal
    :visible="synchronization"
    @cancel="() => { rewclone() }"
    :width= "794"
    :footer="null"
    title="同步到门户网"
  >
    <a-form :form="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 12 }" style="padding-bottom: 50px">
      <a-form-item label="来源">
        <a-input
          v-decorator="['source', { rules: [{ required: false}] }]"
        />
      </a-form-item>
      <a-form-item label="标题">
        <a-input
          v-decorator="['title', { rules: [{ required: true, message: '请输入标题' }] }]"
        />
      </a-form-item>
      <a-form-item label="选择栏目">
        <a-select
          v-decorator="[
          'targetsId',
          { rules: [{ required: true, message: '请选择栏目' }] },
        ]"
          placeholder="请选择同步到门户网栏目"
          @change="handleSelectChange"
        >
          <a-select-option v-for="item in column" :value="item.id" :key="item.name">
            {{item.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃同步？" @confirm="rewclone"  okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="check" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-modal>
</template>

<script>
export default {
  name: 'rewardout',
  props: {
    synchronization: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      data: '',
      form: this.$form.createForm(this),
      column: {},
      loading: false,
      prizeId: ''
    }
  },
  mounted () {
    this.$get('/dict/getTargets').then((res) => {
      let statu = res.data.status
      if (statu === 1) {
        this.column = res.data.data.targets
      } else {
        this.column = ''
      }
    })
  },
  methods: {
    handleSelectChange (e) {
    },
    setTableValues (r) {
      this.prizeId = r.id
    },
    // 关闭
    rewclone () {
      this.$emit('close')
      this.rest()
    },
    // 提交
    check () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          values.prizeId = this.prizeId
          this.$post('/prize/menHu', values).then(() => {
            this.rest()
            this.$message.success('同步到门户网成功')
            this.loading = false
            this.rewclone()
          })
        }
      })
    },
    rest () {
      this.form.resetFields()
      this.loading = false
    }
  }
}
</script>

<style scoped>

</style>
