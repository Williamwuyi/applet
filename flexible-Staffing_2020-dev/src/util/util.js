import Taro from '@tarojs/taro'
// 获取页面胶囊的位置
function getCapsulePosition (){
    Taro.getSystemInfo({}).then(res =>{
        Taro.$navBarMarginTop = res.statusBarHeight || 0
        // console.log('输出系统打印的数据',res.statusBarHeight)
    })
};
/**
 * [isTelPhone 判断手机格式是否正确]
 *
 * @param   {String}    str      [手机号]
 * @returns {Boolean}            [手机号格式正确返回true，错误返回false]
 */
const isTelPhone = (str) => {
    let reg = /^(1[3456789]\d{9})$/;
    return reg.test(str);
  }
/**
 * [logError 请求接口报错处理]
 *
 * @param   
 * @returns 
 */
const logError = (name,info)=>{
    console.log('错误信息', name,info)
}
// 存入人员信息到缓存中
const setLaborIntoStorage = (labor)=>{
    let laborInfo = {...labor}
    Taro.setStorageSync('labor',laborInfo)  
}
// 判断缓存中有没有labor.mobile值,没有则未注册
const hasMobile = () =>{
    let laborInfo = Taro.getStorageSync('labor')
    let mobile = laborInfo.mobile
    return isTelPhone(mobile)
}
// 判断缓存中有没有身份证
const hasID = () => {
    let laborInfo = Taro.getStorageSync('labor')
    let paper_number = laborInfo.paper_number
    if (paper_number)return true
    else{return false}
    
}

module.exports = {
    getCapsulePosition,
    isTelPhone,
    logError,
    setLaborIntoStorage,
    hasMobile,
    hasID,
}