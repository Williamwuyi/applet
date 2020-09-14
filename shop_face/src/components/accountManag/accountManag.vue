<template>
  <div>
    <h4>账号管理</h4>
    <a-button type="primary" @click="showmodal">新增</a-button>
    <a-table
      :rowKey="record => record.id"
      :columns="columns"
      :customRow="customRow"
      :dataSource="datagroup"
      :pagination="pagination"
      :scroll="{x:1150}"
      bordered
      :style="{'margin-top':'15px'}"
      @change="handleTableChange"
    >
      <span slot="action" slot-scope="record">
        <a-button class="btn1" type="primary" size="small" @click="changeRole(record)">修改角色</a-button>
        <a-button class="btn1" type="primary" size="small" @click="changePwd(record)">重置密码</a-button>
        <a-button class="btn1" type="primary" size="small" @click="checkRoot(record)">查看权限</a-button>
        <a-button class="btn1" type="danger" size="small" @click="delAccount(record)">删除</a-button>
      </span>
      <span slot="roles" slot-scope="roleId">
        <span v-for="(n,index) in optArr" :key="index">
          <span v-if="roleId==n.id">{{n.name}}</span>
        </span>
      </span>
      <span slot="actype" slot-scope="isCashier">{{isCashier==1?'收银员账号':'普通账号'}}</span>
      <span slot="match" slot-scope="record">
        <a-button type="primary" size="small" @click="setDept(record)">配置</a-button>
      </span>
    </a-table>

    <a-modal
      :visible="visible"
      title="编辑账号信息"
      width="400px"
      okText="确定"
      cancelText="取消"
      :afterClose="afterClose"
      :destroyOnClose="true"
      :maskClosable="false"
      @cancel="closemodal"
      @ok="subacount"
    >
      <a-form :form="form">
        <a-form-item label="账号名" :labelCol="{span:8}" :wrapperCol="{span:14}">
          <a-input
            :disabled="isEidt"
            :maxLength="20"
            v-decorator="['username',{rules:[{required:true,message:'请输入账号名'}],getValueFromEvent:validatorWhiteSpace}]"
          ></a-input>
          <a-input type="hidden" v-decorator="['id']"></a-input>
        </a-form-item>
        <a-form-item label="密码" v-if="!isEidt" :labelCol="{span:8}" :wrapperCol="{span:14}">
          <a-input
            type="password"
            v-decorator="['password',{rules:[{required:true,message:'请输入密码'}],getValueFromEvent:validatorWhiteSpace}]"
          ></a-input>
        </a-form-item>
        <a-form-item v-if="!isEidt" :labelCol="{span:8}" :wrapperCol="{span:14}">
          <a-input type="password" v-model="passwordRE" @change="controlPwd($event,'passwordRE')"></a-input>
          <span slot="label">
            <span style="color:red">*</span>确认密码
          </span>
        </a-form-item>
        <a-form-item label="角色" :labelCol="{span:8}" :wrapperCol="{span:14}">
          <a-select v-decorator="['roleId',{rules:[{required:true,message:'请选择角色'}]}]">
            <a-select-option v-for="(n,index) in optArr" :key="index" :value="n.id">{{n.name}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="真实姓名" :labelCol="{span:8}" :wrapperCol="{span:14}">
          <a-input
            :maxLength="20"
            v-decorator="['nickname',{getValueFromEvent:validatorWhiteSpace}]"
          ></a-input>
        </a-form-item>
        <a-form-item label="手机号" :labelCol="{span:8}" :wrapperCol="{span:14}">
          <a-input :maxLength="11" v-decorator="['mobile',{getValueFromEvent:validatorPhone}]"></a-input>
        </a-form-item>
        <a-form-item :wrapperCol="{span:22,offset:2}">
          <a-checkbox v-decorator="['isWarn',{valuePropName:'checked'}]">是否接收报警信息</a-checkbox>
          <a-checkbox v-decorator="['isCashier',{valuePropName:'checked'}]">是否为收银员账号</a-checkbox>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      :visible="rootVisible"
      title="权限信息"
      width="400px"
      :footer="null"
      :bodyStyle="{maxHeight:'600px',overflow:'auto'}"
      @cancel="()=>this.rootVisible=false"
    >
      <a-form-item label="权限树" :labelCol="{span:8}" :wrapperCol="{span:12}">
        <a-tree showLine :treeData="treeData"></a-tree>
      </a-form-item>
    </a-modal>

    <!-- 重置密码 -->
    <a-modal
      :visible="resetPwdVisible"
      title="重置密码"
      width="400px"
      @cancel="closeResetPwd"
      @ok="subResetPwd"
    >
      <a-form-item label="账号名" :labelCol="{span:8}" :wrapperCol="{span:14}">
        <span>{{curName}}</span>
      </a-form-item>
      <a-form-item label="新密码" :labelCol="{span:8}" :wrapperCol="{span:14}">
        <a-input type="password" v-model="newPassword" @change="controlPwd($event,'newPassword')"></a-input>
      </a-form-item>
      <a-form-item label="重复密码" :labelCol="{span:8}" :wrapperCol="{span:14}">
        <a-input
          type="password"
          v-model="repeatPassword"
          @change="controlPwd($event,'repeatPassword')"
        ></a-input>
      </a-form-item>
    </a-modal>

    <!-- 门店管理 -->
    <match-dept :userId="userId" :visibleDept="visibleDept" @closemodal="closeUploadDept" />
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import tableSelectRow from "@/components/commons/tableSelectRow.js";
import formPattern from "@/components/commons/formPattern.js";
import MatchDept from "./MatchDept";

const columns = [
  { title: "账号名", width: "12%", dataIndex: "username", key: "1" },
  {
    title: "角色",
    width: "12%",
    dataIndex: "roleId",
    key: "4",
    scopedSlots: { customRender: "roles" }
  },
  { title: "真实姓名", width: "12%", dataIndex: "nickname", key: "2" },
  { title: "手机号", width: "12%", dataIndex: "mobile", key: "3" },
  {
    title: "管理门店",
    width: "10%",
    key: "6",
    align: "center",
    scopedSlots: { customRender: "match" }
  },
  {
    title: "类别",
    width: "11%",
    dataIndex: "isCashier",
    key: "5",
    scopedSlots: { customRender: "actype" }
  },
  {
    title: "操作",
    key: "root",
    width: "31%",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  components: { MatchDept },
  data() {
    return {
      columns,
      pagination: {},
      datagroup: [],
      visible: false,
      optArr: [],
      treeData: [],
      rootVisible: false,
      resetPwdVisible: false,
      curName: "",
      curUser: [],
      newPassword: "",
      repeatPassword: "",
      passwordRE: "",
      isEidt: false,
      visibleDept: false,
      userId: ""
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  mounted() {
    this.fetch();
    const param = {
      pageNum: 1,
      pageSize: 10000,
      token: this.$store.state.token
    };
    // 获取所有的角色信息
    axios
      .post(
        this.root_path.rootIP +
          this.root_path.api6001 +
          "/auth/role/getRoleList",
        qs.stringify(param)
      )
      .then(res => {
        this.optArr = res.data.datas.records;
      })
      .catch(error => {
        console.log("获取角色信息失败:", error);
      });
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
            "/auth/user/getUserList",
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
    // 弹出，新增
    showmodal() {
      this.visible = true;
      this.passwordRE = "";
    },
    // 关闭弹窗,取消按钮
    closemodal() {
      this.visible = false;
    },
    // 提交,编辑或新增对应不同接口
    subacount() {
      const form = this.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        if (this.isEidt) {
          //编辑
          const param = {
            type: values.type,
            password: values.password,
            nickname: values.nickname,
            mobile: values.mobile,
            roleId: values.roleId,
            id: values.id,
            iscasher: values.isCashier ? 1 : 0,
            isWarn: values.isWarn
          };
          axios
            .post(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/auth/user/updateUser",
              qs.stringify(param)
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
            .catch(error => {
              alert("操作失败：", error);
            });
        } else {
          //新增
          if (values.password !== this.passwordRE) {
            this.$notification.error({ message: "密码不一致" });
            return;
          }
          const param = {
            ...values,
            isCashier: values.isCashier ? 1 : 0
          };
          axios
            .post(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/auth/user/addUser",
              param
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
            .catch(error => {
              alert("操作失败:" + error);
            });
        }
      });
    },
    // 查看权限
    checkRoot(data) {
      this.rootVisible = true;
      const param = {
        roleId: data.roleId,
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
          let x = res.data.datas.children;
          if (x.length == 0) {
            return;
          }
          this.treeData = this.creatTreeData(x);
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
    // 修改角色
    changeRole(data) {
      const form = this.form;
      this.visible = true;
      this.isEidt = true;
      setTimeout(() => {
        form.setFieldsValue({
          ...data,
          isCashier: data.isCashier == 1 ? true : false
        });
      }, 50);
    },
    // 管理员重置密码————弹出
    changePwd(data) {
      this.resetPwdVisible = true;
      this.curName = data.username;
      this.curUser = [data.id];
    },
    // 重置密码————取消
    closeResetPwd() {
      this.resetPwdVisible = false;
      this.newPassword = "";
      this.repeatPassword = "";
    },
    // 重置密码————确定
    subResetPwd() {
      if (this.newPassword == null || this.newPassword == "") {
        this.$notification.error({ message: "请输入密码！" });
        return false;
      }
      if (this.newPassword != this.repeatPassword) {
        this.$notification.error({ message: "两次密码输入不一致!" });
        return false;
      }
      const param = {
        newPwd: this.newPassword,
        userIdList: this.curUser
      };
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/auth/user/resetPwdByManager",
          qs.stringify(param, { indices: false })
        )
        .then(res => {
          if (res.data.resp_code == 0) {
            this.resetPwdVisible = false;
            this.newPassword = "";
            this.repeatPassword = "";
            this.fetch({ current: this.pagination.current });
            this.$notification.success({ message: "操作成功" });
          } else {
            this.$notification.warning({ message: res.data.resp_msg });
          }
        })
        .catch(err => {
          alert("操作失败:" + err);
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
    // 删除帐号信息
    delAccount(data) {
      let _this = this;
      let id = data.id;
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
                "/auth/user/removeById/" +
                id
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
    // 配置可管理部门/门店
    setDept(data) {
      this.visibleDept = true;
      this.userId = data.id;
    },
    // 关闭管理门店
    closeUploadDept() {
      this.visibleDept = false;
    },
    afterClose() {
      this.isEidt = false;
    },
    // 空格校验
    validatorWhiteSpace(event) {
      return formPattern.validatorWhiteSpace(event);
    },
    // 电话校验
    validatorPhone(event) {
      return formPattern.validatorPhone(event);
    },
    // 空格校验
    controlPwd(event, datas) {
      this[datas] = formPattern.validatorWhiteSpace(event);
    }
  }
};
</script>

<style scoped>
</style>