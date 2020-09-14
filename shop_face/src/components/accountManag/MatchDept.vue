<template>
  <div>
    <a-modal
      :visible="visibleDept"
      title="管理门店"
      width="400px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="()=>$emit('closemodal')"
      @ok="addRule"
    >
      <a-tree
        checkable
        :treeData="treeData"
        :checkedKeys="checkedKeys"
        @check="onCheck"
        @expand="handleExpand"
        :defaultCheckedKeys="defaultCheckedKeys"
      ></a-tree>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

import commonJS from "@/components/commons/common.js";

export default {
  props: ["visibleDept", "userId"],
  data() {
    return {
      treeData: [],
      expandedKeys: [],
      defaultCheckedKeys: [],
      checkedKeys: [],
      treeArr: [],
      topId: "",
    };
  },
  mounted() {
    this.getDeptTree();
  },
  methods: {
    //获取部门数据，并存到数组，将最上级树节点保存
    getDeptTree() {
      this.treeArr = [];
      axios
        .get(
          this.root_path.rootIP + this.root_path.api6001 + "/org/dept/findAll"
        )
        .then((res) => {
          let arr = res.data.datas;
          for (let item of arr) {
            //过滤不必要的数组对象，返回新的数组结构
            if (item.parentId == 0) {
              this.topId = item.deptId;
            }
            this.treeArr.push({
              title: item.deptName,
              key: item.deptUnikey,
              pid: item.parentId,
              id: item.deptId,
              // disableCheckbox:item.isStore=='N'?true:false
            });
          }
          this.treeData = commonJS.transData(
            this.treeArr,
            "id",
            "pid",
            "children"
          );
        });
    },
    handleExpand(expandedKeys) {
      this.expandedKeys = expandedKeys;
    },
    //勾选树
    onCheck(checkedKeys) {
      this.checkedKeys = checkedKeys;
    },
    //提交选中的部门
    addRule() {
      let newobj = {
        deptUnikeyList: this.checkedKeys,
        userId: this.userId,
      };
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/auth/dept-user/updateByEqgIdList",
          qs.stringify(newobj, { indices: false })
        )
        .then((res) => {
          if (res.data.resp_msg == "操作成功") {
            this.$notification.success({ message: "操作成功" });
            this.checkedKeys = []; //重设选中的状态
            this.$emit("closemodal");
          } else {
            alert("操作失败！" + res.data.resp_msg);
          }
        })
        .catch((error) => {
          alert("操作失败：" + error);
        });
    },
  },
  watch: {
    visibleDept() {
      //当选择的规则id发生变化时，清空选中状态，并获取数据以选中.
      if (this.visibleDept) {
        this.defaultCheckedKeys = [];
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/auth/dept-user/getShopsByUserId?userId=" +
              this.userId
          )
          .then((res) => {
            let arr = res.data.datas;
            if (arr == "" || arr == null) {
              return;
            }
            for (let n of arr) {
              if (n.parentId != 0) {
                this.defaultCheckedKeys.push(n.deptUnikey);
              }
            }
                this.checkedKeys = this.defaultCheckedKeys;
                this.expandedKeys = this.checkedKeys
          });
      }
    },
  },
};
</script>

<style scoped>
</style>