import Taro, { Component } from "@tarojs/taro";
import { View, Text } from "@tarojs/components";
import { AtIcon, AtButton, AtFloatLayout, AtToast } from "taro-ui";
import { hasMobile, hasID } from "../../util/util"
import PropTypes from 'prop-types'
import './index.scss'

// ****************************
// 接口说明
// buttonName 右下角按钮
// skeletonNavigate 按钮点击事件
// ****************************
export default class Skeleton extends Component {
  static propTypes = {
    buttonName: PropTypes.string,
    skeletonNavigate: PropTypes.func,
    // orderID: PropTypes.number
  }
  static defaultProps = {
    buttonName: '申请任务',
    // 调用方的点击按钮事件侦听
    onSkeletonNavigate: null,
    // 调用方传入订单号
    orderID: null,
    // 是否签署了合同
    hasSign: false,
    // 传入订单状态,用于通知订单详情页面组件的显示 normal,confirm,pended,active,finish
    orderStatus: 'normal',
    // 调用方传入标签号
    // tagName:'',
    // 调用方传入薪酬
    salary: '',
    // 截止日期
    deadline: '',
    // 开始日期
    starttime: '',
    // 订单内容
    orderContent: '',
    // 订单标题
    orderTitle: ''
  }
  constructor(props) {
    super(props)
    this.state = {
      // 是否有手机号码
      hasPhoneNumber: false,
      // 是否有身份证
      hasIdNumber: false,
      // 显示轻提示
      toastShow: false
    }
  }
  componentWillMount() {
    //先确定是否有手机号码和身份证号码,确定点击跳转的权限
    this.checkPhoneAndId()
    // 加载之前,使用orderID 来请求数据
    // 打印一下获取数据
    console.log('skeleton组件:orderID', this.props.orderID)
  }

  componentDidMount() { }

  componentWillUnmount() { }

  componentDidShow() { }

  componentDidHide() { }
  
  skeletonNavigate(e) {
    console.log('eee', e)
    // 阻止事件冒泡
    // e.stopPropagation()
    const orderID = this.props.orderID
    const hasSign = this.props.hasSign
    // 向调用本组件的页面传值,点击事件有调用方定义 订单号和该订单是否签署了合同
    this.props.onSkeletonNavigate(orderID, hasSign)
  }
  // 点击卡片直接跳转到详情页
  jumpToDetail() {
    // 显示加载状态
    this.setState({
      toastShow: true
    })
    console.log('跳转到', this.props.orderID, '状态', this.props.orderstatus)
    let id = this.props.orderID
    let status = this.props.orderstatus || 'normal'
    let hasPhone = this.state.hasPhoneNumber
    let hasId = this.state.hasIdNumber
    console.log('status', status)
    if (!hasPhone) {
      Taro.navigateTo({
        url: '../../pages/agreement/index',
      })
    } else if (!hasId) {
      Taro.navigateTo({
        url: '../../pages/identifyCard/index',
      })
    } else {
      Taro.navigateTo({
        url: `../../pages/order/index?id=${id}&status=${status}`,
      })
    }
  }
  // 获取缓存中的身份证信息和手机号码,判定点击权限
  checkPhoneAndId() {
    let hasPhoneNumber = hasMobile()
    let hasIdNumber = hasID()
    this.setState({
      hasPhoneNumber: hasPhoneNumber,
      hasIdNumber: hasIdNumber
    })
  }
// closeToast
closeToast(){
  this.setState({
    toastShow:false
  })
}
  render() {
    return (
      <View className='at-row at-row--wrap border-grey bg-white border-radius-10' orderID={this.props.orderID} >
        <View className='skeleton-tag'>
          {/* <Text className='color-orange padding-right-10'>10000</Text> 积分 */}
          {/* <AtTag name='tag1' size='small'  className='blue-tag margin-left-10'>{this.props.tagName}</AtTag> */}
        </View>
        <View className='title at-col at-col-4 padding-10 margin-left-10'>{this.props.orderTitle}
        </View>
        {/* aticon  */}
        <View className='icon at-col at-col-4 padding-10' onClick={this.jumpToDetail.bind(this)}>
          <AtIcon value='chevron-right' size='24' className='margin-left-10'></AtIcon>
        </View>
        <View className='salary padding-10 margin-left-10 color-red'><Text>{this.props.salary} </Text>/ 单</View>
        <View className='summary at-col at-col-4 padding-10 margin-left-10'>
          <View className='content  font-size-14 color-black'>{this.props.orderContent}
          </View>
          <View className='starttime  font-size-14 color-grey'>任务时间:{this.props.starttime}--{this.props.deadline}</View>
          {/* <View className='deadline  font-size-14 color-grey'>截止时间2020-03-30</View> */}
          <View className='skeleton-apply text-center'>
            <View className='button_style'><AtButton size='small' className='bg-blue-light width-400' taroKeys={this.props.orderID} onClick={this.skeletonNavigate.bind(this)}>{this.props.buttonName}</AtButton></View>
          </View>
        </View>
        {/* 轻提示 */}
        <AtToast hasMask isOpened={this.state.toastShow} duration={1000} text='跳转中' onClose={this.closeToast.bind(this)} status='loading' ></AtToast>
      </View>
    );
  }
}
