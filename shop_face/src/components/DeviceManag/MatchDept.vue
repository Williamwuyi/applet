<template>
  <div>
    <a-modal
      :visible="visibleDept"
      title="配置部门/门店"
      width="400px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="()=>$emit('closemodal')"
      @ok="addRule"
    >
      <div class="rightText">当前选择：{{deptName}}</div>
      <a-tree
        :treeData="newTreeData"
        @select="onSelect"
        :selectedKeys="[value]"
        :defaultExpandedKeys="[expandValue]"
      ></a-tree>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

export default {
  props: {
    visibleDept: {
      type: Boolean
    },
    deviceUnikey: {
      type: String
    },
    deviceUuid: {
      type: String
    },
    newTreeData: {
      type: Array
    }
  },
  data() {
    return {
      value: "",
      deptUnikey: "",
      deptName: "",
      expandValue: ""
    };
  },
  methods: {
    // 树选择操作
    onSelect(selectedKeys) {
      this.deptUnikey = selectedKeys[0];
      this.value = selectedKeys[0];
    },
    // 提交操作
    addRule() {
      const param = {
        deptUnikey: this.deptUnikey,
        deviceUnikey: this.deviceUnikey,
        deviceUuid: this.deviceUuid
      };
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/device-dept/saveOrUpdate",
          qs.stringify(param)
        )
        .then(res => {
          if (res.data.resp_code == 0) {
            this.$emit("closemodal");
            this.$notification.success({ message: "操作成功" });
          } else {
            this.$notification.warning({ message: res.data.resp_msg });
          }
        })
        .catch(error => {
          alert("操作失败：", error);
        });
    }
  },
  watch: {
    visibleDept() {
      //当选择的规则id发生变化时，清空选中状态，并获取数据以选中
      if (this.visibleDept) {
        this.value = "";
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/eq/device-dept/findByDeviceUnikey/" +
              this.deviceUnikey
          )
          .then(res => {
            if (res.data.datas == null) {
              this.deptName = "暂无";
              this.value = "";
            } else {
              this.deptName = res.data.datas.deptName;
              this.value = res.data.datas.deptUnikey;
            }
          });
      }
      this.expandValue = this.newTreeData[0]["key"];
    }
  }
};
</script>

<style scoped>
.rightText {
  text-align: right;
  font-size: 16px;
}
</style>