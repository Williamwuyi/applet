<template>
  <div class="box">
    <div v-if="$store.state.iscasher==1" style="height:30px;line-height:30px;margin-top:5px;">
      <span style="float:left;margin-left:20px;">
        当前账号：
        <span style="color:#1890ff;font-weight: bold;">{{$store.state.userName}}</span>
      </span>
      <span style="float:right;margin-right:20px;">
        <a-button size="small" @click="loginout">退出登录</a-button>
      </span>
    </div>
    <a-divider style="margin:5px 0"></a-divider>
    <div style="margin-bottom: 5px;">
      <div v-if="visFlag" class="selbox">
        <span>选择门店：</span>
        <span>
          <a-select size="small" @change="onChange" :style="{width:'100px'}" v-model="deptUnikey">
            <a-select-option
              v-for="n in shopArr"
              :key="n.deptId"
              :value="n.deptUnikey"
            >{{n.deptName}}</a-select-option>
          </a-select>
        </span>
      </div>
      <div v-if="visFlag" class="selbox">
        <span>选择摄像头：</span>
        <span>
          <a-select
            size="small"
            @change="changeDev"
            v-model="deviceunikey"
            :style="{width:'100px'}"
          >
            <a-select-option value>全部</a-select-option>
            <a-select-option
              v-for="(n,index) in devArr"
              :key="index"
              :value="n.deviceunikey"
            >{{n.deviceaddress}}</a-select-option>
          </a-select>
        </span>
      </div>
    </div>

    <div style="text-align:right">
      <a-checkbox v-model="autoRefresh" @change="changeRefresh">自动刷新</a-checkbox>
      <a-button :style="{marginLeft:'10px'}" size="small" icon="reload" @click="selfRefresh">刷新</a-button>
    </div>
    <!-- 抓拍图展示区 @click="selimg(n)"-->
    <a-spin :spinning="spinning" :delay="400">
      <div class="imgbox" @scroll="imgBoxScroll" :style="{height:mHeight+'px'}">
        <a-row :gutter="15" :style="{marginLeft:0,marginRight:0}">
          <a-col :span="rowsanp" v-for="(n,index) in snapRecords" :key="index">
            <div class="bg" @click="selimg(n)">
              <img
                :src="root_path.rootIP+root_path.api6001+'/eq/hardware/showPersonImg?fileName='+n.snapImage+'&snapUnikey='+n.snapUnikey"
                :style="{width:'100%'}"
              />
              <span class="abs" v-if="n.identifyStatus!=0">
                {{n.personName}}
                <span v-if="n.identifyStatus==1">(员工)</span>
                <span v-else-if="n.identifyStatus==2">(会员)</span>
                <span v-else-if="n.identifyStatus==-1">(黑名单)</span>
              </span>
            </div>
          </a-col>
        </a-row>
      </div>
    </a-spin>
    <a-divider style="margin:10px 0"></a-divider>
    <div class="botSub">
      <label style="margin-left:20px">
        手机号
        <span style="color:red">*</span>：
      </label>
      <a-input
        :maxLength="11"
        size="small"
        type="text"
        :style="{width:'120px'}"
        v-model="phoneNum"
        @change="controlNum"
      ></a-input>
    </div>
    <div class="botSub">
      <a-button size="small" @click="reset">重置</a-button>
      <a-button size="small" type="primary" @click="subMember" :loading="loading">确定</a-button>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import SockJS from "sockjs-client";
import formPattern from "@/components/commons/formPattern.js";

export default {
  data() {
    return {
      shopArr: [],
      devArr: [],
      snapRecords: [],
      deviceunikey: "",
      perImgpath: "",
      phoneNum: "",
      deptUnikey: "",
      websock: null,
      recordId: "",
      rowsanp: 8,
      personStatus: "",
      visFlag: true,
      size: 12,
      spinning: false,
      countFlag: 1,
      mHeight: "",
      autoRefresh: true,
      loading: false
    };
  },
  mounted() {
    // 加载门店数据
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
        console.log("获取门店数据：", this.shopArr);
        if (this.shopArr != null) {
          //如果门店不为空
          this.deptUnikey = this.shopArr[0].deptUnikey; //设置初始门店
          console.log("摄像头id：", this.deptUnikey);
          this.getRecord(""); //摄像头默认为空（全部）
          this.initWebSocket(); //初始连接websock
        }
        // 加载摄像头数据
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/eq/device-dept/getDeviceListByDept/" +
              this.deptUnikey
          )
          .then(res1 => {
            this.devArr = res1.data.datas;
            console.log("获取摄像头数据：", this.devArr);
            // this.getRecord(this.devArr);
            // if(this.devArr!=null){
            //   this.deviceunikey=this.devArr[0].deviceunikey;//设置初始摄像头
            //   if(this.deviceunikey==null||this.deviceunikey==''){
            //     this.getRecord('all');
            //   }else{
            //     }
            // }
          });
      })
      .catch(error => {});
    // 页面宽度调整
    let _this = this;
    this.getWinWidth();
    window.onresize = function() {
      _this.getWinWidth();
    };
    // 页面滚动
    // this.imgBoxScroll()
  },
  methods: {
    // 选择识别照片
    selimg(data) {
      let a = document.getElementsByClassName("bg");
      for (let i = 0; i < a.length; i++) {
        a[i].className = "bg";
      }
      event.target.parentElement.className = "bg active";
      this.perImgpath =
        this.root_path.rootIP +
        this.root_path.api6001 +
        "/eq/hardware/showPersonImg?snapUnikey=" +
        data.snapUnikey +
        "&fileName=" +
        data.snapImage;
      this.recordId = data.recordId;
      this.personStatus = data.identifyStatus;
    },
    //退出登录
    loginout() {
      let _this = this;
      this.$confirm({
        title: "是否确认退出",
        okText: "确定",
        cancelText: "取消",
        onOk() {
          _this.setNameStorage();
          _this.$router.$addRoutes([
            //重设路由表
            {
              path: "/login",
              name: "login",
              component: () => import("@/components/Login/login")
            },
            { path: "*", component: () => import("@/components/404page/404") }
          ]);
          _this.$router.push({ name: "login" });
        }
      });
    },
    // 退出登录时，只留存账号名
    setNameStorage() {
      let name = localStorage.getItem("userName");
      let isRememberPwd = localStorage.getItem("isRememberPwd");
      localStorage.clear();
      localStorage.setItem("name", name);
      localStorage.setItem("isRememberPwd", isRememberPwd);
    },
    // 选择门店时，触发查找设备操作
    onChange(value) {
      if (this.websock != null) {
        this.closeWebsock();
      }
      this.deviceunikey = ""; //重置设备选择框
      this.snapRecords = []; //清空上一个设备查询的识别结果
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/device-dept/getDeviceListByDept/" +
            value
        )
        .then(res => {
          this.devArr = res.data.datas;
          // if(this.devArr==null||this.devArr.length==0){
          //  return;
          // }else{
          //   this.deviceunikey=this.devArr[0].deviceunikey;
          //   if(this.deviceunikey==null||this.deviceunikey==''){
          this.changeDev("");
          // }else{
          //   }
          // }
        });
      this.selfRefresh();
    },
    // 获取识别记录
    getRecord(data) {
      console.log("摄像设备ID：", data, "门店ID：", this.deptUnikey);
      if (data == null || data == "") {
        data = "all";
      }
      const param = {
        size: this.size,
        snapUnikey: data,
        deptUnikey: this.deptUnikey
      };
      this.spinning = true;
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/identify-record/getBySnapUnikey?time=" +
            Math.random(),
          { params: param }
        )
        .then(res => {
          this.snapRecords = res.data.datas.records;
          this.spinning = false;
        })
        .catch(error => {});
    },
    // 选择设备时，查找识别记录，连接WS
    changeDev(value) {
      if (this.websock != null) {
        this.closeWebsock();
      }
      console.log("选中摄像头ID为：", value);
      this.countFlag = 1;
      this.size = 12;
      this.initWebSocket();
      this.getRecord(value);
    },
    // 注册会员信息
    subMember() {
      if (this.phoneNum == "" || this.phoneNum == null) {
        this.$message.error("请输入手机号");
        return;
      } else if (this.perImgpath == "" || this.perImgpath == null) {
        this.$message.error("请选择需要注册的人员图片");
        return;
      } else if (this.personStatus != 0) {
        this.$message.error("当前所选已是库内人员！");
        return;
      } else if (!/^1[3456789]\d{9}$/.test(this.phoneNum)) {
        this.$message.error("请输入正确手机号码！");
        return;
      }
      this.loading = true;
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/person/uploadFileFromUrl",
          qs.stringify({ url: this.perImgpath })
        )
        .then(res => {
          let img = res.data.datas;
          const param = {
            personPhone: this.phoneNum,
            imgPaths: img,
            status: 2,
            personName: "普通会员",
            deptUnikey: this.deptUnikey
          };
          axios
            .post(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/eq/identify-record/addPerson?" +
                qs.stringify({ recordId: this.recordId }),
              param
            )
            .then(res => {
              if (res.data.resp_code == 0) {
                this.$notification.success({
                  message: "操作成功",
                  bottom: "50px"
                });
                this.phoneNum = "";
              } else if (res.data.resp_code == 444) {
                const params = {
                  deptUnikey: this.deptUnikey,
                  personUnikey: res.data.datas.personUnikey
                };
                const param = {
                  recordId: this.recordId,
                  status: 1
                };
                let _this = this;
                this.$confirm({
                  title: "该客户已在其他门店注册",
                  content: "点击确定，将从其他门店获取该会员信息至本店。",
                  width: 350,
                  okText: "确定",
                  okType: "primary",
                  cancelText: "取消",
                  onOk() {
                    axios
                      .all([
                        axios.post(
                          _this.root_path.rootIP +
                            _this.root_path.api6001 +
                            "/org/shareperson/add",
                          qs.stringify(params)
                        ),
                        axios.post(
                          _this.root_path.rootIP +
                            _this.root_path.api6001 +
                            "/eq/identify-record/changeStatus",
                          qs.stringify(param)
                        )
                      ])
                      .then(
                        axios.spread((res_pre, res_status) => {
                          if (res_pre.data.resp_code == 0) {
                            _this.$notification.success({
                              message: "操作成功",
                              bottom: "50px"
                            });
                            _this.phoneNum = "";
                          } else {
                            _this.$notification.warning({
                              message: res_pre.data.resp_msg
                            });
                          }
                        })
                      )
                      .catch(error => {
                        alert("操作失败!", error);
                      });
                  }
                });
              } else {
                this.$notification.warning({ message: res.data.resp_msg });
              }
              this.loading = false;
            })
            .catch(error => {
              this.loading = false;
              alert("操作失败:", error);
            });
        });
    },
    // 重置
    reset() {
      this.phoneNum = "";
      this.cleanPreImg();
    },
    // websockt连接
    initWebSocket() {
      if (!this.autoRefresh) {
        return;
      }
      //初始化websockt
      let websock = new SockJS(
        this.root_path.webSockt + this.deviceunikey
      );
      // let websock = new SockJS(
      //   this.root_path.rootIP + ":6001/sock-js?user=welcome:"+this.$store.state.comUnikey+":all"
      // );
      this.websock = websock;
      this.websock.onmessage = this.websocketonmessage;
      let _this = this;
      this.$router.afterEach(function() {
        _this.websock.close();
      });
    },
    websocketonmessage(e) {
      this.getRecord(this.deviceunikey);
    },
    // 关闭websock
    closeWebsock() {
      this.websock.close();
    },
    getWinWidth() {
      let w = window.document.body.clientWidth;
      let y = window.document.body.clientHeight;
      if (w < 280) {
        this.rowsanp = 12;
      } else {
        this.rowsanp = 8;
      }
      if (w < 410) {
        this.visFlag = false;
      } else {
        this.visFlag = true;
      }
      if (y > 500) {
        let aa = y - 300;
        this.mHeight = aa > 450 ? 450 : aa;
      } else {
        this.mHeight = 250;
      }
    },
    // 设置图片区域滚动事件
    imgBoxScroll() {
      let x = document.getElementsByClassName("imgbox")[0].scrollTop;
      let ad = document.getElementsByClassName("imgbox")[0].scrollHeight;
      let ac = document.getElementsByClassName("imgbox")[0].clientHeight;
      let t = ad - ac - x;
      if (!this.spinning && this.countFlag < 5) {
        //最多加载5倍数据
        if (t < 30) {
          this.countFlag++;
          this.spinning = true;
          this.size = this.countFlag * 12;
          this.getRecord(this.deviceunikey);
        }
      }
    },
    // 校验电话号码
    controlNum(event) {
      // this.phoneNum=formPattern.validatorPhone(event);
    },
    // 自动刷新状态修改
    changeRefresh(e) {
      let status = e.target.checked;
      if (!status) {
        //取消自动刷新时，断开ws
        if (this.websock != null) {
          this.closeWebsock();
        }
      } else {
        this.initWebSocket();
        this.getRecord(this.deviceunikey);
      }
    },
    // 手动刷新
    selfRefresh() {
      this.cleanPreImg();
      this.getRecord(this.deviceunikey);
    },
    // 清空人员选择的状态及图片
    cleanPreImg() {
      let a = document.getElementsByClassName("bg");
      for (let i = 0; i < a.length; i++) {
        a[i].classList.remove("active");
      }
      this.perImgpath = "";
    }
  }
};
</script>

<style scoped>
.box {
  width: 100%;
  max-width: 470px;
  margin: 0 auto;
  /* padding: 15px; */
  overflow-x: hidden;
  overflow-y: hidden;
}
.box::-webkit-scrollbar,
.box::-moz-scrollbar,
.box::-ms-scrollbar {
  display: none;
}
.imgbox {
  height: 250px;
  overflow-x: hidden;
  overflow-y: auto;
  background-color: #f1f5fe;
}
.imgbox .bg {
  margin-top: 20px;
  height: 120px;
  line-height: 120px;
  overflow: hidden;
  position: relative;
  background-color: #aaa;
  cursor: pointer;
}
.imgbox .bg .abs {
  display: inline-block;
  width: 100%;
  text-align: center;
  color: white;
  position: absolute;
  bottom: 0;
  left: 0;
  line-height: normal;
}
.imgbox .bg .abs span {
  color: white;
}
.imgbox .bg.active {
  box-shadow: 2px 2px 10px #1890ff, -2px -2px 10px #1890ff;
}
.imgbox::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}
.imgbox::-webkit-scrollbar-track {
  background: rgb(239, 239, 239);
  border-radius: 2px;
}
.imgbox::-webkit-scrollbar-thumb {
  background: #bfbfbf;
  border-radius: 10px;
}
.imgbox::-webkit-scrollbar-thumb:hover {
  background: #333;
}
.botSub {
  margin-bottom: 10px;
  margin-right: 10px;
  display: inline-block;
}
.selbox {
  display: inline-block;
  margin-left: 20px;
}
.selbox span:first-child {
  display: inline-block;
}
</style>