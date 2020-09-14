<template>
  <div class="sildtree" ref="sildtree">
    <div>
      <span class="part_title">菜单列表</span>
      <a-button @click="btnAdd" class="classMenu">增加</a-button>
    </div>
    <a-divider></a-divider>
    <!-- 左侧菜单树 -->
    <a-tree
      @rightClick="rightClick"
      :treeData="treeData"
      @select="onSelect"
      :selectedKeys="selectedKeys"
    ></a-tree>
    <!-- ></a-directory-tree> -->
    <!-- 右击左侧菜单栏目选项 -->
    <ul ref="actions" class="deptclass" v-if="isshow" :style="acStyle">
      <li class="flag" @click="addMenu">新增</li>
      <li class="flag" @click="editMenu">修改</li>
      <li class="flag" @click="delMenu">删除</li>
      <!-- <li class="flag" @click="addUrl">添加Url</li> -->
    </ul>
    <!-- 右击左侧菜单弹出列表 -->
    <a-modal
      :maskClosable="false"
      :visible="addVisible"
      title="新增/修改菜单"
      :afterClose="()=>isEdit=false"
      width="400px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="disAdd"
      @ok="menuFrom"
    >
      <a-form :form="form1">
        <a-form-item label="上级菜单" :labelCol="{span:6}" :wrapperCol="{span:16}">
          <!--
            :showArrow="false" 支持清除
            :notFoundContent="null"当下拉列表为空时显示的内容
            :showArrow="false"是否显示下拉小箭头
          -->
          <a-select
            :style="{width:'235px'}"
            v-model="key"
            placeholder="顶级菜单则不选"
            :defaultActiveFirstOption="false"
            :showArrow="true"
            :allowClear="true"
            :notFoundContent="null"
          >
            <a-select-option v-for="m in allParent" :key="m.icon" :value="m.key">{{m.title}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="地址URL" :labelCol="{span:6}" :wrapperCol="{span:16}">
          <!-- read-only -->
          <!-- <span v-if="key==null">
            <a-input read-only v-decorator="['menu_url']" placeholder="地址格式为:/menu"></a-input>
          </span>
          <span v-else>-->
          <a-input v-decorator="['menu_url']" :allowClear="true" placeholder="地址格式为:/menu"></a-input>
          <!-- </span> -->
        </a-form-item>

        <a-form-item label="菜单名称" :labelCol="{span:6}" :wrapperCol="{span:16}">
          <a-input v-decorator="['menu_name']" placeholder :allowClear="true"></a-input>
          <a-input type="hidden" v-decorator="['menu_id']"></a-input>
        </a-form-item>
        <a-form-item label="排序" :labelCol="{span:6}" :wrapperCol="{span:16}">
          <a-input v-decorator="['menu_sort']"></a-input>
        </a-form-item>
        <a-form-item label="图标icon" :labelCol="{span:6}" :wrapperCol="{span:16}">
          <a-input v-decorator="['menu_icon']" :allowClear="true"></a-input>
        </a-form-item>是否隐藏菜单:
        <a-checkbox v-model="autoRefresh" @change="changeRefresh"></a-checkbox>
      </a-form>
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
      treeData: [], //接收菜单树数据
      isshow: false,
      acStyle: {},
      allParent: [], //接收父级节点下拉数据
      key: null,
      addVisible: false, //表单窗口默认关闭
      selectedKeys: [], //存当前节点的key——点击
      autoRefresh: false,
      isEdit: false
    };
  },
  inject: ["reload"],
  beforeCreate() {
    this.form1 = this.$form.createForm(this);
  },
  created() {
    this.getMenuTree();
  },
  mounted() {
    document.addEventListener("click", e => {
      //鼠标点击任意非树的位置，隐藏树操作
      if (e.target.className != "flag") {
        this.isshow = false;
      }
    });
    // 获取父级栏目下拉列表
    axios
      .post(
        this.root_path.rootIP + this.root_path.api6001 + "/auth/menu/getAll"
      )
      .then(res => {
        let value = res.data.datas.children;
        console.log("加载父菜单栏目:", value);
        this.allParent = value;
      });
  },
  methods: {
    //获取菜单数组，形成树结构
    getMenuTree() {
      axios
        .post(
          this.root_path.rootIP + this.root_path.api6001 + "/auth/menu/getAll"
        )
        .then(res => {
          let allMenuData = res.data.datas.children;
          console.log("获取菜单数据结构：", allMenuData);
          this.treeData = allMenuData;
          // this.creatTreeData(allMenuData);
        })
        .catch(error => {
          console.log("获取菜单信息失败:", error);
        });
    },
    //构造新的树
    creatTreeData(data) {
      let newarr = [];
      let len = data.length;
      for (let n = 0; n < len; n++) {
        if (data[n].children.length == 0) {
          //无子节点
          newarr.push(data[n]);
        } else {
          //有子节点
          newarr.push(data[n]);
          let x = data[n].children;
          let newarr2 = [];
          let xlen = x.length;
          for (let i = 0; i < xlen; i++) {
            if (x[i].children.lenght == 0) {
              newarr2.push(x[i]);
            } else {
              newarr2.push(x[i]);
              let newarr3 = [];
              let y = x[i].children;
              let ylen = y.length;
              for (let j = 0; j < ylen; j++) {
                if (y[j].children.length == 0) {
                  newarr3.push(y[j]);
                } else {
                }
              }
              x[i].datas = newarr3;
            }
          }
          data[n].datas = newarr2;
        }
      }
      return newarr;
    },
    //按钮——新增
    btnAdd() {
      this.addMenu();
    },
    // 新增
    addMenu() {
      this.isshow = false;
      this.isEdit = true;
      this.addVisible = true;
    },
    // 修改
    editMenu() {
      this.isshow = false;
      this.isEdit = true;
      const form1 = this.form1;
      let keys = this.selectedKeys;
      if (keys.length == 0) {
        this.addVisible = false;
        this.$notification.warning({ message: "请选中菜单节点！" });
      } else {
        this.addVisible = true;
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/auth/menu/queryById?id=" +
              keys
          )
          .then(res => {
            let fom = res.data.datas;
            console.log("获取选中菜单数据", fom);
            let url, id, name, parentId, icon, hidden, key, sort;
            for (key in fom) {
              if (key == "id") {
                id = fom[key];
              } else if (key == "url") {
                url = fom[key];
              } else if (key == "name") {
                name = fom[key];
              } else if (key == "parentId") {
                parentId = fom[key];
              } else if (key == "icon") {
                icon = fom[key];
              } else if (key == "hidden") {
                hidden = fom[key];
              } else if (key == "sort") {
                sort = fom[key];
              }
            }
            setTimeout(() => {
              form1.setFieldsValue({
                menu_url: url,
                menu_id: id,
                menu_name: name,
                menu_sort:sort,
                menu_icon: icon
              });
              this.autoRefresh = hidden;
              if (parentId == 0) {
                this.key = null;
              } else {
                this.key = parentId;
              }
            }, 5);
          });
      }
    },
    // 弹出新增菜单，确认提交信息
    menuFrom() {
      const form1 = this.form1;
      console.log("获取获取下拉值:", this.key);
      form1.validateFields((err, values) => {
        let hidden = values.hidden;
        let icon = values.menu_icon;
        let name = values.menu_name;
        let url = values.menu_url;
        let id = values.menu_id;
        let sort =values.menu_sort;
        let parentid;
        if (this.key != null || this.key != undefined) {
          if (url == undefined || url == null) {
            this.$message.warning("请输入URL地址！");
          } else if (url.lastIndexOf("/") != 0) {
            this.$message.warning("地址URL格式不正确");
          }
        }
        if (name == null) {
          this.$message.warning("菜单名称不能空");
        } else {
          console.log("父id：", this.key);
          if (this.key == null || this.key == undefined) {
            parentid = 0;
          } else {
            parentid = this.key;
          }
          const param = {
            hidden: this.autoRefresh,
            icon: icon,
            parentId: parentid,
            path: url,
            pathMetod: url,
            url: url,
            name: name,
            id: id,
            sort: sort,
            type: 1
          };
          axios
            .post(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/auth/menu/saveOrUpdate",
              qs.stringify(param)
            )
            .then(res => {
              console.log("返回数据：", res.data.resp_msg);
              this.addVisible = false;
              this.reload(); //刷新页面
              this.$notification.success({ message: "保存成功!" });
            })
            .catch(error => {
              console.log("数据传输失败:", error);
            });
        }
      });
    },
    changeRefresh(e) {
      console.log(e);
      let status = e.target.checked;
      const form1 = this.form1;
      this.autoRefresh = status;
    },
    //删除菜单
    delMenu() {
      this.isshow = false;
      let _this = this;
      let selectKey = _this.selectedKeys;
      let parentid, name;
      if (selectKey.length == 0) {
        this.$notification.warning({ message: "请选中菜单节点！" });
      } else {
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/auth/menu/queryById?id=" +
              selectKey
          )
          .then(res => {
            let fom = res.data.datas;
            for (let key in fom) {
              if (key == "name") {
                name = fom[key];
              }
            }
            this.$confirm({
              title: "是否确定删除: ' " + name + " '",
              okText: "确定",
              okType: "danger",
              cancelText: "取消",
              onOk() {
                axios
                  .delete(
                    _this.root_path.rootIP +
                      _this.root_path.api6001 +
                      "/auth/menu/removeById/" +
                      _this.selectedKeys
                  )
                  .then(res => {
                    _this.$notification.success({ message: "删除成功" });
                    _this.reload(); //刷新页面
                  })
                  .catch(error => {
                    alert("操作失败：" + error);
                  });
              }
            });
          });
      }
    },
    //菜单树鼠标右键
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
    },
    // 点击菜单，传递菜单key，在右侧表中显示当前菜单信息
    onSelect(selectedKeys) {
      console.log("选择菜单传过来的key", selectedKeys);
      this.$emit("reloadStaffTable", selectedKeys[0]);
      this.selectedKeys = selectedKeys;
      this.$emit("clearSelectrows");
    },
    // 取消新增
    disAdd() {
      this.addVisible = false;
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
  text-align: center;
  font-size: 14px;
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
.ant-divider-horizontal {
  margin: 0 0;
}
.deptclass li:hover {
  color: lightseagreen;
}
.ant-form-item {
  margin-bottom: 8px;
}
.classMenu {
  height: 24px;
  padding: 0px 6px;
}
.ant-col-8 {
  width: 25% !important;
}
</style>