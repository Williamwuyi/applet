<template>
  <div>
    <h4>菜单管理</h4>
    <div class="container">
      <menu-tree @reloadStaffTable="findMenu" />
      <div class="sildtable">
        <div class="btns1">
          <a-button type="primary" @click="addURL">增加</a-button>
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
            <a-button type="danger" size="small" @click="delMenu(record)">删除</a-button>
          </span>
        </a-table>
      </div>
    </div>
    <div>
      <!-- 新增URl-->
      <a-modal
        :maskClosable="false"
        :visible="urlVisible"
        title="新增URL"
        :afterClose="()=>isEdit=false"
        width="400px"
        okText="确定"
        cancelText="取消"
        :destroyOnClose="true"
        @cancel="cancel"
        @ok="menuUrl"
      >
        <a-form :form="form1">
          <a-form-item label="URL" :labelCol="{span:5}" :wrapperCol="{span:16}">
            <a-input v-decorator="['menu_url']" placeholder="添加多个url,必须用英文逗号隔开"></a-input>
          </a-form-item>
        </a-form>
      </a-modal>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import MenuTree from "./menuTree";
// import AddEdit from "./AddEdit";

const columns = [
  {
    title: "名称",
    width: "15%",
    dataIndex: "remark",
    key: "1"
  },
  {
    title: "URL",
    dataIndex: "url",
    width: "15%",
    key: "2"
  },
  {
    title: "操作",
    key: "root",
    width: "5%",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  components: { MenuTree },
  data() {
    return {
      datalist: [],
      pagination: {},
      urlVisible: false, //打开增加弹出框，默认关闭
      columns,
      menuId: 2,
      visible: false,
      showimg: "",
      personimg: "",
      mannerableMenus: [],
      spinning: false
    };
  },
  //<a-form :form="form1" > 有表单标签，必须创建
  beforeCreate() {
    this.form1 = this.$form.createForm(this);
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
      // this.fetch({
      //   size: pagination.pageSize,
      //   current: pagination.current
      // });
    },
    fetch() {
      if (this.menuId != undefined || this.menuId != null) {
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/auth/action/getListByMenuId?menuId=" +
              this.menuId
          )
          .then(res => {
            this.datalist = res.data.datas;
            console.log("获取菜单接口地址信息：", res.data.datas);
          });
      }
    },
    addURL() {
      if (this.menuId != undefined || this.menuId != null) {
        if (this.menuId == "2") {
          this.$message.warning("默认注册管理添加，请选中左侧节点增加");
        }
        this.urlVisible = true;
      } else {
        this.$notification.warning({ message: "请左侧节点增加" });
      }
      // console.log(this.menuId)
      // alert(this.menuId)
    },
    //url表单提交
    menuUrl() {
      const form1 = this.form1;
      if (this.menuId != 0) {
        form1.validateFields((err, values) => {
          let url = values.menu_url;
          const param = {
            menuId: this.menuId,
            urlList: url
          };
          axios
            .post(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/auth/menu/addActionByUrl",
              qs.stringify(param)
            )
            .then(res => {
              console.log("添加url保存返回信息：", res.data);
              if (res.data.resp_code == 0) {
                this.$notification.warning({ message: "添加成功" });
                // location.reload();//刷新页面
                this.findMenu(this.menuId);
                this.fetch();
                this.urlVisible = false;
              } else {
                this.$notification.warning({ message: res.data.resp_msg });
              }
            })
            .catch(error => {
              alert("操作失败：", error);
            });
        });
      }
    },

    // url表单删除
    delMenu(data) {
      let _this = this;
      const menu_Ids = _this.menuId;
      this.$confirm({
        title: "是否确定删除？",
        okText: "确定",
        okType: "danger",
        cancelText: "取消",
        onOk() {
          const param = {
            actionIds: data.uuid,
            menuId: _this.menuId
          };
          axios
            .post(
              _this.root_path.rootIP +
                _this.root_path.api6001 +
                "/auth/menu/deleteMenuAction",
              qs.stringify(param)
            )
            .then(res => {
              console.log("删除返回返回信息：", res.data);
              if (res.data.resp_code == 0) {
                _this.$notification.warning({ message: "删除成功" });
                _this.findMenu(_this.menuId);
                _this.fetch();
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

    //根据点击的菜单节点，展示
    findMenu(res) {
      console.log("点击菜单ID", res);
      this.menuId = res;
      this.fetch();
    },
    // 取消新增
    cancel() {
      this.urlVisible = false;
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
  /* position: absolute;
  left: 250px;
  top: 0;
  right: 20px;
  margin-left: 20px; */
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