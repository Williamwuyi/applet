<template>
  <div>
    <h4>注册管理</h4>
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
      <a-form-item label="状态">
        <a-select :style="{width:'120px'}" v-model="status">
          <a-select-option :value="0">未处理</a-select-option>
          <a-select-option :value="1">已处理</a-select-option>
          <a-select-option :value="-1">已忽略</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="对象类型">
        <a-select :style="{width:'120px'}" v-model="identifyStatus">
          <a-select-option value>全部</a-select-option>
          <a-select-option value="1">员工</a-select-option>
          <a-select-option value="2">会员</a-select-option>
          <a-select-option value="-1">黑名单</a-select-option>
          <a-select-option value="0">陌生人</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="起止日期">
        <a-date-picker
          v-model="startTime"
          format="YYYY-MM-DD HH:mm:ss"
          :showTime="{defaultValue:moment('00:00:00','HH:mm:ss')}"
          @openChange="handleStartOpenChange"
        />
        <span style="font-size:18px;">~</span>
        <a-date-picker
          v-model="endTime"
          :showTime="{defaultValue:moment('23:59:59','HH:mm:ss')}"
          :open="endOpen"
          format="YYYY-MM-DD HH:mm:ss"
          @openChange="handleEndOpenChange"
        />
      </a-form-item>
      <a-form-item label class="searchBtns">
        <a-button type="primary" size="small" @click="setDatetime('today')">今天</a-button>
        <a-button type="primary" size="small" @click="setDatetime('yestoday')">昨天</a-button>
        <a-button type="primary" size="small" @click="setDatetime('week')">一周</a-button>
        <a-button type="primary" size="small" @click="setDatetime('month')">本月</a-button>
      </a-form-item>
      <a-form-item label class="searchBtns">
        <a-button type="primary" @click="search">搜索</a-button>
        <a-button type="primary" @click="resetValue">重置</a-button>
      </a-form-item>
    </a-form>
    <!-- <div style="float:right;">
      <a-checkbox>自动刷新</a-checkbox>
    </div>-->
    <a-divider></a-divider>
    <a-spin :spinning="spinning" :delay="500">
      <a-row :gutter="30">
        <a-col
          :lg="8"
          :xl="6"
          :xxl="4"
          v-for="n in datalist"
          :key="n.recordId"
          :style="{marginBottom:'2em'}"
        >
          <a-card
            class="card_line_p"
            :style="{width:'100%',marginTop:'30px;'}"
            :bodyStyle="{padding:'10px 14px'}"
          >
            <!-- n.snapImage -->
            <div class="imgbox">
              <img
                :src="root_path.rootIP+root_path.api6001+'/eq/hardware/showPersonImg?fileName='+n.snapImage+'&snapUnikey='+n.snapUnikey"
                style="width:100%;"
              />
            </div>
            <p>
              姓名：
              <span v-if="n.identifyStatus==1">{{n.personName+'(员工)'}}</span>
              <span v-else-if="n.identifyStatus==2">{{n.personName+'(会员)'}}</span>
              <span v-else-if="n.identifyStatus==-1">{{n.personName+'(黑名单)'}}</span>
              <span v-else>{{n.personName}}</span>
            </p>
            <p>门店：{{n.deptName}}</p>
            <p>设备：{{n.deviceName}}</p>
            <p>时间：{{n.createTime.slice(-14)}}</p>
            <p>
              状态：
              <span v-if="n.status==0">未处理</span>
              <span v-else-if="n.status==1">已处理</span>
              <span v-else-if="n.status==-1">已忽略</span>
            </p>
            <p style="text-align:center;">
              <a-dropdown :disabled="n.identifyStatus!=0">
                <a-menu slot="overlay" @click="handleMenuClick($event,n)">
                  <a-menu-item key="menber">会员注册</a-menu-item>
                  <a-menu-item key="visitor">临时注册</a-menu-item>
                  <a-menu-item key="blacklist">黑名单</a-menu-item>
                </a-menu>
                <a-button :style="{width:'80px'}">
                  操&nbsp;作
                  <a-icon type="down" />
                </a-button>
              </a-dropdown>
              <a-button :disabled="n.status!=0" type="primary" @click="ignorePer(n)">忽略</a-button>
            </p>
          </a-card>
        </a-col>
      </a-row>
    </a-spin>
    <a-divider></a-divider>
    <a-pagination
      showQuickJumper
      :pageSize="12"
      @change="changepage"
      v-model="current"
      :total="total"
      :style="{textAlign:'right'}"
    />

    <!-- 会员注册 -->
    <add-member
      ref="menberForm"
      :visible="visible"
      :deptOptions="allShops"
      :personimg="personimg"
      :mSpinning="mSpinning"
      @cancel="closeModal"
      @create="subMember"
    />

    <add-black-list
      ref="blacklistForm"
      :visible="blacklistvisible"
      :personimg="personimg"
      :mSpinning="mSpinning"
      @cancel="closeModal"
      @create="subBlacklist"
    />
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import moment from "moment";

import addMember from "./addMember";
import addBlackList from "./addBlackList";

export default {
  components: { addMember, addBlackList },
  data() {
    return {
      startTime: null,
      endTime: null,
      endOpen: false,
      current: 1,
      total: 0,
      datalist: [],
      deptUnikey: null,
      status: null, //搜索状态
      identifyStatus: null, //对象类型
      allShops: [],
      visible: false,
      personimg: "",
      recordId: "",
      spinning: false,
      mSpinning: false,
      blacklistvisible: false
    };
  },
  mounted() {
    this.getSnapImg();
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
    moment,
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
        status: this.status,
        identifyStatus: this.identifyStatus,
        startTime,
        endTime,
        deptUnikey: this.deptUnikey,
        size: 12,
        current: this.current
      };
      this.spinning = true;
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/identify-record/list",
          { params: param }
        )
        .then(data => {
          this.spinning = false;
          if (data.data.datas.total >= 3000) {
            this.total = 3000;
          } else {
            this.total = data.data.datas.total;
          }

          if (
            data.data.datas.records == null ||
            data.data.datas.records.length == 0
          ) {
            console.log("没搜索到数据");
            this.$notification.warning({ message: "未搜索到数据！" });
            this.datalist = data.data.datas.records;
          } else {
            console.log("有数据", data.data.datas.records);
            this.datalist = data.data.datas.records;
          }
        })
        .catch(err => {
          this.spinning = false;
        });
    },
    //点击分页切换
    changepage(page, pageSize) {
      this.current = page;
      this.getSnapImg();
    },
    // 搜索
    search() {
      this.current = 1;
      this.getSnapImg();
    },
    // 重置
    resetValue() {
      this.status = null;
      this.startTime = null;
      this.endTime = null;
      this.deptUnikey = null;
      this.current = 1;
      this.getSnapImg();
    },
    // 抓拍注册操作集合
    handleMenuClick(e, res) {
      let types = e.key;
      if (types == "menber") {
        this.menberRgtOpen(res);
      } else if (types == "visitor") {
        this.menberRgtOpen(res);
      } else if (types == "blacklist") {
        this.blacklistRgtOpen(res);
      }
    },
    // 会员注册————弹出注册框，组合数据
    menberRgtOpen(data) {
      this.visible = true;
      const form = this.$refs.menberForm.form;
      let imgsrc =
        this.root_path.rootIP +
        this.root_path.api6001 +
        "/eq/hardware/showPersonImg?snapUnikey=" +
        data.snapUnikey +
        "&fileName=" +
        data.snapImage;
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/person/uploadFileFromUrl",
          qs.stringify({ url: imgsrc })
        )
        .then(res => {
          const param = {
            personName: data.personName,
            imgPaths: res.data.datas,
            deptUnikey: data.deptUnikey,
            personSex: data.gender,
            personAge: data.age
          };
          form.setFieldsValue(param);
          this.personimg = imgsrc;
          this.recordId = data.recordId;
        });
    },
    // 黑名单注册————弹出注册框，组合数据
    blacklistRgtOpen(data) {
      this.blacklistvisible = true;
      const form = this.$refs.blacklistForm.form;
      let imgsrc =
        this.root_path.rootIP +
        this.root_path.api6001 +
        "/eq/hardware/showPersonImg?snapUnikey=" +
        data.snapUnikey +
        "&fileName=" +
        data.snapImage;
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/person/uploadFileFromUrl",
          qs.stringify({ url: imgsrc })
        )
        .then(res => {
          const param = {
            person: {
              imgPaths: res.data.datas,
              personName: data.personName,
              personSex: data.gender
            }
          };
          form.setFieldsValue(param);
          this.personimg = imgsrc;
          this.recordId = data.recordId;
        });
    },
    // 关闭注册框
    closeModal() {
      this.visible = false;
      this.blacklistvisible = false;
      this.mSpinning = false;
    },
    // 注册会员——提交
    subMember() {
      const form = this.$refs.menberForm.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        if (this.mSpinning) {
          return;
        } 
        // else if (!/^1[3456789]\d{9}$/.test(values.personPhone)) {
        //   this.$message.error("请输入正确手机号码！");
        //   return;
        // }
        this.mSpinning = true;
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/eq/identify-record/addPerson?" +
              qs.stringify({ recordId: this.recordId }),
            values
          )
          .then(res => {
            if (res.data.resp_code == 0) {
              this.$notification.success({ message: "操作成功" });
              this.visible = false;
              this.getSnapImg();
            } else if (res.data.resp_code == 444) {
              const params = {
                deptUnikey: values.deptUnikey,
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
                          _this.$notification.success({ message: "操作成功" });
                          _this.visible = false;
                          _this.getSnapImg();
                        } else {
                          this.$notification.warning({
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
            this.mSpinning = false;
          })
          .catch(err => {
            this.mSpinning = false;
            alert("操作失败:", err);
          });
      });
    },
    // 黑名单注册
    subBlacklist() {
      const form = this.$refs.blacklistForm.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        if (this.mSpinning) {
          return;
        } else if (!/^1[3456789]\d{9}$/.test(values.personPhone)) {
          this.$message.error("请输入正确手机号码！");
          return;
        }
        this.mSpinning = true;
        const param = {
          recordId: this.recordId,
          status: 1
        };
        axios
          .all([
            axios.put(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/org/blacklist/saveOrUpdate",
              { ...values }
            ),
            axios.post(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/eq/identify-record/changeStatus",
              qs.stringify(param)
            )
          ])
          .then(
            axios.spread((res_pre, res_status) => {
              if (res_pre.datas.resp_code == 0) {
                this.$notification.success({ message: "操作成功" });
                this.blacklistvisible = false;
                this.getSnapImg();
              } else {
                this.$notification.warning({ message: res_pre.data.resp_msg });
              }
              this.mSpinning = false;
            })
          )
          .catch(error => {
            this.mSpinning = false;
            alert("操作失败！" + error);
          });
      });
    },
    // 忽略客户
    ignorePer(data) {
      const param = {
        recordId: data.recordId,
        status: -1
      };
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/identify-record/changeStatus",
          qs.stringify(param)
        )
        .then(res => {
          this.getSnapImg();
        });
    },
    // 开始时间——设置可选时间
    // disabledStartDate(startValue){
    //   if (!startValue || !this.endTime) {
    //     return false;
    //   }
    //   return startValue.valueOf() > this.endTime.valueOf();
    // },
    // 结束时间——设置可选时间
    // disabledEndDate(endValue){
    //   let sTime = this.startTime;
    //   if (!endValue || !this.startTime) {
    //     return false;
    //   }
    //   return this.startTime.valueOf() >= endValue.valueOf();
    // },
    handleStartOpenChange(open) {
      if (!open) {
        this.endOpen = true;
      }
    },
    handleEndOpenChange(open) {
      this.endOpen = open;
      if (this.startTime.valueOf() >= this.endTime.valueOf()) {
        this.$notification.warning({ message: "日期选择不正确" });
      }
    },
    // 按钮设置时间
    setDatetime(data) {
      this.startTime = null;
      this.endTime = null;
      let a = new Date();
      let x, y;
      switch (data) {
        case "today":
          a.setHours(0, 0, 0, 0);
          x = moment(a, "YYYY-MM-DD HH:mm:ss");
          this.startTime = x;
          this.current = 1;
          this.getSnapImg();
          break;
        case "yestoday":
          a.setDate(a.getDate() - 1);
          a.setHours(0, 0, 0, 0);
          x = moment(a, "YYYY-MM-DD HH:mm:ss");
          a.setHours(23, 59, 59, 999);
          y = moment(a, "YYYY-MM-DD HH:mm:ss");
          this.startTime = x;
          this.endTime = y;
          this.current = 1;
          this.getSnapImg();
          break;
        case "week":
          a.setDate(a.getDate() - 6);
          a.setHours(0, 0, 0, 0);
          x = moment(a, "YYYY-MM-DD HH:mm:ss");
          this.startTime = x;
          this.current = 1;
          this.getSnapImg();
          break;
        case "month":
          a.setDate(1);
          a.setHours(0, 0, 0, 0);
          x = moment(a, "YYYY-MM-DD HH:mm:ss");
          this.startTime = x;
          this.current = 1;
          this.getSnapImg();
          break;
      }
    }
  }
};
</script>

<style scoped>
.searchBtns button {
  margin: 0 5px;
}
.card_line_p p {
  margin-bottom: 0.5em;
}
.card_line_p .imgbox {
  margin-bottom: 0.5em;
  height: 220px;
  line-height: 220px;
  overflow: hidden;
}
</style>