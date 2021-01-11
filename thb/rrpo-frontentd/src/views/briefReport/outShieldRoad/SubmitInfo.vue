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
                        <a-input-number @blur="cityBlur(n.deptId, index, $event)" defaultValue="0" :min="0" :disabled="disableds"></a-input-number>
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
      disableds: false,
      defaultValue: 0,
      cityId: '',
      arrData: [],
      objData: {},
      cityValue: ''
    }
  },
  mounted () {
    this.$get('/dept/list', {deptId: 0, status: 1}).then(res => {
      this.cityS = res.data
    })
  },
  methods: {
    modalclosel () {
      this.$emit('close')
      this.arrData = []
    },
    modalsubmit () {
      this.loading = true
      let arr = []
      for (let i = 0; i < this.arrData.length; i++) {
        console.log(this.arrData[i])
        if (this.arrData[i] !== undefined) {
          arr.push(this.arrData[i])
        }
      }
      let json = JSON.stringify(arr)
      this.$emit('success', json)
      this.arrData = []
    },
    cityBlur (id, index, event) {
      let obj = {}
      obj.deptId = id
      obj.count = event.target.value
      this.arrData[index] = obj
    }
  }
}
</script>

<style scoped>

</style>
