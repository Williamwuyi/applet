<template>
  <div>
    <h4>客流对比月报表</h4>
    <a-form layout="inline">
      <a-form-item label="门店">
        <a-select :style="{width:'150px'}" v-model="deptUnikey">
          <a-select-option v-for="n in shopArr" :key="n.deptId" :value="n.deptUnikey">{{n.deptName}}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="指定月">
        <a-month-picker v-model="contrasDay" />
      </a-form-item>
      <a-form-item label="对比月">
        <a-month-picker v-model="day" />
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
      shopArr: [],
      deptUnikey: "",
      myBarChart: null,
      myMenberChart: null,
      deptName: "",
      ageData: {},
      day: null,
      contrasDay: null,
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

    // 初始化图表
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
        day: this.getMonday(this.day),
        contrasDay: this.getMonday(this.contrasDay),
        deptUnikey: this.deptUnikey,
        type: 3
      };
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/stat-customer/contrasDateStat",
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
        }
      };
      if (option && typeof option === "object") {
        this[canvasid].setOption(option, true);
      }
      window.onresize = () => {
        this[canvasid].resize();
      };
    },
    // 过滤日期，获取所选日期的周一
    getMonday(data) {
      let a = new Date(data);
      a.setDate("01");
      let x = moment(a, "YYYY-MM-DD");
      return x.format("YYYY-MM-DD");
    },
    // 切换tabs,展示新图表
    changeTabs(data) {
      if (data == 2) {
        setTimeout(() => {
          this.myBarChart = this.$echarts.init(this.$refs.myBarChart);
          this.createCanvas("myBarChart", "男", "女", "manCount", "womanCount");
        }, 200);
      }
    }
  }
};
</script>

<style scoped>
</style>