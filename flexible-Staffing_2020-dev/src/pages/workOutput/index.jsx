import Taro, { Component, Events } from "@tarojs/taro";
import { View, Text, Input, Button, Image } from "@tarojs/components";
import { AtButton, AtTextarea, AtImagePicker,AtToast } from "taro-ui";
import { labor_edit } from "../../config/base"
import api from "../../service/api"
import NavBar from "taro-navigationbar"

const util = require("../../util/util")
import "./index.scss";

export default class WorkOutput extends Component {
    constructor() {
        super(...arguments);
        this.state = {
            // 凭证描述
            descContent: '',
            // 图片缓存路径
            files: [],
            // 视频缓存路径
            fileVideo:[],
            // 图片添加按钮显示
            showAddBtn:true,
            // 展示无数据组件,
            showNoData: true,
            // 轻提示
            toastMessage:'',
            toastStatus:'',
            showToast:false,
            // orderId订单号
            orderId:''
        };
    }
    componentWillMount() {
       this.setState({
        orderId:this.$router.params.orderId
       })
    }
    componentDidMount() {
    }
    componentWillUnmount() { }
    componentDidShow() { }
    componentDidHide() { }
    // 上传凭证页面应该接受一个订单号
    config = {
        navigationBarTitleText: "gxvashgvxhahg",
        navigationStyle: 'custom'
    };
    ChangeDescContent(value) {
        this.setState({
            descContent: value
        })
    }
    
      onFail (mes) {
        console.log(mes)
      }
      onImageClick (index, file) {
        console.log(index, file)
      }

    
    //   toast自动关闭
    closeToast(){
        this.setState({
            showToast:false
        })
    }
    // 上传视频
    UpdateVideo(fileVideo,orderId){
        let token = Taro.getStorageSync("token");
        console.log('上传视频地址：',fileVideo)

        //视频上传完成后清理指定缓存视频路径
        Taro.removeStorageSync('fileVideo')


        Taro.uploadFile({
            //   url: base+labor_identify,
            header: {
                'content-type': 'multipart/form-data',
            'Authorization': token
        },
        name: 'video',
        filePath: fileVideo,
        formData: {
            // 填入参数
            'contractId':orderId
        },
        success: value => {
            console.log('视频上传返回值value',value)
            //视频上传完成后清理指定缓存视频路径
            Taro.removeStorageSync('fileVideo')
             // 成功则跳回原来的页面
            // Taro.navigateBack({
            //   delta:1,
            // })
          }
        })
        
    }
    // 添加视频
    onVideo(){
        const self = this;
        Taro.chooseVideo({
            sourceType: ['album','camera'],//,'camera' 视频选择的来源camera:打开摄像头拍摄，album:从相册选取
            maxDuration: 15,
            camera: 'back',
            success(res) {
            let file=res.tempFilePath
              console.log('获取视频文件地址',res.tempFilePath)
              self.setState({
                fileVideo:file,
                showNoData: false
              })
              Taro.setStorageSync('fileVideo',file);
            }
        })
        console.log('添加视频')
    }
    // 图片添加
    onChange (files) {
        this.setState({
          files:files
        })
        console.log('files',files)
        if(files.length>=5){
            this.setState({
                showAddBtn:false
            })
        }else{
            this.setState({
                showAddBtn:true
            })
        }
      }
    // 上传图片和其他相关细细  上传凭证 未完工
    uploadStuff(imgUrl,orderId){
        let token = Taro.getStorageSync("token");
        console.log('查看图片地址',imgUrl)
        Taro.uploadFile({
        //   url: base+labor_identify,
          header: {
            'content-type': 'multipart/form-data',
            'Authorization': token
          },
          name: 'images',
          filePath: imgUrl,
          formData: {
            // 填入参数
            'contractId':orderId
          },
          success: value => {
            console.log('图片上传返回value',value)
            // 成功则跳回原来的页面
            Taro.navigateBack({
              delta:1,
            })
          }
        })
    }
      //   提交按钮
      submit(){
        // 确定图片上传,补充内容填写
        let orderId = this.state.orderId
        let file = this.state.files//图片文件路径
        let fileVideo = Taro.getStorageSync("fileVideo");
        console.log(typeof fileVideo);//查验数据类型
        console.log('视频地址filevideo:',fileVideo)
        let descContent = this.state.descContent
        let fileurl = file[0].url
        if(fileVideo == null ||fileVideo==''){
            this.setState({
                showToast:true,
                toastStatus:'error',
                toastMessage:'未提交视频凭证'
            })
        }else if(file.length == 0){
            // console.log('文件长度为0')
            this.setState({
                showToast:true,
                toastStatus:'error',
                toastMessage:'请上传图片凭证'
            })
        }else if(descContent.length == 0){
            this.setState({
                showToast:true,
                toastStatus:'error',
                toastMessage:'请填写说明'
            })
        }else{
            this.setState({
                showToast:true,
                toastStatus:'loading',
                toastMessage:'提交中...'
            })
            console.log('图片文件',fileurl)
            // 视频图片接口
            this.UpdateVideo(fileVideo,orderId)
            // 上传图片接口
            this.uploadStuff(fileurl,orderId)
            // 成功则跳回原来的页面
            Taro.navigateBack({
                delta:1,
              })
        }
      }


    render() {
        let showToast = this.state.showToast
        let showNoData = this.state.showNoData
        return (
            <View>
                <NavBar title='上传凭证' back></NavBar>
                <AtToast hasMask isOpened={showToast} duration={1000}  onClose={this.closeToast.bind(this)} text={this.state.toastMessage} status={this.state.toastStatus}></AtToast>
                <View className='padding-20 at-article__p'>上传您的工作凭证</View>
                {/* 身份证上传 */}
                <View className='box-shadow-blue width-400 padding-top-40'>
                    <View className='imagePicker'>
                   {/* 添加图片凭证 */}
                    <AtImagePicker
                        count={5}
                        length={5}  
                        showAddBtn={this.state.showAddBtn} //是否显示添加图片按钮               
                        files={this.state.files}
                        onChange={this.onChange.bind(this)}
                        onFail={this.onFail.bind(this)}
                        onImageClick={this.onImageClick.bind(this)}
                    />
                    </View>
                    <View className='imagePicker'>
                        {/* 添加视频按钮 */}
                        {showNoData 
                        ?
                            <AtButton 
                            className='recVideo' 
                            onClick={this.onVideo.bind(this)}
                            fileVideo={this.state.fileVideo}>视频添加</AtButton>
                        :
                            <Video className='videoBf'
                                src={this.state.fileVideo}
                                controls={true}
                                autoplay={false}
                                poster=''//视频默认未播放前的图片
                                initialTime='0'
                                id='video'
                                loop={false}
                                muted={false}
                            />
                        }
                    </View>
                    <View className='padding-10 margin-top-50'>
                        <AtTextarea
                            value={this.state.descContent}
                            onChange={this.ChangeDescContent.bind(this)}
                            maxLength={200}
                            placeholder='描述'
                        />
                    </View>
                </View>
                
                <AtButton className='bg-blue submit_button' onClick={this.submit.bind(this)} >提交凭证</AtButton>
            </View>
        );
    }
}