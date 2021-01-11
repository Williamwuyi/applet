<template>
  <div style="width: 100%">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="3" :sm="24">
              <a-button type="primary"  @click="addClick">
                增加
              </a-button>
            </a-col>
          </a-row>
        </div>
      </a-form>
      <hr />
<!--   列表   -->
      <a-list item-layout="horizontal" :data-source="arr">
        <a-list-item slot="renderItem" slot-scope="item, index">
          <a-list-item-meta
            :description= "'创建于：' + item.date"
          >
            <a slot="title" href="#">{{ item.name }}</a>
          </a-list-item-meta>
          <a slot="actions" style="color: #4a9ff5" @click="edit(item)">修改</a>
          <a slot="actions" style="color: #FF4D4F"  @click="more(item)">删除</a>
        </a-list-item>
      </a-list>
    </div>
<!--添加    -->
    <year-group-add
    :GroupAddVisiable="GroupAddVisiable"
    @close="GroupClose"
    @success="GroupSuccess"
    />
<!--  修改  -->
    <year-group-edit
    :YearGroupEditVisiable="YearGroupEditVisiable"
    @close="GroupEditClose"
    @success="GroupEditSuccess"
    ref="group"
    />
  </div>
</template>

<script>
import YearGroupEdit from './YearGroupEdit'
import YearGroupAdd from './YearGroupAdd'
export default {
  name: 'YearGroup',
  components: {YearGroupAdd, YearGroupEdit},
  data () {
    return {
      advanced: false,
      GroupAddVisiable: false,
      YearGroupEditVisiable: false,
      arr: []
    }
  },
  mounted () {
    this.fach()
  },
  methods: {
    fach () {
      this.$get('/check/menus/list').then(res => {
        this.arr = res.data.data
      })
    },
    // 增加
    addClick () {
      this.GroupAddVisiable = true
    },
    GroupSuccess () {
      this.GroupAddVisiable = false
      this.fach()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    GroupClose () {
      this.GroupAddVisiable = false
    },
    // 修改
    edit (item) {
      this.YearGroupEditVisiable = true
      this.$refs.group.getName(item)
    },
    GroupEditSuccess () {
      this.YearGroupEditVisiable = false
      this.fach()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    GroupEditClose () {
      this.YearGroupEditVisiable = false
    },
    // 删除
    more (item) {
      let that = this
      that.$confirm({
        title: `是否删除“${item.name}”该模块?，注意！！ （删除会导致该模块的题目以及该模块的成绩都一起被删除)`,
        centered: true,
        onOk () {
          that.$delete('/check/menus/deleteById/{menusId}', {menusId: item.standardId}).then(() => {
            that.$message.success('删除成功')
            that.fach()
          })
        },
        onCancel () {
          that.fach()
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
