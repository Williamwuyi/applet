<template>
  <div>
    <a-modal
      :visible="visible"
      title="新增/修改会员信息"
      okText="确定"
      cancelText="取消"
      width="900px"
      :destroyOnClose="true"
      :maskClosable="false"
      @cancel="()=>{$emit('cancel')}"
      @ok="()=>{$emit('create')}"
    >
      <a-spin :spinning="spinning">
        <a-form :form="form">
          <!-- <a-row :gutter="30"> -->
          <!-- <a-col :span="18"> -->
          <a-row>
            <a-col :span="10">
              <a-form-item label="姓名" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input
                  :maxLength="15"
                  v-decorator="['personName',{rules:[{required:true,message:'请输入姓名'}],getValueFromEvent:validatorWhiteSpace}]"
                ></a-input>
                <a-input type="hidden" v-decorator="['personId']"></a-input>
                <a-input type="hidden" v-decorator="['status',{initialValue:2}]"></a-input>
              </a-form-item>
              <a-form-item label="手机" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input
                  :maxLength="11"
                  v-decorator="['personPhone',{rules:[{required:true,message:'请输入手机号'}],getValueFromEvent:validatorPhone}]"
                ></a-input>
              </a-form-item>
              <a-form-item label="备注" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-textarea v-decorator="['remarks']"></a-textarea>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="性别" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-select v-decorator="['personSex',{initialValue:0}]">
                  <a-select-option :value="0">男</a-select-option>
                  <a-select-option :value="1">女</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="所属门店" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-select v-decorator="['deptUnikey',{rules:[{required:true,message:'请选择门店'}]}]">
                  <a-select-option
                    v-for="(n,index) in deptOptions"
                    :key="index"
                    :value="n.deptUnikey"
                  >{{n.deptName}}</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="年龄" :labelCol="{span:8}" :wrapperCol="{span:16}">
                <a-input
                  :maxLength="10"
                  v-decorator="['personAge',{getValueFromEvent:validatorPhone}]"
                ></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12" style="margin-left:77px">
              <!-- <a-form-item :style="{border,borderRadius:'5px',transition:'all 0.3s'}">
                <span v-if="personimg!=''">
                  <img :src="personimg" :style="{'width':'100%'}" />
                </span>
                <span v-else-if="showimg!=''&&showimg!=undefined">
                  <img :src="showimg" :style="{'width':'100%'}" />
                </span>
                <span v-else></span>
                <span class="file">
                  选择图片
                  <input type="file" accept="image/jpeg" @change="getFile($event)" />
                </span>
                <a-input
                  type="hidden"
                  v-decorator="['imgPaths',{rules:[{required:true,message:'图片不能为空'}]}]"
                ></a-input>
              </a-form-item>-->

              <a-form-item :style="{border,borderRadius:'5px',transition:'all 0.3s'} ">
                <a-upload
                  list-type="picture-card"
                  :file-list="fileList"
                  @preview="handlePreview"
                  @change="handleChange"
                  :customRequest="customRequest"
                >
                  <div v-if="fileList.length < 3">
                    <a-icon type="plus" />
                    <div class="ant-upload-text">添加图片</div>
                  </div>
                </a-upload>
                <a-modal
                  :visible="previewVisible"
                  :footer="null"
                  @cancel="handleCancel"
                  v-decorator="['imgPaths',{rules:[{required:true,message:'图片不能为空'}]}]"
                >
                  <!-- 显示最大图片化 -->
                  <img alt="example" style="width: 100%" :src="previewImage" />
                </a-modal>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
function getBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = error => reject(error);
  });
}
import axios from "axios";
import formPattern from "@/components/commons/formPattern.js";
export default {
  // props: ["visible", "deptOptions", "showimg", "imgIsQualify", "spinning",'listfile'],
  props: {
    visible: Boolean,
    deptOptions: Array,
    showimg: String,
    imgIsQualify: Boolean,
    spinning: Boolean,
    listfile: Array
  },
  data() {
    return {
      border: "0px",
      previewVisible: false,
      imgPaths: [],
      previewImage: [],
      fileList: []
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  mounted() {},
  methods: {
    // 上传多个图片
    handleCancel() {
      this.previewVisible = false;
    },
    async handlePreview(file) {
      console.log("handlePreview", file);
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj);
      }
      this.previewImage = file.url || file.preview;
      this.previewVisible = true;
    },
    handleChange({ fileList }) {
      console.log("handleChange", fileList);
      this.fileList = fileList;
    },
    // 自定义上传方法
    customRequest(file) {
      let _this = this;
      let formData = new FormData();
      formData.append("file", file.file);
      const reader = new FileReader();
      reader.readAsDataURL(file.file);
      reader.onloadend = function(e) {
        console.log('加载e',e); // 打印图片的base64
        if (e && e.target && e.target.result) {
          file.onSuccess();
          let files;
          for (let key in _this.fileList) {
            files = _this.fileList[key].originFileObj;
          }
          _this.getFile(files);
        } else {
          file.onError();
        }
      };
    },

    getFile(file) {
      // if (event.target.files.length == 0) {
      //判断文件操作，如果点击选择文件后又取消操作，则不执行
      // return;
      // }
      // let file = event.target.files[0];
      // console.log("++++", URL.createObjectURL(file));
      this.$emit("getbaseimgurl", file);
      let formData = new FormData();
      const form = this.form;
      formData.append("file", file);
      this.border = "0px";
      this.$emit("checkImgIsQualify", true); //初始设置图片的人脸检测状态为true
      let _this = this;
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/hardware/arcfaceCheck",
          formData
        )
        .then(res => {
          console.log("检查人脸：", res);
          if (res.data.message == "未检出到人脸") {
            //未检测到人脸时，border、图片阀值为false，不允许提交
            this.border = "1px solid red";
            this.$message.error("未检测出人脸");
            this.$emit("checkImgIsQualify", false);
          } else if (res.data.message == "success") {
            axios
              .post(
                this.root_path.rootIP +
                  this.root_path.api6001 +
                  "/org/person/imgUpload",
                formData
              )
              .then(res => {
                console.log('添加图片地址：',res.data.datas); //方法的图片名称地址：  /2020-07-08/1f325ed509074134990913952a394f01.jpg
                if (res.data.resp_code == 0) {
                  _this.imgPaths.push(res.data.datas); //将图片地址存到一个数组里面
                  let path = _this.imgPaths + ","; //将数组转换成字符串
                  let impaths = path.substring(0, path.lastIndexOf(",")); //去掉字符串后面添加的逗号
                  form.setFieldsValue({
                    imgPaths: impaths
                  });
                  this.$notification.success({ message: "图片添加成功" });
                }
              })
              .catch(error => {
                alert("添加失败：" + error);
              });
          } else {
            this.border = "1px solid red";
            this.$message.error("图片人脸不合规");
            this.$emit("checkImgIsQualify", false);
          }
        })
        .catch(error => {
          this.border = "1px solid red";
          this.$emit("checkImgIsQualify", false);
          alert("操作失败!");
        });
    },
    // 电话校验
    validatorPhone(event) {
      return formPattern.validatorPhone(event);
    },
    // 空格校验
    validatorWhiteSpace(event) {
      return formPattern.validatorWhiteSpace(event);
    }
  },
  watch: {
    visible(newValue, oldValue) {
      //弹窗打开时，初始化警告框
      if (newValue == true) {
        this.border = "0px";
      }
      // this.shopDept=this.deptOptions.filter(item=>item.isStore=="Y");
    },
    listfile(o, n) {
      this.listfile = o;
      console.log("****************", this.listfile);
      this.fileList = this.listfile;
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