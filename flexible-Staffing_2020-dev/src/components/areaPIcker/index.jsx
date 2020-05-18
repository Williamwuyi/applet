import Taro, { Component } from "@tarojs/taro";
import { View, PickerView, PickerViewColumn } from "@tarojs/components";
import { AtTag, AtButton } from "taro-ui";
import { pub_area } from "../../config/base"
import PropTypes from 'prop-types'
import api from "../../service/api"
import './index.scss'

// ****************************

// ****************************
export default class AreaPicker extends Component {
    static propTypes = {
        //   是否显示地址选择器组件,boolean
        pickerShow: PropTypes.bool
    }
    static defaultProps = {
        //   
        pickerShow: false,
        // ex,now,期望工作地址还是现居地
        place:''
    }

    constructor(props) {
        super(props)
        this.state = {
            //默认值
            value: [0, 0, 0],
            // 省
            provinces: [],
            // 市
            citys: [],
            // 区
            areas: [],

        }
    }

    componentWillMount() {
        // 初始化加载,先获取省市区数据

    }

    componentDidMount() { 
        this.getProvinces()
        this.getCitys(1)
    }

    componentWillUnmount() { }

    componentDidShow() { }

    componentDidHide() { }
    // 获取省份数据 传递参数是parent_id,获取上一级的数组填入他们的area_id,最终提交的市area_id
    getProvinces() {
        api.get(pub_area, {}).then(res => {
            let area = res.data.areas
            console.log('获取到省的信息', res.data.areas)
            // 获取到的信息传入省的数组
            this.setState({
                provinces: area
            })
        })
    }
    // 获取市数据,传入省份的area_id 
    getCitys(area_id) {
        api.get(pub_area, { parentId: area_id }).then(res => {
            let area = res.data.areas
            console.log('获取到市的信息', res.data.areas)
            const areas = area[0].area_id
            // 获取到的信息传入市的数组
            this.setState({
                citys: area
            })
            this.getAreas(areas)
        })
    }
    // 获取区数据,传入市的area_id
    getAreas(area_id) {
        api.get(pub_area, { parentId: area_id }).then(res => {
            let area = res.data.areas
            // console.log('获取到区的信息', res.data.areas)
            // 获取到的信息传入区的数组
            this.setState({
                areas: area
            })
        })
    }
    // 获取数据
    // 点击框体事件
    // props需要由调用方更改,返回监听函数,更改pickerShow为false
    handlePickerShow(params) {
        console.log("点击事件打印的是", params)
        // params为false,不传值,为true传值
        if (params) {
            const { provinces, citys, areas, value } = this.state
            // 将选择的城市信息显示到输入框 2020429tag
            const tempAreaInfo = provinces[value[0]].area_name + '' + citys[value[1]].area_name+ areas[value[2]].area_name
            const tempCodeInfo = areas[value[2]].area_id
            console.log('点击之后value',tempAreaInfo)
            Taro.eventCenter.trigger('hidePicker', tempAreaInfo,tempCodeInfo,this.props.place)

        } else {
            // console.log("不传值");
            Taro.eventCenter.trigger('hidePicker', 'hide','hide','hide')
        }
        // 调用监听事件,取消显示
    }
    // onchange事件
    cityChange(e) {
        const pickerValue = e.detail.value
        const { provinces, citys, value } = this.state
        //准备和保存中的数据作对比
        const provinceNum = pickerValue[0]
        const cityNum = pickerValue[1]
        const countyNum = pickerValue[2]
        // 当前选择和state保存的省份不一致,表示滑动了省份,此时市默认市省的第一组数据
        if (value[0] != provinceNum) {
            // console.log('value',value[0])
            const area_id = provinces[provinceNum].area_id
            console.log('area_code',area_id)
            this.getCitys(area_id)
            this.setState({
                value: [provinceNum, 0, 0]
            })
        } else if (value[1] != cityNum) {
            // 滑动选择了第二项数据，即市，此时区显示省市对应的第一组数据
            const area_id = citys[cityNum].area_id
            console.log('市的area_code',area_id)
            this.getAreas(area_id)
            this.setState({
                value: [provinceNum, cityNum, 0],
            })
        } else {
            // 滑动选择了区
            this.setState({
                value: [provinceNum, cityNum, countyNum]
            })
        }
        console.log('选择出来的数据是',this.state.value)
    }
    // onchange事件
    render() {
        const { provinces, citys, areas, value } = this.state
        const { pickerShow } = this.props
        return (
            <View className={pickerShow ? 'address-picker-container show' : 'address-picker-container'}>
                <View className="picker-content">
                    <View className="dialog-header">
                        <View className="dialog-button cancel" onClick={this.handlePickerShow.bind(this, false)}>取消</View>
                        <View className="dialog-title">请选择省市区</View>
                        <View className="dialog-button" onClick={this.handlePickerShow.bind(this, true)} >确定</View>
                    </View>
                    <PickerView onChange={this.cityChange} value={value} className='picker-view-wrap'>
                        <PickerViewColumn>
                            {
                                provinces.map((province, index) => {
                                    return <View className="picker-item" key={index}>{province.area_name}</View>
                                })
                            }
                        </PickerViewColumn>
                        <PickerViewColumn>
                            {
                                citys.map((city, index) => {
                                    return <View className="picker-item" key={index}>{city.area_name}</View>
                                })
                            }
                        </PickerViewColumn>
                        <PickerViewColumn>
                            {
                                areas.map((area, index) => {
                                    return <View className="picker-item" key={index}>{area.area_name}</View>
                                })
                            }
                        </PickerViewColumn>
                    </PickerView>
                </View>
            </View>
        );
    }
}
