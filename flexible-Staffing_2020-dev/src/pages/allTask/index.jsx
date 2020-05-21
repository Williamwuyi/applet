import Taro, { Component } from "@tarojs/taro";
import { View} from "@tarojs/components";
import { AtButton, AtTabs, AtTabsPane ,AtToast,AtMessage} from "taro-ui";
import { NoData } from "../../components/noData/index"
import api from "../../service/api"
import { hasMobile, hasID } from "../../util/util"
import { order_list,order_confirm} from "../../config/base"
import Skeleton from '../../components/skeleton'

const util = require("../../util/util")
import "./index.scss";

export default class AllTask extends Component {
    constructor() {
        super(...arguments);
        this.state = {
            // 当前激活是 0-3 confirm ,pended,active,finish
            current: 0,
            // 订单数组
            orderArray: [],
            // 展示无数据组件,
            showNoData: false,
            // 显示加载状态
            showLoading: true,
            // 是否有手机号码
            hasPhoneNumber: false,
            // 是否有身份证
            hasIdNumber: false,
            // 提示框
            toastText:'加载中...',
            // 调用方传入订单号
            orderID: null,
            // 传入订单状态,用于通知订单详情页面组件的显示 normal,confirm,pended,active,finish
            orderStatus: 'normal',
            showToast:false,
            toastStatus:'loading'

        };
    }
    componentWillMount() {
        // 检验是否有手机和身份证
        this.checkPhoneAndId()
        // 获取订单
        this.getOrderInfo('confirm')
    }

    componentDidMount() {}
    componentWillUnmount() { }
    componentDidShow() {
        // console.log("页面重新出现")
        // 页面重新出现时,根据Curren值来确定获取对应订单
        let statusName = ['confirm', 'pended', 'active', 'finish']
        let current = this.state.current
        this.getOrderInfo(statusName[current])
    }
    componentDidHide() { }
    config = {
        navigationBarTitleText: "",
        navigationStyle: 'custom'
    };

    // 点击重置刷新数据
    handleClick(value) {
        this.setState({
            current: value,
            showLoading: true
        })
        // console.log('点击的table',value)
        // 点击的值不同,请求的name不同
        let statusName = ['confirm', 'pended', 'active', 'finish']
        this.getOrderInfo(statusName[value])
    }

    // 指派订单
    getOrderInfo(statusName) {
        // 显示加载框
        this.setState({
            showToast:true
        })
        api.get(order_list, { page: 1, limit: 10, status: statusName }).then(res => {
            // if (res.data.page.list.length == 0) {
            //     console.log('page加载为空：',res.data.page)
            // }else{
                console.log('订单', res.data.page)
                let length = res.data.page.list.length
                let list = res.data.page.list
                if (length != 0) {
                    // 如果获取到的数组长度不为零,有数据,不展示[noData]组件
                    this.setState({
                    orderArray: list,
                    showNoData: false,
                    showToast: false
                })
            } else {
                this.setState({
                    showNoData: true,
                    showToast: false
                })
            }
        // }
        })
    }
    // 确认，详情按钮事件
    onTheTask(arg1, arg2, e) {
        this.setState({
            showToast:true
        })
        console.log('接任务', arg1, arg2, e)
        let orderId = arg1
        let hasSign = arg2
        let hasPhone = this.state.hasPhoneNumber
        let hasId = this.state.hasIdNumber
        
        if (!hasPhone) {
            Taro.navigateTo({
                url: '../../pages/phoneNumLogin/index'
            })
        } else if (!hasId) {
            Taro.navigateTo({
                url: '../identifyCard/index'
            })
        } else if (!hasSign) {
            Taro.navigateTo({
                url: `../../pages/contract/index?orderId=${orderId}`
            })
        } else {
            if(this.state.current==0){
                // 确定当前订单,接收参数暂时不确定
                console.log('确定订单')
                api.get(order_confirm, { orderId: orderId }).then(res => {
                    console.log('确认订单：',res)
                    if(res.statusCode == 200){
                        this.handleClick(1);
                    }else{
                        this.handleClick(0);
                    }
                })
            }else if(this.state.current==1){    //当current为1时为待开始点击按钮
                console.log('待开始任务')
                let status='pended'
                Taro.navigateTo({
                    url: `../../pages/order/index?id=${orderId}&status=${status}`,
                  })
            }else if(this.state.current==2){
                console.log('进行中')
                Taro.navigateTo({
                    url: '../workOutput/index'
                  })
            }else if(this.state.current==3){
                console.log('已经完成任务')
                let status='finish'
                Taro.navigateTo({
                    url: `../../pages/order/index?id=${orderId}&status=${status}`,
                  })
            }
            
            
        }
    }
    // 查看详情
    pendedTheTask(){}
    activeTheTask(){}
    finishTheTask(){}
    // 获取缓存中的身份证信息和手机号码,判定点击权限
    checkPhoneAndId() {
        let hasPhoneNumber = hasMobile()
        let hasIdNumber = hasID()
        this.setState({
            hasPhoneNumber: hasPhoneNumber,
            hasIdNumber: hasIdNumber
        })
    }
    // closeToast
    closeToast(){
        this.setState({
            showToast:false
        })
    }
    render() {
        let showNoData = this.state.showNoData
        let orderArray = this.state.orderArray
        let showLoading = this.state.showLoading
        const tabList = [{ title: '待确认' }, { title: '待开始' }, { title: '进行中' }, { title: '已完成' }]
        return (
            <View className='tab-bg'>
                    <AtMessage />
                    <AtTabs current={this.state.current} tabList={tabList} onClick={this.handleClick.bind(this)}>
                    {/* 待确认，确认按钮confirm */}
                    <AtTabsPane className='padding-20' current={this.state.current} >
                            {/*加载框 */}
                            {showNoData ?
                                <NoData></NoData>
                                : orderArray.map((order) => {
                                    return <Skeleton buttonName='确认任务' key={order.order_id} orderID={order.order_id} salary={order.s_pay}
                                    starttime={order.start_time} deadline={order.end_time} orderContent={order.mission_name}
                                    orderTitle={order.order_name} onSkeletonNavigate={this.onTheTask.bind(this)}
                                    orderstatus='confirm' hasSign={order.sign_status}
                                    ></Skeleton>
                                    // 此处应该将是否有签署合同的状态量传进去
                                })
                            }
                    </AtTabsPane>
                    {/* 待开始，查看详情 pended*/}
                    <AtTabsPane current={this.state.current} index={1}>
                        {showNoData ?
                            <NoData></NoData>
                            : orderArray.map((order) => {
                                return <Skeleton buttonName='查看详情' key={order.order_id} orderID={order.order_id} salary={order.s_pay}
                                starttime={order.start_time} deadline={order.end_time} orderContent={order.mission_name}
                                    orderTitle={order.order_name} onSkeletonNavigate={this.onTheTask.bind(this)}
                                    orderstatus='pended' hasSign={order.sign_status}
                                ></Skeleton>
                            })
                        }
                    </AtTabsPane>
                    {/* 已进行，上传凭证 active*/}
                    <AtTabsPane current={this.state.current} index={2}>
                    <View className='class2'>
                        {showNoData ?
                            <NoData></NoData>
                            : orderArray.map((order) => {
                                return <Skeleton buttonName='上传凭证' key={order.order_id} orderID={order.order_id} salary={order.s_pay}
                                starttime={order.start_time} deadline={order.end_time} orderContent={order.mission_name}
                                orderTitle={order.order_name} onSkeletonNavigate={this.onTheTask.bind(this)}
                                orderstatus='active' hasSign={order.sign_status}
                                ></Skeleton>
                            })
                        }
                    </View>
                    </AtTabsPane>
                    {/* 完成进行，查看详情 finish*/}
                    <AtTabsPane current={this.state.current} index={3}>
                        {showNoData ?
                            <NoData></NoData>
                            : orderArray.map((order) => {
                                return <Skeleton buttonName='查看详情' key={order.order_id} orderID={order.order_id} salary={order.s_pay}
                                starttime={order.start_time} deadline={order.end_time} orderContent={order.mission_name}
                                    orderTitle={order.order_name} onSkeletonNavigate={this.onTheTask.bind(this)}
                                    orderstatus='finish' hasSign={order.sign_status}
                                ></Skeleton>
                                
                            })
                        }
                    </AtTabsPane>
                </AtTabs>
                <AtToast hasMask text={this.state.toastText} status={this.state.toastStatus} onClose={this.closeToast.bind(this)}  isOpened={this.state.showToast}></AtToast>
            </View >
        );
    }
}