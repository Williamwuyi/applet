<template>
  <div>
    <div class="hidbox" :style="{height:tabsHeight+'px'}">
      <a-row :gutter="30">
        <a-col
          :lg="6"
          :xl="4"
          :xxl="3"
          v-for="(n,index) in datalist"
          :key="index"
          :style="{marginBottom:'2em'}"
        >
          <a-card
            class="card_line_p"
            :style="{width:'100%',marginTop:'30px;'}"
            :bodyStyle="{padding:'10px 14px'}"
          >
            <div class="showimg">
              <div>
                <img
                  :src="root_path.rootIP+root_path.api6001+'/eq/hardware/showPersonImg?fileName='+n.snapImage+'&snapUnikey='+n.snapUnikey"
                />
                <!-- <p>抓拍图片</p> -->
              </div>
            </div>
            <p>门店：{{n.deptName}}</p>
            <p>相似度：{{(n.similarity).slice(-6,-4)+'%'}}</p>
            <p>时间：{{n.createTime}}</p>
            <p style="text-align:center;">
              <a-button :style="{width:'120px'}" @click="changePerImg(n)">抓拍替换底图</a-button>
            </p>
          </a-card>
        </a-col>
      </a-row>
    </div>
    <a-divider></a-divider>
    <a-pagination
      size="small"
      :pageSize="16"
      showQuickJumper
      @change="changepage"
      v-model="current"
      :total="total"
      :style="{textAlign:'right'}"
    />
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

export default {
  props: {
    personUnikey: {
      type: String
    },
    tabsHeight: {
      type: Number
    },
    personId: {
      type: String
    }
  },
  data() {
    return {
      current: 1,
      total: 0,
      datalist: []
    };
  },
  methods: {
    // 获取个人的识别记录
    getPerSnapimg() {
      const param = {
        personUnikey: this.personUnikey,
        size: 16,
        current: this.current
      };
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/eq/identify-record/getByPersonUnikey",
          { params: param }
        )
        .then(res => {
          if (res.data.datas.total >= 3000) {
            this.total = 3000;
          } else {
            this.total = res.data.datas.total;
          }
          this.datalist = res.data.datas.records;
        });
    },
    // 分页
    changepage(page, pageSize) {
      this.current = page;
      this.getPerSnapimg();
    },
    // 抓拍替换底图
    changePerImg(data) {
      let _this = this;
      let imgsrc =
        this.root_path.rootIP +
        this.root_path.api6001 +
        "/eq/hardware/showPersonImg?snapUnikey=" +
        data.snapUnikey +
        "&fileName=" +
        data.snapImage;
      this.$confirm({
        title: "是否替换人员底库图？",
        okText: "确定",
        okType: "info", //confirm
        cancelText: "取消",
        onOk() {
          axios
            .post(
              _this.root_path.rootIP +
                _this.root_path.api6001 +
                "/org/person/uploadFileFromUrl",
              qs.stringify({ url: imgsrc })
            )
            .then(res => {
              const param = {
                imgPaths: res.data.datas,
                personId: _this.personId,
                status: 2,
                personName: data.personName
              };
              let recordId = data.recordId;
              axios
                .post(
                  _this.root_path.rootIP +
                    _this.root_path.api6001 +
                    "/eq/identify-record/addPerson?" +
                    qs.stringify({ recordId }),
                  param
                )
                .then(res => {
                  if (res.data.resp_code == 0) {
                    _this.$notification.success({ message: "操作成功" });
                  } else {
                    _this.$notification.warning({ message: res.data.resp_msg });
                  }
                });
            });
        }
      });
    }
  }
};
</script>

<style scoped>
.showimg {
  width: 100%;
  text-align: center;
}
.showimg div {
  /* width: 45%; */
  display: inline-block;
  height: 130px;
  overflow: hidden;
}
.showimg div p {
  text-align: center;
  margin-bottom: 5px;
}
.showimg div img {
  width: 100%;
  height: 100%;
}
.card_line_p p {
  margin-bottom: 0.5em;
}
.hidbox {
  overflow: auto;
  overflow-x: hidden;
}
</style>