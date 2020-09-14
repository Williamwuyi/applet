<template>
  <div>
    <h4>设备列表</h4>
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
        :rowKey="record => record.deviceunikey"
        :columns="columns2"
        :dataSource="datalist"
        :pagination="pagination"
        :scroll="{x:1500}"
        bordered
        :style="{'margin-top':'15px','max-width':'1500px'}"
        @change="handleTableChange"
      >
        <span slot="action" slot-scope="record">
          <a-button type="primary" size="small" @click="editDevice(record)">编辑</a-button>
          <a-button size="small" @click="easyClean(record)">一键清空</a-button>
          <a-button size="small" @click="easyAdd(record)">一键添加</a-button>
          <a-button type="danger" size="small" @click="delDevice(record)">删除</a-button>
        </span>
        <span slot="status" slot-scope="devicestatus">
          <span v-if="devicestatus=='0'">正常</span>
          <span v-else-if="devicestatus=='1'">未启用</span>
          <span v-else-if="devicestatus=='2'">损坏</span>
          <span v-else-if="devicestatus=='3'">未连接</span>
          <span v-else-if="devicestatus=='4'">已连接</span>
          <span v-else>undefined</span>
        </span>
        <span slot="manner" slot-scope="record">
          <!-- v-if="record.deviceuuid" -->
          <a-button type="primary" size="small" @click="setDeptDev(record)">配置</a-button>
        </span>
      </a-table>
    </div>
    <a-modal
      ref="deviceListForm"
      :maskClosable="false"
      :visible="visible"
      title="新增/修改设备信息"
      width="800px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="closemodal"
      @ok="addDevice"
    >
      <a-form :form="form">
        <a-row :gutter="20">
          <a-col :span="12">
            <a-form-item label="设备名称" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input
                v-decorator="['deviceaddress',{rules:[{required:true,message:'请输入设备名称'}],getValueFromEvent:validatorWhiteSpace}]"
              ></a-input>
              <a-input type="hidden" v-decorator="['uuid']"></a-input>
              <a-input type="hidden" v-decorator="['devicechannel',{initialValue:'8011'}]">通道号</a-input>
              <a-input type="hidden" v-decorator="['basedeviceuuid',{initialValue:'76'}]">设备类型</a-input>
              <a-input type="hidden" v-decorator="['deviceport',{initialValue:'8011'}]">设备端口</a-input>
              <a-input type="hidden" v-decorator="['devicecallback']">设备回调地址</a-input>
            </a-form-item>
            <!-- <a-form-item label="通道号" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input v-decorator="['devicechannel',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
            </a-form-item>-->
            <a-form-item label="设备预览账号" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input v-decorator="['deviceaccout',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
            </a-form-item>
            <a-form-item label="设备账号密码" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input v-decorator="['devicepwd',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
            </a-form-item>
            <!-- <a-form-item v-if="deviceID=='NIREN_MAGNETISM'" label="设备延时操作" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input v-decorator="['deviceperatortime',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
            </a-form-item>-->
            <!-- <a-form-item label="ftp账号" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input v-decorator="['ftpaccout',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
            </a-form-item>
            <a-form-item label="ftp密码" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input v-decorator="['ftppwd',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
            </a-form-item>-->
            <!-- <a-form-item label="设备回调地址" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input v-decorator="['devicecallback',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
            </a-form-item>-->
            <a-form-item :wrapperCol="{span:16,offset:8}">
              <a-checkbox v-decorator="['isCount',{valuePropName:'checked'}]">是否作为统计计数设备</a-checkbox>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <!-- <a-form-item label="设备类型" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-select v-decorator="['basedeviceuuid',{rules:[{required:true,message:'请选择设备类型'}]}]" @change="finduuid" >
                <a-select-option v-for="(n,index) in devicearr" :key="index" :value="n.uuid.toString()">{{n.devicename}}</a-select-option>
              </a-select>
            </a-form-item>-->
            <!-- <a-form-item v-if="deviceID=='NIREN_MAGNETISM'?true:deviceID=='ZKT_CARD_PRINT'" label="设备调用模式" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-select v-decorator="['devicemodel']">
                <a-select-option value="0">常开</a-select-option>
                <a-select-option value="1">常闭</a-select-option>
                <a-select-option value="SSR">SSR</a-select-option>
              </a-select>
            </a-form-item>-->
            <!-- <a-form-item label="设备端口" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input v-decorator="['deviceport',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
            </a-form-item>-->
            <a-form-item label="设备IP地址" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input
                v-decorator="['deviceip',{rules:[{required:true,message:'请输入设备IP地址'}],getValueFromEvent:validatorWhiteSpace}]"
              ></a-input>
            </a-form-item>
            <a-form-item label="设备硬件码" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-input
                v-decorator="['deviceuuid',{rules:[{required:true,message:'请输入设备硬件码'}],getValueFromEvent:validatorWhiteSpace}]"
              ></a-input>
            </a-form-item>
            <a-form-item label="备注" :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-textarea v-decorator="['remark']"></a-textarea>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <!-- 配置部门 -->
    <match-dept
      :newTreeData="newTreeData"
      :visibleDept="showDeptMatch"
      :deviceUnikey="mDeviceUnikey"
      :deviceUuid="mDeviceUuid"
      @closemodal="closeUploadDept"
    />
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import commonJS from "@/components/commons/common.js";
import formPattern from "@/components/commons/formPattern.js";
import MatchDept from "./MatchDept";

const columns2 = [
  {
    title: "设备名称",
    width: 180,
    dataIndex: "deviceaddress",
    key: "7",
    fixed: "left"
  },
  { title: "设备类型", width: 180, dataIndex: "devicename", key: "2" },
  { title: "通道号", width: 80, dataIndex: "devicechannel", key: "3" },
  { title: "设备ip地址", width: 170, dataIndex: "deviceip", key: "4" },
  {
    title: "配置门店/部门",
    width: 130,
    scopedSlots: { customRender: "manner" },
    key: "5"
  },
  { title: "设备端口", width: 100, dataIndex: "deviceport", key: "9" },
  {
    title: "当前状态",
    width: 100,
    dataIndex: "devicestatus",
    key: "10",
    scopedSlots: { customRender: "status" }
  },
  { title: "备注", width: 220, dataIndex: "remark", key: "18" },
  {
    title: "操作",
    width: 340,
    key: "root",
    fixed: "right",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  components: { MatchDept },
  data() {
    return {
      columns2,
      datalist: [],
      pagination: {},
      visible: false,
      devicearr: [],
      deviceID: "",
      devicemodelarr: [
        { val: 0, til: "常开" },
        { val: 1, til: "常闭" }
      ],
      addOrEditFlag: false,
      allHosts: [],
      managerId: "",
      mDeviceUnikey: "",
      mDeviceUuid: "",
      showDeptMatch: false,
      newTreeData: []
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  mounted() {
    //获取所有的主机,根据下拉选择的主机id，获取不同主机下的设备信息
    axios
      .get(this.root_path.rootIP + this.root_path.api6001 + "/eq/manager/list")
      .then(res => {
        this.allHosts = res.data.datas;
        if (this.allHosts.length !== 0) {
          this.managerId = this.allHosts[0].id;
          this.fetch();
          this.getBaseDevice();
        }
      });
    this.resetTreeNodes();
  },
  methods: {
    //重载表格__设备
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
    //加载表格__设备
    fetch(params = {}) {
      //表格的dataindex必须和json数据对象一一对应才能自动被展示
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/hardware/getHardwareDeviceInfoList",
          {
            params: { limit: 10, managerId: this.managerId, ...params }
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
          pagination.current = data.data.pageNum;
          this.datalist = data.data.list;
          this.pagination = pagination;
        });
    },
    //弹出新增框
    createDevice() {
      this.visible = true;
    },
    //添加设备，重设设备类型，避免非泥人继电器显示通道模式
    addDevice() {
      const form = this.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        let ipLocation = /^(?:(?:1[0-9][0-9]\.)|(?:2[0-4][0-9]\.)|(?:25[0-5]\.)|(?:[1-9][0-9]\.)|(?:[0-9]\.)){3}(?:(?:1[0-9][0-9])|(?:2[0-4][0-9])|(?:25[0-5])|(?:[1-9][0-9])|(?:[0-9]))$/;
        let deviceips = values.deviceip;
        if (this.managerId == "") {
          alert("请先选择主机!");
          return;
        } else if (!ipLocation.test(deviceips)) {
          alert("设备ip地址不正确");
          return;
        }
        const param = {
          ...values,
          devicecallback:
            "http://" + deviceips + ":9898/getJuLongIdentifyCallBack"
        };
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/eq/hardware/saveHardwareDeviceInfo",
            qs.stringify({ ...param, managerId: this.managerId })
          )
          .then(res => {
            if (res.data.code == 200) {
              if (this.addOrEditFlag) {
                //编辑时跳转到当前页
                this.fetch({ page: this.pagination.current });
              } else {
                //新建时，跳转到新建的项所在页
                this.fetch({ page: 1 });
                this.pagination.current = 1;
              }
              form.resetFields();
              this.visible = false;
              this.addOrEditFlag = false;
              this.deviceID = "";
              this.$notification.success({ message: "操作成功" });
            } else {
              this.$notification.warning({ message: res.data.msg });
            }
          })
          .catch(error => {
            alert("操作失败：" + error);
          });
      });
    },
    //根据选择的基础设备id判断是否显示devicemodel等切换显示的选项
    finduuid(value) {
      for (let i of this.devicearr) {
        if (i.uuid == value) {
          this.deviceID = i.devicemode;
          return;
        }
      }
    },
    //编辑设备信息
    editDevice(res) {
      this.visible = true;
      this.addOrEditFlag = true; //阀值判断为编辑操作
      this.finduuid(res.basedeviceuuid);
      const form = this.form;
      setTimeout(() => {
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/eq/hardware/getIsCountByDeviceUnikey",
            qs.stringify({ deviceUnikey: res.deviceunikey })
          )
          .then(res1 => {
            let xxx = res1.data.data;
            form.setFieldsValue({ ...res, isCount: xxx });
          });
      }, 50);
    },
    //删除设备
    delDevice(res) {
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
                "/eq/hardware/deleteHardwareDeviceById",
              qs.stringify({ id, managerId })
            )
            .then(res => {
              _this.fetch({ page: _this.pagination.current });
            })
            .catch(error => {
              alert("操作失败:" + error);
            });
        }
      });
    },
    //关闭弹出框，重设设备类型，避免非泥人继电器显示通道模式
    closemodal() {
      this.visible = false;
      this.deviceID = "";
    },
    // 选择主机
    changeHost(value) {
      this.fetch({ page: 1 });
      this.getBaseDevice();
    },
    // 根据选择的主机，获取不同主机下的基本设备
    getBaseDevice() {
      //将基本设备表存到数组中
      const param = {
        managerId: this.managerId,
        limit: 1000
      };
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/hardware/getBaseDeviceList",
          { params: param }
        )
        .then(res => {
          this.devicearr = res.data.list;
        });
    },
    // 弹出设备绑定部门
    setDeptDev(res) {
      this.showDeptMatch = true;
      this.mDeviceUnikey = res.deviceunikey;
      this.mDeviceUuid = res.deviceuuid;
    },
    // 关闭配置部门弹窗
    closeUploadDept() {
      this.showDeptMatch = false;
    },
    // 控制设备管理的门店
    resetTreeNodes() {
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
            let allDepts = res_depts.data.datas;
            let newArr = [];
            // let newTreeNodes=designTree(mannerableDept,allDepts,newArr);
            let newTreeNodes = commonJS.designTree(
              mannerableDept,
              allDepts,
              newArr
            );
            let treeArr = [];
            for (let item of newTreeNodes) {
              //过滤不必要的数组对象，返回新的数组结构
              treeArr.push({
                title: item.deptName,
                key: item.deptUnikey,
                pid: item.parentId,
                id: item.deptId
              });
            }
            _this.newTreeData = commonJS.transData(
              treeArr,
              "id",
              "pid",
              "children"
            );
          })
        );
    },
    // 空格校验
    validatorWhiteSpace(event) {
      return formPattern.validatorWhiteSpace(event);
    },
    // 一键清空设备信息
    easyClean(data) {
      let deviceUnikey = data.deviceunikey;
      let managerId = this.managerId;
      let _this = this;
      this.$confirm({
        title: "是否确定一键清空？",
        content:
          "将删除系统会员信息及对应关系，请谨慎操作。一键清空后请将对应摄像机里的人员数据信息进行全部删除，否则将发生对应冲突。",
        okText: "确定",
        okType: "danger",
        cancelText: "取消",
        onOk() {
          axios
            .post(
              _this.root_path.rootIP +
                _this.root_path.api6001 +
                "/eq/hardware/deleteHardwareDeviceUserByDerviceunikey",
              qs.stringify({ deviceUnikey, managerId })
            )
            .then(res => {
              _this.fetch({ page: _this.pagination.current });
            })
            .catch(error => {
              alert("操作失败:" + error);
            });
        }
      });
    },
    // 一键添加设备信息
    easyAdd(data) {
      let deviceUnikey = data.deviceunikey;
      let managerId = this.managerId;
      let _this = this;
      this.$confirm({
        title: "是否确定一键添加？",
        content: "该操作将上传、更新所有人员信息到设备中！",
        okText: "确定",
        okType: "primary",
        cancelText: "取消",
        onOk() {
          axios
            .post(
              _this.root_path.rootIP +
                _this.root_path.api6001 +
                "/eq/hardware/addAllDeviceUserByDerviceunikey",
              qs.stringify({ deviceUnikey, managerId })
            )
            .then(res => {
              _this.fetch({ page: _this.pagination.current });
            })
            .catch(error => {
              alert("操作失败:" + error);
            });
        }
      });
    }
  }
};
</script>

<style scoped>
</style>