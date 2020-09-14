<template>
  <div>
    <a-button type="primary" :style="{float:'right'}" @click="showModal">修改</a-button>
    <a-descriptions bordered title="公司信息">
      <a-descriptions-item label="公司名称" :span="3">{{comMsg.comName}}</a-descriptions-item>
      <a-descriptions-item label="联系人">{{comMsg.comLeader}}</a-descriptions-item>
      <a-descriptions-item label="电话">{{comMsg.comPhone}}</a-descriptions-item>
      <a-descriptions-item label="收银系统类型">
        <span v-if="comMsg.posType=='MeiZhuang'">魅妆</span>
        <span v-else-if="comMsg.posType=='MDD'">美得得</span>
        <span v-else></span>
      </a-descriptions-item>

      <a-descriptions-item label="地址" :span="2">{{comMsg.comAddress}}</a-descriptions-item>
      <a-descriptions-item label="公司token">{{comMsg.posToken}}</a-descriptions-item>
      <a-descriptions-item label="logo" :span="2">
        <img v-if="comMsg.comLogo!=null" :src="comMsg.comLogo" />
      </a-descriptions-item>
      <a-descriptions-item label="相似度">{{comMsg.similarLimit}}</a-descriptions-item>
      <a-descriptions-item label="执照" :span="2">
        <img v-if="comMsg.comBusiness!=null" :src="comMsg.comBusiness" />
      </a-descriptions-item>
      <a-descriptions-item label="邮箱">{{comMsg.comEmail}}</a-descriptions-item>
    </a-descriptions>

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
              <a-form-item label="手机号" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input
                  :maxLength="11"
                  v-decorator="['comPhone',{rules:[{required:true,message:'请输入手机号'}],getValueFromEvent:validatorPhone}]"
                ></a-input>
              </a-form-item>
              <a-form-item label="详细地址" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input v-decorator="['comAddress']"></a-input>
              </a-form-item>

              <a-form-item label="公司token" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input v-decorator="['posToken',{getValueFromEvent:validatorEmial}]"></a-input>
              </a-form-item>
              <a-form-item label="公司LOGO" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <span v-if="logoimg.slice(-4)!='null'">
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
              <a-form-item label="收银类型" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-select v-decorator="['posType']">
                  <a-select-option value="MDD">美得得</a-select-option>
                  <a-select-option value="MeiZhuang">魅妆</a-select-option>
                </a-select>
              </a-form-item>

              <a-form-item label="相似度" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-select v-decorator="['similarLimit']">
                  <a-select-option value="0.75">0.75</a-select-option>
                  <a-select-option value="0.80">0.80</a-select-option>
                  <a-select-option value="0.85">0.85</a-select-option>
                  <a-select-option value="0.90">0.90</a-select-option>
                  <a-select-option value="0.95">0.95</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="邮箱" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input v-decorator="['comEmail',{getValueFromEvent:validatorEmial}]"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="公司执照" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <span v-if="businessimg.slice(-4)!='null'">
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

export default {
  data() {
    return {
      comMsg: {},
      visible: false,
      businessimg: "",
      showimgbusiness: "",
      logoimg: "",
      showimglogo: "",
      spinning: false
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  mounted() {
    this.getCompanyMsg();
  },
  methods: {
    // 获取公司信息
    getCompanyMsg() {
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/company/findById/" +
            this.$store.state.comUnikey
        )
        .then(res => {
          this.comMsg = res.data.datas;
        });
    },
    // 弹出
    showModal() {
      const form = this.form;
      this.visible = true;
      setTimeout(() => {
        this.showimgbusiness =
          this.comMsg.comBusiness != "" && this.comMsg.comBusiness != null
            ? this.root_path.imgView + this.comMsg.comBusiness
            : "";
        this.showimglogo =
          this.comMsg.comLogo != "" && this.comMsg.comLogo != null
            ? this.root_path.imgView + this.comMsg.comLogo
            : "";
        form.setFieldsValue(this.comMsg);
      }, 50);
    },
    // 关闭弹窗
    closeModal() {
      this.visible = false;
      this.clearImg();
    },
    // 提交数据
    subCompany() {
      const form = this.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        this.spinning = true;
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/org/company/updateBySelf",
            qs.stringify(values)
          )
          .then(res => {
            if (res.data.resp_code == 0) {
              this.visible = false;
              this.$notification.success({ message: "操作成功！" });
              this.getCompanyMsg();
              this.clearImg();
            } else {
              this.$notification.error({ message: resCom.data.resp_msg });
            }
            this.spinning = false;
          })
          .catch(error => {
            alert("操作失败!", error);
            this.spinning = false;
          });
      });
    },
    // 选择图片
    getFile(event, opts, img) {
      //opts:代表表单字段，img：代表手动选择文件时的本地路径
      if (event.target.files.length == 0) {
        //判断文件操作，如果点击选择文件后又取消操作，则不执行
        return;
      }
      const form = this.form;
      let file = event.target.files[0];
      let formData = new FormData();
      this[img] = URL.createObjectURL(file);
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
    // 清空弹出框的图片信息
    clearImg() {
      this.businessimg = "";
      this.showimgbusiness = "";
      this.logoimg = "";
      this.showimglogo = "";
    },
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