import Taro, { Component } from "@tarojs/taro";
import { View} from "@tarojs/components";
import { AtButton } from "taro-ui";
import NavBar from 'taro-navigationbar'
import api from "../../service/api"
import { order_list, agreement_confirm } from "../../config/base"

const util = require("../../util/util")
import "./index.scss";

export default class video extends Component {
    constructor() {
        super(...arguments);
        this.state = {
            // 视频添加按钮是否显示
            showBtn:true,
            // 临时路径
            tempPath:[],

        }

    }
    componentWillMount() {
    }

    componentDidMount() {}

    componentWillUnmount() {}

    componentDidShow() {}

    componentDidHide() {}

    config = {
        navigationStyle: 'custom'
    };
    // 录制或者选择视频
    onTranscribe(){
        const self = this;
        Taro.chooseVideo({
            sourceType: ['album','camera'],//视频选择的来源  album从相册选择视频  camera使用相机拍摄视频
            maxDuration:60,        //拍摄视频最长拍摄时间60，单位秒
            camera: 'back',          //back为默认后置摄像头，front为前置摄像头
            success: function(res) {
                let path = res.tempFilePath
                console.log("视频添加路径："+path)
                self.setState({
                    tempPath:path,
                    showBtn:false
                })
                Taro.setStorageSync('videoPath',path);
            }
        })
    }
    // 上传视频
    onUpdateVideo(){
        let token = Taro.getStorageSync("token");
        let videoPath = Taro.getStorageSync("videoPath");
        console.log('上传视频地址：',videoPath)
        Taro.uploadFile({
            //   url: base+labor_identify,
            header: {
                'content-type': 'multipart/form-data',
                'Authorization': token
            },
            name: 'video',
            filePath: videoPath,
            // formData: {
                // 填入参数
                //     'contractId':orderId
                // },
                success: value => {
                    console.log('视频上传返回值value',value)
                    //视频上传完成后清理指定缓存视频路径
                    Taro.removeStorageSync('videoPath')
                    // 成功则跳回原来的页面
                    // Taro.navigateBack({
                        //   delta:1,
                        // })
                    }
                })
                console.log('上传中......')
    }

    render() {
        let showBtn = this.state.showBtn
        let tempPath = this.state.tempPath
        return (
            <View>
                {/* 返回及头部标题 */}
                 <NavBar id="navBar" title='活体认证' back></NavBar>
                 <View className='textHint'>视频录制或添加认证</View>
                 <View className='components-page box-shadow-blue width-400 padding-top-40'>
                    <View>
                    {showBtn
                        ?
                        <AtButton className='recVideo' onClick={this.onTranscribe.bind(this)}>录制视频</AtButton>
                        :
                        <Video
                        src={this.state.tempPath}
                        controls={true}
                        autoplay={false}
                        poster=''//初始视频默认图片
                        initialTime='0'
                        id='video'
                        loop={false}
                        muted={false}
                        />
                    }
                    </View>
                </View>
                    <AtButton className='updateVideo' onClick={this.onUpdateVideo.bind(this)}>上传视频</AtButton>
            </View>
        )
    }
}