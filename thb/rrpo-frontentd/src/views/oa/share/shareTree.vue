<template>
  <div class="sildtree" ref="sildtree">
    <div>
      <span class="part_title">文件列表</span>
      <!-- 当部门树有节点时，隐藏新增 -->
      <a-button type="primary" size="small" @click="btnAdd" v-if="treeData.length==0">增加</a-button>
    </div>
    <a-directory-tree
      @rightClick="rightClick"
      :treeData="treeData"
      @select="onSelect"
      :selectedKeys="selectedKeys"
      :expandAction="false"
    ></a-directory-tree>
    <a-modal
      :maskClosable="false"
      :visible="addVisible"
      title="新增/修改部门"
      :afterClose="()=>isEdit=false"
      width="400px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="disAdd"
      @ok="addDept"
    >
      <a-form :form="form1">
        <a-form-item label="部门名称" :labelCol="{span:8}" :wrapperCol="{span:16}">
          <a-input v-decorator="['deptName',{getValueFromEvent:validatorWhiteSpace}]"></a-input>
        </a-form-item>
        <a-form-item label="上级部门" :labelCol="{span:8}" :wrapperCol="{span:16}">
          <a-input read-only v-decorator="['showparentname']"></a-input>
          <a-input type="hidden" v-decorator="['parentId']"></a-input>
          <a-input type="hidden" v-decorator="['deptId']"></a-input>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
export default {
  data () {
    return {
      treeData: [
        {
          key: '0',
          name: '公共文件夹',
          folder: '公共文件夹',
          value: '公共文件夹'
        },
        {
          key: '1',
          value: '公共文件夹',
          folder: '部门文件夹',
          children: [{
            key: '11',
            value: '公共文件夹',
            folder: '开发部门'
          },
          {
            key: '12',
            value: '公共文件夹',
            folder: '实施部门'
          },
          {
            key: '2',
            value: '公共文件夹',
            folder: '我的文件夹'
          }
          ]
        },
        {
          key: '2',
          value: '公共文件夹',
          folder: '市场部'
        }
      ],
      isshow: false,
      acStyle: {},
      deptkey: '', // 存当前节点的key——右键
      deptUnikey: '',
      depttitle: '', // 存当前节点的title——右键
      addVisible: false,
      // deptparentkey: "", //存父节点的key——右键
      deptparenttitle: '', // 存父节点的title——右键
      staffcount: '',
      selectedKeys: [], // 存当前节点的key——点击
      topId: '',
      importVisible: false,
      allshop: [],
      partShop: [],
      indeterminate: false,
      checkAll: false,
      checkedList: []
    }
  },
  beforeCreate () {
    this.form1 = this.$form.createForm(this)
  },
  // created () {
  //   this.getDeptTree()
  // },
  mounted () {
    document.addEventListener('click', e => {
      // 鼠标点击任意非树的位置，隐藏树操作
      // eslint-disable-next-line eqeqeq
      if (e.target.className != 'flag') {
        this.isshow = false
      }
    })
  },
  methods: {
    // 部门树鼠标右键,设置操作栏位置与显隐,deptkey、depttitle存储右键时的当前部门id和名称
    rightClick ({ event, node }) {
      let x0 = event.clientX
      let y0 = event.clientY
      let elem = this.$refs.sildtree
      let x1 =
          elem.offsetLeft +
          elem.offsetParent.offsetLeft +
          elem.offsetParent.offsetParent.offsetLeft +
          elem.offsetParent.offsetParent.offsetParent.offsetLeft
      let y1 =
          elem.offsetParent.offsetTop + elem.offsetParent.offsetParent.offsetTop
      let left = x0 - x1
      let top = y0 - y1
      this.acStyle = { left: left + 'px', top: top + 'px' }
      this.isshow = true
      this.deptkey = node.eventKey
      this.depttitle = node.title
      // this.deptparentkey = node.$parent.eventKey;
      this.deptparenttitle = node.$parent.title
    },
    // 关闭树右键菜单，弹出新增部门对话框
    showmodal () {
      this.isshow = false
      this.addVisible = true
      this.checkedList = []
      this.checkAll = false
      this.indeterminate = false
      // const form1 = this.form1
      this.partShop = this.allshop
      setTimeout(() => {
        // form1.setFieldsValue({
        //   parentId: this.deptkey,
        //   showparentname: this.depttitle
        // })
      }, 5)
    },
    // 新增部门
    addDept () {
    },
    disAdd () {
    },
    // 修改部门
    editDept () {
    },
    // 删除部门
    delDept () {
      this.isshow = false
      this.$confirm({
        title: '是否确定删除？',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk () {

        }
      })
    },
    // 点击部门，传递部门key，在人员表中显示当前部门的人员。
    onSelect (selectedKeys) {
      this.$emit('reloadStaffTable', selectedKeys[0])
      this.selectedKeys = selectedKeys
      this.$emit('clearSelectrows')
    },
    // 构建部门的多选按钮
    resetCheckbox (data) {
      if (data != null) {
        let x = []
        for (let n of data) {
          x.push({
            label: n.deptName,
            value: n.deptUnikey
          })
        }
        return x
      }
    },
    validatorWhiteSpace () {

    },
    // 全选按钮
    onCheckAllChange (e) {
      let xx = []
      for (let n of this.partShop) {
        xx.push(n.value)
      }
      Object.assign(this, {
        checkedList: e.target.checked ? xx : [],
        indeterminate: false,
        checkAll: e.target.checked
      })
    },
    // 编辑时门店id构造数据
    resetMdata (data) {
      // eslint-disable-next-line eqeqeq
      if (data == null || data == '') {
        return []
      } else {
        let a = data.slice(1, -1)
        let x = a.split(',')
        return x
      }
    },
    // 校验门店编号
    selvalidator (value) {
      let x = []
      for (let n of value) {
        x.push(n.replace(/[^A-Za-z0-9]+/g, ''))
      }
      return x
    }
  }
}
</script>

<style scoped>
  .sildtree {
    float: left;
    width: 300px;
    min-height: auto;
    /* overflow: auto; */
    padding: 10px 0;
    margin-left: 10px;
    border: 1px solid #e0e0e0;
    position: relative;
  }
  .sildtree > div:first-child {
    margin-bottom: 10px;
  }
  .sildtree .part_title {
    display: inline-block;
    width: 80px;
    height: 24px;
    line-height: 24px;
    text-align: center;
    font-size: 14px;
    border: 1px solid #999999;
    margin: 0 5px;
  }
  .deptclass {
    width: 160px;
    height: auto;
    list-style-type: none;
    padding: 5px 0;
    position: absolute;
    z-index: 999999;
    border: 1px solid #555;
    box-shadow: 0 0 10px #f5f5f5;
    background-color: white;
  }
  .deptclass li {
    margin: 0 5px;
    height: 35px;
    line-height: 35px;
    border-bottom: 1px dashed #ddd;
    cursor: pointer;
  }
  .deptclass li:hover {
    color: lightseagreen;
  }
  .ant-form-item {
    margin-bottom: 8px;
  }
</style>
