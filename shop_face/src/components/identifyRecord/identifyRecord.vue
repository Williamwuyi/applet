<template>
  <div>
    <h4>识别记录</h4>
    <a-form :form="form1" layout="inline">
      <a-form-item label="姓名">
        <a-input v-model="personName"></a-input>
      </a-form-item>
      <a-form-item label="门店">
        <a-select :style="{width:'150px'}" v-model="deptUnikey">
          <a-select-option
            v-for="n in allShops"
            :key="n.deptId"
            :value="n.deptUnikey"
          >{{n.deptName}}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="起止日期">
        <a-date-picker
          format="YYYY-MM-DD HH:mm:ss"
          :showTime="{defaultValue:moment('00:00:00','HH:mm:ss')}"
          v-model="startTime"
        />
        <span style="font-size:18px;">~</span>
        <a-date-picker
          format="YYYY-MM-DD HH:mm:ss"
          :showTime="{defaultValue:moment('23:59:59','HH:mm:ss')}"
          v-model="endTime"
        />
      </a-form-item>
      <a-form-item label class="searchBtns">
        <a-button type="primary" @click="search">搜索</a-button>
        <a-button type="primary" @click="resetValue" style="margin-left: 20px;">重置</a-button>
      </a-form-item>
    </a-form>
    <a-divider></a-divider>
    <a-table
      :rowKey="record => record.recordId"
      :columns="columns"
      :dataSource="datalist"
      :pagination="pagination"
      :scroll="{x:1000}"
      bordered
      :style="{'margin-top':'15px'}"
      @change="handleTableChange"
    ></a-table>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import moment from "moment";
const columns = [
  { title: "姓名", dataIndex: "personName", key: "2" },
  {
    title: "时间",
    dataIndex: "createTime",
    key: "3",
  },
  {
    title: "门店",
    dataIndex: "deptName",
    key: "4",
  },
];

export default {
  data() {
    return {
      columns,
      allShops: [],
      deptUnikey: "",
      personName: "",
      startTime: null,
      endTime: null,
      current: 1,
      total: 0,
      pagination: {},
      datalist: [],
    };
  },
  inject: ["reload"],
  mounted() {
    // 获取父级栏目下拉列表
    this.fetch();
    // 获取所有门店
    axios
      .get(
        this.root_path.rootIP +
          this.root_path.api6001 +
          "/auth/dept-user/getShopsByUserId?userId=" +
          this.$store.state.userid
      )
      .then((res) => {
        let xa = res.data.datas;
        this.allShops = xa.filter((item) => item.isStore == "Y");
        if (this.allShops != null) {
          //如果门店不为空
          this.deptUnikey = this.allShops[0].deptUnikey; //设置初始门店
        }
      });
  },
  beforeCreate() {
    this.form1 = this.$form.createForm(this);
  },
  methods: {
    moment,
    //重载表格
    handleTableChange(pagination) {
      //表格操作触发,重载表格
      const pager = { ...this.pagination };
      pager.current = pagination.current;
      this.pagination = pager;
      this.fetch({
        size: pagination.pageSize,
        current: pagination.current,
      });
    },
    //加载表格
    fetch() {
      let params = {
        current: this.pagination.current,
        size: 10,
        personName: this.personName,
        deptUnikey: this.deptUnikey,
         endTime:
            this.endTime == null
              ? null
              : this.endTime.format("YYYY/MM/DD HH:mm:ss"),
          startTime:
            this.startTime == null
              ? null
              : this.startTime.format("YYYY/MM/DD HH:mm:ss")
      };
      //表格的dataindex必须和json数据对象一一对应才能自动被展示
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/attend-record/list",
          { params: { size: 10, identifyStatus: 1, ...params } }
        )
        .then((data) => {
          console.log("获取门店信息：", data.data.datas);
          const pagination = { ...this.pagination };
          pagination.pageSize = data.data.datas.size;
          pagination.total = data.data.datas.total;
          this.datalist = data.data.datas.records;
          this.pagination = pagination;
        });
    },
    resetValue() {
      this.endTime = null;
      this.startTime = null;
      this.deptUnikey = null;
      this.personName = "";
      this.current = 1;
    },
    search() {
      this.fetch();
    },
  },
};
</script>

<style scoped>
</style>