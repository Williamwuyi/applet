<template>
  <div>
    <h4>员工管理</h4>
    <div class="container">
      <dept-tree
        @reloadStaffTable="findDeptStaff"
        @clearSelectrows="cleardRowKeys"
        @getMannerableDept="getMannerableDept"
      />
      <div class="sildtable">
        <div class="btns1">
          <a-button type="primary" @click="addperson">增加</a-button>
        </div>
        <!-- <div class="btns2">
          <a-input-search placeholder="请输入名字" style="width: 240px"/>
          <a-button type="primary">导入</a-button>
          <a-button type="primary">导出</a-button>
        </div>-->
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
          <span slot="check" slot-scope="imgPaths">
            <a-button type="primary" size="small" @click="checkphoto(imgPaths)">查看</a-button>
          </span>
          <span slot="deptname" slot-scope="deptId">
            <span v-for="n in deptOptions" :key="n.personId">
              <span v-if="deptId==n.deptId">{{n.deptName}}</span>
            </span>
          </span>
          <span slot="sextype" slot-scope="personSex">
            <span v-if="personSex==0">男</span>
            <span v-else-if="personSex==1">女</span>
          </span>
          <span slot="jobtype" slot-scope="positionId">
            <span v-if="positionId==0">店长</span>
            <span v-else-if="positionId==1">店员</span>
          </span>
          <span slot="action" slot-scope="record">
            <a-button type="primary" size="small" @click="editStaff(record)">编辑</a-button>
            <a-button type="danger" size="small" @click="delStaff(record)">删除</a-button>
          </span>
        </a-table>
      </div>
    </div>

    <!-- 人员新增修改 -->
    <add-edit
      ref="personForm"
      :visible="visible"
      :deptOptions="mannerableDepts"
      :personimg="personimg"
      :showimg="showimg"
      :imgIsQualify="imgIsQualify"
      :spinning="spinning"
      @checkImgIsQualify="checkImgIsQualify"
      @cancel="handleCancel"
      @create="handleCreate"
      @handelpostimg="uploadimg"
      @getbaseimgurl="getbaseimgurl"
    />

    <!-- 查看照片 -->
    <a-modal :visible="imgshow" :footer="null" @cancel="()=>this.imgshow=false">
      <div>
        <img :src="modal_img_src" style="width:100%" />
      </div>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

import DeptTree from "./deptTree";
import AddEdit from "./AddEdit";

const columns = [
  {
    title: "姓名",
    width: "15%",
    dataIndex: "person.personName",
    key: "name"
  },
  {
    title: "部门",
    dataIndex: "deptId",
    key: "1",
    width: "15%",
    scopedSlots: { customRender: "deptname" }
  },
  {
    title: "性别",
    dataIndex: "person.personSex",
    key: "2",
    width: "10%",
    scopedSlots: { customRender: "sextype" }
  },
  { title: "录入日期", dataIndex: "entryTime", key: "5", width: "15%" },
  {
    title: "职务",
    dataIndex: "person.positionId",
    key: "3",
    width: "15%",
    scopedSlots: { customRender: "jobtype" }
  },
  {
    title: "照片",
    key: "photo",
    width: "10%",
    dataIndex: "person.imgPaths",
    scopedSlots: { customRender: "check" }
  },
  {
    title: "操作",
    width: "20%",
    key: "root",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  components: { DeptTree, AddEdit },
  data() {
    return {
      datalist: [],
      pagination: {},
      columns,
      deptStaff: "",
      visible: false,
      deptOptions: [],
      showimg: "",
      personimg: "",
      imgIsQualify: true,
      addOrEditFlag: false,
      imgshow: false,
      modal_img_src: "",
      mannerableDepts: [],
      spinning: false
    };
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
          this.root_path.rootIP + this.root_path.api6001 + "/org/staff/list",
          {
            params: { current: 1, size: 10, deptId: this.deptStaff, ...params }
          }
        )
        .then(data => {
          const pagination = { ...this.pagination };
          pagination.pageSize = data.data.datas.size;
          pagination.total = data.data.datas.total;
          // this.selectedRowKeys=[];
          this.datalist = data.data.datas.records;
          this.pagination = pagination;
        });
    },
    // 弹出人员新增
    addperson() {
      this.visible = true;
      this.imgIsQualify = true;
    },
    //根据点击的部门节点，展示对应部门下的员工
    findDeptStaff(res) {
      this.deptStaff = res;
      this.fetch();
    },
    //清空selcetrowkeys.
    cleardRowKeys() {
      // this.selectedRowKeys = [];
    },
    // 图片检测设置状态
    checkImgIsQualify(data) {
      this.imgIsQualify = data;
    },
    //取消添加人员,重置人员图片src
    handleCancel() {
      this.visible = false;
      this.personimg = "";
      this.showimg = "";
      this.spinning = false;
    },
    //添加人员信息，提交表单
    handleCreate() {
      const form = this.$refs.personForm.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        if (!this.imgIsQualify) {
          alert("人员照片不合规!");
          return;
        }
        if (this.spinning) {
          return;
        }
        this.spinning = true;
        axios
          .put(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/staff/saveOrUpdate",
            {
              ...values
            }
          )
          .then(res => {
            if (res.data.resp_code == 0) {
              //添加用户成功
              if (this.addOrEditFlag) {
                //操作为编辑时
                this.fetch({ current: this.pagination.current });
              } else {
                //操作位新增时
                this.fetch({ current: 1 });
                this.pagination.current = 1;
              }
              this.$notification.success({ message: "操作成功" });
              form.resetFields();
              this.cleardRowKeys();
              this.personimg = "";
              this.showimg = "";
              this.visible = false;
              this.addOrEditFlag = false; //阀值复位
            } else {
              this.$notification.warning({ message: res.data.resp_msg });
            }
            this.spinning = false;
          })
          .catch(error => {
            this.spinning = false;
            alert("操作失败：" + error);
          });
      });
    },
    //上传图片
    uploadimg(data) {
      const form = this.$refs.personForm.form;
      form.setFieldsValue({ person: { imgPaths: data } });
    },
    //添加图片时，获取本地的图片url
    getbaseimgurl(data) {
      this.personimg = URL.createObjectURL(data);
    },
    // 编辑员工信息
    editStaff(data) {
      //选择的行数大于1时不做操作
      const form = this.$refs.personForm.form;
      this.addOrEditFlag = true; //设置阀值，判断操作是否为新增或编辑
      this.visible = true;
      this.imgIsQualify = true;
      setTimeout(() => {
        axios
          .get(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/staff/getById/" +
              data.personId
          )
          .then(res => {
            let data = res.data.datas;
            this.showimg = this.root_path.imgView + data.person.imgPaths;
            form.setFieldsValue(data);
          });
      }, 50);
    },
    // 删除员工信息
    delStaff(data) {
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
                "/org/staff/removeById/" +
                data.personId
            )
            .then(res => {
              _this.fetch({ current: _this.pagination.current });
            });
        }
      });
    },
    // 查看照片
    checkphoto(data) {
      this.imgshow = true;
      this.modal_img_src = this.root_path.imgView + data;
    },
    // 新增人员时的可选择部门
    getMannerableDept(data) {
      this.mannerableDepts = data.newTreeNodes;
      this.deptOptions = data.allDepts;
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
.sildtable .btns1,
.sildtable .btns2 {
  margin-top: 15px;
}
.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}
</style>