<template>
  <div>
    <a-spin :spinning="spinning">
      <a-timeline class="linebox" :style="{height:tabsHeight+'px'}">
        <a-timeline-item v-for="(n,index) in datalist" :key="index">
          {{n.consumeTime.slice(0,-4)}}
          <br />
          在&nbsp;{{n.deptName}}&nbsp;原价&nbsp;{{n.originalPrice}}元,实际消费&nbsp;{{n.payment}}元
          <a-button type="link" @click="showDetial(n.detailsList)">查看详细</a-button>
          <br />
          购物明细：{{n.description}}
        </a-timeline-item>
        <a-timeline-item v-if="datalist.length!=0"></a-timeline-item>
        <a-timeline-item v-else>暂无数据</a-timeline-item>
      </a-timeline>
    </a-spin>
    <a-divider></a-divider>
    <a-pagination
      size="small"
      showQuickJumper
      @change="changepage"
      v-model="current"
      :total="total"
      :style="{textAlign:'right'}"
    />

    <a-modal
      :visible="visible"
      title="购物详情"
      width="500px"
      :destroyOnClose="true"
      :footer="null"
      @cancel="()=>visible=false"
    >
      <a-table
        :rowKey="record => record.id"
        :columns="columns"
        :dataSource="goodsList"
        bordered
        :style="{'margin-top':'15px'}"
        :pagination="false"
      ></a-table>
    </a-modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

const columns = [
  { title: "商品名称", dataIndex: "tradeName", key: "1" },
  { title: "数量", dataIndex: "number", key: "2" },
  { title: "原价", dataIndex: "originalPrice", key: "3" },
  { title: "实收金额", dataIndex: "payment", key: "4" }
];

export default {
  props: {
    personUnikey: {
      type: String
    },
    tabsHeight: {
      type: Number
    }
  },
  data() {
    return {
      current: 1,
      total: 0,
      datalist: [],
      spinning: false,
      visible: false,
      columns,
      goodsList: []
    };
  },
  methods: {
    // 根据人员key获取订单记录
    getOrderRecord() {
      let param = [
        { column: "person_unikey", type: "eq", value: this.personUnikey }
      ];
      let s1 = JSON.stringify(param);
      let values = {
        current: this.current,
        size: 10,
        condition: s1
      };
      this.spinning = true;
      axios
        .post(
          this.root_path.rootIP + this.root_path.api6001 + "/order/record/list",
          qs.stringify(values)
        )
        .then(res => {
          this.spinning = false;
          if (res.data.datas.total >= 3000) {
            this.total = 3000;
          } else {
            this.total = res.data.datas.total;
          }
          this.datalist = res.data.datas.records;
        })
        .catch(err => {
          this.spinning = false;
        });
    },
    // 分页
    changepage(page, pageSize) {
      this.current = page;
      this.getOrderRecord();
    },
    // 查看详细
    showDetial(data) {
      this.goodsList = data;
      this.visible = true;
    }
  }
};
</script>

<style scoped>
.linebox {
  overflow: hidden auto;
  padding-top: 5px;
  padding: 0 10%;
}
</style>