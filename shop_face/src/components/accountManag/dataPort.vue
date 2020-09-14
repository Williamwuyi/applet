<template>
  <div>
    <h4>数据接口</h4>
    <div class="container">
      <div class="sildtable">
        <div class="btns1">
          <a-button type="primary" @click="addPort">增加</a-button>
        </div>
        <a-table
          :rowKey="record => record.personId"
          :columns="columns"
          :dataSource="datalist"
          :pagination="pagination"
          :scroll="{x:800}"
          bordered
          :style="{'margin-top':'15px'}"
          @change="handleTableChange"
        >
          <span slot="action" slot-scope="record">
            <a-button type="primary" size="small" @click="editPort(record)">编辑</a-button>
            <a-button type="danger" size="small" @click="delPort(record)">删除</a-button>
          </span>
        </a-table>
      </div>
    </div>
    <div>
      <!-- 新增URl-->
      <a-modal
        :maskClosable="false"
        :visible="portVisible"
        title="新增/编辑 数据"
        :afterClose="()=>isEdit=false"
        width="400px"
        okText="确定"
        cancelText="取消"
        :destroyOnClose="true"
        @cancel="cancel"
        @ok="dataCommit"
      >
        <a-form :form="formData">
          <a-form-item label="接口名称：" :labelCol="{span:5}" :wrapperCol="{span:16}">
            <a-input v-decorator="['remark']"></a-input>
            <a-input v-decorator="['uuid']" type="hidden"></a-input>
          </a-form-item>
          <a-form-item label="接口地址：" :labelCol="{span:5}" :wrapperCol="{span:16}">
            <a-input v-decorator="['type']" type="hidden"></a-input>
            <a-input v-decorator="['url']" placeholder="接口地址请以斜杠开头 '/ '"></a-input>
          </a-form-item>
          <!-- <a-form-item label="接口类型：" :labelCol="{span:5}" :wrapperCol="{span:16}">
            <a-input v-decorator="['type']"></a-input>
          </a-form-item>-->
        </a-form>
      </a-modal>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

const columns = [
  {
    title: "接口名称",
    width: "15%",
    dataIndex: "remark",
    key: "1"
  },
  {
    title: "接口地址",
    dataIndex: "url",
    width: "15%",
    key: "2"
  },
  {
    title: "接口类型",
    dataIndex: "type",
    width: "5%",
    key: "3"
  },
  {
    title: "操作",
    key: "root",
    width: "10%",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  data() {
    return {
      datalist: [],
      pagination: {},
      portVisible: false, //打开增加弹出框，默认关闭
      columns,
      visible: false,
      isEdit: false,
      showimg: "",
      personimg: "",
      mannerableMenus: [],
      spinning: false
    };
  },
  inject: ["reload"],
  //<a-form :form="formData" > 有表单标签，必须创建
  beforeCreate() {
    this.formData = this.$form.createForm(this);
  },
  mounted() {
    this.fetch();
  },
  methods: {
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
      axios
        .get(
          this.root_path.rootIP + this.root_path.api6001 + "/auth/action/list",
          {
            params: { current: 1, size: 10, deptId: this.deptStaff, ...params }
          }
        )
        .then(res => {
          const pagination = { ...this.pagination };
          pagination.pageSize = res.data.datas.size;
          pagination.total = res.data.datas.total;
          console.log("获取接口数据信息：", res.data.datas.records);
          this.datalist = res.data.datas.records;
          this.pagination = pagination;
        });
    },
    // 增加按钮
    addPort() {
      this.isEdit = true;
      this.portVisible = true; //打开增加from表单
    },
    // 修改
    editPort(data) {
      let _this = this;
      _this.isEdit = true;
      _this.portVisible = true; //打开增加from表单
      const formData = this.formData;
      // data 数据字段有：remark，type，url，uuid
      axios
        .get(
          _this.root_path.rootIP +
            _this.root_path.api6001 +
            "/auth/action/findById/" +
            data.uuid
        )
        .then(res => {
          let fom = res.data.datas;
          console.log("获取接口数据", fom);
          formData.setFieldsValue(fom);
        })
        .catch(error => {
          alert("操作失败：", error);
        });
    },
    //接口数据提交
    dataCommit() {
      const formData = this.formData;
      formData.validateFields((err, values) => {
        let url = values.url;
        if (url == undefined || url == null) {
          this.$message.warning("请输入URL地址！");
          return;
        } else if (url.charAt(0) != "/") {
          this.$message.warning("地址URL格式不正确");
          return;
        } else {
          let a = url.indexOf("/", url.indexOf("/") + 1);
          let urls = url.substring(1, a);
          values.type = urls;
          // setTimeout(() => {
          //   formData.setFieldsValue({
          //     type: urls
          //   });
          //   values.type = urls;
          // }, 5);
        }
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/auth/action/saveOrUpdate",
            qs.stringify(values)
          )
          .then(res => {
            console.log("添加接口数据返回信息：", res.data);
            if (res.data.resp_code == 0) {
              this.portVisible = false;
              this.reload(); //刷新页面
              this.$notification.warning({ message: res.data.resp_msg });
            } else {
              this.$notification.warning({ message: res.data.resp_msg });
            }
          })
          .catch(error => {
            alert("操作失败：", error);
          });
      });
    },

    //删除
    delPort(data) {
      let _this = this;
      _this.$confirm({
        title: "是否确定删除？",
        okText: "确定",
        okType: "danger",
        cancelText: "取消",
        onOk() {
          axios
            .delete(
              _this.root_path.rootIP +
                _this.root_path.api6001 +
                "/auth/action/removeById/" +
                data.uuid
            )
            .then(res => {
              console.log("删除返回返回信息：", res.data);
              if (res.data.resp_code == 0) {
                _this.$notification.warning({ message: res.data.resp_msg });
                _this.reload();
              } else {
                _this.$notification.warning({ message: res.data.resp_msg });
              }
            })
            .catch(error => {
              alert("操作失败：", error);
            });
        }
      });
    },

    // 取消新增
    cancel() {
      this.portVisible = false;
    }
  }
};
</script>

<style scoped>
.top {
  margin: 10px;
}
.container {
  margin-top: 20px;
  position: relative;
}
.sildtable {
  float: left;
  margin-left: 20px;
  width: calc(100% - 280px);
  height: auto;
}
.sildtable .btns2 {
  margin-top: 15px;
}
.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}
.container[data-v-18977122] {
  margin-top: 0px;
}
.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}
</style>