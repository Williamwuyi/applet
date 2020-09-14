<template>
  <div>
    <h4>基础设备列表</h4>
    <div>
      <a-form layout="inline">
        <a-form-item label="请先选择主机">
          <a-select :style="{width:'200px'}" v-model="managerId" @change="changeHost">
            <a-select-option v-for="(n,index) in allHosts" :key="index" :value="n.id">{{n.name}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="createDevice" :disabled="managerId==''">新建</a-button>
        </a-form-item>
      </a-form>
    </div>
    <div>
      <a-table
        :rowKey="record => record.uuid"
        :columns="columns2"
        :dataSource="datalist"
        :pagination="pagination"
        :scroll="{x:1330}"
        bordered
        :style="{'margin-top':'15px','max-width':'1330px'}"
        @change="handleTableChange"
      >
        <span slot="action" slot-scope="text,record">
          <a-button type="primary" size="small" @click="editBasicDev(record)">编辑</a-button>
          <a-button type="primary" size="small" @click="delBasicDev(record)">删除</a-button>
        </span>
        <span slot="devtype" slot-scope="devicetype">
          <span v-for="(n,index) in devicetypearr" :key="index">
            <span v-if="devicetype==n.dictvalue">{{n.dictname}}</span>
          </span>
        </span>
        <span slot="twotype" slot-scope="twolevel">
          <span v-for="(n,index) in twolevelarr" :key="index">
            <span v-if="twolevel==n.dictvalue">{{n.dictname}}</span>
          </span>
        </span>
        <span slot="worktype" slot-scope="workmode">
          <span v-for="(n,index) in workmodearr" :key="index">
            <span v-if="workmode==n.dictvalue">{{n.dictname}}</span>
          </span>
        </span>
      </a-table>
    </div>
    <!-- 新建修改设备 -->

    <a-modal
      ref="deviceForm"
      :maskClosable="false"
      :visible="createDeviceVis"
      title="新建/修改基础设备"
      width="800px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="()=>this.createDeviceVis=false"
      @ok="addDevice"
    >
      <a-form :form="form">
        <a-row :gutter="20">
          <a-col :span="12">
            <a-form-item label="设备名称" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input
                v-decorator="['devicename',{rules:[{required:true,message:'请输入设备名称'}],getValueFromEvent:validatorWhiteSpace}]"
              ></a-input>
              <a-input type="hidden" v-decorator="['uuid']"></a-input>
            </a-form-item>
            <a-form-item label="设备型号" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <!-- <a-input v-decorator="['devicemode']"></a-input> -->
              <a-select v-decorator="['devicemode',{rules:[{required:true,message:'请选择设备型号'}]}]">
                <a-select-option
                  v-for="(n,index) in devicemodearr"
                  :key="index"
                  :value="n.dictvalue"
                >{{n.dictname}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="二级类型" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-select v-decorator="['twolevel',{rules:[{required:true,message:'请选择二级类型'}]}]">
                <a-select-option
                  v-for="(n,index) in twolevelarr"
                  :key="index"
                  :value="n.dictvalue"
                >{{n.dictname}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="设备品牌" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input
                v-decorator="['devicebrand',{rules:[{required:true,message:'请输入设备品牌'}],getValueFromEvent:validatorWhiteSpace}]"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="设备类型" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-select v-decorator="['devicetype',{rules:[{required:true,message:'请选择设备类型'}]}]">
                <a-select-option
                  v-for="(n,index) in devicetypearr"
                  :key="index"
                  :value="Number(n.dictvalue)"
                >{{n.dictname}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="工作模式" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-select v-decorator="['workmode',{rules:[{required:true,message:'请选择工作模式'}]}]">
                <a-select-option
                  v-for="(n,index) in workmodearr"
                  :key="index"
                  :value="n.dictvalue"
                >{{n.dictname}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="备注" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-textarea v-decorator="['remark']"></a-textarea>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import formPattern from "@/components/commons/formPattern.js";

const columns2 = [
  {
    title: "设备名称",
    width: 180,
    dataIndex: "devicename",
    key: "1",
    fixed: "left"
  },
  { title: "设备型号", width: 180, dataIndex: "devicemode", key: "2" },
  {
    title: "设备类型",
    width: 180,
    dataIndex: "devicetype",
    key: "3",
    scopedSlots: { customRender: "devtype" }
  },
  {
    title: "二级类型",
    width: 120,
    dataIndex: "twolevel",
    key: "4",
    scopedSlots: { customRender: "twotype" }
  },
  {
    title: "工作模式",
    width: 120,
    dataIndex: "workmode",
    key: "6",
    scopedSlots: { customRender: "worktype" }
  },
  { title: "设备品牌", width: 150, dataIndex: "devicebrand", key: "7" },
  { title: "备注", width: 220, dataIndex: "remark", key: "9" },
  {
    title: "操作",
    width: 180,
    key: "root",
    fixed: "right",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  data() {
    return {
      columns2,
      pagination: {},
      datalist: [],
      createDeviceVis: false,
      devicetypearr: [],
      workmodearr: [],
      twolevelarr: [],
      devicemodearr: [],
      managerId: "",
      allHosts: []
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  created() {},
  mounted() {
    //获取所有的主机,根据下拉选择的主机id，获取不同主机下的设备信息
    axios
      .get(this.root_path.rootIP + this.root_path.api6001 + "/eq/manager/list")
      .then(res => {
        this.allHosts = res.data.datas;
        if (this.allHosts.length !== 0) {
          this.managerId = this.allHosts[0].id;
          this.fetch();
          // 获取设备类型、二级类型、设备型号、工作模式
          axios
            .get(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/eq/hardware/getHardwareDictList?dictcode=DEVICE_TYPE&managerId=" +
                this.managerId
            )
            .then(res => {
              this.devicetypearr = res.data;
            });
          axios
            .get(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/eq/hardware/getHardwareDictList?dictcode=TWOLEVEL&managerId=" +
                this.managerId
            )
            .then(res => {
              this.twolevelarr = res.data;
            });
          axios
            .get(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/eq/hardware/getHardwareDictList?dictcode=DEVICE_MODE&managerId=" +
                this.managerId
            )
            .then(res => {
              this.devicemodearr = res.data;
            });
          axios
            .get(
              this.root_path.rootIP +
                this.root_path.api6001 +
                "/eq/hardware/getHardwareDictList?dictcode=WORK_MODE&managerId=" +
                this.managerId
            )
            .then(res => {
              this.workmodearr = res.data;
            });
        }
      });
  },
  methods: {
    //重载表格__基本设备
    handleTableChange(pagination) {
      //表格操作触发,重载表格
      const pager = { ...this.pagination };
      pager.current = pagination.current;
      this.pagination = pager;
      this.fetch({
        limit: pagination.pageSize,
        page: pagination.current
      });
    },
    //加载表格__基本设备
    fetch(params = {}) {
      //表格的dataindex必须和json数据对象一一对应才能自动被展示
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/hardware/getBaseDeviceList",
          {
            params: { limit: 10, ...params, managerId: this.managerId }
          }
        )
        .then(data => {
          if (data.data.code == 301) {
            //当出现异常时，设置数组为空，禁用新增
            this.datalist = [];
            this.managerId = "";
            alert(data.data.msg);
            return;
          }
          const pagination = { ...this.pagination };
          pagination.pageSize = data.data.pageSize;
          pagination.total = data.data.total;
          this.datalist = data.data.list;
          this.pagination = pagination;
        });
    },
    //打开弹出框
    createDevice() {
      this.createDeviceVis = true;
    },
    //新增基础设备
    addDevice() {
      const form = this.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/eq/hardware/saveBaseDevice",
            qs.stringify({ ...values, managerId: this.managerId })
          )
          .then(res => {
            if (res.data.code == 200) {
              this.fetch({ page: this.pagination.current });
              form.resetFields();
              this.createDeviceVis = false;
              this.$notification.success({ message: "操作成功" });
            } else {
              this.$notification.warning({ message: res.data.msg });
            }
          })
          .catch(error => {
            alert("添加失败:" + error);
          });
      });
    },
    //编辑设备
    editBasicDev(res) {
      this.createDeviceVis = true;
      const form = this.form;
      setTimeout(() => {
        form.setFieldsValue(res);
      }, 50);
    },
    //删除
    delBasicDev(res) {
      let id = res.uuid;
      let managerId = this.managerId;
      let _this = this;
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
                "/eq/hardware/deleteBaseDeviceById",
              qs.stringify({ id, managerId })
            )
            .then(res => {
              _this.fetch({ page: _this.pagination.current });
            });
        }
      });
    },
    // 选择主机
    changeHost(value) {
      // console.log(this.managerId);
      this.fetch({ page: 1 });
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