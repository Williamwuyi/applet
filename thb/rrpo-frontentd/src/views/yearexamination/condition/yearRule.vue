<template>
  <div style="width: 100%">
    <!-- 表格区域 -->
    <div style="min-height: 500px">
      <a-button type="primary" style="margin-bottom: 5px" @click="handledd">
        添加
      </a-button>
<!--   列表   -->
      <a-list :grid="{ gutter: 16, column: 4 }" :data-source="data">
        <a-list-item slot="renderItem" slot-scope="item, index">
          <a-card hoverable style="width: 300px">
            <a-card-meta :title="item.yearDate + '年度考核规则'">
              <a-avatar
                slot="avatar"
                src="static/img/tielu.jpg"
              />
            </a-card-meta>
           <div style="min-height: 86px">
             <hr style="margin: 15px 0;border:none;border-top:1px dashed #ccc;"/>
             <div style="text-align: center" class="htb">
               <div style="float: left;height: 70px" v-for="wach in item.list" :key="wach.name">
                 <p>{{wach.name}}</p>
                 <p style=""><span style="font-size: 22px">{{wach.count}} </span> 条</p>
               </div>
             </div>
           </div>
             <template slot="actions" class="ant-card-actions" style="margin-top: -10px">
               <a-tooltip placement="top">
                 <template slot="title">
                   <span>绑定或修改</span>
                 </template>
                 <a-icon  type="setting" theme="twoTone"  twoToneColor="#4a9ff5"  @click="handleAdd(item)" ></a-icon>
               </a-tooltip>
               <a-tooltip placement="top">
                 <template slot="title">
                   <span>导入</span>
                 </template>
                 <a-icon type="upload" @click="clickDr(item.yearId)" style="color: #CD853F" />
               </a-tooltip>
               <a-tooltip placement="top">
                 <template slot="title">
                   <span>导出模板</span>
                 </template>
                 <a-icon type="download" @click="clickEr(item.yearId)" style="color: #CDAD00"/>
               </a-tooltip>
               <a-tooltip placement="top">
                 <template slot="title">
                   <span>查看详细</span>
                 </template>
                 <a-icon type="eye"  @click="cardClick(item)" style="color: #52c41a" />
               </a-tooltip>
               <a-tooltip placement="top">
                 <template slot="title">
                   <span>删除</span>
                 </template>
                 <a-icon  type="delete"    @click="handleDliele(item)" style="margin:0 5px; color: red" ></a-icon>
               </a-tooltip>
             </template>
          </a-card>
        </a-list-item>
      </a-list>
    </div>
    <!--  绑定  -->
    <ruledd
      :yearCondVisiable="yearCondVisiable"
      @success="yearCondAdd"
      @close="yearClose"
      ref="bd"
    />
    <!--  新增  -->
    <year-rule-add
      :visible="visible"
      @sucusee="yearRulefAdd"
      @close="yearRuleClose"
    />
    <!--  查看详细  -->
    <year-rule-look
      :yearRulelookVisible="yearRulelookVisible"
      @success="handleEdit"
      @close="hanleeditclose"
      ref="yearlk"
    />
    <!--  文件选择  -->
    <moban
      :visible="VisibleClose"
      @cancel="chooseClose"
      @check="chooseCreat"
      ref="Dr"
    />
  </div>
</template>
<script>
import moban from './moban'
import haoer from './yearRuledited'
import yearRuleAdd from './yearRuleAdd'
import yearRuleLook from './yearRuleLook'
export default {
  name: 'yearRule',
  components: {yearRuleAdd, yearRuleLook, ruledd: haoer, moban},
  data () {
    return {
      data: [],
      yearCon: {},
      loading: false,
      sad: {id: 'menusId'},
      yearId: '',
      visible: false,
      // 新增
      yearCondVisiable: false,
      // 修改
      yearRulelookVisible: false,
      VisibleClose: false,
      // 表格
      dataSource: [],
      count: 2
    }
  },
  mounted () {
    this.fach()
  },
  methods: {
    // 渲染
    fach () {
      this.loading = true
      this.$get('/check/year/list').then(res => {
        // this.dataSource = res.data.data
        this.data = res.data.data
        this.loading = false
      })
    },
    // 重置
    reset () {
      // 重置列排序规则
      this.pagination.current = 1
      this.pagination.pageSize = 8
      this.fach()
    },
    // 删除
    handleDliele (item) {
      let that = this
      that.$confirm({
        title: `是否删除“${item.yearDate}”年度考核，一经删除该年的所有考核成绩都会被删除?`,
        centered: true,
        onOk () {
          that.$delete('/check/year/deleteByLists/' + item.yearId).then(() => {
            that.$message.success('删除成功')
            that.fach()
          })
        },
        onCancel () {
          that.fach()
        }
      })
    },
    // 添加功能
    handledd () {
      this.visible = true
    },
    yearRulefAdd () {
      this.visible = false
      this.fach()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    yearRuleClose () {
      this.visible = false
    },
    // 绑定功能
    handleAdd (item) {
      this.yearCondVisiable = true
      this.$refs.bd.getYear(item)
    },
    yearCondAdd () {
      this.yearCondVisiable = false
      this.fach()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    yearClose () {
      this.yearCondVisiable = false
    },
    // 查看详细
    cardClick (key) {
      this.yearRulelookVisible = true
      this.$refs.yearlk.getFach(key)
    },
    handleEdit () {
      this.yearRulelookVisible = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.fach()
    },
    hanleeditclose () {
      this.yearRulelookVisible = false
      this.fach()
    },
    // 导出模板
    clickEr (record) {
      this.$export('/check/menus-year/importTemplate', {yearId: record})
    },
    // 执行了导入操作
    clickDr (res) {
      this.VisibleClose = true
      this.$refs.Dr.getId(res)
    },
    chooseClose () {
      this.VisibleClose = false
    },
    chooseCreat () {
      this.VisibleClose = false
      this.fach()
    }
  }
}
</script>
<style lang="less" scoped>
  .editable-cell {
    position: relative;
  }

  .editable-cell-input-wrapper,
  .editable-cell-text-wrapper {
    padding-right: 24px;
  }

  .editable-cell-text-wrapper {
    padding: 5px 24px 5px 5px;
  }

  .editable-cell-icon,
  .editable-cell-icon-check {
    position: absolute;
    right: 0;
    width: 20px;
    cursor: pointer;
  }

  .editable-cell-icon {
    line-height: 18px;
    display: none;
  }

  .editable-cell-icon-check {
    line-height: 28px;
  }

  .editable-cell:hover .editable-cell-icon {
    display: inline-block;
  }

  .editable-cell-icon:hover,
  .editable-cell-icon-check:hover {
    color: #108ee9;
  }

  .editable-add-btn {
    margin-bottom: 8px;
  }
  .cardList {
    margin-left: 8%;
    padding-top: 8px;
  }
  .htb {
    display: flex;
  }
  .htb div {
    flex: 1;
  }
  .ant-card-body {
    min-height: 175px;
  }
</style>
