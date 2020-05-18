import Taro, { Component } from "@tarojs/taro";
import { View, Text ,Image} from "@tarojs/components";
import { AtButton, AtList, AtListItem } from "taro-ui";
import api from "../../service/api";
import {base,contract_sign ,contract_preview} from "../../config/base";
import NavBar from 'taro-navigationbar';
import "./index.scss";

export default class Contract extends Component {
  constructor() {
    super(...arguments);
    this.state = {
      // 合同名称
      contractTitle:'测试用合同',
      // 合同图片地址
      contractContent:[],
      // 合同ID
      orderId:'',
      contractId:''
    };
  }
  componentWillMount() {
    this.getOrderId()
   }

  componentDidMount() {

   }

  componentWillUnmount() { }

  componentDidShow() { }

  componentDidHide() { }

  config = {
    navigationBarTitleText: "gxvashgvxhahg",
    navigationStyle: "custom"
  };
  getOrderId(){
    let id =this.$router.params.orderId
    console.log('id',id)
    this.setState({
      orderId:id
    })
    // 获取 agreement_confirm 当前的协议,携带参数{orderid:id}
    api.get(contract_sign,{orderId:id}).then(res=>{
      let contractId = res.data.contract.contract_id
      console.log('合同id1',contractId)
      let temp_file = res.data.contract.temp_file.split(',')
      console.log('数组',temp_file)
      this.setState({
        contractContent:temp_file,
        contractId:contractId
      })
      // for(let j = 0;j<temp_file.length;j++){
      //   api.get(contract_preview,{filename:temp_file[j]}).then(img=>{
      //     this.setState({
      //       contractContent:[...this.state.contractContent,img]
      //     })
      //   })

      // }
    })
  }
  // 去签名,携带订单ID/或者是合同id
  toSign(){
    console.log('去签订合同')
    let contractId = this.state.contractId
    Taro.navigateTo({
      url:`../../pages/sign/index?contractId=${contractId}`
    })
  }

  render() {
    let imgs = this.state.contractContent
    return (
      <View>
          <NavBar title='我的协议' back></NavBar>
    {/* <View className='at-article__h1 text-center'>{this.state.contractTitle}</View> */}
           {/* <View className='at-article__p'>
             {
              this.state.contractCode}
           </View> */}
          <View className='at-article__p'>
            { imgs.map((url)=>{
              // return <View key={url} style={{backgroundImage: `url(${contract_preview}+${url})`}} ></View>
              return <View key={url}> <Image mode='widthFix' className='contract-content' src={contract_preview + url}></Image></View>
            })}
          </View>
          
          <View><AtButton className='bg-blue' onClick={this.toSign.bind(this)}>签写姓名</AtButton></View>
      </View>
    );
  }
}
