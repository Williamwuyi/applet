<template>
  <div>
    <a-modal
      :visible="visible"
      title="新增/修改人员信息"
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
          <a-row :gutter="30">
            <a-col :span="6">
              <a-form-item :style="{border,borderRadius:'5px',transition:'all 0.3s'}">
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
                <input
                  type="hidden"
                  v-decorator="['person.imgPaths',{rules:[{required:true,message:'图片不能为空'}]}]"
                />
              </a-form-item>
            </a-col>
            <a-col :span="18">
              <a-row>
                <a-col :span="12">
                  <a-form-item label="姓名" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-input
                      :maxLength="15"
                      v-decorator="['person.personName',{rules:[{required:true,message:'请输入姓名'}],getValueFromEvent:validatorWhiteSpace}]"
                    ></a-input>
                    <a-input type="hidden" v-decorator="['personId']"></a-input>
                    <a-input type="hidden" v-decorator="['person.status']"></a-input>
                    <a-input type="hidden" v-decorator="['person.personId']"></a-input>
                  </a-form-item>
                  <a-form-item label="部门" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-select v-decorator="['deptId',{rules:[{required:true,message:'请选择部门'}]}]">
                      <a-select-option
                        v-for="(n,index) in deptOptions"
                        :key="index"
                        :value="n.deptId"
                      >{{n.deptName}}</a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item label="邮箱" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-input
                      :maxLength="50"
                      v-decorator="['person.personEmail',{getValueFromEvent:validatorEmial}]"
                    ></a-input>
                  </a-form-item>
                  <a-form-item label="备注" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-textarea v-decorator="['person.remarks']"></a-textarea>
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="性别" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-select v-decorator="['person.personSex',{initialValue:0}]">
                      <a-select-option :value="0">男</a-select-option>
                      <a-select-option :value="1">女</a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item label="联系电话" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-input
                      :maxLength="11"
                      v-decorator="['person.personPhone',{getValueFromEvent:validatorPhone}]"
                    ></a-input>
                  </a-form-item>
                  <a-form-item label="职务" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-select v-decorator="['person.positionId']">
                      <a-select-option value="0">店长</a-select-option>
                      <a-select-option value="1">店员</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
              </a-row>
            </a-col>
          </a-row>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import formPattern from "@/components/commons/formPattern.js";
export default {
  props: [
    "visible",
    "deptOptions",
    "personimg",
    "showimg",
    "imgIsQualify",
    "spinning"
  ],
  data() {
    return {
      border: "0px"
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  methods: {
    getFile(event) {
      if (event.target.files.length == 0) {
        //判断文件操作，如果点击选择文件后又取消操作，则不执行
        return;
      }
      let file = event.target.files[0];
      this.$emit("getbaseimgurl", file);
      let formData = new FormData();
      formData.append("file", file);
      this.border = "0px";
      this.$emit("checkImgIsQualify", true); //初始设置图片的人脸检测状态为true
      axios
        .post(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/hardware/arcfaceCheck",
          formData
        )
        .then(res => {
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
                this.$emit("handelpostimg", res.data.datas);
                this.$notification.success({ message: "图片添加成功" });
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
    },
    validatorEmial(event) {
      return formPattern.validatorEmial(event);
    }
  },
  watch: {
    visible(newValue, oldValue) {
      //弹窗打开时，初始化警告框
      if (newValue == true) {
        this.border = "0px";
      }
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