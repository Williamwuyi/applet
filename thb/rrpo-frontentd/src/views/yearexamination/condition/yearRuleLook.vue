<template>
  <a-modal
    :visible="yearRulelookVisible"
    :width= "1200"
    :footer="null"
    @cancel="() => { onClose() }"
    style="text-align: center;"
  >
    <h2 style="text-align: center; font-size: 31px; line-height: 31px">{{msg}}</h2>
    <table v-if="wachws.list.length > 0"  width="100%"  border="1" class="t1" align="center" cellpadding="0" cellspacing="0" style="font-size: 14px" v-for="(wachws, index) in arr" :key="wachws.name">
      <tr v-if="index ===  0">
        <th style="width: 120px">一级指标</th>
        <th>评估内容</th>
<!--        <th style="width: 108px">操作</th>-->
      </tr>
      <tr v-if="wachws.list.length > 0">
        <td :rowspan="wachws.list.length + 1" style="width: 120px">{{wachws.name}}</td>
      </tr>
      <tr v-for="item in wachws.list" :key="item.menusId">
        <td  style="text-align: left">{{item.content}}</td>
<!--        <td style="width: 108px"><a-button  @click="condProof(item)">解除绑定</a-button></td>-->
      </tr>
    </table>
  </a-modal>
</template>

<script>
export default {
  name: 'yearRulelook',
  props: ['yearRulelookVisible'],
  data () {
    return {
      arr: [],
      msg: '年度考核详细信息',
      yearId: '',
      yearDate: '',
      prames: {}

    }
  },
  methods: {
    getFach (key) {
      this.yearDate = key.yearDate
      this.prames.yearId = key.yearId
      this.yearId = key.yearId
      this.msg = key.name
      this.fach()
    },
    fach () {
      this.$get('/check/menus-year/list', this.prames).then(res => {
        if (res.status === 200) {
          this.arr = res.data.data
        } else {
          this.$message.error('本规则还未绑定任何扣分项')
          this.onClose()
        }
      })
    },
    onClose () {
      this.rest()
      this.$emit('close')
    },
    rest () {
      this.arr = []
    }
    // condProof (item) {
    //   let that = this
    //   that.$confirm({
    //     title: '是否解除绑定关系？',
    //     centered: true,
    //     onOk () {
    //       const params = {
    //         listS: item.menusYearId
    //       }
    //       console.log(params)
    //       that.$delete('/check/menus-year/deleteByLists/', params).then(() => {
    //         that.$message.success('解绑成功')
    //         that.fach()
    //       })
    //     }
    //   })
    // }
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
</style>
