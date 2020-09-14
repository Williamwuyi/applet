<template>
  <!-- 布局 -->
  <a-layout id="components-layout-demo-custom-trigger">
    <a-layout-sider :trigger="null" collapsible v-model="collapsed" class="sider">
      <div class="logo">
        <img src="./jiebao_logo.png" />
        <h4 v-if="!collapsed">智慧门店系统</h4>
      </div>

      <!-- 菜单 -->
      <a-menu
        :style="{marginTop:'64px'}"
        theme="light"
        mode="inline"
        :openKeys="openkey"
        :selectedKeys="[key]"
        @openChange="openChange"
        @select="onselect"
      >
        <template v-for="item in $store.state.menus">
          <a-menu-item v-if="item.children.length==0&&item.isroot&&item.url" :key="item.key">
            <router-link :to="item.url">
              <a-icon :type="item.icon"></a-icon>
              <span>{{item.title}}</span>
            </router-link>
          </a-menu-item>
          <!-- 新增 (有子节点时，说明是父级菜单，当前菜单没有链接，无子节点时，说明可能为子节点或父节点)-->
          <!-- <show-sub-menu v-else-if="item.children.length!=0&&item.isroot" :menu-info="item" :key="item.key" /> -->
          <sub-menu
            v-else-if="item.children.length!=0&&item.isroot"
            :menu-info="item"
            :key="item.key"
          />
        </template>
      </a-menu>
    </a-layout-sider>

    <a-layout class="rightlayout" :style="{marginLeft:collapsed?'80px':'200px'}">
      <a-layout-header class="header">
        <a-icon class="trigger" :type="collapsed ? 'menu-unfold' : 'menu-fold'" @click="showsider" />
        <span>欢迎进入智慧营销辅助系统</span>

        <!-- 顶部右侧栏个人信息 -->
        <div class="top_right_person" :style="{marginRight:collapsed?'80px':'200px'}">
          <!-- 通知信息 -->
          <!-- <a class="person_msg">
            <a-icon type="bell" />
          </a>-->

          <!-- 用户信息 -->
          <div
            :style="{display:'inline-block',margin:'0 15px',cursor:'pointer',position:'relative'}"
            @mouseenter="noshow=1"
            @mouseleave="noshow=-1"
          >
            <a-icon type="user" style="font-size:18px;padding:0 5px;"></a-icon>
            {{$store.state.userName}}
            <ul class="opts" v-if="noshow==1">
              <span></span>
              <li style="border-top:0;" @click="showmodal">修改密码</li>
              <!-- <li>选项111</li> -->
            </ul>
          </div>
          <!-- 退出登录 -->
          <div :style="{display:'inline-block',cursor:'pointer'}" @click="loginout">
            <a-icon type="logout" :style="{fontSize:'18px',padding:'0 5px'}" />退出登录
          </div>
        </div>
      </a-layout-header>

      <!-- 菜单链接切换栏 -->
      <!-- <div class="topmenu">
        <a-tabs hideAdd type="editable-card" :tabBarStyle="{border:'0',margin:'0',marginTop:'5px'}">
          <a-tab-pane v-for="pane in panes" :key="pane.key" :closable="pane.closable">
            <span slot="tab" :slot-scope="pane.key">{{pane.title}}</span>
          </a-tab-pane>
        </a-tabs>
      </div>-->

      <a-layout-content class="content">
        <a-card>
          <keep-alive>
            <router-view v-if="$route.meta.keepAlive"></router-view>
          </keep-alive>
          <router-view v-if="!$route.meta.keepAlive"></router-view>
        </a-card>
      </a-layout-content>
      <!-- <a-layout-footer style="textAlign: center">版权所有：捷报技术有限公司</a-layout-footer> -->
    </a-layout>

    <a-modal
      :visible="visible"
      title="修改密码"
      width="380px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="visible=false"
      @ok="changePwd"
    >
      <a-form :form="form">
        <a-form-item label="原始密码" :labelCol="{span:8}" :wrapperCol="{span:14}">
          <a-input
            type="password"
            v-decorator="['orgPwd',{rules:[{required:true,message:'请输入原始密码'}],getValueFromEvent:validatorWhiteSpace}]"
          ></a-input>
        </a-form-item>
        <a-form-item label="新密码" :labelCol="{span:8}" :wrapperCol="{span:14}">
          <a-input
            type="password"
            v-decorator="['newPwd',{rules:[{required:true,message:'请输入新密码'}],getValueFromEvent:validatorWhiteSpace}]"
          ></a-input>
        </a-form-item>
        <a-form-item label="重复新密码" :labelCol="{span:8}" :wrapperCol="{span:14}">
          <a-input
            type="password"
            v-decorator="['rePwd',{rules:[{required:true,message:'请重复新密码'}],getValueFromEvent:validatorWhiteSpace}]"
          ></a-input>
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout>
</template>

<script>
// import showSubMenu from "./showSubMenu";
import axios from "axios";
import qs from "qs";
import formPattern from "@/components/commons/formPattern.js";
import { Menu } from "ant-design-vue";
const SubMenu = {
  template: `
      <a-sub-menu :key="menuInfo.key" v-bind="$props" v-on="$listeners">
        <span slot="title">
          <a-icon :type="menuInfo.icon" />
          <span>{{ menuInfo.title }}</span>
        </span>
        <template v-for="item in menuInfo.children">
          <a-menu-item v-if="item.children.length==0&&item.isroot&&item.url" :key="item.key">
            <router-link :to="item.url">
              <span>{{ item.title }}</span>
            </router-link>
          </a-menu-item>
          <sub-menu v-else-if="item.children.length!=0&&item.isroot" :key="item.key" :menu-info="item" />
        </template>
      </a-sub-menu>
    `,
  name: "SubMenu",
  isSubMenu: true,
  props: {
    ...Menu.SubMenu.props,
    menuInfo: {
      type: Object,
      default: () => ({})
    }
  }
};

const panes = [
  { title: "Tab 1", key: "1" },
  { title: "Tab 2", key: "2" },
  { title: "Tab 3", key: "3", closable: false }
];

export default {
  // components: { showSubMenu },
  components: { "sub-menu": SubMenu },
  data() {
    return {
      collapsed: false,
      panes,
      ruter: "",
      key: "1",
      openkey: [],
      noshow: -1,
      visible: false
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  mounted() {
    this.getMuneKey();
  },
  methods: {
    showsider() {
      this.collapsed = !this.collapsed;
    },
    // 根据浏览器地址，判断当前所在的菜单位置
    getMuneKey() {
      this.ruter = window.location.pathname;
      this.ccc(this.$store.state.menus);
    },
    ccc(arr) {
      //获取当前页面所在的菜单位置
      for (let item of arr) {
        if (item.children.length != 0) {
          this.ccc(item.children);
        } else {
          if (this.ruter == item.url) {
            this.key = item.key;
            this.xxx(item.key, this.$store.state.menus);
            return;
          }
        }
      }
    },
    xxx(key, arr) {
      //根据菜单位置，获取是否有展开的菜单
      for (let item of arr) {
        if (item.children.length != 0) {
          let child = item.children;
          for (let n of child) {
            if (n.key == key) {
              this.openkey = [item.key];
              return;
            }
          }
        }
      }
    },
    // 选择菜单时，更换key
    onselect({ item, key, selectedKeys }) {
      this.key = key;
    },
    // 菜单伸缩
    openChange(openKeys) {
      let len = openKeys.length;
      if (len == 0) {
        this.openkey = [];
      } else {
        this.openkey = openKeys;
      }
    },
    //退出登录
    loginout() {
      let _this = this;
      this.$confirm({
        title: "是否确认退出",
        okText: "确定",
        cancelText: "取消",
        onOk() {
          // localStorage.clear();//清空本地所有用户信息
          _this.setNameStorage();
          _this.$router.$addRoutes([
            //重设路由表
            {
              path: "/login",
              name: "login",
              component: () => import("@/components/Login/login")
            },
            { path: "*", component: () => import("@/components/404page/404") }
          ]);
          _this.$router.push({ name: "login" }).catch(err => {});
        }
      });
    },
    // 弹出修改密码框
    showmodal() {
      this.visible = true;
    },
    // 修改当前账号密码
    changePwd() {
      const form = this.form;
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        axios
          .post(
            this.root_path.rootIP +
              this.root_path.api6001 +
              "/auth/user/resetPwd",
            qs.stringify(values)
          )
          .then(res => {
            if (res.data.resp_code == 0) {
              this.visible = false;
              this.$notification.success({ message: res.data.resp_msg });
            } else {
              this.$notification.error({ message: res.data.resp_msg });
            }
          })
          .catch(error => {
            alert("操作失败：" + error);
          });
      });
    },
    // 退出登录时，只留存账号名
    setNameStorage() {
      let name = localStorage.getItem("userName");
      let isRememberPwd = localStorage.getItem("isRememberPwd");
      localStorage.clear();
      localStorage.setItem("name", name);
      localStorage.setItem("isRememberPwd", isRememberPwd);
    },
    // 空格校验
    validatorWhiteSpace(event) {
      return formPattern.validatorWhiteSpace(event);
    }
  },
  watch: {
    $route(to, from) {
      this.getMuneKey();
    }
  }
};
</script>

<style scoped >
#components-layout-demo-custom-trigger {
  min-height: 100vh !important;
}
#components-layout-demo-custom-trigger .trigger {
  font-size: 20px;
  line-height: 64px;
  padding: 0 24px;
  cursor: pointer;
  transition: color 0.3s;
}

#components-layout-demo-custom-trigger .trigger:hover {
  color: #d1e9ff;
}

#components-layout-demo-custom-trigger .logo {
  position: fixed;
  z-index: 9;
  width: 100%;
  height: 64px;
  line-height: 64px;
  background-color: #1890ff;
}
#components-layout-demo-custom-trigger .logo img {
  vertical-align: middle;
  margin-left: 20px;
}
#components-layout-demo-custom-trigger .logo h4 {
  display: inline-block;
  padding: 0 15px;
  font-size: 20px;
  color: white;
  margin-bottom: 0;
}
#components-layout-demo-custom-trigger .sider {
  overflow: auto;
  height: 100vh;
  position: fixed;
  left: 0;
  transition: all 0.3s;
  background-color: white;
}
#components-layout-demo-custom-trigger .sider::-webkit-scrollbar {
  display: none;
}
.rightlayout {
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  zoom: 1;
}
.rightlayout .header {
  position: fixed;
  top: 0;
  padding: 0;
  width: 100%;
  line-height: 64px;
  font-size: 16px;
  z-index: 100;
  background-color: #1890ff;
  color: white;
}
.rightlayout .content {
  margin: 12px 12px 0px;
  margin-top: 77px;
}
.rightlayout .topmenu {
  margin-top: 64px;
  padding: 0 20px;
  height: 50px;
  background-color: white;
  box-shadow: 0 2px 5px #ddd;
}
.rightlayout .top_right_person {
  float: right;
  line-height: 64px;
  font-size: 16px;
  transition: all 0.3s;
  padding-right: 25px;
}

/* bell */
.rightlayout .top_right_person .person_msg {
  display: inline-block;
  width: 40px;
  height: 40px;
  line-height: 40px;
  font-size: 18px;
  text-align: center;
  color: white;
}
.rightlayout
  .topmenu
  >>> .ant-tabs.ant-tabs-card
  .ant-tabs-card-bar
  .ant-tabs-ink-bar {
  visibility: visible;
}
.rightlayout
  .topmenu
  >>> .ant-tabs.ant-tabs-card
  .ant-tabs-card-bar
  .ant-tabs-tab {
  padding: 0 24px;
  background-color: white;
  border: 0;
}
.rightlayout .topmenu >>> .ant-tabs-ink-bar {
  height: 1px;
}
.opts {
  position: absolute;
  top: 54px;
  left: 0;
  list-style-type: none;
  width: 120px;
  text-align: center;
}
.opts span {
  display: block;
  width: 0;
  height: 0;
  margin-left: 25px;
  border: 10px solid white;
  border-top-color: transparent;
  border-left-color: transparent;
  border-right-color: transparent;
}
.opts li {
  line-height: 40px;
  height: 40px;
  text-align: center;
  color: #333;
  background-color: white;
  box-shadow: 0 10px 10px #ddd;
  border-top: 2px solid #f0f0f0;
}
.opts li:hover {
  color: #1890ff;
}
</style>