<template>
  <div>
    <appendix refId="-1" refType="5" :is-upload="true" ref="appendix"></appendix>
  </div>
</template>
<script>
export default {
  name: 'Test',
  components: {appendix: () => import('../../components/appendix/Appendix')},
  methods: {
    test () {
      console.log('test function')
      this.$refs.appendix.fetch()
    }
  }
}
</script>
