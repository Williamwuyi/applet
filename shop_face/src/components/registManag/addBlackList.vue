<template>
  <a-modal
    :visible="visible"
    title="新增黑名单信息"
    okText="确定"
    cancelText="取消"
    width="600px"
    :destroyOnClose="true"
    :maskClosable="false"
    @cancel="()=>{$emit('cancel')}"
    @ok="()=>{$emit('create')}"
  >
    <a-spin :spinning="mSpinning">
      <a-form :form="form">
        <a-row :gutter="16">
          <a-col :span="7">
            <a-form-item>
              <span v-if="personimg!=''">
                <img :src="personimg" :style="{'width':'100%'}" />
              </span>
              <a-input
                type="hidden"
                v-decorator="['person.imgPaths',{rules:[{required:true,message:'图片不能为空'}]}]"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="17">
            <a-form-item label="姓名" :labelCol="{span:6}" :wrapperCol="{span:14}">
              <a-input
                :maxLength="15"
                v-decorator="['person.personName',{rules:[{required:true,message:'请输入姓名'}],getValueFromEvent:validatorWhiteSpace}]"
              ></a-input>
              <a-input type="hidden" v-decorator="['personId']"></a-input>
              <a-input type="hidden" v-decorator="['person.status',{initialValue:-1}]"></a-input>
              <a-input type="hidden" v-decorator="['type',{initialValue:0}]"></a-input>
            </a-form-item>
            <a-form-item label="性别" :labelCol="{span:6}" :wrapperCol="{span:14}">
              <a-select v-decorator="['person.personSex',{initialValue:0}]">
                <a-select-option :value="0">男</a-select-option>
                <a-select-option :value="1">女</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="备注" :labelCol="{span:6}" :wrapperCol="{span:14}">
              <a-textarea v-decorator="['remark']"></a-textarea>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import formPattern from "@/components/commons/formPattern.js";
export default {
  props: ["visible", "personimg", "mSpinning"],
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  methods: {
    // 空格校验
    validatorWhiteSpace(event) {
      return formPattern.validatorWhiteSpace(event);
    }
  }
};
</script>

<style scoped>
</style>