<template>
  <div>
    <h4>会员消费日统计</h4>
    <a-form layout="inline">
      <a-form-item label="门店">
        <a-select :style="{width:'150px'}" v-model="deptUnikey">
          <a-select-option v-for="n in shopArr" :key="n.deptId" :value="n.deptUnikey">{{n.deptName}}</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="姓名|电话">
        <a-input placeholder="输入姓名或电话" v-model="searchTerm" :style="{width:'140px'}"></a-input>
      </a-form-item>

      <a-form-item label="是否消费">
        <a-select :style="{width:'100px'}" v-model="isBuy">
          <a-select-option value="1">是</a-select-option>
          <a-select-option value="0">否</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="时间">
        <a-date-picker v-model="seltime" :allowClear="false"></a-date-picker>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="search">搜索</a-button>
      </a-form-item>
    </a-form>

    <a-table
      :rowKey="record => record.id"
      :columns="columns"
      :dataSource="datalist"
      size="small"
      :scroll="{x:800}"
      bordered
      :style="{'margin-top':'15px'}"
    >
      <span slot="consump" slot-scope="isBuy">
        <span v-if="isBuy">是</span>
        <span v-else>否</span>
      </span>
    </a-table>
    <a-divider></a-divider>
    <!-- 分页 -->
    <!-- <a-pagination showQuickJumper :pageSize="10" @change="changepage" v-model="current" :total="total" :style="{textAlign:'right'}" /> -->
  </div>
</template>

<script>
import axios from "axios";
import moment from "moment";

const columns = [
  { title: "店铺名称", width: "14.2%", dataIndex: "deptName", key: "1" },
  { title: "姓名", width: "14.2%", dataIndex: "personName", key: "2" },
  { title: "电话号码", width: "14.2%", dataIndex: "personPhone", key: "3" },
  {
    title: "是否消费",
    width: "14.2%",
    dataIndex: "isBuy",
    scopedSlots: { customRender: "consump" },
    key: "4"
  }
];
export default {
  data() {
    return {
      columns,
      datalist: [],
      shopArr: [],
      deptUnikey: "",
      size: 1,
      searchTerm: "", //名字|手机号码查询条件
      isBuy: "", //是否消费条件
      current: 1,
      total: 0,
      seltime: null, //时间条件
      deptName: "",
      ageData: {}
    };
  },
  mounted() {
    // 获取有权限的门店
    axios
      .get(
        this.root_path.rootIP +
          this.root_path.api6001 +
          "/auth/dept-user/getShopsByUserId?userId=" +
          this.$store.state.userid
      )
      .then(res => {
        let data = res.data.datas;
        this.shopArr = data.filter(item => item.isStore == "Y");
      })
      .catch(err => {});

    // 设置初始时间
    let a = new Date();
    this.seltime = moment(a, "YYYY-MM-DD");
  },
  methods: {
    getDaysReport() {
      if (this.deptUnikey == "") {
        this.$notification.warning({ message: "门店不能为空" });
        return;
      }
      const params = {
        day: this.seltime.format("YYYY-MM-DD"),
        current: this.current,
        isBuy: this.isBuy,
        searchTerm: this.searchTerm,
        deptUnikey: this.deptUnikey
      };
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/order/stat-member/statByDay",
          { params }
        )
        .then(res => {
          console.log("获取会员日统计", res.data.datas.records);
          if (res.data.datas.records.length != 0) {
            this.datalist = res.data.datas.records;
            // if(this.datalist.length!=0){
            //   console.log('获取会员日统计===[]0',res.data.datas[0])
            //   this.deptName=res.data.datas[0].deptName;
            // }else{
            // }
            this.$notification.warning({ message: res.data.resp_msg });
          } else {
            this.$notification.warning({ message: "未搜索到数据！" });
          }
        });
    },
    // 搜索
    search() {
      this.getDaysReport();
    }
  }
};
</script>

<style scoped>
</style>