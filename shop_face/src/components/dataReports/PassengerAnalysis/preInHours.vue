<template>
  <div>
    <h4>客流时报表</h4>
    <a-form layout="inline">
      <a-form-item label="门店">
        <a-select :style="{width:'150px'}" v-model="deptUnikey">
          <a-select-option v-for="n in shopArr" :key="n.deptId" :value="n.deptUnikey">{{n.deptName}}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="时间">
        <a-date-picker v-model="seltime" :allowClear="false" :disabledDate="disabledEndDate"></a-date-picker>
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
      bordered
      :style="{'margin-top':'15px'}"
    >
      <span slot="hours" slot-scope="timeSlot">
        <span v-for="(n,index) in datehours" :key="index">
          <span v-if="timeSlot-1==index">{{datehours[index]}}</span>
        </span>
      </span>
    </a-table>

    <a-tabs v-model="actTabs" @change="changeTabs">
      <a-tab-pane :key="1" tab="会员分析">
        <div ref="myMenberChart" style="width:100%;height:400px;"></div>
      </a-tab-pane>
      <a-tab-pane :key="2" tab="性别分析">
        <div ref="myChart" style="width:100%;height:400px;"></div>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
import axios from "axios";
import moment from "moment";
import common from "@/components/commons/common.js";

const datehours = [
  "00:00-00:59",
  "01:00-01:59",
  "02:00-02:59",
  "03:00-03:59",
  "04:00-04:59",
  "05:00-05:59",
  "06:00-06:59",
  "07:00-07:59",
  "08:00-08:59",
  "09:00-09:59",
  "10:00-10:59",
  "11:00-11:59",
  "12:00-12:59",
  "13:00-13:59",
  "14:00-14:59",
  "15:00-15:59",
  "16:00-16:59",
  "17:00-17:59",
  "18:00-18:59",
  "19:00-19:59",
  "20:00-20:59",
  "21:00-21:59",
  "22:00-22:59",
  "23:00-23:59"
];

const columns = [
  {
    title: "时间段",
    width: "16%",
    dataIndex: "timeSlot",
    key: "1",
    scopedSlots: { customRender: "hours" }
  },
  { title: "来客人数", width: "16%", dataIndex: "allCount", key: "2" },
  { title: "男", width: "16%", dataIndex: "manCount", key: "3" },
  { title: "女", width: "16%", dataIndex: "womanCount", key: "4" },
  { title: "会员", width: "16%", dataIndex: "l_isMember", key: "5" },
  { title: "非会员", width: "16%", dataIndex: "l_notMember", key: "6" }
];
export default {
  data() {
    return {
      columns,
      datalist: [],
      shopArr: [],
      deptUnikey: "",
      seltime: null,
      myChart: null,
      myMenberChart: null,
      datehours,
      deptName: "",
      actTabs: 1
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

    // 初始化图表(只初始化tabs默认展示的，其他的需要在选择tab标签时初始化)
    this.myMenberChart = this.$echarts.init(this.$refs.myMenberChart);
    this.createCanvas(
      "myMenberChart",
      "会员",
      "非会员",
      "l_isMember",
      "l_notMember"
    );
  },
  methods: {
    getHoursReport() {
      if (this.deptUnikey == "") {
        this.$notification.warning({ message: "门店不能为空" });
        return;
      }
      const params = {
        day: this.seltime.format("YYYY-MM-DD"),
        deptUnikey: this.deptUnikey
      };
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/stat-customer/statByHour",
          { params }
        )
        .then(res => {
          if (res.data.resp_code == 0) {
            let datas = res.data.datas;
            this.datalist = common.reconsitution(datas);
            if (this.datalist.length != 0) {
              this.deptName = res.data.datas[0].deptName;
              this.createCanvas(
                "myMenberChart",
                "会员",
                "非会员",
                "l_isMember",
                "l_notMember"
              );
            } else {
              this.createCanvas(
                "myMenberChart",
                "会员",
                "非会员",
                "l_isMember",
                "l_notMember"
              );
            }
          } else {
            this.$notification.warning({ message: res.data.resp_msg });
          }
        });
    },
    // 搜索
    search() {
      this.actTabs = 1;
      this.getHoursReport();
    },
    disabledEndDate(time) {
      let a = new Date();
      return time.valueOf() > moment(a, "YYYY-MM-DD").valueOf();
    },
    createCanvas(canvasid, name1, name2, cap1, cap2) {
      let option = null;
      option = {
        title: {
          text: this.deptName + "|" + this.seltime.format("YYYY-MM-DD"),
          left: "center"
        },
        legend: {
          data: [name1, name2],
          right: "20%"
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow"
          }
        },
        xAxis: {
          name: "时间段",
          type: "category",
          data: this.datehours
        },
        yAxis: {
          name: "人数",
          type: "value"
        },
        series: [
          {
            data: this.datalist.map(item => item[cap1]), //返回数据中的所有男性数据
            type: "bar",
            barGap: 0,
            name: name1
          },
          {
            data: this.datalist.map(item => item[cap2]),
            type: "bar",
            name: name2
          }
        ],
        toolbox: {
          right: "8%",
          feature: {
            restore: {},
            saveAsImage: {},
            dataView: { readOnly: true }
          }
        },
        dataZoom: [
          {
            start: 0,
            end: 100
          },
          {
            type: "inside"
          }
        ]
      };
      if (option && typeof option === "object") {
        this[canvasid].setOption(option, true);
      }
      window.onresize = () => {
        this[canvasid].resize();
      };
    },
    // 切换tabs,展示新图表
    changeTabs(data) {
      if (data == 2) {
        setTimeout(() => {
          this.myChart = this.$echarts.init(this.$refs.myChart);
          this.createCanvas("myChart", "男", "女", "manCount", "womanCount");
        }, 200);
      }
    }
  }
};
</script>

<style scoped>
</style>