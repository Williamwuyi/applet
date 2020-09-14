export default{
  // 校验电话||年龄————表单
  validatorPhone:function(event){
    return event.target.value.replace(/[^0-9]+/,'');
  },
  // 校验空格————表单
  validatorWhiteSpace:function(event){
    return event.target.value.replace(/\s+/,'');
  },
  // 校验邮箱————表单
  validatorEmial:function(event){
    return event.target.value.replace(/[^A-Za-z0-9@\.]+/,'');
  },
  // 校验http的IP地址————表单
  validatorIP:function(event){
    return event.target.value.replace(/[^(http)0-9@\.\/:]+/,'');
  }
}