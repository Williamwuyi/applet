import Taro, { Component } from "@tarojs/taro";
import { View, Text, Input, Button } from "@tarojs/components";
import { AtButton, AtInput, AtModal, AtModalHeader, AtModalContent, AtModalAction, AtCheckbox,AtToast } from "taro-ui";
import NavBar from 'taro-navigationbar'
import { agreement, agreement_confirm } from "../../config/base"
import { setLaborIntoStorage } from "../../util/util"
import api from '../../service/api'

const util = require("../../util/util")
import "./index.scss";

export default class Agreement extends Component {
    constructor() {
        super(...arguments);
        this.state = {
            agreementContent: '',
            agreementTitle: '',
            checkedList: [' '],
            readContract: false,
            showMessage:false,
            buttonClickMessage:'',
            status:''

        };
        this.checkboxOption = [{
            value: 'list1',
            label: '我已阅读并同意协议内容',
            // desc: '我已阅读并同意协议内容'
        }]
    }
    componentWillMount() {
        this.getAgreement()

    }

    componentDidMount() { }

    componentWillUnmount() { }

    componentDidShow() { }

    componentDidHide() { }
    phoneNumberLogin() { }

    config = {
        navigationBarTitleText: "gxvashgvxhahg",
        navigationStyle: 'custom'
    };
    // 获取协议内容
    getAgreement() {
        api.get(agreement).then(res => {
            let agreementTitle = res.data.agreement.title
            let agreementContent = res.data.agreement.html.replace(/\s+/g, "").replace(/<[^>]*>|<\/[^>]*>/gm, ' ').split(' ')
            // replace(/<[^>]*>|<\/[^>]*>/gm,'')
            // replace(/<p>/g,'<view>').replace(/<\/p>/g,'</view>')
            // console.log('协议', agreementContent)
            this.setState({
                agreementContent: agreementContent,
                agreementTitle: agreementTitle
            })
        })
    }
    handleChange(value) {
        this.setState({
            checkedList: value,
            readContract: !this.state.readContract
        })
    }
    //   去绑定手机号码
    toBindPhoneNumber() {
        // 点击触发加载弹窗
        this.setState({
            showMessage:true,
            status:'loading',
            buttonClickMessage:'正在跳转'
        })
        api.get(agreement_confirm).then(res => {
            console.log('同意协议', res)
            let message = res.data.msg
            if (res.data.code == 0) {
                // 跳转
                Taro.navigateTo({
                    url: '../phoneNumLogin/index'
                })
            }else{
                // 弹窗内容更改
                this.setState({
                    status:'',
                    showMessage:true,
                    buttonClickMessage:message
                })

            }
        })

    }
    render() {
        let agreementContent = this.state.agreementContent
        let agreementTitle = this.state.agreementTitle
        let showMessage =this.state.showMessage
        let buttonClickMessage=this.state.buttonClickMessage
        let status = this.state.status
        return (
            <View>
                <NavBar title='确认协议' back></NavBar>
                <View className='at-article__h1 text-center'>{agreementTitle}</View>
                {agreementContent.map((item, index) => {
                    return <View key={index} className='at-article__p'>{item}</View>
                })
                }
                <AtCheckbox
                    options={this.checkboxOption}
                    selectedList={this.state.checkedList}
                    onChange={this.handleChange.bind(this)}
                />
                <AtToast hasMask isOpened={showMessage} text={buttonClickMessage} status={status}></AtToast>
                <AtButton className='bg-blue' disabled={!this.state.readContract} onClick={this.toBindPhoneNumber.bind(this)}>去绑定手机号码</AtButton>
            </View>
        );
    }
}