import Taro, { Component } from "@tarojs/taro";
import { View, Text } from "@tarojs/components";
import { AtTag, AtButton, AtList, AtListItem } from "taro-ui";
import './index.scss'




export default class OrderCard extends Component {
    static defaultProps = {
        isTaskDesc: false,
        isTaskReq: false,
        title: '',
        thumb: '',
        desc: '',
        requirementsTitle: '',
        requirementsContent: ''
    }
    componentWillMount() { }

    componentDidMount() { }

    componentWillUnmount() { }

    componentDidShow() { }

    componentDidHide() { }

    render() {
        const taskReqTitle = [...this.props.requirementsTitle]
        const taskReqContent = [...this.props.requirementsContent]
        return (
            <View >
                <AtList>
                    <AtListItem title={this.props.title} thumb={this.props.thumb}></AtListItem>
                </AtList>
                {/* 是否为描述 */}
                {this.props.isTaskDesc && <View className='at-article__p minheight-150 padding-20'>{this.props.desc}</View>}
                {/* 是否为要求 */}
                {this.props.isTaskReq &&
                    <View className='at-row minheight-150'>
                        <View className='at-col at-col-1 at-col--auto padding-20'>
                            {taskReqTitle.map((item) => {
                                return <View key={item}>{item}:</View>
                            })}
                        </View>
                        <View className='at-col margin-left-20 padding-20'>
                            {taskReqContent.map((item) => {
                                return <View key={item}>{item}</View>
                            })}
                        </View>
                    </View>
                }
            </View>

        );
    }
}
