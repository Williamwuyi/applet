import Taro, { Component } from './node_modules/@tarojs/taro'
import { View, PickerView, PickerViewColumn } from './node_modules/@tarojs/components'
import PropTypes from './node_modules/prop-types'
import api from '../../service/api'

import './index.scss'

class AddressPicker extends Component {
    static defaultProps={
        pickerShow:true
    }
 static   state = {
        value: [0, 0, 0],
        // 省
        provinces: [],
        // 市
        citys: [],
        // 区
        areas: [],
        // 组合起来的城市信息
        areaInfo: '',
    }
   
    componentWillMount(){
        // this.getCityArray()
        // 初始化加载,先获取省市区数据
        this.getProvinces()
        this.getCitys('0001')
        this.getAreas('0002')
    }

    cityChange (e) {
        let that = this
        // console.log('打印一下e的值',e)
        // 选择器有三列,value值记录在[0,1,2]
        const pickerValue = e.detail.value
        const { provinces, citys, value } = this.state
        const provinceNum = pickerValue[0]
        const cityNum = pickerValue[1]
        const countyNum = pickerValue[2]
        // 如果省份选择项和之前不一样，表示滑动了省份，此时市默认是省的第一组数据，
        if (value[0] != provinceNum) {
            // console.log('state里面存储的省份',value[0])
            // console.log('当前选择的省份',provinceNum)
            const area_code = provinces[provinceNum].area_code
            // console.log('省的区域码',area_code)
            that.getCitys(area_code)
            let city = that.state.citys[0].area_code
            console.log('city',city)
            that.getAreas(city)
            this.setState({
                value: [provinceNum, 0, 0],
            })
        } else if (value[1] != cityNum) {
            // 滑动选择了第二项数据，即市，此时区显示省市对应的第一组数据
            const area_code = citys[cityNum].area_code
            // console.log('市的area_code',area_code)
            that.getAreas(area_code)
            this.setState({
                value: [provinceNum, cityNum, 0],
            })
        } else {
            // 滑动选择了区
            this.setState({
                value: [provinceNum, cityNum, countyNum]
            })
        }
        console.log('选择出来的数据是',that.state.value)
    }

    //  params true代表传递地址，false不传递
    handlePickerShow (params:Boolean) {
        // params: boolean
        if (params ) {
            // console.log(this.props)
            const { provinces, citys, areas, value } = this.state
            // 将选择的城市信息显示到输入框
            const tempAreaInfo = provinces[value[0]].area_name + '' + citys[value[1]].area_name+ areas[value[2]].area_name
            // + '' + citys[value[1]].area_name + areas[value[2]].area_name
            console.log('城市信息',tempAreaInfo)
            this.setState({
                areaInfo: tempAreaInfo
            }, () => {
                this.props.onHandleToggleShow(this.state.areaInfo)
            })
        }
    }

    // 获取地区数据
    getCityArray(){
        api.get('http://192.168.20.105:99/app/pub/area',{parent_id:'0000'}).then(res=>{
            console.log('获取到省的信息',res)
        })
        api.get('http://192.168.20.105:99/app/pub/area',{parent_id:'0001'}).then(res=>{
            console.log('获取到市的信息',res)
        })
        api.get('http://192.168.20.105:99/app/pub/area',{parent_id:'0002'}).then(res=>{
            console.log('获取到区的信息',res)
        })
    }
    // 获取省份数据 传递参数是parent_id,获取上一级的数组填入他们的area_id,最终提交的市area_id
    getProvinces(){
        api.get('http://192.168.20.105:99/app/pub/area',{}).then(res=>{
            let  area = res.data.areas
            console.log('获取到省的信息',res.data.areas)
            // 获取到的信息传入省的数组
            this.setState({
                provinces:area
            })
        })
    }
    // 获取市数据,传入省份的area_id 
    getCitys(area_id){
        api.get('http://192.168.20.105:99/app/pub/area',{parent_id:area_id}).then(res=>{
            let  area = res.data.areas
            console.log('获取到市的信息',res.data.areas)
            // 获取到的信息传入市的数组
            this.setState({
                citys:area
            })
        })
    }
    // 获取区数据,传入市的area_id
    getAreas(area_id){
        api.get('http://192.168.20.105:99/app/pub/area',{parent_id:area_id}).then(res=>{
            let  area = res.data.areas
            console.log('获取到区的信息',res.data.areas)
            // 获取到的信息传入区的数组
            this.setState({
                areas:area
            })
        })
    }

    render () {
        const { provinces, citys, areas, value } = this.state
        const { pickerShow } = this.props
        return (
            <View className={pickerShow? 'address-picker-container show': 'address-picker-container'} onClick={this.handlePickerShow.bind(this,true)}>
                <View className="picker-content">
                    <View className="dialog-header">
                        <View className="dialog-button cancel" onClick={this.handlePickerShow.bind(this,false)}>取消</View>
                        <View className="dialog-title">请选择省市区</View>
                        <View className="dialog-button" onClick={this.handlePickerShow.bind(this,true)}>确定</View>
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
        )
    }
}

AddressPicker.propTypes = {
    // pickerShow: PropTypes.bool.isRequired,
    // onHandleToggleShow: PropTypes.func.isRequired,
}

export default AddressPicker