<template>
  <div>
    <h4>公司群组管理</h4>
    <a-button type="primary" @click="showmodal">新增</a-button>
    <a-table
      :rowKey="record => record.comId"
      :columns="columns2"
      :dataSource="datalist"
      :pagination="pagination"
      :scroll="{x:1100}"
      bordered
      :style="{'margin-top':'15px'}"
      @change="handleTableChange"
    >
      <span slot="action" slot-scope="record">
        <a-button type="primary" size="small" @click="editComp(record)">编辑</a-button>
        <a-button type="danger" size="small" @click="delComp(record)">删除</a-button>
      </span>
      <span slot="check" slot-scope="comBusiness">
        <a-button type="primary" size="small" @click="showComImg(comBusiness)">查看</a-button>
      </span>
      <span slot="showlogo" slot-scope="comLogo">
        <!-- <img v-if="comLogo!=null" style="max-height:100px;width:auto" :src="root_path.imgView+comLogo"> -->
        <a-button type="primary" size="small" @click="showComImg(comLogo)">查看</a-button>
      </span>
    </a-table>

    <a-modal
      :visible="visible"
      title="新增/编辑"
      width="800px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      :bodyStyle="{minHeight:'500px'}"
      @cancel="closeModal"
      @ok="subCompany"
    >
      <a-spin :spinning="spinning">
        <a-form :form="form">
          <a-row>
            <a-col :span="12">
              <a-form-item label="公司名称" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input
                  :maxLength="20"
                  v-decorator="['comName',{rules:[{required:true,message:'请输入公司名'}],getValueFromEvent:validatorWhiteSpace}]"
                ></a-input>
                <a-input type="hidden" v-decorator="['comId']"></a-input>
              </a-form-item>
              <a-form-item v-if="!isEdit" label="搭载主机" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-select v-decorator="['hostIP',{rules:[{required:true,message:'请选择主机类型'}]}]">
                  <a-select-option
                    v-for="(n,index) in baseHost"
                    :key="index"
                    :value="n.id"
                  >{{n.name}}</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="详细地址" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input v-decorator="['comAddress']"></a-input>
              </a-form-item>
              <a-form-item
                v-if="isEdit"
                label="门店数量限制"
                :labelCol="{span:8}"
                :wrapperCol="{span:16}"
              >
                <a-input-number
                  :min="0"
                  :max="100"
                  :parser="value => value.replace(/[^0-9]+/,'')"
                  v-decorator="['maxShopnum',{initialValue:3}]"
                  :style="{width:'100%'}"
                ></a-input-number>
              </a-form-item>
              <a-form-item label="公司LOGO" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <span v-if="logoimg!=''">
                  <img :src="logoimg" :style="{'max-height':'100px','width':'auto'}" />
                </span>
                <span v-else-if="showimglogo!=''&&showimglogo!=undefined">
                  <img :src="showimglogo" :style="{'max-height':'100px','width':'auto'}" />
                </span>
                <span v-else></span>
                <span class="file">
                  选择图片
                  <input type="file" @change="getFile($event,'comLogo','logoimg')" />
                </span>
                <a-input type="hidden" v-decorator="['comLogo']"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="联系人" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input
                  :maxLength="20"
                  v-decorator="['comLeader',{rules:[{required:true,message:'请输入联系人姓名'}],getValueFromEvent:validatorWhiteSpace}]"
                ></a-input>
              </a-form-item>
              <a-form-item label="手机号" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input
                  :maxLength="11"
                  v-decorator="['comPhone',{rules:[{required:true,message:'请输入手机号'}],getValueFromEvent:validatorPhone}]"
                ></a-input>
              </a-form-item>
              <a-form-item label="邮箱" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input v-decorator="['comEmail',{getValueFromEvent:validatorEmial}]"></a-input>
              </a-form-item>
              <a-form-item
                v-if="!isEdit"
                label="门店数量限制"
                :labelCol="{span:8}"
                :wrapperCol="{span:16}"
              >
                <a-input-number
                  :min="0"
                  :max="100"
                  :parser="value => value.replace(/[^0-9]+/,'')"
                  v-decorator="['maxShopnum',{initialValue:3}]"
                  :style="{width:'100%'}"
                ></a-input-number>
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="公司执照" :labelCol="{span:4}" :wrapperCol="{span:16}">
                <span v-if="businessimg!=''">
                  <img :src="businessimg" :style="{'max-height':'200px','width':'auto'}" />
                </span>
                <span v-else-if="showimgbusiness!=''&&showimgbusiness!=undefined">
                  <img :src="showimgbusiness" :style="{'max-height':'200px','width':'auto'}" />
                </span>
                <span v-else></span>
                <span class="file">
                  选择图片
                  <input type="file" @change="getFile($event,'comBusiness','businessimg')" />
                </span>
                <a-input type="hidden" v-decorator="['comBusiness']"></a-input>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
import formPattern from "@/components/commons/formPattern.js";

const columns2 = [
  { title: "公司名称", width: "13%", dataIndex: "comName", key: "1" },
  {
    title: "公司LOGO",
    width: "10%",
    dataIndex: "comLogo",
    key: "2",
    scopedSlots: { customRender: "showlogo" }
  },
  {
    title: "公司执照",
    width: "10%",
    dataIndex: "comBusiness",
    key: "7",
    scopedSlots: { customRender: "check" }
  },
  { title: "联系人", width: "10%", dataIndex: "comLeader", key: "3" },
  { title: "手机号", width: "10%", dataIndex: "comPhone", key: "4" },
  { title: "邮箱", width: "10%", dataIndex: "comEmail", key: "5" },
  { title: "详细地址", width: "24%", dataIndex: "comAddress", key: "6" },
  {
    title: "操作",
    width: "13%",
    key: "root",
    scopedSlots: { customRender: "action" }
  }
];

export default {
  data() {
    return {
      datalist: [],
      pagination: {},
      columns2,
      visible: false,
      businessimg: "",
      showimgbusiness: "",
      logoimg: "",
      showimglogo: "",
      isEdit: false,
      spinning: false,
      hostIP: "",
      baseHost: []
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  mounted() {
    this.fetch();
    axios
      .get(
        this.root_path.rootIP + this.root_path.api6001 + "/eq/base-manager/list"
      )
      .then(res => {
        this.baseHost = res.data.datas;
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
        size: pagination.pageSize,
        current: pagination.current
      });
    },
    //加载表格__基本设备
    fetch(params = {}) {
      //表格的dataindex必须和json数据对象一一对应才能自动被展示
      axios
        .get(
          this.root_path.rootIP + this.root_path.api6001 + "/org/company/list",
          {
            params: { size: 10, ...params }
          }
        )
        .then(data => {
          const pagination = { ...this.pagination };
          pagination.pageSize = data.data.datas.size;
          pagination.total = data.data.datas.total;
          pagination.current = data.data.datas.current;
          this.datalist = data.data.datas.records;
          this.pagination = pagination;
        });
    },
    // 选择图片
    getFile(event, opts, img) {
      //opts:代表表单字段，img：代表手动选择文件时的本地路径
      if (event.target.files.length == 0) {
        //判断文件操作，如果点击选择文件后又取消操作，则不执行
        return;
      }
      let file = event.target.files[0];
      this[img] = URL.createObjectURL(file);
      let formData = new FormData();
      const form = this.form;
      formData.append("file", file);
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/company/imgUpload",
          formData
        )
        .then(res => {
          if (res.data.resp_code == 0) {
            form.setFieldsValue({ [opts]: res.data.datas });
            this.$notification.success({ message: "图片添加成功" });
          } else {
            this.$notification.warning({ message: res.data.resp_msg });
          }
        })
        .catch(error => {
          alert("添加失败：" + error);
        });
    },
    // 取消弹窗
    closeModal() {
      this.visible = false;
      this.clearImg();
    },
    // 弹出编辑框
    showmodal() {
      this.visible = true;
      this.isEdit = false;
    },
    // 表单提交
    subCompany() {
      const form = this.form;
      let hostIP = form.getFieldValue("hostIP");
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        this.spinning = true;
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/company/saveOrUpdate",
            values
          )
          .then(resCom => {
            console.log('resCom:',resCom)
            if (this.isEdit) {
              //编辑
              if (resCom.data.resp_code == 0) {
                this.visible = false;
                this.$notification.success({ message: "操作成功！" });
                this.fetch({ current: this.pagination.current });
                this.clearImg();
              } else {
                this.$notification.error({ message: resCom.data.resp_msg });
              }
              this.spinning = false;
            } else {
              //新增,附带建初始管理员账号
              if (resCom.data.resp_code == 0) {
                let comData = resCom.data.datas;
                const param = {
                  username: comData.comPhone,
                  password: "123456", //初始密码
                  roleId: "1249606345485832194", //默认角色
                  comUnikey: comData.comId,
                  nickname: comData.comLeader,
                  mobile: comData.comPhone
                };
                const paramHost = {
                  name: comData.comName,
                  baseManagerId: hostIP,
                  comUnikey: comData.comId
                };
                axios
                  .all([
                    axios.post(
                      this.root_path.rootIP +
                        this.root_path.api6001 +
                        "/auth/user_admin/addUser",
                      param
                    ),
                    axios.post(
                      this.root_path.rootIP +
                        this.root_path.api6001 +
                        "/eq/manager/saveOrUpdate",
                      qs.stringify(paramHost)
                    )
                  ])
                  .then(
                    axios.spread((resUser, resHost) => {
                      console.log(resUser,'--------reshost:',resHost)
                      if (
                        resUser.data.resp_code == 0 &&
                        resHost.data.resp_code == 0
                      ) {
                        this.visible = false;
                        this.fetch({ current: this.pagination.current });
                        this.$notification.success({
                          message: "操作成功",
                          description:
                            "账号名：" +
                            param.username +
                            ", 初始密码：" +
                            param.password +
                            "。请尽快修改账号密码！",
                          duration: 10
                        });
                        this.clearImg();
                      } else {
                        this.$notification.warning({
                          //resUser.data.resp_msg
                          message: "操作异常",
                          description:
                            "新建账号：" +
                            resUser.data.resp_msg +
                            ",新建主机：" +
                            resHost.data.resp_msg
                        });
                      }
                      this.spinning = false;
                    })
                  )
                  .catch(error => {
                    alert.log("操作失败", error);
                    this.spinning = false;
                  });
              }
            }
          })
          .catch(error => {
            alert("操作公司失败：" + error);
            this.spinning = false;
          });
      });
    },
    // 清空弹出框的图片信息
    clearImg() {
      this.businessimg = "";
      this.showimgbusiness = "";
      this.logoimg = "";
      this.showimglogo = "";
    },
    // 编辑
    editComp(data) {
      const form = this.form;
      this.visible = true;
      this.isEdit = true;
      setTimeout(() => {
        this.showimgbusiness =
          data.comBusiness != "" && data.comBusiness != null
            ? this.root_path.imgView + data.comBusiness
            : "";
        this.showimglogo =
          data.comLogo != "" && data.comLogo != null
            ? this.root_path.imgView + data.comLogo
            : "";
        form.setFieldsValue(data);
      }, 50);
    },
    // 删除
    delComp(data) {
      let _this = this;
      let comId = data.comId;
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
                "/org/company/deleteById/" +
                comId
            )
            .then(res => {
              if (_this.pagination.total % _this.pagination.pageSize == 1) {
                _this.fetch({ current: _this.pagination.current - 1 });
              } else {
                _this.fetch({ current: _this.pagination.current });
              }
            })
            .catch(error => {
              alert("操作失败:" + error);
            });
        }
      });
    },
    // 查看公司照片
    showComImg(data) {},
    // 电话校验
    validatorPhone(event) {
      return formPattern.validatorPhone(event);
    },
    // 空格校验
    validatorWhiteSpace(event) {
      return formPattern.validatorWhiteSpace(event);
    },
    // 校验邮箱
    validatorEmial(event) {
      return formPattern.validatorEmial(event);
    }
  }
};
</script>

<style scoped>
.file {
  position: relative;
  display: inline-block;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 0px 15px;
  overflow: hidden;
  color: #595959;
  text-decoration: none;
  text-indent: 0;
  line-height: 30px;
  margin-top: 5px;
  transition: all 0.3s;
}
.file input {
  position: absolute;
  font-size: 100px;
  right: 0;
  top: 0;
  left: 0;
  bottom: 0;
  opacity: 0;
}
.file:hover {
  border-color: #40a9ff;
  color: #40a9ff;
  text-decoration: none;
}
</style>