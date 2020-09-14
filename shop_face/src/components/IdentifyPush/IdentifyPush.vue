<template>
  <div>
    <h4>识别推送</h4>
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
      <a-form-item label="姓名">
        <a-input :style="{width:'120px'}" v-model="personName"></a-input>
      </a-form-item>
      <a-form-item label="起止日期">
        <a-date-picker
          :disabledDate="disabledStartDate"
          v-model="startTime"
          :showTime="{defaultValue:moment('00:00:00','HH:mm:ss')}"
          format="YYYY-MM-DD HH:mm:ss"
        />
        <span style="font-size:18px;">~</span>
        <a-date-picker
          :disabledDate="disabledEndDate"
          v-model="endTime"
          :showTime="{defaultValue:moment('23:59:59','HH:mm:ss')}"
          format="YYYY-MM-DD HH:mm:ss"
        />
      </a-form-item>
      <a-form-item label class="searchBtns">
        <a-button type="primary" @click="search">搜索</a-button>
        <a-button type="primary" @click="resetValue">重置</a-button>
      </a-form-item>
    </a-form>
    <!-- <a-checkbox style="float:right;">自动刷新</a-checkbox> -->
    <a-divider></a-divider>
    <a-row :gutter="30">
      <a-col
        :md="12"
        :lg="8"
        :xl="8"
        :xxl="6"
        v-for="(n,index) in datalist"
        :key="index"
        :style="{marginBottom:'2em'}"
      >
        <a-card
          class="card_line_p"
          :style="{width:'100%',marginTop:'30px;'}"
          :bodyStyle="{padding:'10px 14px'}"
        >
          <div class="showimg">
            <div>
              <img :src="root_path.imgView+n.personImage" />
              <p>底库图片</p>
            </div>
            <div>
              <img
                :src="root_path.rootIP+root_path.api6001+'/eq/hardware/showPersonImg?fileName='+n.snapImage+'&snapUnikey='+n.snapUnikey"
              />
              <p>抓拍图片</p>
            </div>
          </div>
          <div style="padding:0 20px;margin-top:15px;">
            <p>
              <span :title="n.personName">姓名：{{n.personName.slice(0,8)}}</span>
              <span style="margin-left:10%">门店：{{n.deptName}}</span>
            </p>
            <p v-if="n.similarity!=null">相似度：{{(n.similarity).slice(-6,-4)+'%'}}</p>
            <p v-else>相似度：暂无</p>
            <p>时间：{{n.createTime}}</p>
            <p style="text-align:center;">
              <a-button @click="showmodal(n)" :style="{width:'120px'}">操作</a-button>
            </p>
          </div>
        </a-card>
      </a-col>
    </a-row>
    <a-divider></a-divider>
    <a-pagination
      showQuickJumper
      :pageSize="12"
      @change="changepage"
      v-model="current"
      :total="total"
      :style="{textAlign:'right'}"
    />

    <!-- 弹出查看人员信息 -->
    <a-modal
      :visible="visible"
      title="基本信息"
      centered
      width="96%"
      :destroyOnClose="true"
      :bodyStyle="{height:mdHeight+'px'}"
      :footer="null"
      @cancel="()=>visible=false"
    >
      <a-row v-if="personalMsg!=null">
        <a-col :span="4">
          <p>姓名：{{personalMsg.personName}}</p>
        </a-col>
        <a-col :span="4">
          <p>电话：{{personalMsg.personPhone}}</p>
          <p>年龄：{{personalMsg.personAge}}</p>
        </a-col>
        <a-col :span="4">
          <p>
            性别：
            <span v-if="personalMsg.personSex==0">男</span>
            <span v-else>女</span>
          </p>
          <!-- <p>会员等级：{{}}</p> -->
          <!-- <p>客户经理：{{}}</p> -->
        </a-col>
        <a-col :span="4">
          <p>
            门店(抓拍地)：{{personalMsg.deptName}}
            <span></span>
          </p>
        </a-col>
        <a-col :span="4">
          <div style="max-width:100%;width:100px;">
            <img :src="root_path.imgView+personalMsg.imgPaths" :style="{width:'100%'}" />
          </div>
        </a-col>
      </a-row>
      <a-tabs size="large" @change="changeTab">
        <a-tab-pane tab="抓拍记录" key="1">
          <!-- 组件 -->
          <perSnapRecord
            ref="perSnapRecord"
            :tabsHeight="mdHeight-320"
            :personId="personId"
            :personUnikey="personUnikey"
          />
        </a-tab-pane>
        <a-tab-pane tab="订单记录" key="2">
          <!-- 组件 -->
          <orderRecord ref="orderRecord" :tabsHeight="mdHeight-320" :personUnikey="personUnikey" />
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import moment from "moment";

import perSnapRecord from "./perSnapRecord";
import orderRecord from "./OrderRecord";

export default {
  components: { perSnapRecord, orderRecord },
  data() {
    return {
      visible: false,
      mdHeight: "",
      startTime: null,
      endTime: null,
      deptUnikey: null,
      personName: "",
      current: 1,
      total: 0,
      datalist: [],
      personalMsg: null,
      personUnikey: "",
      personId: "",
      allShops: []
    };
  },
  mounted() {
    let _this = this;
    window.onresize = function() {
      _this.setHeight();
    };
    this.setHeight();
    this.getSnapImg();
    // 获取所有门店
    axios
      .get(
        this.root_path.rootIP +
          this.root_path.api6001 +
          "/auth/dept-user/getShopsByUserId?userId=" +
          this.$store.state.userid
      )
      .then(res => {
        let xa = res.data.datas;
        this.allShops = xa.filter(item => item.isStore == "Y");
      });
  },
  methods: {
    moment, //页面模块用到moment时，需要手动定义moment函数
    // 预览框设置高度
    setHeight() {
      let h = window.document.body.clientHeight;
      this.mdHeight = h - 48 - 55;
    },
    // 弹出
    showmodal(data) {
      this.personUnikey = data.personUnikey;
      this.visible = true;
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/person/getByUnikey/" +
            this.personUnikey
        )
        .then(res => {
          this.personalMsg = res.data.datas;
          this.personalMsg["deptName"] = data.deptName;
          this.personId = this.personalMsg.personId;
        });
      setTimeout(() => {
        this.$refs.perSnapRecord.getPerSnapimg();
      }, 200);
    },
    // 获取识别记录
    getSnapImg() {
      let startTime =
        this.startTime == null
          ? null
          : this.startTime.format("YYYY/MM/DD HH:mm:ss");
      let endTime =
        this.endTime == null
          ? null
          : this.endTime.format("YYYY/MM/DD HH:mm:ss");
      let param = {
        personName: this.personName,
        startTime,
        endTime,
        deptUnikey: this.deptUnikey,
        size: 12,
        current: this.current,
        identifyStatus: 2
      };
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/identify-record/list",
          { params: param }
        )
        .then(data => {
          if (data.data.datas.total >= 3000) {
            this.total = 3000;
          } else {
            this.total = data.data.datas.total;
          }
          this.datalist = data.data.datas.records;
        });
    },
    // 点击分页
    changepage(page, pageSize) {
      this.current = page;
      this.getSnapImg();
    },
    // 点击tabs切换
    changeTab(key) {
      if (key == "2") {
        setTimeout(() => {
          this.$refs.orderRecord.getOrderRecord();
        }, 200);
      }
    },
    // 开始时间——设置可选时间
    disabledStartDate(startValue) {
      if (!startValue || !this.endTime) {
        return false;
      }
      return startValue.valueOf() > this.endTime.valueOf();
    },
    // 结束时间——设置可选时间
    disabledEndDate(endValue) {
      if (!endValue || !this.startTime) {
        return false;
      }
      return this.startTime.valueOf() >= endValue.valueOf();
    },
    // 搜索
    search() {
      this.current = 1;
      this.getSnapImg();
    },
    // 重置搜索
    resetValue() {
      this.startTime = null;
      this.endTime = null;
      this.deptUnikey = null;
      this.personName = "";
      this.current = 1;
      this.getSnapImg();
    }
  }
};
</script>

<style scoped>
.showimg {
  width: 100%;
  text-align: center;
}
.showimg div {
  width: 45%;
  display: inline-block;
  height: 130px;
  line-height: 130px;
  overflow: hidden;
}
.showimg div p {
  text-align: center;
  margin-bottom: 5px;
}
.showimg div img {
  width: 100%;
  /* height: 170px; */
}
.card_line_p p {
  margin-bottom: 0.5em;
}
.searchBtns button {
  margin: 0 5px;
}
</style>