<template>
  <div class="sildtree" ref="sildtree">
<!--    <div>-->
<!--      <span class="part_title">地区列表</span>-->
<!--    </div>-->
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
  name: 'comTree',
  data () {
    return {
      treeData: [],
      loading: false,
      replaceFields: {},
      selectedKeys: []
    }
  },
  mounted () {
    this.loading = true
    // 树结构获取数据 部门dept   地区/area
    this.$get('dept').then((r) => {
      // this.replaceFields = {title: 'areaName', key: 'id'}
      this.treeData = r.data.rows.children
      this.loading = false
    })
  },
  methods: {
    // 点击，传递部门key，在人员表中显示当前部门的人员。
    onSelect (selectedKeys) {
      this.$emit('reloadStaffTable', selectedKeys[0])
      console.log('选中的id', selectedKeys)
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
    position:absolute;
    height:700px;
    overflow:auto;
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
