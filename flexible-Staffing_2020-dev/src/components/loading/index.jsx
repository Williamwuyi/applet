import Taro, { Component } from "@tarojs/taro";
import { View, Text } from "@tarojs/components";
import { AtToast,AtTag, AtButton, AtList, AtListItem } from "taro-ui";
import './index.scss'




export default class Loading extends Component {
    static defaultProps = {
        showLoading:'false'
    }
    constructor(props){
        super(props)
        this.state= {
            text:'加载中...',
            hasMask:true
        }
    }
    componentWillMount() { }

    componentDidMount() {
     }

    componentWillUnmount() { 
    }

    componentDidShow() { }

    componentDidHide() { }
    changeText(){
        if(this.state.text =='加载中...'){
            this.setState({
                text:'加载中'
            })
        }else{
            let newtext  = this.state.text +'.'
            this.setState({
                text :newtext
            })
        }
        // console.log('111')
    }

    render() {
        return (
            <AtToast hasMask isOpened text={this.state.text} status='loading' hasMask={this.state.hasMask} duration={0} isOpened={this.props.showLoading}></AtToast>
        );
    }
}