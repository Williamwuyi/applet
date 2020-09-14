<template>
  <div class="sildtree" ref="sildtree">
    <div>
      <span class="part_title">地区列表</span>
    </div>
    <a-directory-tree
      :treeData="treeData"
      @select="onSelect"
      :replaceFields="replaceFields"
      :selectedKeys="selectedKeys"
      :expandAction="false"
    ></a-directory-tree>
  </div>
</template>

<script>
export default {
  name: 'RegionTree',
  data () {
    return {
      treeData: [],
      selectedKeys: [],
      replaceFields: {}
    }
  },
  mounted () {
    this.$get('/area').then((r) => {
      // 替换后台获取的字段
      this.replaceFields = { title: 'areaName', key: 'id' }
      this.treeData = r.data.rows.children
      this.deptTreeKeys = +new Date()
    })
  },
  methods: {
    // 点击部门，传递部门key，在人员表中显示当前部门的人员。
    onSelect (selectedKeys, e) {
      console.log(e.selectedNodes[0].data.props)
      this.$emit('reloadStaffTable', selectedKeys[0])
      console.log('选中地区的id', selectedKeys)
      this.selectedKeys = selectedKeys
      this.$emit('clearSelectrows')
    }
  }
}
</script>

<style scoped>
  .sildtree {
    float: left;
    width: 300px;
    min-height: 400px;
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
    height: 32px;
    line-height: 28px;
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
