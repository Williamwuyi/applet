export default {
  title: {
    text: '事件统计',
    subtext: '统计数据',
    x: 'center'
  },
  tooltip: {
    trigger: 'item'
    // // formatter: function (params) {
    //   // console.log(params)
    //   // var res = params.data.name + '<br />'
    //   // console.log(res)
    //   // var myseries = this.series
    //   // console.log(myseries)
    //   // for (var i = 0; i < myseries.length; i++) {
    //   //   for (var k = 0; k < myseries[i].data.length; k++) {
    //   //     if (myseries[i].data[k].name === params.name) {
    //   //       res += myseries[i].name + ':' + myseries[i].data[k].value + '<br />'
    //   //     }
    //   //   }
    //   // }
    //   // return res
    // }
  },
  legend: {
    orient: 'vertical',
    x: 'left',
    data: ['事故统计', '事件统计']
  },
  dataRange: {
    // min: 0,
    // max: 4000,
    // x: 'left',
    // y: 'bottom',
    // text: ['max', 'min'],
    // realtime: false,
    // calculable: true,
    // inRange: {
    //   color: ['lightskyblue', 'yellow', 'orangered']
    // }
    x: 'left',
    y: 'bottom',
    splitList: [
      {start: 1500},
      {start: 900, end: 1500},
      {start: 310, end: 1000},
      {start: 200, end: 300},
      {start: 10, end: 200, label: '10 到 200（自定义label）'},
      {start: 5, end: 5, label: '5（自定义特殊颜色）', color: 'black'},
      {end: 10}
    ],
    color: ['#E0022B', '#E09107', '#A3E00B']
  },
  toolbox: {
    show: true,
    orient: 'vertical',
    x: 'right',
    y: 'center',
    feature: {
      mark: {show: true},
      dataView: {show: true, readOnly: false},
      restore: {show: true},
      saveAsImage: {show: true}
    }
  },
  roamController: {
    show: true,
    x: 'right',
    mapTypeControl: {
      'china': true
    }
  },
  series: [
    // {
    //   name: '事件类型',
    //   type: 'map',
    //   mapType: '湖南',
    //   roam: false,
    //   itemStyle: {
    //     normal: {label: {show: true}},
    //     emphasis: {label: {show: true}}
    //   },
    {
      name: '事故统计',
      type: 'map',
      mapType: '湖南',
      // 控制地图大小
      mapLocation: {
        x: '240',
        y: 'center',
        height: '75%'
      },
      roam: false,
      itemStyle: {
        normal: {
          label: {
            show: true,
            textStyle: {
              color: 'rgb(249, 249, 249)'
            }
          }
        },
        emphasis: {label: {show: true}}
      },
      data: [
        {name: '长沙市', value: 130},
        {name: '株洲市', value: 540},
        {name: '湘潭市', value: 731},
        {name: '衡阳市', value: 525},
        {name: '永州市', value: 940},
        {name: '常德市', value: 1340},
        {name: '岳阳市', value: 740},
        {name: '张家界市', value: 4353},
        {name: '益阳市', value: 5},
        {name: '怀化市', value: 508},
        {name: '邵阳市', value: 346},
        {name: '娄底市', value: 987},
        {name: '郴州市', value: 987},
        {name: '湘西土家族苗族自治州', value: 76}
      ]
    },
    {
      name: '事件统计',
      type: 'map',
      mapType: '湖南',
      // 控制地图大小
      mapLocation: {
        x: '240',
        y: 'center',
        height: '75%'
      },
      roam: false,
      itemStyle: {
        normal: {
          label: {
            show: true,
            textStyle: {
              color: 'rgb(249, 249, 249)'
            }
          }
        },
        emphasis: {label: {show: true}}
      },
      data: [
        {name: '长沙市', value: 60},
        {name: '株洲市', value: 40},
        {name: '湘潭市', value: 31},
        {name: '衡阳市', value: 125},
        {name: '永州市', value: 410},
        {name: '常德市', value: 90},
        {name: '岳阳市', value: 200},
        {name: '张家界市', value: 453},
        {name: '益阳市', value: 59},
        {name: '怀化市', value: 58},
        {name: '邵阳市', value: 34},
        {name: '娄底市', value: 7},
        {name: '郴州市', value: 987},
        {name: '湘西土家族苗族自治州', value: 76}
      ]
    }
  ]
}
