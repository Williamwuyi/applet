<template>
  <div>
    <a-modal
      :visible="visible"
      title="新增会员信息"
      okText="确定"
      cancelText="取消"
      width="900px"
      :destroyOnClose="true"
      :maskClosable="false"
      @cancel="()=>{$emit('cancel')}"
      @ok="()=>{$emit('create')}"
    >
      <a-spin :spinning="mSpinning">
        <a-form :form="form">
          <a-row :gutter="30">
            <a-col :span="6">
              <a-form-item>
                <span>
                  <img :src="personimg" :style="{'width':'100%'}" />
                </span>
                <input type="hidden" v-decorator="['imgPaths']" />
              </a-form-item>
            </a-col>
            <a-col :span="18">
              <a-row>
                <a-col :span="12">
                  <a-form-item label="姓名" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-input
                      :maxLength="15"
                      v-decorator="['personName',{rules:[{required:true,message:'请输入姓名'}],getValueFromEvent:validatorWhiteSpace}]"
                    ></a-input>
                    <a-input type="hidden" v-decorator="['personId']"></a-input>
                    <a-input type="hidden" v-decorator="['status',{initialValue:2}]"></a-input>
                  </a-form-item>
                  <a-form-item label="电话" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-input
                      v-decorator="['personPhone',{rules:[{required:true,message:'请输入手机号'}],getValueFromEvent:validatorPhone}]"
                    ></a-input>
                  </a-form-item>
                  <a-form-item label="备注" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-textarea v-decorator="['remarks']"></a-textarea>
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="性别" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-select v-decorator="['personSex']">
                      <a-select-option :value="0">男</a-select-option>
                      <a-select-option :value="1">女</a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item label="年龄" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-input v-decorator="['personAge',{getValueFromEvent:validatorPhone}]"></a-input>
                  </a-form-item>
                  <a-form-item label="所属门店" :labelCol="{span:8}" :wrapperCol="{span:16}">
                    <a-select v-decorator="['deptUnikey']" disabled>
                      <a-select-option
                        v-for="(n,index) in deptOptions"
                        :key="index"
                        :value="n.deptUnikey"
                      >{{n.deptName}}</a-select-option>
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
  props: ["visible", "deptOptions", "personimg", "mSpinning"],
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  methods: {
    // 电话校验
    validatorPhone(event) {
      return formPattern.validatorPhone(event);
    },
    // 空格校验
    validatorWhiteSpace(event) {
      return formPattern.validatorWhiteSpace(event);
    }
  },
  watch: {}
};
</script>

<style scoped>
</style>