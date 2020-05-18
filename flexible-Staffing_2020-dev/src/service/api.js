// *******************
// 封装基本的请求发送方法
// 对外暴露接口get post
// 参数 url 传输的数据,请求头,content-type
//
//
// *********************
// basOption方法
import Taro from "@tarojs/taro";
import { HTTP_STATUS } from "../config/HTTP_STATUS";
import { base ,login_verify,login} from "../config/base";
import { logError ,setLaborIntoStorage} from "../util/util";

export default {
  baseOption(params, method = "GET") {
    let { url, data } = params;
    // 校验token是否存在,是否有效
    const token = Taro.getStorageSync("token");
    // console.log("token", token);
    // if (!token)login()
    // *********************
    let contentType = "application/x-www-form-urlencoded";
    contentType = params.contentType || contentType;
    const option = {
      isShowLoading: false,
      url: base + url,
      // url:   url,
      data: data,
      method: method,
      header: {
        "content-type": contentType,
        // 预留token
        'Authorization': token
      },
      success(res) {
        // if (res.statusCode === HTTP_STATUS.NOT_FOUND) {
        //   return logError("api", "请求资源不存在");
        // } else if (res.statusCode === HTTP_STATUS.BAD_GATEWAY) {
        //   return logError("api", "服务端出现了问题");
        // } else if (res.statusCode === HTTP_STATUS.FORBIDDEN) {
        //   return logError("api", "没有访问权限");
        // } else if (res.statusCode === HTTP_STATUS.SUCCESS) {
        //   return res.data;
        // }
        return res.data;
      },
      error(e) {
        logError("api", e);
      }
    };
    return Taro.request(option);
  },
  // get方法
  get(url, data = "") {
    let option = {
      url,
      data
    };
    return this.baseOption(option);
  },
  // post 方法
  post(url, data, contentType) {
    let params = {
      url,
      data,
      contentType
    };
    return this.baseOption(params, "POST");
  },
  // login方法
  login() {
    // 获取缓存中的token值,有则使用verify,没有使用login
    let token = Taro.getStorageSync("token");
    console.log("有", token);
    if (token) {
      Taro.request({
        url: base+login_verify,
        // data: {
        //   token: token
        // },
        header: {
          "content-type": "application/x-www-form-urlencoded",
           // 预留token
          'Authorization': token
        },
        method: "POST"
      }).then(res => {
        // console.log("使用token登录结果",res)
        let code = res.data.code;
        let laborInfo = res.data.labor
        if (code === 0) {
          console.log("登录成功");
        // 将劳务人员信息存入缓存中
        setLaborIntoStorage(laborInfo)
        } else {
          console.log("登录失败");
          Taro.removeStorageSync("token");
          this.login();
        }
      });
    } else {
      // console.log('token失效,使用login进行登录')
      Taro.login({
        success(res) {
          // console.log('获取code的值',res.code)
          let code = res.code;
          Taro.request({
            url: base+login,
            data: { code: code },
            method: "POST",
            header: { "content-type": "application/x-www-form-urlencoded" }
          }).then(value => {
            console.log("重新获取到的login的信息", value);
            //将token值放入到缓存中  
            let new_token = value.data.token;
            let laborInfo = value.data.labor
            Taro.setStorageSync('token',new_token);
            // 将劳务人员信息放入缓存中
            setLaborIntoStorage(laborInfo)
            // const newone = Taro.getStorageSync({
            //     key:'token'
            // });
            // console.log("已经重新写入", newone);
          });
        }
      });
    }
  },
  // 获取城市信息请求
};

