import Taro, { Component } from "@tarojs/taro";
import { View, Picker, Text, Label } from "@tarojs/components";
import { AtForm, AtInput, AtButton } from "taro-ui";
import NavBar from 'taro-navigationbar'
import "./index.scss";

export default class PersonInfo extends Component {
    constructor() {
        super(...arguments);
        this.state = {
        };
    }

    componentWillMount() {
    }

    componentDidMount() { }

    componentWillUnmount() { }

    componentDidShow() { }

    componentDidHide() { }

    config = {
        navigationBarTitleText: "gxvashgvxhahg",
        navigationStyle: 'custom'
    };
    getPhoneNumber(e){
        console.log('e',e.detail)
    }
  

    render() {
        return (
            <View>
                <NavBar title='登录' back background='#0081ff' color='#fff' iconTheme='white'></NavBar>
                <View className='login-header order-transparent text-center'>
                    <View className='login-tip font-size-18 color-white'>请选择您的登录方式,建议使用微信快速登录</View>
                </View>
                {/* 两种登录方式 */}
                <View className='margin-top-50 login-button' ><AtButton className='bg-blue' openType='getPhoneNumber' onGetPhoneNumber={this.getPhoneNumber}>微信快速授权/登录</AtButton></View>
                <View className='margin-top-10 login-button'><AtButton>输入手机号登录</AtButton></View>
            </View>
        );
    }
}
