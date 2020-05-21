/* eslint-disable import/prefer-default-export */
// ******************
// 请求接口的基本配置
// base http请求接口
// base = "http://114.116.174.5:8080"
// "http://192.168.20.105:99"
// *******************
export const base = "http://192.168.20.105:99"
// export const base = "http://114.116.174.5:8080"
export const register_sms = "/app/register/sms"     //src/pages/phoneNumLogin
export const register = "/app/register"             //src/pages/phoneNumLogin
// login微信小程序登录
// verify token登录
// login code登录
export const login_verify ="/app/login/verify"      //src/service
export const login = "/app/login"                   //src/service
// labor 劳务人员
// info 获取人员个人信息
// deit 人员表单提交
export const labor_info = "/app/labor/info"         //src/pages/personInfo || phoneNumLogin
export const labor_edit = "/app/labor/edit"         //src/pages/identifyCard ||personInfo
// pub 通用数据接口 
// pub_area:获取下级地区列表
// pub_dict:获取制定类型索引
export const pub_area = "/app/pub/area"
export const pub_dict = "/app/pub/dict"             //src/pages/personInfo 
// oder任务订单
// pub获取推荐订单
// list获取登录人员订单
// detail获取订单细节
export const order_pub="/app/order/pub"             //src/pages/home
export const order_list="/app/order/list"           //src/pages/home ||allTask
export const order_detail="/app/order/detail"        //src/pages/order  
export const order_confirm="/app/order/confirm"     //src/pages/allTask
// 实名注册
export const labor_identify ="/app/labor/identify/idCardVerify"
//电子合同
export const labor_contract ="/app/labor/contract/list"         //src/pages/acontractList
// 用工平台协议
// agreement_confirm 确认协议
// agreement 获取当前协议
export const agreement_confirm="/app/labor/agreement/confirm"       //  src/pages/agreement
export const agreement = "/app/labor/agreement"         //src/pages/agreement
// 
export const contract_sign = '/app/contract/sign'           //src/pages/contract || sign
export const contract_preview=base+'/app/contract/preview?filename='        //src/pages/contract
// 图片路径
// 身份证认证背景提示图片
// export const IdGuideImg=['id1.jpg','id2.jpg']
// 订单细节icon
// export const orderDetailIcon=['missionName.png','missionPay.png','workingTime.png','missionContent.png']
// 轮播照片
// export const SwiperList= ['swiper1.png','.swiper2.png','.swiper3.png']
