<template>
  <div class="fullBg" :style="{height:mdHeight+'px'}">
    <div class="logopic" v-if="!isHid"></div>
    <div class="loginBox">
      <div class="div1">
        <img :src="textPic" />
      </div>
      <div class="div2">
        <div class="input">
          <span>
            <a-icon type="user" />
          </span>
          <span style="width:10px;font-size:20px;">|</span>
          <input type="text" v-model="name" placeholder="请输入用户名" />
        </div>
        <div class="input">
          <span>
            <a-icon type="lock" />
          </span>
          <span style="width:10px;font-size:20px;">|</span>
          <input type="password" v-model="pwd" placeholder="请输入密码" />
        </div>
        <div class="remenber">
          <a-checkbox v-model="isRememberPwd" @change="qwe">记住密码</a-checkbox>
        </div>
        <div class="sub">
          <!-- <button type="button" @click="ccc" :loading="loading">登录</button> -->
          <a-button @click="ccc" :loading="loading">登录</a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

function checkLogin(data) {
  if (data == "" || data == null) {
    return false;
  } else {
    return true;
  }
}

export default {
  data() {
    return {
      mdHeight: null,
      textPic: require("@/components/Login/loginText.png"),
      name: "",
      pwd: "",
      menus: [],
      loading: false,
      isRememberPwd: false,
      isHid: false
    };
  },
  created() {
    let _this = this;
    if (window.location.pathname == "/login") {
      window.document.onkeydown = function(e) {
        let key = window.event.keyCode;
        if (key == 13) {
          _this.ccc(); //优化，登录成功才能取消键盘事件
          window.document.onkeydown = null;
        }
      };
    } else {
      window.document.onkeydown = null;
    }
  },
  mounted() {
    let _this = this;
    window.onresize = function() {
      _this.setHeight();
    };
    this.setHeight();
    // 判断是否有选择过记住密码。
    if (localStorage.getItem("isRememberPwd") == "true") {
      this.isRememberPwd = true;
    }
    this.showPwd();
  },
  methods: {
    // 欢迎框设置高度
    setHeight() {
      let h = window.document.body.clientHeight;
      this.mdHeight = h;
      if (h < 705) {
        this.isHid = true;
      } else {
        this.isHid = false;
      }
    },
    ccc() {
      //验证登录,获取token和权限表、用户名。
      let _this = this;
      if (checkLogin(this.name)) {
        if (checkLogin(this.pwd)) {
        } else {
          this.$notification.error({
            message: "请输入密码！"
          });
          return;
        }
      } else {
        this.$notification.error({
          message: "请输入用户名！"
        });
        return;
      }
      const param = {
        //账号密码
        username: this.name,
        password: this.pwd
      };
      this.rememberPwd(param); //是否记住密码
      this.loading = true;
      axios
        .post(
          this.root_path.rootIP + this.root_path.api6001 + "/auth/index/login",
          qs.stringify(param)
        )
        .then(res => {
          this.loading = false;
          if (res.data.resp_code != 0) {
            this.$notification.error({ message: res.data.resp_msg });
          } else if (res.data.resp_code == 0) {
            //登录成功
            let datas = res.data.datas.menu.children;
            let menu = this.creatTreeData(datas);
            const userinfo = {
              token: res.data.datas.token,
              name: res.data.datas.user.username,
              roleid: res.data.datas.user.roleId,
              comUnikey: res.data.datas.user.comUnikey,
              iscasher: res.data.datas.user.isCashier,
              userid: res.data.datas.user.id
            };
            this.$store.commit("setUserInfo", userinfo); //设置用户信息
            this.$store.commit("setmMenuflag", true);
            if (userinfo.iscasher == 0) {
              //非收银员账号
              this.$store.commit("setMenus", menu); //设置菜单
              this.$store.commit("getRootBtn", menu); //过滤全部有权限的按钮
              this.$router.$addRoutes(this.$store.state.addRoute); //初始添加动态路由
              // this.$store.commit("setmMenuflag", true); //设置标记，此时已加载过路由表了。
              this.$router.push({ name: "welcome" }).catch(err => {}); //跳转到首页(ShowMember)
            } else if (userinfo.iscasher == 1) {
              //收银员账号
              this.$store.commit("setCasherRoute");
              this.$router.$addRoutes(this.$store.state.addRoute);
              this.$router.push({ name: "CashierPost" }).catch(err => {});
            }
          }
        })
        .catch(error => {
          alert("登录失败！", error);
        });
    },
    // 记住密码时，存储用户名、密码
    rememberPwd(data) {
      if (this.isRememberPwd) {
        localStorage.setItem("name", data.username);
        localStorage.setItem("password", data.password);
      } else {
        localStorage.setItem("name", "");
        localStorage.setItem("password", "");
      }
    },

    // 记住密码时，显示账号名、密码
    showPwd() {
      if (this.isRememberPwd) {
        this.name = localStorage.getItem("name")
          ? localStorage.getItem("name")
          : null;
        this.pwd = localStorage.getItem("password")
          ? localStorage.getItem("password")
          : null;
      }
    },
    // 创建树
    creatTreeData(data) {
      //构造新的权限树(无权限的不显示)
      let newarr = [];
      let compntisroot;
      let len = data.length;
      for (let n = 0; n < len; n++) {
        if (data[n].isHidden) {
          //没有权限时，直接跳过
        } else if (data[n].isroot) {
            compntisroot = true;
          //有权限
          if (data[n].children.length == 0) {
            //有权限、无子节点
            newarr.push(data[n]);
          } else {
            //有权限、有子节点
            newarr.push(data[n]);
            let x = data[n].children;
            let newarr2 = [];
            let xlen = x.length;
            for (let i = 0; i < xlen; i++) {
              if (x[i].isHidden) {
              } else {
                if (x[i].children.lenght == 0) {
                  newarr2.push(x[i]);
                } else {
                  newarr2.push(x[i]);
                  let y = x[i].children;
                  let newarr3 = [];
                  let ylen = y.length;
                  for (let j = 0; j < ylen; j++) {
                    if (y[j].isHidden) {
                    } else {
                      if (y[j].children.length == 0) {
                        newarr3.push(y[j]);
                      } else {
                      }
                    }
                  }
                  x[i].children = newarr3;
                }
              }
            }
            data[n].children = newarr2;
          }
        }
      }
      localStorage.setItem("compntisroot", compntisroot);
      return newarr;
    },
    // 设置是否记住密码的状态值，存至浏览器
    qwe(e) {
      localStorage.setItem("isRememberPwd", e.target.checked);
    }
  }
};
</script>

<style scoped>
.fullBg {
  width: 100%;
  background: url("./loginBg01.png") center no-repeat;
  background-size: 100% 100%;
  background-attachment: fixed;
  position: relative;
  overflow: hidden;
  /* min-height: 800px; */
}
.fullBg .logopic {
  max-width: 1000px;
  height: 110px;
  margin: 0 auto;
  margin-top: 100px;
  background: url("./loginLogo.png") center no-repeat;
}
.fullBg .loginBox {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 540px;
  padding: 20px 0;
  transform: translate(-50%, -51%);
}
.fullBg .loginBox .div1 {
  margin: 0 auto;
  text-align: center;
}
.fullBg .loginBox .div2 {
  width: 420px;
  margin: 0 auto;
  margin-top: 50px;
}
.fullBg .loginBox .div2 .input {
  width: 100%;
  height: 60px;
  line-height: 60px;
  font-size: 20px;
  border: 2px solid #ddd;
  background-color: white;
  margin-top: 30px;
}
.fullBg .loginBox .div2 .input > span {
  float: left;
  text-align: center;
  display: block;
  width: 70px;
  height: 100%;
  line-height: 56px;
  font-size: 20px;
}
.fullBg .loginBox .div2 .input input {
  float: left;
  height: 100%;
  border: 0;
  outline: none;
  text-indent: 10px;
  width: 320px;
}
.fullBg .loginBox .div2 .input input::-webkit-input-placeholder {
  color: #aaa;
  font-size: 16px;
}
.fullBg .loginBox .div2 .sub {
  width: 100%;
  height: 50px;
  margin-top: 20px;
}
.fullBg .loginBox .div2 .sub button {
  width: 100%;
  height: 100%;
  line-height: 50px;
  background-color: #1890ff;
  color: white;
  letter-spacing: 5px;
  font-size: 20px;
  outline: none;
  border: 0;
  cursor: pointer;
  border-radius: 5px;
  transition: all 0.3s;
}
.fullBg .loginBox .div2 .sub button:active {
  background-color: #40a9ff;
}
.remenber {
  margin-top: 20px;
  font-size: 16px;
}
</style>