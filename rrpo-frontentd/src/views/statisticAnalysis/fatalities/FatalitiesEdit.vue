<template>
  <a-drawer
    title="编辑"
    :maskClosable="false"
    width=45%
    placement="right"
    :closable="true"
    @close="onClose"
    :visible="fatalitiesEditVisiable"
    >
    <a-form :form="form">
      <a-row>
        <a-col :span="12">
          <a-form-item label='发生地市' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="citySChange" v-decorator="['cityCsId',{rules:[{required: true, message: '请选择地市'}]}]">
              <a-select-option v-for="(n,index) in cityS" :key="index" :value="n.deptId" >
                {{n.deptName}}
              </a-select-option>
            </a-select>
            <a-input hidden v-decorator="['accidentId']"/>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='发生县区' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['cityQxId',{rules:[{required: true, message: '请先选择市级，再选县区'}]}]">
              <a-select-option v-for="(n,index) in cityQ" :key="index" :value="n.deptId" >
                  {{n.deptName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="12">
          <a-form-item label='派出所' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-tree-select
              @change="handleCheck"
              @expand="handleExpand"
              :expandedKeys="expandedKeys"
              :replaceFields="replaceFields"
              :showSearch="false"
              :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
              :load-data="onLoadData"
              :treeData="deptTreeData"
              v-decorator="['policeId',{rules:[{required: true, message: '请选择派出所'}]}]"
            >
            </a-tree-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='线路' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['lineId',{rules:[{required: true, message: '请选择线路'}]}]">
              <a-select-option v-for="(n,index) in lineData" :key="index" :value="n.dictId">
                {{n.fieldName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='地点' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-input v-decorator="['address',{rules:[{required: true, message: '请先填写地点'}]}]"></a-input>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item  label='时间' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-date-picker show-time v-decorator="['date',{rules:[{required: true, message: '请选择时间'}]}]" @change="dateTime">
            </a-date-picker>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='车务段' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['trainId',{rules:[{required: true, message: '请选择车务段'}]}]">
              <a-select-option v-for="(n,index) in trainData" :key="index" :value="n.dictId">
                {{n.fieldName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='工务段' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['trackId',{rules:[{required: true, message: '请选择工务段'}]}]">
              <a-select-option v-for="(n,index) in trackData" :key="index" :value="n.dictId">
                {{n.fieldName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
        <a-form-item label='事故性质' :labelCol="{span:8}" :wrapperCol="{span:16}">
          <a-select @change="sgxzOnChange" v-decorator="['nature',{rules:[{required: true, message: '请选择事故性质'}]}]">>
            <a-select-option value="A">一般铁路事故</a-select-option>
            <a-select-option value="B">意外事故</a-select-option>
            <a-select-option value="C">自杀事故</a-select-option>
          </a-select>
        </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='事故情形' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="sgqxOnChange" v-decorator="['conditions',{rules:[{required: true, message: '请选择事故情形'}]}]">
              <a-select-option value="A">横穿铁路</a-select-option>
              <a-select-option value="B">沿铁路纵向行走</a-select-option>
              <a-select-option value="C">钻车底</a-select-option>
              <a-select-option value="D">线路上玩耍</a-select-option>
              <a-select-option value="E">其他情形</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='站内区间' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="znqjOnChange" v-decorator="['instationSection',{rules:[{required: true, message: '请选择站内区间'}]}]">
              <!--              <a-select-option v-for="(n,index) in sectionData" :key="index" :value="n.dictId">-->
              <!--                {{n.fieldName}}-->
              <!--              </a-select-option>-->
              <a-select-option value="A">站内</a-select-option>
              <a-select-option value="B">区间</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='护路模式' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="hlmsOnChange" v-decorator="['road',{rules:[{required: true, message: '请选择护路模式'}]}]">
              <!--            <a-select-option v-for="(n,index) in roadData" :key="index" :value="n.dictId">-->
              <!--              {{n.fieldName}}-->
              <!--            </a-select-option>-->
              <a-select-option value="A">专业护理区间</a-select-option>
              <a-select-option value="B">有奖义务护理区间</a-select-option>
              <a-select-option value="C">其他</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='年龄段' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="nldOnChange" v-decorator="['age',{rules:[{required: true, message: '请选择年龄段'}]}]">
              <!--              <a-select-option v-for="(n,index) in ageData" :key="index" :value="n.dictId">-->
              <!--                {{n.fieldName}}-->
              <!--              </a-select-option>-->
              <a-select-option value="A">0-6岁</a-select-option>
              <a-select-option value="B">7-18岁</a-select-option>
              <a-select-option value="C">19-64岁</a-select-option>
              <a-select-option value="D">64岁以上</a-select-option>
              <a-select-option value="E">不详</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='身份判断' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="sfpdOnChange" v-decorator="['identity',{rules:[{required: true, message: '请选择身份'}]}]">
              <a-select-option value="A">中小学生</a-select-option>
              <a-select-option value="B">精神病患者</a-select-option>
              <a-select-option value="C">其他残障人员</a-select-option>
              <a-select-option value="D">其他</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='性别' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['sex',{rules:[{required: true, message: '请选择性别'}]}]">
              <a-select-option v-for="(n,index) in sexData" :key="index" :value="n.dictId">
                {{n.fieldName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='居住地' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="jzdOnChange" v-decorator="['jzd',{rules:[{required: true, message: '请选择居住地'}]}]">
              <a-select-option value="A">本村</a-select-option>
              <a-select-option value="B">本乡</a-select-option>
              <a-select-option value="C">本县</a-select-option>
              <a-select-option value="D">其他</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='封闭程度' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="fbcdOnChange" v-decorator="['closed',{rules:[{required: true, message: '请选择封闭程度'}]}]">
              <!--              <a-select-option v-for="(n,index) in closedData" :key="index" :value="n.dictId">-->
              <!--                {{n.fieldName}}-->
              <!--              </a-select-option>-->
              <a-select-option value="A">全封闭</a-select-option>
              <a-select-option value="B">因社会管理原因造成栅栏开口</a-select-option>
              <a-select-option value="C">因铁路原因造成栅栏开口</a-select-option>
              <a-select-option value="D">未封闭</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='离铁路远近' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="ltlyjOnChange" v-decorator="['distance',{rules:[{required: true, message: '请选择离铁路远近'}]}]">
              <a-select-option value="A">500米以内</a-select-option>
              <a-select-option value="B">500米到2.5公里</a-select-option>
              <a-select-option value="C">2.5公里以上</a-select-option>
              <a-select-option value="D">不详</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='电脑分析系数' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-input v-model="dnxs" :read-only="true"></a-input>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='电脑调节系数' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-input v-model="dntjxs" :read-only="true"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='公安分析系数' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="dntjxsOnChange" v-model="gaxsDefaultValue">
              <a-select-option v-for="(n,index) in gaxsData" :key="index" :value="n.id">
                {{n.id}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='换算伤亡' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-input v-model='deathToll' :read-only="true"></a-input>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="是否确认取消？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'FatalitiesEdit',
  props: {
    fatalitiesEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      policeIds: '',
      cityS: [], // 市级
      cityQ: [], // 县级
      // cityJ: [], // 街道
      sexData: [], // 性别
      lineData: [], // 线路
      trainData: [], // 车务段
      trackData: [], // 工务段
      natureData: [], // 事故性质
      sectionData: [], // 站内区间
      roadData: [], // 护路模式
      closedData: [], // 封闭程度
      ageData: [], // 年龄段
      dateTimes: '',
      deptTreeData: [], // 派出所
      expandedKeys: [],
      checkedKeys: [],
      replaceFields: {},
      dnxs: null,
      nldValue: '',
      fbcdValue: '',
      sgqxValue: '',
      ltlyjValue: '',
      sfpdValue: '',
      znqjValue: '',
      jzdValue: '',
      sgxzValue: '',
      hlmsValue: '',
      gaxsData: [],
      newData: '',
      gaxsDefaultValue: null,
      deathToll: null,
      formItemLayout,
      form: this.$form.createForm(this)
    }
  },
  mounted () {
    this.fetch()
    this.getDictionary()
  },
  computed: {
    dntjxs () {
      return this.dnxs !== null ? parseFloat(this.dnxs - 0.05).toFixed(2) : null
    }
  },
  methods: {
    // 加载市级
    fetch () {
      this.$get('/dept/list', {deptId: 0}).then(res => {
        this.cityS = res.data
      })
    },
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    // 取消
    onClose () {
      this.reset()
      this.form.resetFields()
      this.$emit('close')
    },
    // 选择地级市是，查找县级市
    citySChange (key) {
      this.$get('/dept/list', {deptId: key}).then(res => {
        this.cityQ = res.data
      })
      // 选中后清空，以下联级
      this.form.setFieldsValue({
        cityQxId: ''
      })
    },
    // 选择县级市，加载派出所（街道）
    // cityQonChange (key) {
    //   this.$get('/dept/list', {deptId: key}).then(res => {
    //     this.cityJ = res.data
    //   })
    // },
    dateTime (data, string) {
      this.dateTimes = string
    },
    // 获取数据字典对应的数据显示
    getDictionary () {
      // 性别
      this.$get('/dict/getListTable', {parentId: 'a6f622b8bb74725e811b053b3422c209', pageSize: 50}).then(res => {
        this.sexData = res.data.data.records
      })
      // 线路
      this.$get('/dict/getListTable', {parentId: '1867989d0aaaf82f79b34070cc77d766', pageSize: 50}).then(res => {
        this.lineData = res.data.data.records
      })
      this.$get('/dict/getListTable', {parentId: 'e635761320e42bb7174839f30b9d8408', pageSize: 50}).then(res => {
        this.trainData = res.data.data.records
      })
      this.$get('/dict/getListTable', {parentId: '521c7e776dbad0ee1fe118e9aee63121', pageSize: 50}).then(res => {
        this.trackData = res.data.data.records
      })
      // 事故性质
      this.$get('/dict/getListTable', {parentId: 'b730c43d0c0723636433a8f325650632', pageSize: 50}).then(res => {
        this.natureData = res.data.data.records
      })
      // 站内区间
      this.$get('/dict/getListTable', {parentId: '7db3ce75ef3dd8b92830c325a81e10b4', pageSize: 50}).then(res => {
        this.sectionData = res.data.data.records
      })
      // 护路模式
      this.$get('/dict/getListTable', {parentId: '3c539bb9577d64f75d5e2039268037ea', pageSize: 50}).then(res => {
        this.roadData = res.data.data.records
      })
      // 封闭程度
      this.$get('/dict/getListTable', {parentId: '96b76029ac74b1ad1c73642b6ddb9dab', pageSize: 50}).then(res => {
        this.closedData = res.data.data.records
      })
      // 年龄段
      this.$get('/dict/getListTable', {parentId: 'dc5b3760be9b7a9cd3497f38abc2fc12', pageSize: 50}).then(res => {
        this.ageData = res.data.data.records
      })
    },
    onLoadData (treeNode) {
      return new Promise(resolve => {
        if (treeNode.dataRef.children) {
          resolve()
          return
        }
        setTimeout(() => {
          this.$get('/dept/ListGA', {deptId: treeNode.dataRef.deptId}).then((r) => {
            treeNode.dataRef.children = r.data
            this.deptTreeData = [...this.deptTreeData]
            resolve()
          })
        }, 500)
      })
    },
    handleCheck (checkedKeys) {
      console.log('选中的派出所id ', checkedKeys)
      this.checkedKeys = checkedKeys
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    // 求系数
    onInterface () {
      const param = {
        age: this.nldValue,
        closed: this.fbcdValue,
        conditions: this.sgqxValue,
        distance: this.ltlyjValue,
        identity: this.sfpdValue,
        instationSection: this.znqjValue,
        jzd: this.jzdValue,
        nature: this.sgxzValue,
        road: this.hlmsValue
      }
      this.$post('/accident/accident/func', param).then(res => {
        let value = res.data.data
        console.log('系数数据：', res.data)
        switch (value) {
          case 'A':this.dnxs = 0.1
            break
          case 'B': this.dnxs = 0.2
            break
          case 'C': this.dnxs = 0.3
            break
          case 'D': this.dnxs = 0.4
            break
          case 'E': this.dnxs = 0.5
            break
          case 'F': this.dnxs = 0.6
            break
          case 'G': this.dnxs = 0.7
            break
          case 'H': this.dnxs = 0.8
            break
          case 'I': this.dnxs = 0.9
            break
          case 'J': this.dnxs = 1.0
            break
        }
      })
    },
    nldOnChange (value) {
      this.nldValue = value
      this.onInterface()
    },
    fbcdOnChange (value) {
      this.fbcdValue = value
      this.onInterface()
    },
    sgqxOnChange (value) {
      this.sgqxValue = value
      this.onInterface()
    },
    ltlyjOnChange (value) {
      this.ltlyjValue = value
      this.onInterface()
    },
    sfpdOnChange (value) {
      this.sfpdValue = value
      this.onInterface()
    },
    znqjOnChange (value) {
      this.znqjValue = value
      this.onInterface()
    },
    jzdOnChange (value) {
      this.jzdValue = value
      this.onInterface()
    },
    sgxzOnChange (value) {
      this.sgxzValue = value
      this.onInterface()
    },
    hlmsOnChange (value) {
      this.hlmsValue = value
      this.onInterface()
    },
    dntjxsOnChange (value) {
      this.deathToll = value
    },
    setFormValues (user) {
      console.log('编辑;', user)
      // 实现拉取表单数据并且赋值 areaId
      let fields = ['accidentId', 'date', 'cityCsId', 'cityQxId', 'policeId', 'lineId',
        'address', 'trainId', 'trackId', 'nature', 'conditions',
        'instationSection', 'road', 'closed', 'sex', 'age', 'identity', 'jzd', 'distance', 'dnxs', 'dntjxs', 'gaxs', 'deathToll']
      Object.keys(user).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
      setTimeout(() => {
        this.form.setFieldsValue({
          cityCsId: user.deptCs.deptId, // 市级
          cityQxId: user.deptQx.deptId, //  区县
          policeId: user.deptPolice.deptName, //  派出所
          lineId: user.dictXl.dictId, // 线路
          trainId: user.dictCwd.dictId, //  车务段
          trackId: user.dictGwd.dictId, //   工务段
          sex: user.sexDict.dictId // 性别
          // nature: user.dictNature.dictId, // 事故性质
          // instationSection: user.dictInstationSection.dictId, // 站区间
          // road: user.dictRoad.dictId, // 护路模式
          // closed: user.dictClosed.dictId, // 封闭程度
          // age: user.dictAge.dictId // 年龄段
        })
        this.policeIds = user.deptPolice.deptId
        this.dnxs = user.dnxs
        this.gaxsDefaultValue = user.gaxs
        this.deathToll = user.deathToll
        if (user.deptCs.deptId !== '') {
          this.$get('/dept/list', {deptId: user.deptCs.deptId}).then(res => {
            this.cityQ = res.data
          })
        }
      }, 5)
    },
    // 修改后保存
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          if (values.policeId === '3000' || values.policeId === '4000' || values.policeId === '5000') {
            this.$notification.warning({message: '系统提示', description: '请选择正确的派出所！', duration: 4})
            return
          }
          if (this.dateTimes !== '') {
            values.date = this.dateTimes
          }
          if (values.policeId === '') {
            values.policeId = this.policeIds
          }
          console.log('提交参数：', values)
          this.$post('/accident/accident/saveOrUpdate', values).then(res => {
            if (res.data.status === 1) {
              this.reset()
              this.$emit('success')
              this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
            } else {
              this.$notification.warning({message: '系统提示', description: '操作失败！', duration: 4})
              this.loading = false
              this.$emit('close')
            }
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  },
  watch: {
    fatalitiesEditVisiable () {
      this.loading = true
      if (this.fatalitiesEditVisiable) {
        // /area
        this.$get('/dept/ListGA', {rank: 4}).then((r) => {
          console.log('派出所：', r.data)
          this.replaceFields = { key: 'deptId', title: 'deptName', value: 'deptId' }
          this.deptTreeData = r.data
          this.loading = false
        })
      }
    },
    dntjxs (newData, old) {
      let arr = []
      let ab = newData * 100
      for (let i = 0; i <= 5; i++) {
        let id = (ab - i) / 100
        arr.push({id: id})
      }
      this.gaxsData = arr
      console.log('gaxs:', arr)
      this.gaxsDefaultValue = arr[1].id
      this.deathToll = this.gaxsDefaultValue
    }
  }
}
</script>
