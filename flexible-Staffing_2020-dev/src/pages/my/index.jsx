import Taro, { Component } from "@tarojs/taro";
import { View, OpenData } from "@tarojs/components";
import { AtAvatar, AtList, AtListItem, AtButton } from "taro-ui";
import {hasID} from "../../util/util"
import "./index.scss";
import api from "../../service/api"

export default class My extends Component {
  constructor() {
    super(...arguments);
    this.state = {
      // 身份证是否已经注册
      hasId:false,
      // 我的身份证
      idText:'去认证',
      // 箭头方向
      idArrow:'right'

    };
  }
  componentWillMount() {
    this.checkedId()
   }

  componentDidMount() { }

  componentWillUnmount() { }

  componentDidShow() { }

  componentDidHide() { }
  // 获取身份证信息
  checkedId(){
    let hasId = hasID()
    let idText  = ''
    let idArrow = 'right'
    if(hasId){
       idText = '已认证',
       idArrow =''
    }else{
       idText='去认证',
       idArrow = 'right'
    }
    console.log('是否有身份证号',hasId)
    this.setState({
      hasId:hasId,
      idText:idText,
      idArrow:idArrow
    })
  }
  // 跳转到具体页面
  navigateTo(name){
    let url = null
    switch(name){
      case 'task':{
         url= '../../pages/contractList/index';
        break;
      }
      case 'id':{
        if(this.state.hasId){
          url='';
        }else{
          url='../../pages/identifyCard/index';
        }
        break;
      }
      case 'personInfo':{
        if(this.state.hasId){
          url='../../pages/showPersonInfo/index';
        }else{
          url='../../pages/identifyCard/index';
        }
        // url='../../pages/identifyCard/index';
        // url='../../pages/showPersonInfo/index';
        break;
      }
      case 'agreement':{
        url='../agreement/index';
        break;
      }
      case 'video':{
        url='../../pages/video/index';
        break;
      }
      default:
         url = null;
         break;
    }
    if(url){
      Taro.navigateTo({
        url:url,
      })
    }
  }
  // 重新登录按钮
  reLogin(){
    // 重新登录
    api.login()
    Taro.navigateTo({
      url:'../../pages/index/index'
    })
  }

  render() {
    return (
      <View>
        {/* 头像部分 */}
        <View className='border-transparent personal-header text-center'>
          {/* <AtAvatar
            className='header-icon'
            image="http://storage.360buyimg.com/mtd/home/32443566_635798770100444_2113947400891531264_n1533825816008.jpg"
            size='large'
            circle
          ></AtAvatar> */}
          {/* 调用开放 */}
          <View className='width-height-400  header-icon circle'> <OpenData  type='userAvatarUrl' /></View>
          <View className='font-size-18 color-lightgrey'><OpenData  type='userNickName' /></View>
          {/* <View className='font-size-14 color-lightgrey margin-top-10'>181****1211</View> */}
        </View>
        {/* 个人数值 */}
        {/* <View className='at-row at-row--wrap margin-tb-20'>
          <View className='at-col at-col-4 text-center'>余额(元)</View>
          <View className='at-col at-col-4 text-center'>积分</View>
          <View className='at-col at-col-4 text-center'>红包(个)</View>
          <View className='at-col at-col-4 text-center color-orange'>100</View>
          <View className='at-col at-col-4 text-center color-orange'>2000</View>
          <View className='at-col at-col-4 text-center color-orange'>0</View>
        </View> */}
        {/* 列表 */}
        <View>
          <AtList>
            <AtListItem
              title='个人资料'
              // extraText='详细信息'
              extraText={this.state.idText}
              arrow='right'
              // arrow={this.state.idArrow}  //箭头
              iconInfo={{ size: 25, color: "#78A4FA", value: "calendar" }}
              onClick={this.navigateTo.bind(this,'personInfo')}
            />
            <AtListItem
              title='我的合同'
              extraText='合同信息'
              arrow='right'
              iconInfo={{ size: 25, color: "#FF4949", value: "bookmark" }}
              onClick={this.navigateTo.bind(this,'task')}
            />
            <AtListItem
              title='我的协议'
              extraText='协议信息'
              arrow='right'
              iconInfo={{ size: 25, color: "#82C272", value: "folder" }}
              onClick={this.navigateTo.bind(this,'agreement')}
            />
            {/* <View data-way='123'  onClick={this.navigateTo.bind(this)}>123</View> */}
            {/* <AtListItem
              title='我的身份证'
              extraText={this.state.idText}
              arrow={this.state.idArrow}
              iconInfo={{ size: 25, color: "#D87B7B", value: "money" }}
              onClick={this.navigateTo.bind(this,'id')}
            />  */}
          </AtList>
          <AtListItem
              title='活体认证'
              extraText='去认证'
              arrow='right'
              iconInfo={{ size: 25, color: "#82C272", value: "money" }}
              onClick={this.navigateTo.bind(this,'video')}
            />
        </View>
        {/* <View className=' width-200 margin-top-50'><AtButton className='blue-solid-tag' onClick={this.reLogin.bind(this)}>重新登录</AtButton></View> */}
        {/* <AtButton openType='getUserInfo' onGetUserInfo={this.getUserInfo}>huo</AtButton> */}
      </View>
    );
  }
}
