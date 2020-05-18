import Taro, { Component } from '@tarojs/taro'
import 'taro-ui/dist/style/index.scss' 
import Index from './pages/index'
const util = require('./util/util')

import api from "./service/api"

import './app.scss'

// 如果需要在 h5 环境中开启 React Devtools
// 取消以下注释：
// if (process.env.NODE_ENV !== 'production' && process.env.TARO_ENV === 'h5')  {
//   require('nerv-devtools')
// }

class App extends Component {

  componentDidMount () {
    util.getCapsulePosition()
    // console.log('login运行')
    // 登录之时就会将员工信息放入缓存
    api.login()
    // this.getPersonInfo()
  }

  componentDidShow () {}

  componentDidHide () {}

  componentDidCatchError () {}
// 获取人员信息
// getPersonInfo(){
//   api.get('http://192.168.20.105:99/app/labor/info',{},'application/x-www-form-urlencoded').then(res=>{
//     console.log('获取到个人信息',res)
//   }
// }

  config = {
    pages: [
      // 组件不可在此注册,最前面第一个路径地址，是启动时显示界面
      
      'pages/index/index',
      'pages/video/index',
      'pages/workOutput/index',
      'pages/agreement/index',
      'pages/identifyCard/index',
      'pages/phoneNumLogin/index',
      'pages/showPersonInfo/index',
      'pages/contractList/index',
      'pages/personInfo/index',
      'pages/sign/index',
      'pages/login/index',
      'pages/contract/index',
      'pages/order/index'
      
    ],
    window: {
      backgroundTextStyle: 'light',
      navigationBarBackgroundColor: '#fff',
      navigationBarTitleText: 'WeChat',
      navigationBarTextStyle: 'black',
      navigationStyle:'default'
    }
  }

  // 在 App 类中的 render() 函数没有实际作用
  // 请勿修改此函数
  render () {
    return (
      <Index />
    )
  }
}

Taro.render(<App />, document.getElementById('app'))
