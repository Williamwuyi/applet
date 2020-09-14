<template>
  <a-card :bordered="false" class="card-area">
<!--    <a-button class="editable-add-btn" @click="handleAdd">-->
<!--      添加-->
<!--    </a-button>-->
    <a-button class="editable-add-btn" @click="handleDel">
      彻底删除
    </a-button>
    <a-table :data-source="dataSource" :columns="columns" :pagination="false">
    </a-table>
  </a-card>
</template>
<script>
export default {
  name: 'Recycling',
  data () {
    return {
      dataSource: [],
      count: 2,
      columns: [
        {
          title: '标题',
          dataIndex: 'Aicon',
          scopedSlots: { customRender: 'Aicon' }
        },
        {
          title: '发布人',
          dataIndex: 'sharefile',
          width: '20%'
        },
        {
          title: '时间',
          dataIndex: 'name'
        }
      ]
    }
  },
  mounted () {

  },
  methods: {
    handleAdd () {
    },
    handleDel () {
    },
    addFolder () {
    },
    edit (record) {
    }
  }
}
</script>
<style lang="less" scoped>
  @import "../../../../../static/less/Common";
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
    margin: 8px;
  }
</style>
