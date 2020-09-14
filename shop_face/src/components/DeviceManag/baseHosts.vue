<template>
  <div>
    <h4>基础主机管理</h4>
    <a-button type="primary" @click="showmodal">新增</a-button>
    <a-table
      :rowKey="record => record.id"
      :columns="columns"
      :dataSource="data"
      :pagination="pagination"
      bordered
      :style="{'margin-top':'15px'}"
      @change="handleTableChange"
    >
      <span slot="action" slot-scope="record">
        <a-button type="primary" size="small" @click="editHost(record)">编辑</a-button>
        <a-button type="danger" size="small" @click="delHost(record)">删除</a-button>
      </span>
    </a-table>

    <a-modal
      :visible="visible"
      title="新增/修改设备信息"
      width="500px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="closemodal"
      @ok="subHost"
    >
      <a-form :form="form">
        <a-form-item label="基础主机名称" :labelCol="{span:9}" :wrapperCol="{span:12}">
          <a-input
            :maxLength="20"
            v-decorator="['name',{rules:[{required:true,message:'请输入主机名称'}],getValueFromEvent:validatorWhiteSpace}]"
          ></a-input>
          <a-input type="hidden" v-decorator="['id']"></a-input>
        </a-form-item>
        <a-form-item label="主机url地址" :labelCol="{span:9}" :wrapperCol="{span:12}">
          <a-input
            v-decorator="['url',{rules:[{required:true,message:'请输入主机ip'}],getValueFromEvent:validatorIP}]"
            placeholder="例：http://192.168.0.1:9898"
          ></a-input>
        </a-form-item>
        <a-form-item label="人脸识别接口地址" :labelCol="{span:9}" :wrapperCol="{span:12}">
          <a-input
            v-decorator="['arcUrl',{rules:[{required:true,message:'请输入虹软识别地址'}],getValueFromEvent:validatorIP}]"
            placeholder="例：http://192.168.0.1:9899"
          ></a-input>
        </a-form-item>
        <a-form-item label="备注" :labelCol="{span:9}" :wrapperCol="{span:12}">
          <a-textarea v-decorator="['remark']"></a-textarea>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import formPattern from "@/components/commons/formPattern.js";

const columns = [
  { title: "主机名称", width: "20%", dataIndex: "name", key: "2" },
  { title: "设备接口url地址", width: "20%", dataIndex: "url", key: "7" },
  { title: "人脸识别接口地址", width: "20%", dataIndex: "arcUrl", key: "9" },
  { title: "备注", width: "20%", dataIndex: "remark", key: "10" },
  {
    title: "操作",
    width: "20%",
    key: "11",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  data() {
    return {
      columns,
      data: [],
      pagination: {},
      visible: false,
      addOrEditFlag: false,
      companyArr: []
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  mounted() {
    axios
      .get(
        this.root_path.rootIP + this.root_path.api6001 + "/org/company/findAll"
      )
      .then(res => {
        this.companyArr = res.data.datas;
      });
    this.fetch();
  },
  methods: {
    //重载表格
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
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/base-manager/page",
          {
            params: { current: 1, size: 10, ...params }
          }
        )
        .then(data => {
          const pagination = { ...this.pagination };
          pagination.pageSize = data.data.datas.size;
          pagination.total = data.data.datas.total;
          this.data = data.data.datas.records;
          this.pagination = pagination;
        });
    },
    // 新增，弹出
    showmodal() {
      this.visible = true;
    },
    // 关闭弹窗
    closemodal() {
      this.visible = false;
    },
    // 提交表单
    subHost() {
      const form = this.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/eq/base-manager/saveOrUpdate",
            qs.stringify({ ...values })
          )
          .then(res => {
            if (res.data.resp_code == 0) {
              if (this.addOrEditFlag) {
                //操作为编辑时
                this.fetch({ current: this.pagination.current });
              } else {
                //操作位新增时
                this.fetch({ current: 1 });
                this.pagination.current = 1;
              }
              this.visible = false;
              this.$notification.success({ message: "操作成功" });
              this.addOrEditFlag = false;
            } else {
              this.$notification.warning({ message: res.data.resp_msg });
            }
          })
          .catch(error => {
            alert("操作失败:" + error);
          });
      });
    },
    // 编辑
    editHost(data) {
      const form = this.form;
      this.addOrEditFlag = true;
      this.visible = true;
      setTimeout(() => {
        form.setFieldsValue(data);
      }, 50);
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
                "/eq/base-manager/deleteById/" +
                data.id
            )
            .then(res => {
              _this.fetch({ page: _this.pagination.current });
            })
            .catch(error => {
              alert("操作失败:" + error);
            });
        }
      });
    },
    // 空格校验
    validatorWhiteSpace(event) {
      return formPattern.validatorWhiteSpace(event);
    },
    // 校验IP地址
    validatorIP(event) {
      return formPattern.validatorIP(event);
    }
  }
};
</script>

<style scoped>
</style>