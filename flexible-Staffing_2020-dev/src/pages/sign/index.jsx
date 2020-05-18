import Taro , { Component, Config } from '@tarojs/taro';
import { View, Button, Canvas } from '@tarojs/components';
import {AtToast} from "taro-ui"
import {contract_sign,base} from "../../config/base"
import './index.scss'

let ctx: any = Taro.createCanvasContext('canvas', this);
let startX = 0;
let startY = 0;
let canvasw = 0;
let canvash = 0;

export default class Sign extends Component<any, any> {

  config: Config = {
    navigationBarTitleText: '签名板'
  }
  state = {
    isPaint: false,
    // tempFilePath: '',
    contractId:'',
    toastMessage:'提交中',
    showToast:false,
    toastStatus:''
  }
  componentWillMount() {
    // console.log('合同ID',this.$router.params.contractId)
    let contractId = this.$router.params.contractId
    this.setState({
      contractId:contractId
    })
   }

  initCanvas() {
    ctx = Taro.createCanvasContext('canvas', this);
    ctx.setStrokeStyle('#000000');
    ctx.setLineWidth(4);
    ctx.setLineCap('round');
    ctx.setLineJoin('round');
  }
  canvasStart(e) {
    startX = e.changedTouches[0].x
    startY =  e.changedTouches[0].y
    ctx.beginPath()
  }
  canvasMove(e) {
    if (startX != 0) {
      this.setState({
        isPaint: true
      })
    }
    let x = e.changedTouches[0].x
    let y =  e.changedTouches[0].y
    ctx.moveTo(startX, startY)
    ctx.lineTo(x, y)
    ctx.stroke();
    ctx.draw(true)
    startX = x
    startY = y
  }
  canvasEnd(e) {
    console.log('结束')
  }
  // 取消
  clearDraw() {
    startX = 0;
    startY = 0;
    ctx.clearRect(0, 0, canvasw, canvash);
    ctx.draw(true);
    this.setState({
      isPaint: false,
      tempFilePath: ''
    })
  }

  createImg() {
    //点击提交事件,显示轻通知
    this.setState({
      showToast:true,
      toastMessage:'正在提交',
      toastStatus:'loading'
    })


    let contractId = this.state.contractId
    if (!this.state.isPaint){
      Taro.showToast({
       title: '签名内容不能为空！',
       icon: 'none'
      });
      return false;
     };
    // 生成图片 
    Taro.canvasToTempFilePath({
      canvasId: 'canvas',
      success: res => {
        console.log(res.tempFilePath)
        this.setState({
          tempFilePath: res.tempFilePath
        })
        // 上传图片,成功之后返回到订单页面,并且页面会重新刷新
        this.uploadSign(res.tempFilePath,contractId)
      },
      fail(err) {
        console.log(err)
      }
    })
  }
  // 上传图片
  uploadSign(imgUrl,contractId){
    console.log('sign_contractId',contractId)
    let token = Taro.getStorageSync("token");
    Taro.uploadFile({
      url: base+contract_sign,
      header: {
        'content-type': 'multipart/form-data',
        'Authorization': token
      },
      name: 'sign',
      filePath: imgUrl,
      formData: {
        // 填入参数
        'contractId':contractId
      },
      success: value => {
        console.log('value',value)
        // Taro.navigateTo({
        //   url:'../../pages/index/index'
        // })
        let page = Taro.getCurrentPages().length;
        Taro.navigateBack({
          delta:page
        });
        // console.log("page:",page)
        // console.log("page******:",page3)
        // 成功则跳回原来的页面
        // Taro.redirectTo({url:'../../index/index'});
      }
  })
  }
  // 获取 canvas 的尺寸（宽高）
  getCanvasSize () {
    const query = Taro.createSelectorQuery()
    query.select('#canvas').boundingClientRect(function(res){
      canvasw = res.width
      canvash = res.height
    })
    query.exec()
  }
  // 重新签名
  afreshDraw() {
    this.setState({
      canSign: true
    })
  }

  componentDidMount () {
    this.getCanvasSize()
    this.initCanvas()
  } 
  componentWillUnmount () {
    ctx = null
  } 
  // closeToast
  closeToast(){
    this.setState({
      showToast:false
    })
  }
  render() {
    return (
      <View className="signature">
        <View className="canvas-box">
          <Canvas 
            id="canvas" 
            canvasId="canvas" 
            className="canvas" 
            disableScroll={true}  
            onTouchStart={this.canvasStart.bind(this)} 
            onTouchMove={this.canvasMove.bind(this)} 
            onTouchEnd={this.canvasEnd.bind(this)} 
            onTouchCancel={this.canvasEnd.bind(this)} 
            width="100%;" 
            height="345Px">
          </Canvas>
        </View>

        <View className="layout-flex buttons">
          <Button className="cancel" onClick={this.clearDraw}>清除</Button>
          <Button className="confirm" onClick={this.createImg.bind(this)}>提交</Button>
        </View>

        {/* <View>图片路径：</View>
        <View className="word-break">{this.state.tempFilePath}</View> */}
        <AtToast hasMask isOpened={this.state.showToast} hasMask text={this.state.toastMessage} onClose={this.closeToast.bind(this)} status={this.state.toastStatus}></AtToast>
      </View>
    );
  }
}
