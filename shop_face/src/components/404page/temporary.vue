<template>
  <div style="padding:40px;">
    <h4>订单记录</h4>
    <a-form>
      <a-form-item label="门店" :labelCol="{span:1}" :wrapperCol="{span:16}">
        <a-select :style="{width:'200px'}" v-model="deptUnikey">
          <a-select-option v-for="n in allDapt" :key="n.deptId" :value="n.deptUnikey">{{n.deptName}}</a-select-option>
        </a-select>
        <a-button class="searchBtns" type="primary" size="small" @click="search">搜 索</a-button>
      </a-form-item>
    </a-form>
    <a-table
      :rowKey="record => record.id"
      :columns="columns"
      :dataSource="datalist"
      :pagination="pagination"
      :scroll="{x:800}"
      bordered
      :style="{'margin-top':'15px'}"
      @change="handleTableChange"
    >
      <span slot="times" slot-scope="consumeTime">
        {{consumeTime}}
        <!-- {{consumeTime.slice(0,-4)}} -->
      </span>
    </a-table>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

const columns = [
  { title: "姓名", width: "25%", dataIndex: "personName", key: "1" },
  { title: "电话", width: "25%", dataIndex: "personPhone", key: "2" },
  {
    title: "消费时间",
    width: "25%",
    dataIndex: "consumeTime",
    key: "3",
    scopedSlots: { customRender: "times" }
  },
  { title: "门店", width: "25%", dataIndex: "deptName", key: "4" }
];
export default {
  data() {
    return {
      columns,
      datalist: [],
      deptUnikey: null,
      allDapt: [],
      pagination: {}
    };
  },
  mounted() {
    this.fetch();
    this.getAllDept();
  },
  methods: {
    //重载表格
    handleTableChange(pagination) {
      const pager = { ...this.pagination };
      pager.current = pagination.current;
      this.pagination = pager;
      this.fetch({
        size: pagination.pageSize,
        current: pagination.current
      });
    },
    fetch(params = {}) {
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/order/record/listByLoginUser",
          qs.stringify({
            current: 1,
            size: 10,
            deptUnikey:this.deptUnikey,
            ...params
          })
        )
        .then(data => {
          this.datalist = data.data.datas.records;
          console.log('订单记录：',this.datalist)
          const pagination = { ...this.pagination };
          pagination.pageSize = data.data.datas.size;
          pagination.total = data.data.datas.total;
          this.pagination = pagination;
        });
    },
    getAllDept() {
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/order/record/getAllDept"
        )
        .then(res => {
          console.log("获取订单记录数据：", res.data.datas);
          this.allDapt = res.data.datas;
        });
    },
    search() {
      this.fetch();
    }
  }
};
</script>

<style scoped>
.searchBtns {
  margin-left: 25px;
}
</style>