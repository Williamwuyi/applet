<template>
  <div class="login">
    <a-form @submit.prevent="doLogin" :autoFormCreate="(form) => this.form = form">
<!--      <a-tabs size="large" :tabBarStyle="{textAlign: 'center'}" style="padding: 0 2px;" :activeKey="activeKey"-->
<!--              @change="handleTabsChange">-->
<!--        <a-tab-pane key="1">-->
          <a-alert type="error" :closable="true" v-show="error" :message="error" showIcon
                   style="margin-bottom: 24px;"></a-alert>
          <a-form-item
            fieldDecoratorId="name"
            style="text-align: left"
            :fieldDecoratorOptions="{rules: [{ required: true, message: '请输入账户名', whitespace: true}]}">
            <a-input size="large" style="margin-top: 10px;width: 350px">
              <a-icon slot="prefix" type="user"></a-icon>
            </a-input>
          </a-form-item>
          <a-form-item
            fieldDecoratorId="password"
            style="text-align: left"
            :fieldDecoratorOptions="{rules: [{ required: true, message: '请输入密码', whitespace: true}]}">
            <a-input size="large" type="password" style="margin-top: 10px;width: 350px">
              <a-icon slot="prefix" type="lock"></a-icon>
            </a-input>
          </a-form-item>
<!--        </a-tab-pane>-->
<!--      </a-tabs>-->
      <a-form-item>
        <a-button :loading="loading" style="width: 100%;width: 350px; margin-top: 15px" size="large" htmlType="submit" type="primary">
          登录
        </a-button>
      </a-form-item>
      <div style="text-align: right;margin-top: 25px;"><a href="http://114.116.174.5:888/" target="_blank">门户网站>></a></div>
      <div>
<!--        <a style="float: right" @click="regist">注册账户</a>-->
      </div>
    </a-form>
    <a-modal
      :visible="visible"
      title="绑定组织机构"
      :afterClose="()=>isEdit=false"
      width="500px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      :footer="null"
      @cancel="close"
      @ok="ok"
    >
      <div style="height: 280px;text-align: center">
        <a-tree-select
          placeholder="选择用户的组织机构"
          style="width: 300px;"
          :dropdown-style="{ maxHeight: '200px', overflow: 'auto' }"
          :tree-data="deptTreeData"
          tree-data-simple-mode
          :load-data="onLoadData"
          v-model="deptId">
        </a-tree-select>
        <a-button type="primary" @click="submit()">确认绑定</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>
import {mapMutations} from 'vuex'

export default {
  name: 'Login',
  data () {
    return {
      loading: false,
      visible: false,
      deptTreeData: [],
      deptId: '',
      error: '',
      activeKey: '1'
    }
  },
  computed: {
    systemName () {
      return this.$store.state.setting.systemName
    },
    copyright () {
      return this.$store.state.setting.copyright
    }
  },
  created () {
    this.$db.clear()
    this.$router.options.routes = []
  },
  methods: {
    doLogin () {
      if (this.activeKey === '1') {
        // 用户名密码登录
        this.form.validateFields(['name', 'password'], (errors, values) => {
          if (!errors) {
            this.loading = true
            let name = this.form.getFieldValue('name')
            let password = this.form.getFieldValue('password')
            this.$post('login', {
              username: name,
              password: password
            }).then((r) => {
              let data = r.data.data
              this.saveLoginData(data)
              setTimeout(() => {
                this.loading = false
              }, 500)
              const that = this
              this.$get('/user/getDept').then((r) => {
                if (!r.data) { // 为true是有组织机构,为false则没有组织机构
                  this.$confirm({
                    content: '您未绑定组织机构',
                    centered: true,
                    onOk () {
                      that.$get('/dept/list', {deptId: -1}).then((r) => {
                        console.log('加载数据：', r.data)
                        let newData = r.data
                        newData.forEach(t => {
                          that.deptTreeData.push(
                            { id: t.deptId, pId: t.parentId, value: t.deptId, title: t.deptName }
                          )
                        })
                      })
                      that.visible = true
                    },
                    onCancel () {
                      that.visible = false
                      that.loading = false
                    }
                  })
                } else { // 为true是有组织机构,为false则没有组织机构
                  this.$router.push('/')
                }
              })
            }).catch(() => {
              setTimeout(() => {
                this.loading = false
              }, 500)
            })
          }
        })
      }
    },
    onLoadData (treeNode) {
      return new Promise(resolve => {
        setTimeout(() => {
          this.$get('/dept/list', {deptId: treeNode.dataRef.id}).then((r) => {
            let newData = r.data
            newData.forEach(t => {
              this.deptTreeData.push(
                { id: t.deptId, pId: t.parentId, value: t.deptId, title: t.deptName }
              )
            })
          })
          resolve()
        }, 500)
      })
    },
    // 绑定组织机构
    submit () {
      if (this.deptId !== '') {
        this.$get('/user/bandingDept', {deptId: this.deptId}).then((res) => {
          if (res.data.status === 1) {
            this.$router.push('/')
          }
        })
      } else {
        this.$notification.warning({message: '系统提示', description: '请选中一个组织机构绑定！', duration: 4})
      }
    },
    regist () {
      this.$emit('regist', 'Regist')
    },
    getCaptcha () {
      this.$message.warning('暂未开发')
    },
    handleTabsChange (val) {
      this.activeKey = val
    },
    close () {
      this.visible = false
      this.loading = false
    },
    ok () {
      this.visible = false
    },
    ...mapMutations({
      setToken: 'account/setToken',
      setExpireTime: 'account/setExpireTime',
      setPermissions: 'account/setPermissions',
      setRoles: 'account/setRoles',
      setUser: 'account/setUser',
      setTheme: 'setting/setTheme',
      setLayout: 'setting/setLayout',
      setMultipage: 'setting/setMultipage',
      fixSiderbar: 'setting/fixSiderbar',
      fixHeader: 'setting/fixHeader',
      setColor: 'setting/setColor'
    }),
    saveLoginData (data) {
      console.log(data)
      this.setToken(data.token)
      // this.setExpireTime(data.expireTime)
      this.setUser(data.user)
      this.setPermissions(data.permissions)
      this.setRoles(data.roles)
      this.setTheme(data.config.theme)
      this.setLayout(data.config.layout)
      this.setMultipage(data.config.multiPage === '1')
      this.fixSiderbar(data.config.fixSiderbar === '1')
      this.fixHeader(data.config.fixHeader === '1')
      this.setColor(data.config.color)
    }
  }
}
</script>

<style lang="less" scoped>
  .login {
    .icon {
      font-size: 24px;
      color: rgba(0, 0, 0, 0.2);
      margin-left: 16px;
      vertical-align: middle;
      cursor: pointer;
      transition: color 0.3s;

      &:hover {
        color: #1890ff;
      }
    }
  }
</style>
