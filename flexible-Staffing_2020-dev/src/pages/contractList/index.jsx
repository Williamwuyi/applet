import Taro, { Component } from "@tarojs/taro";
import { View } from "@tarojs/components";
import { AtTabBar } from "taro-ui";
import NavBar from 'taro-navigationbar'
import api from "../../service/api"
import { NoData } from "../../components/noData/index"
import {labor_contract} from "../../config/base"
import "./index.scss";

export default class ContractList extends Component {
  constructor() {
    super(...arguments);
    this.state = {
      // 合同数据列表
      contractArray:[],
      // 提示框
      toastText:'加载中...',
      showToast:false,
      toastStatus:'loading'

    };
  }

  componentWillMount() {
    this.getContracList()
  }

  componentDidMount() { }

  componentWillUnmount() { }

  componentDidShow() { }

  componentDidHide() { }

  config = {
    navigationStyle: 'custom'
  }

  closeToast(){
    this.setState({
        showToast:false
    })
  }

  // 获取合同列表数据
  getContracList(){
     // 显示加载框
     this.setState({
      showToast:true
    })
    api.get(labor_contract, { page: 1, limit: 10}).then(res => {
          console.log('订单', res.data.page)
          let length = res.data.page.list.length
          let list = res.data.page.list
          if (length != 0) {
              // 如果获取到的数组长度不为零,有数据,不展示[noData]组件
              this.setState({
              contractArray: list,
              showToast: false
          })
      } else {
          this.setState({
              showNoData: true,
              showToast: false
          })
      }
  })
  }

  render() {

    return (
      <View>
          <NavBar title='合同列表' back ></NavBar>
          <View className='bg-gray'><NoData ></NoData></View>

          <AtToast hasMask text={this.state.toastText} status={this.state.toastStatus} onClose={this.closeToast.bind(this)}  isOpened={this.state.showToast}></AtToast>
      </View>
    );
  }
}
