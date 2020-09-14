<template>
  <div>
    <h4>角色管理</h4>
    <a-button type="primary" @click="showmodal">新增</a-button>
    <a-table
      :rowKey="record => record.id"
      :columns="columns"
      :customRow="customRow"
      :dataSource="datagroup"
      :pagination="pagination"
      bordered
      :style="{'margin-top':'15px'}"
      @change="handleTableChange"
    >
      <span slot="action" slot-scope="record">
        <a-button type="primary" size="small" @click="editRoles(record)">编辑</a-button>
        <a-button type="primary" size="small" @click="setRoot(record)">设置权限</a-button>
        <a-button type="danger" size="small" @click="delRoles(record)">删除</a-button>
      </span>
    </a-table>

    <a-modal
      :visible="visible"
      title="新增/修改角色"
      width="500px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      :bodyStyle="{maxHeight:'600px',overflow:'auto'}"
      @cancel="()=>this.visible=false"
      @ok="subroles"
    >
      <a-form :form="form">
        <a-form-item label="角色名" :labelCol="{span:8}" :wrapperCol="{span:12}">
          <a-input
            :maxLength="20"
            v-decorator="['name',{rules:[{required:true,message:'请输入角色名'}],getValueFromEvent:validatorWhiteSpace}]"
          ></a-input>
          <a-input type="hidden" v-decorator="['id']"></a-input>
        </a-form-item>
        <a-form-item label="角色属性" :labelCol="{span:8}" :wrapperCol="{span:12}">
          <a-input :maxLength="20" v-decorator="['code',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 权限表 -->
    <a-modal
      :visible="rootVisible"
      title="修改权限信息"
      width="500px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      :bodyStyle="{maxHeight:'600px',overflow:'auto'}"
      @cancel="cancleRoot"
      @ok="subRoot"
    >
      <a-spin :spinning="spinning">
        <a-form-item label="权限设置" :labelCol="{span:8}" :wrapperCol="{span:12}">
          <a-tree
            checkable
            v-model="checkedKeys"
            :selectedKeys="selectedKeys"
            :treeData="treeData"
            @check="onCheck"
          ></a-tree>
        </a-form-item>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import tableSelectRow from "@/components/commons/tableSelectRow.js";
import formPattern from "@/components/commons/formPattern.js";

const columns = [
  { title: "角色名", width: "33%", dataIndex: "name", key: "1" },
  { title: "角色属性", width: "33%", dataIndex: "code", key: "3" },
  {
    title: "操作",
    key: "root",
    width: "33%",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  data() {
    return {
      columns,
      pagination: {},
      datagroup: [],
      visible: false,
      checkedKeys: [],
      selectedKeys: [],
      treeData: [],
      halfCheckedKeys: [],
      rootVisible: false,
      roleId: "",
      spinning: false
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  created() {
    this.getRoleRoot();
  },
  mounted() {
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
      let token = this.$store.state.token;
      //表格的dataindex必须和json数据对象一一对应才能自动被展示
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/auth/role/getRoleList",
          qs.stringify({ current: 1, size: 10, token, ...params })
        )
        .then(data => {
          const pagination = { ...this.pagination };
          pagination.pageSize = data.data.datas.size;
          pagination.total = data.data.datas.total;
          this.datagroup = data.data.datas.records;
          this.pagination = pagination;
        });
    },
    // 新增，弹出框、角色信息
    showmodal() {
      this.visible = true;
    },
    // 提交
    subroles() {
      const form = this.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        let token = this.$store.state.token;
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/auth/role/insertOrUpdateRole?token=" +
              token,
            values
          )
          .then(res => {
            if (res.data.resp_code == 0) {
              this.visible = false;
              this.fetch({ current: this.pagination.current });
              this.$notification.success({ message: "操作成功" });
            } else {
              this.$notification.warning({ message: res.data.resp_msg });
            }
          })
          .catch(err => {
            alert("操作失败:" + error);
          });
      });
    },
    // 编辑
    editRoles(data) {
      const form = this.form;
      this.visible = true;
      setTimeout(() => {
        form.setFieldsValue(data);
      }, 50);
    },
    // 删除
    delRoles(data) {
      let _this = this;
      let id = data.id;
      let token = this.$store.state.token;
      this.$confirm({
        title: "是否确定删除？",
        okText: "确定",
        okType: "danger",
        cancelText: "取消",
        onOk() {
          axios
            .post(
              _this.root_path.rootIP +
                _this.root_path.api6001 +
                "/auth/role/deleteRoles",
              qs.stringify({ ids: id, token })
            )
            .then(res => {
              _this.fetch({ current: _this.pagination.current });
            })
            .catch(error => {
              alert("操作失败:" + error);
            });
        }
      });
    },
    onCheck(checkedKeys, info) {
      this.halfCheckedKeys = info.halfCheckedKeys;
    },
    // 根据角色id获取角色的权限树。
    getRoleRoot() {
      const param = {
        roleId: this.$store.state.roleid,
        token: this.$store.state.token
      };
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/auth/role/getRoleMenus",
          qs.stringify(param)
        )
        .then(res => {
          let Data = res.data.datas.children;
          this.treeData = this.creatTreeData(Data);
        });
    },
    creatTreeData(data) {
      //构造新的权限树(无权限的不显示)
      let newarr = [];
      let len = data.length;
      for (let n = 0; n < len; n++) {
        if (!data[n].isroot) {
          //没有权限时，直接跳过
        } else {
          //有权限
          if (data[n].children.length == 0) {
            //有权限、无子节点
            newarr.push(data[n]);
          } else {
            //有权限、有子节点
            newarr.push(data[n]);
            let x = data[n].children;
            let newarr2 = [];
            let xlen = x.length;
            for (let i = 0; i < xlen; i++) {
              if (!x[i].isroot) {
              } else {
                if (x[i].children.lenght == 0) {
                  newarr2.push(x[i]);
                } else {
                  newarr2.push(x[i]);
                  let y = x[i].children;
                  let newarr3 = [];
                  let ylen = y.length;
                  for (let j = 0; j < ylen; j++) {
                    if (!y[j].isroot) {
                    } else {
                      if (y[j].children.length == 0) {
                        newarr3.push(y[j]);
                      } else {
                      }
                    }
                  }
                  x[i].children = newarr3;
                }
              }
            }
            data[n].children = newarr2;
          }
        }
      }
      return newarr;
    },
    // 弹出/修改角色权限信息
    setRoot(data) {
      this.rootVisible = true;
      this.roleId = data.id;
      this.checkedKeys = JSON.parse(data.arg1);
    },
    //权限 取消按钮
    cancleRoot() {
      this.rootVisible = false;
      this.spinning = false;
      this.checkedKeys = [];
      this.halfCheckedKeys = [];
    },
    // 提交权限
    subRoot() {
      let allArr = this.checkedKeys.concat([...this.halfCheckedKeys]);
      const param = {
        roleId: this.roleId,
        arg1: JSON.stringify(this.checkedKeys), //全选项
        arg2: JSON.stringify(this.halfCheckedKeys), //半选项
        token: this.$store.state.token,
        menuIds: allArr
      };
      this.spinning = true;
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/auth/role/updateRolePermission",
          qs.stringify(param, { arrayFormat: "repeat" })
        )
        .then(res => {
          this.spinning = false;
          if (res.data.resp_code == 0) {
            this.rootVisible = false;
            this.checkedKeys = []; //清空勾选的项
            this.halfCheckedKeys = [];
            this.fetch({ current: this.pagination.current });
            this.$notification.success({ message: "操作成功" });
          } else {
            this.$notification.warning({ message: res.data.resp_msg });
          }
        })
        .catch(error => {
          alert("操作失败：", error);
        });
    },
    // 表格行事件
    customRow(record, index) {
      let _this = this;
      return {
        on: {
          click(event) {
            tableSelectRow.click(event);
          }
        }
      };
    },
    // 空格校验
    validatorWhiteSpace(event) {
      return formPattern.validatorWhiteSpace(event);
    }
  }
};
</script>

<style scoped>
</style>