<template>
  <div class="sildtree" ref="sildtree">
    <div>
      <span class="part_title">部门列表</span>
      <!-- 当部门树有节点时，隐藏新增 -->
      <a-button type="primary" size="small" @click="btnAdd" v-if="treeData.length==0">增加</a-button>
    </div>
    <a-directory-tree
      @rightClick="rightClick"
      :treeData="treeData"
      @select="onSelect"
      :selectedKeys="selectedKeys"
      :expandAction="false"
    ></a-directory-tree>

    <ul ref="actions" class="deptclass" v-if="isshow" :style="acStyle">
      <li class="flag" @click="showmodal">添加子部门</li>
      <li class="flag" @click="editDept">修改</li>
      <li class="flag" @click="importPerson">批量导入</li>
      <li class="flag" @click="delDept">删除</li>
    </ul>
    <!-- 新增部门 -->
    <a-modal
      :maskClosable="false"
      :visible="addVisible"
      title="新增/修改部门"
      :afterClose="()=>isEdit=false"
      width="400px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="disAdd"
      @ok="addDept"
    >
      <a-form :form="form1">
        <a-form-item label="上级部门" :labelCol="{span:8}" :wrapperCol="{span:16}">
          <a-input read-only v-decorator="['showparentname']"></a-input>
          <a-input type="hidden" v-decorator="['parentId']"></a-input>
          <a-input type="hidden" v-decorator="['deptId']"></a-input>
        </a-form-item>
        <a-form-item label="部门名称" :labelCol="{span:8}" :wrapperCol="{span:16}">
          <a-input v-decorator="['deptName',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
        </a-form-item>
        <a-form-item :wrapperCol="{span:14,offset: 8}">
          <a-checkbox
            :disabled="isEdit"
            @change="isShopCheck"
            v-decorator="['isStore',{ valuePropName: 'checked' }]"
          >是否为门店</a-checkbox>
        </a-form-item>
        <a-form-item v-if="isSX" label="门店编号/ID" :labelCol="{span:8}" :wrapperCol="{span:16}">
          <a-select
            mode="tags"
            style="width: 100%"
            :token-separators="[',']"
            v-decorator="['storageNo',{getValueFromEvent:selvalidator}]"
          ></a-select>
        </a-form-item>
        <a-form-item v-if="isSX" label="门店地址" :labelCol="{span:8}" :wrapperCol="{span:16}">
          <a-input v-decorator="['address',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
        </a-form-item>
        <a-form-item
          v-if="isSX"
          label="会员共享范围"
          :labelCol="{span:8}"
          :wrapperCol="{span:16}"
          :style="{marginBottom:'0'}"
        >
          <a-checkbox
            :indeterminate="indeterminate"
            @change="onCheckAllChange"
            :checked="checkAll"
          >全选</a-checkbox>
        </a-form-item>
        <a-form-item v-if="isSX" :wrapperCol="{span:22,offset:2}">
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
    <!-- 批量导入 -->
    <a-modal
      :maskClosable="false"
      :visible="importVisible"
      title="批量导入人员信息"
      width="400px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="cancleZIP"
      @ok="subZipMsg"
    >
      <input type="file" @change="getFile($event)" />
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

import commonJS from "@/components/commons/common.js";
import formPattern from "@/components/commons/formPattern.js";

export default {
  data() {
    return {
      treeData: [],
      isshow: false,
      acStyle: {},
      deptkey: "", //存当前节点的key——右键
      deptUnikey: "",
      depttitle: "", //存当前节点的title——右键
      addVisible: false,
      deptparenttitle: "", //存父节点的title——右键
      staffcount: "",
      selectedKeys: [], //存当前节点的key——点击
      topId: "",
      importVisible: false,
      zipURL: "",
      isEdit: false,
      isSX: false,
      allshop: [],
      partShop: [],
      indeterminate: false,
      checkAll: false,
      checkedList: []
    };
  },
  beforeCreate() {
    this.form1 = this.$form.createForm(this);
  },
  created() {
    this.getDeptTree();
  },
  mounted() {
    document.addEventListener("click", e => {
      //鼠标点击任意非树的位置，隐藏树操作
      if (e.target.className != "flag") {
        this.isshow = false;
      }
    });
  },
  methods: {
    //获取部门数据，并存到数组，将最上级树节点保存
    getDeptTree() {
      let _this = this;
      axios
        .all([
          axios.get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/auth/dept-user/getShopsByUserId?userId=" +
              this.$store.state.userid
          ),
          axios.get(
            this.root_path.rootIP + this.root_path.api6001 + "/org/dept/findAll"
          )
        ])
        .then(
          axios.spread(function(res_shops, res_depts) {
            let mannerableDept = res_shops.data.datas;
            let n = mannerableDept.filter(item => item.isStore == "Y");
            _this.allshop = _this.resetCheckbox(n);
            let allDepts = res_depts.data.datas;
            let newArr = [];
            let newTreeNodes = commonJS.designTree(
              mannerableDept,
              allDepts,
              newArr
            ); //有权限的部门（包含父节点）
            let sa = { newTreeNodes, allDepts };
            _this.$emit("getMannerableDept", sa);
            let treeArr = [];
            for (let item of newTreeNodes) {
              //过滤不必要的数组对象，返回新的数组结构
              treeArr.push({
                title: item.deptName,
                // key: item.deptUnikey,
                key: item.deptId,
                pid: item.parentId,
                id: item.deptId
              });
            }
            _this.treeData = commonJS.transData(
              treeArr,
              "id",
              "pid",
              "children"
            );
          })
        );
    },
    //部门树鼠标右键,设置操作栏位置与显隐,deptkey、depttitle存储右键时的当前部门id和名称
    rightClick({ event, node }) {
      let x0 = event.clientX;
      let y0 = event.clientY;
      let elem = this.$refs.sildtree;
      let x1 =
        elem.offsetLeft +
        elem.offsetParent.offsetLeft +
        elem.offsetParent.offsetParent.offsetLeft +
        elem.offsetParent.offsetParent.offsetParent.offsetLeft;
      let y1 =
        elem.offsetParent.offsetTop + elem.offsetParent.offsetParent.offsetTop;
      let left = x0 - x1;
      let top = y0 - y1;
      this.acStyle = { left: left + "px", top: top + "px" };
      this.isshow = true;
      this.deptkey = node.eventKey;
      this.depttitle = node.title;
      // this.deptparentkey = node.$parent.eventKey;
      this.deptparenttitle = node.$parent.title;

      // 判断右键部门时，该部门及子部门是否存在员工（删除操作有效）
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/dept/findById/" +
            this.deptkey
        )
        .then(res => {
          this.isSX = res.data.datas.isStore == "Y" ? true : false;
          this.staffcount = res.data.datas.staffCount;
        });
    },

    //关闭树右键菜单，弹出新增部门对话框
    showmodal() {
      this.isshow = false;
      this.addVisible = true;
      this.isSX = false;
      this.checkedList = [];
      this.checkAll = false;
      this.indeterminate = false;
      const form1 = this.form1;
      this.partShop = this.allshop;
      setTimeout(() => {
        form1.setFieldsValue({
          parentId: this.deptkey,
          showparentname: this.depttitle
        });
      }, 5);
    },
    //新增部门
    addDept() {
      const form1 = this.form1;
      form1.validateFields((err, values) => {
        if (err) {
          return;
        }
        let param;
        if (this.isSX) {
          param = {
            ...values,
            storageNo:
              values.storageNo.length == 0 ? "" : "," + values.storageNo + ",",
            isStore: values.isStore ? "Y" : "N"
          };
        } else {
          param = {
            ...values,
            isStore: values.isStore ? "Y" : "N"
          };
        }
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/dept/saveOrUpdate",
            {
              ...param
            }
          )
          .then(resShop => {
            if (resShop.data.resp_code == 0) {
              // let deptId=resShop.data.datas.deptUnikey;
              const shares = {
                deptUnikey: this.isEdit
                  ? this.deptUnikey
                  : resShop.data.datas.deptUnikey,
                shareDeptUnikeys: this.checkedList
              };
              axios
                .post(
                  this.root_path.rootIP +
                    this.root_path.api6001 +
                    "/org/sharedept/updateByList",
                  qs.stringify(shares, { indices: false })
                )
                .then(resShare => {
                  if (resShare.data.resp_code == 0) {
                    this.$notification.success({ message: "添加成功" });
                    this.addVisible = false;
                    this.isSX = false;
                    this.isEdit = false;
                    this.getDeptTree();
                  } else {
                    this.$notification.warning({
                      message: "会员共享失败，" + resShare.data.resp_msg
                    });
                    this.getDeptTree();
                  }
                });
            } else {
              this.$notification.warning({
                message: "部门/门店操作失败，" + resShop.data.resp_msg
              });
            }
          })
          .catch(error => {
            alert("操作失败：" + error);
          });
      });
    },
    //修改部门
    editDept() {
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
              this.deptkey
          )
          .then(res => {
            let value = res.data.datas;
            this.deptUnikey = value.deptUnikey;
            this.partShop = this.allshop.filter(
              item => item.value != this.deptUnikey
            );
            axios
              .get(
                this.root_path.rootIP +
                  this.root_path.api6001 +
                  "/org/sharedept/list",
                { params: { deptUnikey: this.deptUnikey } }
              )
              .then(resShare => {
                this.checkedList = resShare.data.datas;
                this.changeCheck(this.checkedList);
              });
            form1.setFieldsValue({
              ...value,
              storageNo: this.resetMdata(value.storageNo),
              isStore: value.isStore == "Y" ? true : false,
              showparentname: this.deptparenttitle
            });
          });
      }, 50);
    },
    //删除部门
    delDept() {
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
                  _this.deptkey
              )
              .then(res => {
                _this.$notification.success({ message: "删除成功" });
                _this.getDeptTree();
              })
              .catch(error => {
                alert("操作失败：" + error);
              });
          } else {
            alert("该部门及其子部门下存在员工，操作失败！");
          }
        }
      });
    },
    // 点击部门，传递部门key，在人员表中显示当前部门的人员。
    onSelect(selectedKeys) {
      this.$emit("reloadStaffTable", selectedKeys[0]);
      this.selectedKeys = selectedKeys;
      this.$emit("clearSelectrows");
    },
    //按钮——新增
    btnAdd() {
      const form1 = this.form1;
      if (this.treeData.length == 0) {
        this.addVisible = true;
        setTimeout(() => {
          form1.setFieldsValue({
            parentId: "0"
          });
        }, 10);
        // console.log('当前没有部门，需要新建');
      } else {
        if (this.selectedKeys.length == 0) {
          alert("当前没有选择节点，请选择！");
          // console.log("当前没有选择节点");
          return;
        } else {
          if (this.topId == this.selectedKeys[0]) {
            // console.log("当前选中的是父节点");
          } else {
            // console.log("当前节点为子节点");
          }
        }
      }
    },
    //人员批量导入按钮
    importPerson() {
      this.importVisible = true;
      this.isshow = false;
    },
    // 获取压缩包文件
    getFile(event) {
      if (event.target.files.length == 0) {
        //判断文件操作，如果点击选择文件后又取消操作，则不执行
        return;
      }
      let file = event.target.files[0];
      let formData = new FormData();
      formData.append("file", file);
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/import/zipUpload",
          formData
        )
        .then(res => {
          if (res.data.resp_code == 0) {
            this.zipURL = res.data.datas;
            this.$notification.success({ message: "添加成功" });
          } else {
            this.$notification.warning({ message: res.data.resp_msg });
          }
        })
        .catch(error => {
          alert("添加失败：" + error);
        });
    },
    // 提交人员导入的信息
    subZipMsg() {
      if (this.deptkey == "" || this.deptkey == null) {
        this.$notification.warning({ message: "请选择文件!" });
        return false;
      }
      const param = {
        deptId: this.deptkey,
        file: this.zipURL
      };
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/import/importStaffByZip",
          qs.stringify(param)
        )
        .then(res => {
          if (res.data.resp_code == 0) {
            this.importVisible = false;
            this.zipURL = "";
            this.$notification.success({ message: "添加成功" });
            this.$emit("reloadStaffTable", this.deptkey); //重新加载人员表
          } else {
            this.$notification.warning({ message: res.data.resp_msg });
          }
        })
        .catch(error => {
          alert("操作失败：", error);
        });
    },
    // 取消提交批量人员
    cancleZIP() {
      this.importVisible = false;
      this.deptkey = "";
    },
    // 是否为门店勾选
    isShopCheck(e) {
      this.isSX = e.target.checked;
    },
    // 取消新增部门
    disAdd() {
      this.addVisible = false;
      this.isSX = false;
    },
    // 空格校验
    validatorWhiteSpace(event) {
      return formPattern.validatorWhiteSpace(event);
    },
    // 构建部门的多选按钮
    resetCheckbox(data) {
      if (data != null) {
        let x = [];
        for (let n of data) {
          x.push({
            label: n.deptName,
            value: n.deptUnikey
          });
        }
        return x;
      }
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
        checkAll: e.target.checked
      });
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
    }
  }
};
</script>

<style scoped>
.sildtree {
  float: left;
  width: 250px;
  min-height: 400px;
  /* overflow: auto; */
  padding: 10px 0;
  margin-left: 10px;
  border: 1px solid #e0e0e0;
  position: relative;
}
.sildtree > div:first-child {
  margin-bottom: 10px;
}
.sildtree .part_title {
  display: inline-block;
  width: 80px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  font-size: 14px;
  border: 1px solid #999999;
  margin: 0 5px;
}
.deptclass {
  width: 160px;
  height: auto;
  list-style-type: none;
  padding: 5px 0;
  position: absolute;
  z-index: 999999;
  border: 1px solid #555;
  box-shadow: 0 0 10px #f5f5f5;
  background-color: white;
}
.deptclass li {
  margin: 0 5px;
  height: 35px;
  line-height: 35px;
  border-bottom: 1px dashed #ddd;
  cursor: pointer;
}
.deptclass li:hover {
  color: lightseagreen;
}
.ant-form-item {
  margin-bottom: 8px;
}
</style>