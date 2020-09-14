<template>
  <a-modal
    :visible="subVisiable"
    title="提交信息"
    width="500px"
    okText="确定"
    cancelText="取消"
    @cancel="modalclosel"
    @ok="modalsubmit"
  >
    <div></div>
            <a-row>
              <a-col :span="10" style="margin-left: 110px;">
                <div style="margin: 0 auto">
                  <table>
                    <tr v-for="(n,index) in cityS" :key="index" :value="n.deptId" style="width: 500px">
                      <td style="width: 150px;line-height: 30px">
                        {{n.deptName}}
                      </td>
                      <td style="width: 120px;line-height: 30px">
                        <a-input-number @blur="cityBlur(n.deptId, $event)" defaultValue="0"></a-input-number>
                      </td>
                    </tr>
                  </table>
                </div>
              </a-col>
            </a-row>
  </a-modal>
</template>

<script>
export default {
  name: 'SubmitInfo',
  props: {
    subVisiable: {
      default: false
    }
  },
  data () {
    return {
      cityS: [],
      loading: false,
      cityId: '',
      arrData: [],
      objData: {},
      cityValue: ''
    }
  },
  mounted () {
    this.$get('/dept/list', {deptId: 0}).then(res => {
      this.cityS = res.data
    })
  },
  methods: {
    modalclosel () {
      this.$emit('close')
    },
    modalsubmit () {
      this.loading = true
      let json = JSON.stringify(this.arrData)
      this.$emit('success', json)
    },
    cityBlur (id, event) {
      let obj = {}
      obj.deptId = id
      obj.count = event.target.value
      this.arrData.push(obj)
    }
  }
}
</script>

<style scoped>

</style>
