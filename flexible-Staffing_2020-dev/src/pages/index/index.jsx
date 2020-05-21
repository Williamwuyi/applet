import Taro, { Component } from "@tarojs/taro";
import { View } from "@tarojs/components";
import { AtTabBar} from "taro-ui";
import { hasMobile} from "../../util/util"
import NavBar from 'taro-navigationbar'
import "./index.scss";
import Home from "../home";
import AllTask from "../allTask"
import My from "../my";

export default class Index extends Component {
  constructor() {
    super(...arguments);
    this.state = {
      tabbarCurrent: 0,
      navTitleArray: ['首页', '任务', '我的']
    };
  }

  componentWillMount() { }

  componentDidMount() { }

  componentWillUnmount() { }

  componentDidShow() { }

  componentDidHide() { }
  tabbarClick(value) {
    // console.log('点击题跳转到我的', value)
    // 如果value为1,跳转到我的之前先判断缓存中mobile有没有值
    // tab点击只判定有没有手机号码,不校验是否存在身份证
    let has = hasMobile();
    // let hasId = hasID();
    if (!has) {
      // 没有手机号跳转到注册
      Taro.navigateTo({
        url: '../phoneNumLogin/index'
      })
    } else{
      this.setState({
        tabbarCurrent: value,
      })
      Taro.setNavigationBarTitle({
        title: this.state.navTitleArray[value]
      })
    }
    console.log(this.state.tabbarCurrent)
  }


  config = {
    navigationBarTitleText: "",
    navigationStyle: 'custom'
};
  render() {
    const current = this.state.tabbarCurrent
    return (
      <View>
        <NavBar title={this.state.navTitleArray[current]}></NavBar>
        {current == 0 ?
          <Home></Home> : ''
        }
        {current == 1 ?
          <AllTask></AllTask> : ''
        }
        {current == 2 ?
          <My></My> : ''
        }
        {/* 底部tabar栏 */}
        {/* 避免遮挡 */}
        {/* {current == 0 ?
           <AtDivider content='没有更多了' fontColor='#2d8cf0' lineColor='#2d8cf0' />
           :<View></View>
        } */}
        <View className='padding-bottom-200'></View>
        <AtTabBar
          tabList={[{ title: "首页", iconType: 'home' }, { title: "任务", iconType: 'folder' }, { title: "我的", iconType: 'user' }]}
          current={this.state.tabbarCurrent}
          onClick={this.tabbarClick.bind(this)}
          fixed
          fontSize='12'
        ></AtTabBar>
      </View>
    );
  }
}
