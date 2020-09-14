<template>
  <div>
    <h4>设备传输日志</h4>
    <a-form layout="inline">
      <a-form-item label="门店">
        <a-select :style="{width:'150px'}" v-model="deptUnikey">
          <a-select-option
            v-for="n in allShops"
            :key="n.deptId"
            :value="n.deptUnikey"
          >{{n.deptName}}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="执行状态">
        <a-select :style="{width:'150px'}" v-model="status">
          <a-select-option :value="'N'">正在执行</a-select-option>
          <a-select-option :value="'Y'">已完成</a-select-option>
          <a-select-option :value="'FAIL'">执行失败</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label class="searchBtns">
        <a-button type="primary" @click="search">搜索</a-button>
      </a-form-item>
    </a-form>

    <a-spin :spinning="spinning">
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
        <span slot="devname" slot-scope="hardwareUnikey">
          <span v-for="(n,index) in allDev" :key="index">
            <span v-if="hardwareUnikey==n.deviceunikey">{{n.deviceaddress}}</span>
          </span>
        </span>
        <span slot="rtype" slot-scope="type">
          <span v-if="type==2">设备增加人员</span>
          <span v-else-if="type==-2">设备删除人员</span>
        </span>
        <span slot="errors" slot-scope="record">
          <!-- 如果不是已完成的-->
          <span v-if="record.isFinished!='Y'">
            <span v-if="record.isFinished=='N'">
              <span v-if="record.errorCode=='图片质量差'">{{record.errorCode}}&nbsp;</span>
              <span v-else>图片上传中&nbsp;</span>
            </span>

            <span v-if="record.isFinished=='FAIL'">
              {{record.errorCode}}&nbsp;
              <a-button type="link" size="small" @click="reUpload(record)">重传</a-button>
            </span>
          </span>
        </span>
      </a-table>
    </a-spin>
  </div>
</template>

<script>
import axios from "axios";

const columns = [
  {
    title: "设备名称",
    width: "15%",
    dataIndex: "hardwareUnikey",
    key: "1",
    scopedSlots: { customRender: "devname" }
  },
  { title: "人员姓名", width: "15%", dataIndex: "personName", key: "2" },
  {
    title: "操作类型",
    width: "15%",
    dataIndex: "type",
    key: "7",
    scopedSlots: { customRender: "rtype" }
  },
  { title: "开始时间", width: "15%", dataIndex: "createTime", key: "3" },
  { title: "完成时间", width: "15%", dataIndex: "finishTime", key: "4" },
  {
    title: "错误信息",
    width: "25%",
    key: "5",
    scopedSlots: { customRender: "errors" }
  }
];
export default {
  data() {
    return {
      columns,
      datalist: [],
      pagination: {},
      deptUnikey: "",
      allShops: [],
      allManagerId: [],
      allDev: [],
      status: "N",
      spinning: false
    };
  },
  mounted() {
    // 获取所有门店
    axios
      .get(this.root_path.rootIP + this.root_path.api6001 + "/org/dept/findAll")
      .then(res => {
        let xa = res.data.datas;
        this.allShops = xa.filter(item => item.isStore == "Y");
      });
    // 获取所有主机及其设备
    axios
      .get(this.root_path.rootIP + this.root_path.api6001 + "/eq/manager/list")
      .then(res => {
        //获取所有主机
        let allManagerId = [];
        for (let n of res.data.datas) {
          allManagerId.push(n.id);
        }
        this.allManagerId = allManagerId;
      })
      .then(() => {
        //循环获取设备
        if (this.allManagerId == null || this.allManagerId.length == 0) {
          return;
        }
        let promiseAll = this.allManagerId.map(item => {
          return axios.get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/eq/hardware/getHardwareDeviceInfoList",
            { params: { limit: 10, managerId: item } }
          );
        });
        axios.all(promiseAll).then(resArr => {
          let allDev = [];
          resArr.forEach((res, i) => {
            for (let x of res.data.list) {
              allDev.push({
                deviceunikey: x.deviceunikey,
                deviceaddress: x.deviceaddress
              });
            }
          });
          this.allDev = allDev;
        });
      });
  },
  methods: {
    //重载表格_
    handleTableChange(pagination) {
      //表格操作触发,重载表格
      const pager = { ...this.pagination };
      pager.current = pagination.current;
      this.pagination = pager;
      this.fetch({
        size: pagination.pageSize,
        current: pagination.current
      });
    },
    //加载表格
    fetch(params = {}) {
      //表格的dataindex必须和json数据对象一一对应才能自动被展示
      if (this.deptUnikey == "") {
        this.$notification.warning({ message: "请选择门店" });
        return;
      }
      axios
        .get(
          this.root_path.rootIP + this.root_path.api6001 + "/eq/queue/list",
          {
            params: {
              size: 10,
              deptUnikey: this.deptUnikey,
              isFinished: this.status,
              ...params
            }
          }
        )
        .then(data => {
          const pagination = { ...this.pagination };
          pagination.pageSize = data.data.datas.size;
          pagination.total = data.data.datas.total;
          pagination.current = data.data.datas.current;
          this.datalist = data.data.datas.records;
          this.pagination = pagination;
        });
    },
    // 搜索
    search() {
      this.fetch({ current: this.pagination.current });
    },
    // 重传
    reUpload(data) {
      this.spinning = true;
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/queue/retransmit",
          { params: { id: data.id } }
        )
        .then(res => {
          this.spinning = false;
          this.fetch({ current: this.pagination.current });
        })
        .catch(err => {
          this.spinning = false;
        });
    }
  }
};
</script>

<style scoped>
</style>