<template>
  <div>
    <h4>门店管理</h4>
    <a-button type="primary" @click="showmodal">新增</a-button>
    <a-table
      :rowKey="record => record.id"
      :columns="columns"
      :dataSource="datalist"
      :pagination="pagination"
      :scroll="{x:1000}"
      bordered
      :style="{'margin-top':'15px'}"
      @change="handleTableChange"
    >
      <span slot="isStore" slot-scope="isStore">
        <span v-if="isStore=='Y'">是</span>
        <span v-else-if="isStore=='N'">否</span>
      </span>
      <!-- <a-switch
          checkedChildren="是"
          unCheckedChildren="否"
          :checked="record=='Y'?true:false"
          @click="changeEnable(record)"
      />-->
      <span slot="action" slot-scope="record">
        <a-button type="primary" size="small" @click="editShop(record)">编辑</a-button>
        <a-button type="danger" size="small" @click="delShop(record)">删除</a-button>
      </span>
    </a-table>

    <!-- from表单 -->
    <a-modal
      :visible="addVisible"
      title="新增/修改门店信息"
      :afterClose="()=>isEdit=false"
      width="500px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="closemodal"
      @ok="subShop"
    >
      <a-form :form="form1">
        <a-form-item label="上级部门" :labelCol="{span:7}" :wrapperCol="{span:14}">
          <!-- <a-select
            v-model="parentIds"
            :defaultActiveFirstOption="false"
            :showArrow="true"
            :allowClear="true"
            :notFoundContent="null"
          >
            <a-select-option
              v-for="m in shopParent"
              :key="m.parentId"
              :value="m.deptId"
            >{{m.deptName}}</a-select-option>
          </a-select>-->
          <a-input disabled v-model="shopParent"></a-input>
          <a-input type="hidden" v-model="parentId" v-decorator="['parentId']"></a-input>
        </a-form-item>
        <a-form-item label="部门名称" :labelCol="{span:7}" :wrapperCol="{span:14}">
          <a-input v-decorator="['deptName']"></a-input>
          <a-input type="hidden" v-decorator="['deptId']"></a-input>
        </a-form-item>
        <a-form-item label="排序" :labelCol="{span:7}" :wrapperCol="{span:14}">
          <a-input v-decorator="['sort']"></a-input>
        </a-form-item>
        <a-form-item :wrapperCol="{span:14,offset: 8}">
          <a-checkbox
            :disabled="isEdit"
            @change="isShopCheck"
            v-decorator="['isStore',{ valuePropName: 'checked' }]"
          >是否为门店</a-checkbox>
        </a-form-item>
        <a-form-item v-if="isSX" label="门店编号/ID" :labelCol="{span:7}" :wrapperCol="{span:14}">
          <a-select
            mode="tags"
            style="width: 100%"
            :token-separators="[',']"
            v-decorator="['storageNo']"
          ></a-select>
        </a-form-item>
        <a-form-item v-if="isSX" label="门店地址" :labelCol="{span:7}" :wrapperCol="{span:14}">
          <a-input v-decorator="['address']"></a-input>
        </a-form-item>
        <a-form-item
          v-if="isSX"
          label="会员共享范围"
          :labelCol="{span:7}"
          :wrapperCol="{span:14}"
          :style="{marginBottom:'0'}"
        >
          <a-checkbox
            :indeterminate="indeterminate"
            @change="onCheckAllChange"
            :checked="checkAll"
          >全选</a-checkbox>
        </a-form-item>
        <a-form-item v-if="isSX" :wrapperCol="{span:14,offset:8}">
          <a-checkbox-group :style="{width:'100%'}" @change="changeCheck" v-model="checkedList">
            <a-row>
              <a-col :span="8" v-for="(n,index) in partShop" :key="index">
                <a-checkbox :value="n.value">{{n.label}}</a-checkbox>
              </a-col>
            </a-row>
          </a-checkbox-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

const columns = [
  { title: "部门名称", width: 150, dataIndex: "deptName", key: "2" },
  {
    title: "是否为为门店",
    width: 150,
    dataIndex: "isStore",
    key: "3",
    scopedSlots: { customRender: "isStore" },
  },
  { title: "门店编号/ID", width: 150, dataIndex: "storageNo", key: "4" },
  { title: "门店地址", width: 100, dataIndex: "address", key: "5" },
  {
    title: "操作",
    width: 150,
    key: "6",
    scopedSlots: { customRender: "action" },
  },
];

export default {
  data() {
    return {
      columns,
      datalist: [],
      // shopParent: [],
      shopParent: null,
      parentId: null,
      isEdit: false,
      isShopVaule: false,
      pagination: {},
      addVisible: false,
      addOrEditFlag: false,
      isshow: false,
      staffcount: "",
      isSX: false,
      allshop: [],
      checkAll: false,
      checkedList: [],
      indeterminate: false,
      partShop: [],
    };
  },
  inject: ["reload"],
  mounted() {
    // 获取父级栏目下拉列表
    this.fetch();
    this.getSharedScope();
  },
  beforeCreate() {
    this.form1 = this.$form.createForm(this);
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
        current: pagination.current,
      });
    },
    // 获取共享范围
    getSharedScope() {
      const form1 = this.form1;
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/auth/dept-user/getShopsByUserId?userId=" +
            this.$store.state.userid
        )
        .then((res) => {
          let share = res.data.datas;
          let shared = share.filter((item) => item.isStore == "Y");
          this.allshop = this.resetCheckbox(shared);
          let shopParet;
          let deptId;
          for (let n of share) {
            if (n.parentId == 0) {
              shopParet = n.deptName;
              deptId = n.deptId;
            }
          }
          this.shopParent = shopParet;
          this.parentId = deptId;
        });
    },
    //加载表格
    fetch(params = {}) {
      //表格的dataindex必须和json数据对象一一对应才能自动被展示
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/dept/getShopsByLoginUser",
          {
            params: { current: 1, size: 10, ...params },
          }
        )
        .then((data) => {
          const pagination = { ...this.pagination };
          pagination.pageSize = data.data.datas.size;
          pagination.total = data.data.datas.total;
          this.datalist = data.data.datas.records;
          console.log("获取门店信息：", data.data.datas.records);
          // this.shopParent = data.data.datas.records;
          this.pagination = pagination;
        });
    },
    // 新增
    showmodal() {
      this.isshow = false;
      this.addVisible = true;
      this.isSX = false;
      this.checkedList = [];
      this.checkAll = false;
      this.indeterminate = false;
      this.partShop = this.allshop;
    },
    // 关闭弹窗
    closemodal() {
      this.addVisible = false;
      this.isSX = false;
    },
    // 编辑
    editShop(data) {
      this.isshow = false;
      this.isEdit = true;
      this.addVisible = true;
      this.checkedList = [];
      this.checkAll = false;
      this.indeterminate = false;
      const form1 = this.form1;
      setTimeout(() => {
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/dept/findById/" +
              data.deptId
          )
          .then((res) => {
            let value = res.data.datas;
            this.isSX = res.data.datas.isStore == "Y" ? true : false;
            this.staffcount = res.data.datas.staffCount;
            this.deptUnikey = value.deptUnikey;
            this.partShop = this.allshop.filter(
              (item) => item.value != this.deptUnikey
            );
            axios
              .get(
                this.root_path.rootIP +
                  this.root_path.api6001 +
                  "/org/sharedept/list",
                { params: { deptUnikey: this.deptUnikey } }
              )
              .then((resShare) => {
                this.checkedList = resShare.data.datas;
                this.changeCheck(this.checkedList);
              });
            setTimeout(() => {
              form1.setFieldsValue({
                ...value,
                isStore: value.isStore == "Y" ? true : false, //是否勾选
                storageNo: this.resetMdata(value.storageNo),
              });
              // if (value.parentId == 0) {
              //   this.parentIds = null;
              // } else {
              //   this.parentIds = value.parentId;
              // }
            }, 5);
          });
      }, 5);
    },
    // 删除
    delShop(data) {
      this.isshow = false;
      let _this = this;
      this.$confirm({
        title: "是否确定删除？",
        okText: "确定",
        okType: "danger",
        cancelText: "取消",
        onOk() {
          if (_this.staffcount == 0) {
            axios
              .delete(
                _this.root_path.rootIP +
                  _this.root_path.api6001 +
                  "/org/dept/removeById/" +
                  data.deptId
              )
              .then((res) => {
                _this.$notification.success({ message: "删除成功" });
                _this.reload(); //刷新页面
              })
              .catch((error) => {
                alert("操作失败：" + error);
              });
          } else {
            alert("该部门及其子部门下存在员工，操作失败！");
          }
        },
      });
    },
    // 提交表单
    subShop() {
      const form1 = this.form1;
      form1.validateFields((err, values) => {
        if (err) {
          return;
        }
        // if (this.parentIds == null) {
        //   this.$notification.warning({ message: "上级不能为空" });
        //   return;
        // }
        values.parentId = this.parentId;
        let storge = values.storageNo.toString();
        console.log('storge',storge)
        let param;
        if (this.isSX) {
          param = {
            ...values,
            // storageNo:
            //   values.storageNo.length == 0 ? "" : "," + values.storageNo + ",",
            storageNo:storge,
            isStore: values.isStore ? "Y" : "N",
          };
        } else {
          param = {
            ...values,
            isStore: values.isStore ? "Y" : "N",
          };
        }
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/dept/saveOrUpdate",
            {
              ...param,
            }
          )
          .then((resShop) => {
            console.log("reshop；", resShop);
            if (resShop.data.resp_code == 0) {
              // let deptId=resShop.data.datas.deptUnikey;
              const shares = {
                deptUnikey: this.isEdit
                  ? this.deptUnikey
                  : resShop.data.datas.deptUnikey,
                shareDeptUnikeys: this.checkedList,
              };
              axios
                .post(
                  this.root_path.rootIP +
                    this.root_path.api6001 +
                    "/org/sharedept/updateByList",
                  qs.stringify(shares, { indices: false })
                )
                .then((resShare) => {
                  if (resShare.data.resp_code == 0) {
                    this.$notification.success({ message: "添加成功" });
                    this.reload();
                    this.addVisible = false;
                    this.isSX = false;
                    this.isEdit = false;
                  } else {
                    this.$notification.warning({
                      message: "会员共享失败，" + resShare.data.resp_msg,
                    });
                  }
                });
            } else {
              this.$notification.warning({
                message: "部门/门店操作失败，" + resShop.data.resp_msg,
              });
            }
          })
          .catch((error) => {
            alert("操作失败：" + error);
          });
      });
    },
    // 改变多选框
    changeCheck(checkedValue) {
      this.indeterminate =
        checkedValue.length != 0 && checkedValue.length < this.partShop.length
          ? true
          : false;
      this.checkAll = checkedValue.length == this.partShop.length;
    },
    // 全选按钮
    onCheckAllChange(e) {
      let xx = [];
      for (let n of this.partShop) {
        xx.push(n.value);
      }
      Object.assign(this, {
        checkedList: e.target.checked ? xx : [],
        indeterminate: false,
        checkAll: e.target.checked,
      });
    },
    // 构建部门的多选按钮
    resetCheckbox(data) {
      if (data != null) {
        let x = [];
        for (let n of data) {
          x.push({
            label: n.deptName,
            value: n.deptUnikey,
          });
        }
        return x;
      }
    },
    // 编辑时门店id构造数据
    resetMdata(data) {
      if (data == null || data == "") {
        return [];
      } else {
        let a = data.slice(1, -1);
        let x = a.split(",");
        return x;
      }
    },
    // 校验门店编号
    selvalidator(value) {
      let x = [];
      for (let n of value) {
        x.push(n.replace(/[^A-Za-z0-9]+/g, ""));
      }
      return x;
    },
    //是否勾选门店
    isShopCheck(e) {
      this.isSX = e.target.checked;
    },
  },
};
</script>

<style scoped>
</style>