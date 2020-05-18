import Taro, { Component } from "@tarojs/taro";
import { View, Text } from "@tarojs/components";
import { AtTag, AtButton, AtList, AtListItem } from "taro-ui";
import './index.scss'




export default class NoData extends Component {
    static defaultProps = {

    }
    componentWillMount() { }

    componentDidMount() { }

    componentWillUnmount() { }

    componentDidShow() { }

    componentDidHide() { }

    render() {
        return (
            <View className='nodata_style'>
                {/* <View className='nodata_pic'></View> */}
                <Text className='nodata_title'>暂无任务信息</Text>
            </View>

        );
    }
}
