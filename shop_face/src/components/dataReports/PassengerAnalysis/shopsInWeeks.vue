<template>
  <div>
    <h4>门店群客流周报表</h4>
    <a-form layout="inline">
      <a-form-item label="选择时间">
        <a-select v-model="seltime" :style="{width:'120px'}">
          <a-select-option :value="1">最近一周</a-select-option>
          <a-select-option :value="3">最近三周</a-select-option>
          <a-select-option :value="5">最近五周</a-select-option>
        </a-select>
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
      <span slot="weeks" slot-scope="record">
        <span>{{record.date.slice(-5)+' ~ '+record.endDate.slice(-5)}}</span>
      </span>
    </a-table>

    <a-tabs v-model="actTabs" @change="changeTabs">
      <a-tab-pane :key="1" tab="会员分析">
        <a-row>
          <a-col :span="14">
            <div ref="myMemberBarChart" style="width:100%;height:400px;"></div>
          </a-col>
          <a-col :span="10">
            <div ref="myMemberPieChart" style="width:100%;height:400px;"></div>
          </a-col>
        </a-row>
      </a-tab-pane>
      <a-tab-pane :key="2" tab="性别分析">
        <a-row>
          <a-col :span="14">
            <div ref="myBarChart" style="width:100%;height:400px;"></div>
          </a-col>
          <a-col :span="10">
            <div ref="myPieChart" style="width:100%;height:400px;"></div>
          </a-col>
        </a-row>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
import axios from "axios";
import moment from "moment";
import common from "@/components/commons/common.js";

const columns = [
  {
    title: "日期",
    width: "16%",
    key: "1",
    scopedSlots: { customRender: "weeks" }
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
      seltime: 1,
      myBarChart: null,
      myPieChart: null,
      myMemberBarChart: null,
      myMemberPieChart: null,
      deptName: "",
      ageData: {},
      actTabs: 1,
      memberData: {}
    };
  },
  mounted() {
    // 初始化图表
    this.myMemberBarChart = this.$echarts.init(this.$refs.myMemberBarChart);
    this.myMemberPieChart = this.$echarts.init(this.$refs.myMemberPieChart);
    this.createCanvas(
      "myMemberBarChart",
      "会员",
      "非会员",
      "l_isMember",
      "l_notMember"
    );
    this.createMemberPieCanvas();
  },
  methods: {
    getDaysReport() {
      const params = {
        count: this.seltime
      };
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/stat-customer/comStatByWeek",
          { params }
        )
        .then(res => {
          if (res.data.resp_code == 0) {
            let datas = res.data.datas;
            this.datalist = common.reconsitution(datas);
            if (this.datalist.length != 0) {
              this.deptName = res.data.datas[0].deptName;
              this.ageData = common.countAges(this.datalist);
              this.memberData = common.countMembers(this.datalist);
              this.createCanvas(
                "myMemberBarChart",
                "会员",
                "非会员",
                "l_isMember",
                "l_notMember"
              );
              this.createMemberPieCanvas();
            } else {
              this.createCanvas(
                "myMemberBarChart",
                "会员",
                "非会员",
                "l_isMember",
                "l_notMember"
              );
              this.createMemberPieCanvas();
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
    createCanvas(canvasid, name1, name2, cap1, cap2) {
      let option = null;
      option = {
        legend: {
          data: [name1, name2]
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow"
          }
        },
        xAxis: {
          name: "日期",
          type: "category",
          data: this.datalist.map(
            item => item.date.slice(-5) + " ~ " + item.endDate.slice(-5)
          )
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
    // 饼形图
    createPieCanvas() {
      let option = null;
      option = {
        legend: {
          bottom: 10,
          left: "center",
          data: ["1-18岁", "19-29岁", "30-39岁", "40-49岁", "50岁及以上"]
        },
        tooltip: {
          trigger: "item",
          formatter: "{b} : {c} ({d}%)"
        },
        series: [
          {
            data: [
              { value: this.ageData.age1Count, name: "1-18岁" },
              { value: this.ageData.age2Count, name: "19-29岁" },
              { value: this.ageData.age3Count, name: "30-39岁" },
              { value: this.ageData.age4Count, name: "40-49岁" },
              { value: this.ageData.age5Count, name: "50岁及以上" }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)"
              }
            },
            type: "pie",
            radius: "65%",
            center: ["50%", "50%"]
          }
        ],
        toolbox: {
          right: "8%",
          feature: {
            restore: {},
            saveAsImage: {}
          }
        }
      };
      if (option && typeof option === "object") {
        this.myPieChart.setOption(option, true);
      }
      window.onresize = () => {
        this.myPieChart.resize();
      };
    },
    // 会员、非会员饼形图
    createMemberPieCanvas() {
      let option = null;
      option = {
        legend: {
          bottom: 10,
          left: "center",
          data: ["会员", "非会员"]
        },
        tooltip: {
          trigger: "item",
          formatter: "{b} : {c} ({d}%)"
        },
        series: [
          {
            data: [
              { value: this.memberData.l_isMember, name: "会员" },
              { value: this.memberData.l_notMember, name: "非会员" }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)"
              }
            },
            type: "pie",
            radius: "65%",
            center: ["50%", "50%"]
          }
        ],
        toolbox: {
          right: "8%",
          feature: {
            restore: {},
            saveAsImage: {}
          }
        }
      };
      if (option && typeof option === "object") {
        this.myMemberPieChart.setOption(option, true);
      }
      window.onresize = () => {
        this.myMemberPieChart.resize();
      };
    },
    // 切换tabs,展示新图表
    changeTabs(data) {
      if (data == 2) {
        setTimeout(() => {
          this.myBarChart = this.$echarts.init(this.$refs.myBarChart);
          this.myPieChart = this.$echarts.init(this.$refs.myPieChart);
          this.createCanvas("myBarChart", "男", "女", "manCount", "womanCount");
          this.createPieCanvas();
        }, 200);
      }
    }
  }
};
</script>

<style scoped>
</style>