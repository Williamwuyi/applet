<template>
  <a-modal
  :visible="NoticelookVisiable"
  :width= "1200"
  :footer="null"
  style="height: 800px; overflow: auto"
  @cancel="() => { onClose() }"
  >
  <div class="modalShow">
    <div class="lookHeader">
      <h2>来自{{title}}的月度评选材料上报</h2>
      <div class="msegsize">调整字体:
        <span @click="bingClick(0)">大</span>
        <span @click="bingClick(1)">中</span>
        <span @click="bingClick(2)">小</span>
        </div>
    </div>
  <hr />
  <div class="middle" v-html="createneirong" style="white-space: normal"></div>
  <hr />
    <div class="tab" style="margin-bottom: 20px">
      <appendix :refId="refId" :unique="refId" refType="9" :is-upload="true"></appendix>
    </div>
    <a-form-item
      v-hasPermission="'monthKey:proposal'"
      label="初评意见"
      style="font-size: 22px"
      :labelCol="{span: 7}"
      :wrapperCol="{span: 15, offset: 0}">
      <a-radio-group v-model="value" @change="onChange" style="font-size: 18px">
        <a-radio :value="1">
          可入
        </a-radio>
        <a-radio :value="2">
          不可入
        </a-radio>
      </a-radio-group>
    </a-form-item>
    <div class="footer">
      <a-button @click="onClose">取消</a-button>
      <a-button @click="handleSubmit(0)" style="background-color: #ED5239; color: #fff">返回</a-button>
      <a-button style="background-color: #00C853; color: #fff" @click="handleSubmit(1)">通过</a-button>
    </div>
  </div>
  </a-modal>
</template>
<script>
import Appendix from '@/components/appendix/Appendix'
export default {
  name: 'Noticelook',
  components: {Appendix},
  props: {
    NoticelookVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      createTime: '',
      createUser: '',
      title: '',
      createneirong: '',
      value: '',
      refId: '',
      message: '',
      dataSource: []
    }
  },
  computed: {
    columns () {
      return [{
        title: '文件名',
        dataIndex: 'deptJc.deptName'
      }, {
        title: '上传时间',
        dataIndex: 'wxName'
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.refId = ''
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    setFormValues (user) {
      this.refId = ''
      this.refId = user.wxMonthId
      this.createTime = user.date
      this.title = user.deptJc.deptName
      this.createneirong = user.content
    },
    bingClick (num) {
      var middle = document.querySelector('.middle')
      switch (num) {
        case 0: middle.style.fontSize = '36px'
          break
        case 1: middle.style.fontSize = '24px'
          break
        case 2: middle.style.fontSize = '16px'
          break
      }
    },
    onChange () {
      console.log(this.value)
    },
    // 操作
    handleSubmit (key) {
      this.$get('/wx/month/appear', {monthId: this.refId, status: key}).then(res => {
        this.$emit('close')
        this.$message.success('操作成功')
      })
    }
  }
}
</script>
<style scoped>
.modalShow {
  background-color: #fff;
  min-height: 500px;
}
h2 {
  text-align: center;
  color: #149EC5;
  font-size: 28px;
}
.middle {
  width: 80%;
  margin: 20px auto;
}
.floot {
  height: 100px;
  width: 100%;
  text-align: right;
}
.msegsize {
text-align: center;
font-size: 16px;
background-color: #F7F7F7;
  position: relative;
}
.msegsize span {
  margin-top: 10px;
  margin-left: 10px;
  color: #249DD8;
  cursor:pointer
}
#issuer {
  position: absolute;
  right: 0;
  top: 0;
}
.footer {
  text-align: right;
  margin-top: 5px;
}
</style>
