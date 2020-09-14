<template>
  <div class="bgs" :style="{height:mdHeight-198+'px'}">
    <img :src="picTil" />
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      picBG: require("@/assets/welcomeBG.jpg"),
      picTil: require("@/assets/welcomeTil.png"),
      mdHeight: null
    };
  },
  mounted() {
    let _this = this;
    window.onresize = function() {
      _this.setHeight();
    };
    this.setHeight();
    //测试新登陆时的账号有效性
    // axios.get(this.root_path.rootIP+this.root_path.api6001+"/org/person/list").then(res=>{});
    if (localStorage.getItem("compntisroot") != "true") {
      axios
        .get(
          this.root_path.rootIP +
            this.root_path.api6001 +
            "/org/company/findById/" +
            this.$store.state.comUnikey
        )
        .then(res => {
          let x = res.data.datas.posToken;
          if (x == null || x == "") {
            this.$notification.warning({
              message: "公司Token信息未完善",
              description:
                "为了不影响系统正常使用，请前往'系统设置'>>'公司信息管理',完善公司信息！",
              duration: 10
            });
          }
        });
    }
  },
  methods: {
    setHeight() {
      let h = window.document.body.clientHeight;
      this.mdHeight = h;
    }
  }
};
</script>

<style scoped>
.bgs {
  background: url("../../assets/welcomeBG.jpg") center no-repeat;
  background-size: 100% 100%;
  text-align: center;
  position: relative;
  overflow: hidden;
}
.bgs img {
  position: absolute;
  top: 15%;
  left: 50%;
  transform: translateX(-50%);
  max-width: 90%;
}
</style>