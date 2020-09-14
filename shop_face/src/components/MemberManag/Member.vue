<template>
  <div>
    <h4>会员管理</h4>
    <a-form :form="form1" layout="inline">
      <a-form-item label="姓名|电话">
        <a-input placeholder="输入姓名或电话" v-decorator="['searchTerm']" :style="{width:'140px'}"></a-input>
      </a-form-item>
      <a-form-item label="门店">
        <a-select :style="{width:'150px'}" v-decorator="['deptUnikey']">
          <a-select-option
            v-for="(n,index) in allShops"
            :key="index"
            :value="n.deptUnikey"
          >{{n.deptName}}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="会员范围">
        <a-select :style="{width:'120px'}" v-decorator="['isSelt',{initialValue:'all'}]">
          <a-select-option value="all">所有会员</a-select-option>
          <a-select-option value="Y">本店会员</a-select-option>
          <a-select-option value="N">共享会员</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="性别">
        <a-select :style="{width:'100px'}" v-decorator="['personSex']">
          <a-select-option value="0">男</a-select-option>
          <a-select-option value="1">女</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="起止日期">
        <a-date-picker
          :disabledDate="disabledStartDate"
          format="YYYY-MM-DD HH:mm:ss"
          :showTime="{defaultValue:moment('00:00:00','HH:mm:ss')}"
          v-model="startTime"
        />
        <span style="font-size:18px;">~</span>
        <a-date-picker
          :disabledDate="disabledEndDate"
          format="YYYY-MM-DD HH:mm:ss"
          :showTime="{defaultValue:moment('23:59:59','HH:mm:ss')}"
          v-model="endTime"
        />
      </a-form-item>
      <a-form-item label class="searchBtns">
        <a-button type="primary" @click="search">搜索</a-button>
        <a-button type="primary" @click="resetValue">重置</a-button>
      </a-form-item>
    </a-form>
    <div>
      <a-button type="primary" @click="addMember">新增</a-button>
    </div>
    <a-divider></a-divider>
    <a-table
      :rowKey="record => record.personId"
      :columns="columns"
      :dataSource="datalist"
      :pagination="pagination"
      :scroll="{x:1050}"
      bordered
      :style="{'margin-top':'15px'}"
      @change="handleTableChange"
    >
      <span slot="action" slot-scope="record">
        <span v-if="record.isSelt=='Y'">
          <a-button type="primary" size="small" @click="editMember(record)">编辑</a-button>
          <a-button type="danger" size="small" @click="delMember(record)">删除</a-button>
        </span>
      </span>
      <span slot="sex" slot-scope="personSex">
        <span v-if="personSex==0">男</span>
        <span v-else-if="personSex==1">女</span>
      </span>
      <span slot="isEnable" slot-scope="record">
        <a-switch
          checkedChildren="启用"
          unCheckedChildren="停用"
          :disabled="record.isSelt=='N'"
          :checked="record.isEnable=='Y'?true:false"
          @click="changeEnable(record)"
        />
      </span>
      <span slot="check" slot-scope="personId">
        <a-button type="primary" size="small" @click="checkphoto(personId)">查看</a-button>
      </span>
      <span slot="showdept" slot-scope="deptUnikey">
        <span v-for="(n,index) in allDept" :key="index">
          <span v-if="n.deptUnikey==deptUnikey">{{n.deptName}}</span>
        </span>
      </span>
    </a-table>

    <add-edit-member
      ref="personForm"
      :visible="visible"
      :personimg="personimg"
      :showimg="showimg"
      :imgIsQualify="imgIsQualify"
      :listfile="listfile"
      :deptOptions="allShops"
      :spinning="spinning"
      @checkImgIsQualify="checkImgIsQualify"
      @cancel="handleCancel"
      @create="handleCreate"
      @getbaseimgurl="getbaseimgurl"
    />

    <!-- 查看照片 -->
    <a-modal :visible="imgshow" :footer="null" @cancel="()=>this.imgshow=false">
      <div>
        <img :src="modal_img_src" style="width:100%" />
      </div>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import moment from "moment";
import addEditMember from "./addEditMember";

const columns = [
  {
    title: "姓名",
    width: 120,
    dataIndex: "personName",
    key: "2"
  },
  { title: "电话", width: 120, dataIndex: "personPhone", key: "3" },
  {
    title: "性别",
    width: 120,
    dataIndex: "personSex",
    key: "4",
    scopedSlots: { customRender: "sex" }
  },
  {
    title: "照片",
    width: 80,
    dataIndex: "personId",
    key: "1",
    scopedSlots: { customRender: "check" }
  },
  {
    title: "门店",
    width: 160,
    dataIndex: "deptUnikey",
    key: "5",
    scopedSlots: { customRender: "showdept" }
  },
  {
    title: "状态",
    width: 120,
    key: "6",
    scopedSlots: { customRender: "isEnable" }
  },
  {
    title: "录入日期",
    width: 150,
    dataIndex: "createTime",
    key: "7"
  },
  {
    title: "操作",
    width: 180,
    key: "root",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  components: { addEditMember },
  data() {
    return {
      datalist: [],
      pagination: {},
      columns,
      visible: false,
      modal_img_src: "",
      personimg: "",
      imgshow: false,
      showimg: "",
      spinning: false,
      listfile: [],
      imgIsQualify: true,
      addOrEditFlag: false,
      allDept: [],
      allShops: [],
      startTime: null,
      endTime: null
    };
  },
  inject: ["reload"],
  beforeCreate() {
    this.form1 = this.$form.createForm(this);
  },
  mounted() {
    this.fetch();
    axios
      .get(
        this.root_path.rootIP +
          this.root_path.api6001 +
          "/auth/dept-user/getShopsByUserId?userId=" +
          this.$store.state.userid
      )
      .then(res => {
        let x = res.data.datas;
        this.allShops = x.filter(item => item.isStore == "Y");
      });
    axios
      .get(this.root_path.rootIP + this.root_path.api6001 + "/org/dept/findAll")
      .then(res => {
        this.allDept = res.data.datas;
      });
  },
  methods: {
    moment,
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
      const form = this.form1;
      form.validateFields((err, values) => {
        //获取搜索内容
        if (err) {
          return;
        }
        const dlparam = {
          ...values,
          endTime:
            this.endTime == null
              ? null
              : this.endTime.format("YYYY/MM/DD HH:mm:ss"),
          startTime:
            this.startTime == null
              ? null
              : this.startTime.format("YYYY/MM/DD HH:mm:ss")
        };
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/person/getVisitors",
            { params: { size: 10, ...dlparam, ...params } }
          )
          .then(data => {
            const pagination = { ...this.pagination };
            pagination.pageSize = data.data.datas.size;
            pagination.total = data.data.datas.total;
            this.datalist = data.data.datas.records;
            this.pagination = pagination;
          });
      });
    },
    //弹出会员添加框
    addMember() {
      this.visible = true;
      this.imgIsQualify = true;
    },
    //编辑会员信息
    editMember(data) {
      this.visible = true;
      this.imgIsQualify = true;
      const form = this.$refs.personForm.form;
      setTimeout(() => {
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/person/getByUnikey/" +
              data.personUnikey
          )
          .then(res => {
            console.log(res);
            if (
              res.data.datas.imgPaths == "" ||
              res.data.datas.imgPaths == null
            ) {
              this.listfile = "";
            } else {
              let str = res.data.datas.imgPaths; //后台返回的 多个图片地址用逗号隔开
              let impaths = str.split(","); //分别等到每个逗号隔开地址，并循环
              for (let i = 0; impaths.length > i; i++) {
                //循环中，需要自定义对象中的属性，给定义中的属性赋值，接收每个地址，并存到数组中
                var obj = {};
                obj.uid = i + "1";
                obj.name = "丢失";
                obj.url = this.root_path.imgView + impaths[i];
                this.listfile.push(obj);
              }
              console.log("图片数组对象：", this.listfile);
            }
            form.setFieldsValue(res.data.datas);
          });
      }, 0);
    },
    // 删除会员
    delMember(data) {
      let _this = this;
      this.$confirm({
        title: "是否确定删除？",
        okText: "确定",
        okType: "danger",
        cancelText: "取消",
        onOk() {
          axios
            .delete(
              _this.root_path.rootIP +
                _this.root_path.api6001 +
                "/org/person/delete/" +
                data.personId
            )
            .then(res => {
              _this.fetch({ current: _this.pagination.current });
            });
        }
      });
    },
    // 图片检测设置状态
    checkImgIsQualify(data) {
      this.imgIsQualify = data;
    },
    // 关闭弹框
    handleCancel() {
      this.visible = false;
      this.personimg = "";
      this.showimg = "";
      this.reload();
      this.spinning = false;
    },
    // 提交表单
    handleCreate() {
      const form = this.$refs.personForm.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        console.log("提交表单数据：", values);
        if (!this.imgIsQualify) {
          alert("照片未检测出人脸!");
          return;
        }
        if (this.spinning) {
          return;
        }
        this.spinning = true;
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/person/saveOrUpdate",
            {
              ...values
            }
          )
          .then(res => {
            if (res.data.resp_code == 0) {
              //添加用户成功
              if (this.addOrEditFlag) {
                //操作为编辑时
                this.fetch({ current: this.pagination.current });
              } else {
                //操作位新增时
                this.fetch({ current: 1 });
                this.pagination.current = 1;
              }
              this.$notification.success({ message: "操作成功" });
              form.resetFields();
              this.personimg = "";
              this.showimg = "";
              this.visible = false;
              this.reload();
              this.addOrEditFlag = false; //阀值复位
            } else {
              this.$notification.warning({ message: res.data.resp_msg });
            }
            this.spinning = false;
          })
          .catch(error => {
            this.spinning = false;
          });
      });
    },
    //添加图片时，获取本地的图片url
    getbaseimgurl(data) {
      this.personimg = URL.createObjectURL(data);
    },
    // 会员停用与启用
    changeEnable(data) {
      let x = data.isEnable == "Y" ? "N" : "Y";
      const param = {
        isEnable: x,
        personId: data.personId
      };
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/person/changEnable",
          qs.stringify(param)
        )
        .then(res => {
          this.fetch({ current: this.pagination.current });
        });
    },
    // 查看照片
    checkphoto(data) {
      this.imgshow = true;
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/person/getById/" +
            data
        )
        .then(res => {
          this.modal_img_src = this.root_path.imgView + res.data.datas.imgPaths;
        });
    },
    // 搜索
    search() {
      this.fetch({
        current: this.pagination.current
      });
      this.pagination.current = 1;
    },
    // 重置搜索
    resetValue() {
      const form = this.form1;
      form.resetFields();
      this.endTime = null;
      this.startTime = null;
      this.fetch({
        current: this.pagination.current
      });
      this.pagination.current = 1;
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
    }
  }
};
</script>

<style scoped>
.searchBtns button {
  margin: 0 5px;
}
.ant-switch-checked {
  background-color: #07c160;
}
</style>