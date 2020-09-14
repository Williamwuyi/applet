<template>
  <div>
    <h4>黑名单(待审核)</h4>
    <a-button type="primary" @click="showmodal">新增</a-button>
    <a-table
      :rowKey="record => record.personId"
      :columns="columns"
      :dataSource="datalist"
      :pagination="pagination"
      bordered
      :style="{'margin-top':'15px'}"
      @change="handleTableChange"
    >
      <span slot="action" slot-scope="record">
        <a-button type="primary" size="small" @click="editHost(record)">编辑</a-button>
        <a-button size="small" @click="passPre(record)">审核</a-button>
        <a-button type="danger" size="small" @click="delHost(record)">删除</a-button>
      </span>
      <span slot="sex" slot-scope="personSex">
        <span v-if="personSex==0">男</span>
        <span v-else-if="personSex==1">女</span>
      </span>
      <span slot="check" slot-scope="personId">
        <a-button type="primary" size="small" @click="checkphoto(personId)">查看</a-button>
      </span>
    </a-table>

    <add-edit-black-list
      ref="personForm"
      :visible="visible"
      :personimg="personimg"
      :showimg="showimg"
      :imgIsQualify="imgIsQualify"
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
import addEditBlackList from "./addEditBlackList";

const columns = [
  {
    title: "姓名",
    width: 120,
    dataIndex: "person.personName",
    key: "2"
  },
  {
    title: "性别",
    width: 120,
    dataIndex: "person.personSex",
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
    title: "录入日期",
    width: 150,
    dataIndex: "person.createTime",
    key: "7"
  },
  { title: "备注", width: 120, dataIndex: "remark", key: "6" },
  {
    title: "操作",
    width: 180,
    key: "root",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  components: { addEditBlackList },
  data() {
    return {
      columns,
      datalist: [],
      pagination: {},
      visible: false,
      personimg: "",
      showimg: "",
      imgIsQualify: true,
      imgshow: false,
      modal_img_src: "",
      spinning: false
    };
  },
  mounted() {
    this.fetch();
  },
  methods: {
    //重载表格__基本设备
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
    //加载表格__基本设备
    fetch(params = {}) {
      //表格的dataindex必须和json数据对象一一对应才能自动被展示
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/blacklist/list",
          {
            params: { size: 10, type: 0, isEnable: "N", ...params }
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
    // 弹出新增
    showmodal() {
      this.visible = true;
      this.imgIsQualify = true;
    },
    // 编辑
    editHost(data) {
      this.visible = true;
      this.imgIsQualify = true;
      const form = this.$refs.personForm.form;
      setTimeout(() => {
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/blacklist/getByUnikey/" +
              data.person.personUnikey
          )
          .then(res => {
            if (
              res.data.datas.person.imgPaths == "" ||
              res.data.datas.person.imgPaths == null
            ) {
              this.showimg = "";
            } else {
              this.showimg =
                this.root_path.imgView + res.data.datas.person.imgPaths;
            }
            form.setFieldsValue(res.data.datas);
          });
      }, 0);
    },
    // 删除
    delHost(data) {
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
                "/org/blacklist/removeById/" +
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
      this.spinning = false;
    },
    // 提交表单
    handleCreate() {
      const form = this.$refs.personForm.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        if (!this.imgIsQualify) {
          alert("照片未检测出人脸!");
          return;
        }
        if (this.spinning) {
          return;
        }
        this.spinning = true;
        axios
          .put(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/blacklist/saveOrUpdate",
            {
              ...values
            }
          )
          .then(res => {
            if (res.data.resp_code == 0) {
              //添加用户成功
              this.fetch({ current: this.pagination.current });
              this.$notification.success({ message: "操作成功" });
              form.resetFields();
              this.personimg = "";
              this.showimg = "";
              this.visible = false;
            } else {
              this.$notification.warning({ message: res.data.resp_msg });
            }
            this.spinning = false;
          })
          .catch(error => {
            this.spinning = false;
            alert("操作失败：" + error);
          });
      });
    },
    //添加图片时，获取本地的图片url
    getbaseimgurl(data) {
      this.personimg = URL.createObjectURL(data);
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
    passPre(data) {
      let _this = this;
      this.$confirm({
        title: "是否确定将此人加入黑名单？",
        okText: "确定",
        okType: "primary",
        cancelText: "取消",
        onOk() {
          const param = {
            isEnable: "Y",
            personId: data.personId
          };
          axios
            .post(
              _this.root_path.rootIP +
                _this.root_path.api6001 +
                "/org/blacklist/changEnable",
              qs.stringify(param)
            )
            .then(res => {
              _this.fetch({ current: _this.pagination.current });
            });
        }
      });
    }
  }
};
</script>

<style scoped>
</style>