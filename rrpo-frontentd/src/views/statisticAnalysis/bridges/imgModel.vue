<template>
  <a-modal
    :visible="imgVisible"
    :width="800"
    :footer="null"
    @cancel="() => { Onclose() }"
  >
    <a-carousel arrows>
      <div
        slot="prevArrow"
        slot-scope="props"
        class="custom-slick-arrow"
        style="left: 10px;zIndex: 1;"
      >
        <a-icon type="left-circle" />
      </div>
      <div slot="nextArrow" slot-scope="props" class="custom-slick-arrow" style="right: 10px">
        <a-icon type="right-circle" />
      </div>
      <div v-for="item in arr" :key="item.fileId"><img style="height: auto; width: 100%" :src="item.accessUrl" :alt="item.oldName"></div>
    </a-carousel>
  </a-modal>
</template>

<script>
export default {
  name: 'imgModel',
  props: ['imgVisible'],
  data () {
    return {
      arr: []
    }
  },
  methods: {
    Onclose () {
      this.$emit('cancel')
    },
    getImgUrl (i) {
      this.arr = i
      console.log(i)
    }
  }
}
</script>

<style scoped>
  /* For demo */
  .ant-carousel >>> .slick-slide {
    text-align: center;
    height: auto;
    /*line-height: 160px;*/
    /*background: #364d79;*/
    overflow: hidden;
  }

  .ant-carousel >>> .custom-slick-arrow {
    width: 25px;
    height: 25px;
    font-size: 25px;
    line-height: 18px;
    color: #ffffff;
    background-color: rgba(4,5,10, 0.11);
    opacity: 0.8;
  }
  .ant-carousel >>> .custom-slick-arrow:before {
    display: none;
  }
  .ant-carousel >>> .custom-slick-arrow:hover {
    opacity: 1;
    color: white;
    background-color: rgba(0, 0, 0, 0.11);
  }

  .ant-carousel >>> .slick-slide h3 {
    color: #fff;
  }
</style>
