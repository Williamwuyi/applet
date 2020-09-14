<template>
<!--   搜索区域 -->
    <a-card :bordered="false" class="card-area">
      <a-form layout="horizontal">
        <div>
          <a-row >
            <a-col :md="6" :sm="24" >
              <a-form-item
                label="考核选择"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 15, offset: 1}">
                <a-cascader  change-on-select
                             style="width: 125px;"
                             @change="onChangeprimary"
                             :options="optionsr"
                             placeholder="年度考核规则"
                             ref="primary"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </div>
      </a-form>
<!--  表格区域    -->
      <component :is="componentName"></component>
      </a-card>
</template>
<script>
import yearCondition from '../general/yearCondition'
import yearRule from './yearRule'
export default {
  name: 'yearPrimary',
  components: {yearCondition, yearRule},
  data () {
    return {
      componentName: 'yearRule',
      optionsr: [
        {
          value: '1',
          label: '总规则'
        }
      ]
    }
  },
  methods: {
    // 重置
    reset () {},
    // 下拉
    onChangeprimary (value) {
      if (value.length > 0) {
        this.componentName = yearCondition
      } else {
        this.componentName = yearRule
      }
    }
  }
}
</script>

<style scoped>

</style>
