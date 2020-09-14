<template>
  <div>
    <h4>客流对比时报表</h4>
    <a-form layout="inline">
      <a-form-item label="门店">
        <a-select :style="{width:'150px'}" v-model="deptUnikey">
          <a-select-option
            v-for="(n,index) in shopArr"
            :key="index"
            :value="n.deptUnikey"
          >{{n.deptName}}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="指定日期">
        <a-date-picker v-model="day" />
      </a-form-item>
      <a-form-item label="对比日期">
        <a-date-picker v-model="contrasDay" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="search">搜索</a-button>
      </a-form-item>
    </a-form>

    <a-table
      :rowKey="record => record.id"
      :columns="columns"
      :dataSource="datalist"
      :pagination="{hideOnSinglePage:true}"
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
    <a-divider></a-divider>

    <a-tabs v-model="actTabs" @change="changeTabs">
      <a-tab-pane :key="1" tab="会员分析">
        <div ref="myMenberChart" style="width:100%;height:400px;"></div>
      </a-tab-pane>
      <a-tab-pane :key="2" tab="性别分析">
        <div ref="myBarChart" style="width:100%;height:400px;"></div>
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
    width: "9%",
    dataIndex: "timeSlot",
    key: "555",
    scopedSlots: { customRender: "hours" }
  },
  {
    title: "来客数",
    children: [
      { title: "指定日", width: "9%", dataIndex: "xallCount", key: "2" },
      { title: "对比日", width: "9%", dataIndex: "yallCount", key: "7" }
    ]
  },
  {
    title: "男",
    children: [
      { title: "指定日", width: "9%", dataIndex: "xmanCount", key: "3" },
      { title: "对比日", width: "9%", dataIndex: "ymanCount", key: "8" }
    ]
  },
  {
    title: "女",
    children: [
      { title: "指定日", width: "9%", dataIndex: "xwomanCount", key: "4" },
      { title: "对比日", width: "9%", dataIndex: "ywomanCount", key: "9" }
    ]
  },
  {
    title: "会员",
    children: [
      { title: "指定日", width: "9%", dataIndex: "xl_isMember", key: "5" },
      { title: "对比日", width: "9%", dataIndex: "yl_isMember", key: "10" }
    ]
  },
  {
    title: "非会员",
    children: [
      { title: "指定日", width: "9%", dataIndex: "xl_notMember", key: "6" },
      { title: "对比日", width: "9%", dataIndex: "yl_notMember", key: "11" }
    ]
  }
  // {
  //   title:'指定日',
  //   children:[
  //     {title: "来客数", width: '9%', dataIndex: "xallCount", key: "2"},
  //     {title: "男", width: '9%', dataIndex: "xmanCount", key: "3"},
  //     {title: "女", width: '9%', dataIndex: "xwomanCount", key: "4"},
  //     {title: "会员", width: '9%', dataIndex: "xl_isMember", key: "5"},
  //     {title: "非会员", width: '9%', dataIndex: "xl_notMember", key: "6"}
  //   ]
  // },
  // {
  //   title:'对比日',
  //   children:[
  //     {title: "来客数", width: '9%', dataIndex: "yallCount", key: "7"},
  //     {title: "男", width: '9%', dataIndex: "ymanCount", key: "8"},
  //     {title: "女", width: '9%', dataIndex: "ywomanCount", key: "9"},
  //     {title: "会员", width: '9%', dataIndex: "yl_isMember", key: "10"},
  //     {title: "非会员", width: '9%', dataIndex: "yl_notMember", key: "11"}
  //   ]
  // }
];
export default {
  data() {
    return {
      columns,
      datalist: [],
      shopArr: [],
      deptUnikey: "",
      myBarChart: null,
      myMenberChart: null,
      deptName: "",
      ageData: {},
      day: null,
      contrasDay: null,
      actTabs: 1,
      datehours,
      ydate: "",
      xdate: ""
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

    // 初始化图表
    this.myMenberChart = this.$echarts.init(this.$refs.myMenberChart);
    this.createCanvas(
      "myMenberChart",
      this.xdate + "日会员",
      this.ydate + "日会员",
      this.xdate + "日非会员",
      this.ydate + "日非会员",
      "xl_isMember",
      "yl_isMember",
      "xl_notMember",
      "yl_notMember"
    );
  },
  methods: {
    getDaysReport() {
      if (
        this.deptUnikey == "" ||
        this.day == null ||
        this.contrasDay == null
      ) {
        this.$notification.warning({ message: "请选择完整的查询信息" });
        return;
      }
      const params = {
        day: this.day.format("YYYY-MM-DD"),
        contrasDay: this.contrasDay.format("YYYY-MM-DD"),
        deptUnikey: this.deptUnikey
      };
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/stat-customer/contrasDateStatHour",
          { params }
        )
        .then(res => {
          if (res.data.resp_code == 0) {
            let datas = res.data.datas;
            let x1, y1;
            this.datalist = this.resetHours(datas);
            this.xdate = this.datalist[0].xdate.slice(-5);
            this.ydate = this.datalist[0].ydate.slice(-5);
            if (this.datalist.length != 0) {
              this.deptName = res.data.datas[0].deptName;
              this.createCanvas(
                "myMenberChart",
                this.xdate + "日会员",
                this.ydate + "日会员",
                this.xdate + "日非会员",
                this.ydate + "日非会员",
                "xl_isMember",
                "yl_isMember",
                "xl_notMember",
                "yl_notMember"
              );
            } else {
              this.createCanvas(
                "myMenberChart",
                this.xdate + "日会员",
                this.ydate + "日会员",
                this.xdate + "日非会员",
                this.ydate + "日非会员",
                "xl_isMember",
                "yl_isMember",
                "xl_notMember",
                "yl_notMember"
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
      this.getDaysReport();
    },
    createCanvas(canvasid, name1, name2, name3, name4, cap1, cap2, cap3, cap4) {
      let option = null;
      option = {
        legend: {
          data: [name1, name2, name3, name4]
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
          },
          {
            data: this.datalist.map(item => item[cap3]),
            type: "bar",
            name: name3
          },
          {
            data: this.datalist.map(item => item[cap4]),
            type: "bar",
            name: name4
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
          this.myBarChart = this.$echarts.init(this.$refs.myBarChart);
          this.createCanvas(
            "myBarChart",
            this.xdate + "日男",
            this.ydate + "日男",
            this.xdate + "日女",
            this.ydate + "日女",
            "xmanCount",
            "ymanCount",
            "xwomanCount",
            "ywomanCount"
          );
        }, 200);
      }
    },
    // 整合对比时报表数据
    resetHours(data) {
      let arr = [];
      let x = data[0];
      let y = data[1];
      for (let i = 0; i < 24; i++) {
        arr.push({
          id: x[i].id,
          timeSlot: x[i].timeSlot,
          deptUnikey: x[i].deptUnikey,
          deptName: x[i].deptName,
          xdate: x[i].date,
          xallCount: x[i].allCount,
          xl_isMember: Number(x[i].generalCount) + Number(x[i].paymentCount),
          xl_notMember: x[i].strangerCount, //x[i].allCount-0-x[i].generalCount-x[i].paymentCount
          xmanCount: x[i].manCount,
          xwomanCount: x[i].womanCount,
          ydate: y[i].date,
          yallCount: y[i].allCount,
          yl_isMember: Number(y[i].generalCount) + Number(y[i].paymentCount),
          yl_notMember: y[i].strangerCount,
          ymanCount: y[i].manCount,
          ywomanCount: y[i].womanCount
        });
      }
      return arr;
    }
  }
};
</script>

<style scoped>
</style>