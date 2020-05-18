import Taro, { Component } from "@tarojs/taro";
import { View, Picker, Text, Label, Input } from "@tarojs/components";
import { AtForm, AtInput, AtButton } from "taro-ui";
import {pub_dict ,labor_edit,labor_info,} from "../../config/base"
import {setLaborIntoStorage} from "../../util/util"
import NavBar from 'taro-navigationbar'


// import AreaPIcker from '../../components/areaPicker/index1'
import "./index.scss";

export default class ShowPersonInfo extends Component {
  constructor() {
    super(...arguments);
    this.state = {
        person:''
    };
  }
  componentWillMount() {
    // console.log('看数据',this.state.navBarMarginTop)
    this.getPeronInfo()
  }

  componentDidMount() {
  }

  componentWillUnmount() { }

  componentDidShow() { }

  componentDidHide() { }

  config = {
    navigationBarTitleText: "gxvashgvxhahg",
    navigationStyle: 'custom'
  };
  // 先从缓存里面获取到个人信息以及字典
  getPeronInfo() {
    const personInfo = Taro.getStorageSync('labor')
    this.setState({
      person: personInfo
    })

    console.log('从缓存获取到个人信息', personInfo)
  }



  render() {
      let realname= this.state.person.realname
      let paper_number =this.state.person.paper_number
      let age = this.state.person.age
      let sex = this.state.person.sex == 1?'男':'女'
      let mobile =this.state.person.mobile
      let politics_desc = this.state.person.politics_desc
      let nation_desc = this.state.person.nation_desc
      let edu_level_desc = this.state.person.edu_level_desc

    return (
      <View>
         <NavBar title='个人信息' back></NavBar>
          <View class='selector-border margin-10'><Text className='selector-title'>姓 名 :</Text>
            <View className='selector-content'>
                <View className='picker'>
                {realname}
                </View>
            </View>
          </View>
          <View class='selector-border margin-10'><Text className='selector-title'>身 份 证 :</Text>
            <View className='selector-content'>
                <View className='picker'>
                {paper_number}
                </View>
            </View>
          </View>
          <View class='selector-border margin-10'><Text className='selector-title'>年 龄 :</Text>
            <View className='selector-content'>
                <View className='picker'>
                {age}
                </View>
            </View>
          </View>
          <View class='selector-border margin-10'><Text className='selector-title'>性 别 :</Text>
            <View className='selector-content'>
                <View className='picker'>
                {sex}
                </View>
            </View>
          </View>
          <View class='selector-border margin-10'><Text className='selector-title'>手机号码</Text>
            <View className='selector-content'>
                <View className='picker'>
                {mobile}
                </View>
            </View>
          </View>
          <View class='selector-border margin-10'><Text className='selector-title'>政治面貌</Text>
            <View className='selector-content'>
                <View className='picker'>
                {politics_desc}
                </View>
            </View>
          </View>
          <View class='selector-border margin-10'><Text className='selector-title'>民 族</Text>
            <View className='selector-content'>
                <View className='picker' >
                 {nation_desc}
                </View>
            </View>
          </View>
          <View class='selector-border  selector-border-bottom margin-10'><Text className='selector-title'>教育程度</Text>
            <View className='selector-content'>
                <View className='picker'>
                 {edu_level_desc}
                </View>
            </View>
          </View>
        
      </View>
    );
  }
}
