import Vue from "vue";
import Vuex from "vuex";
Vue.use(Vuex);

//state 是用来定义初始值的
const state = {
  token: "",
  userName: "",
  roleid: "",
  comUnikey: "",
  iscasher:0,
  userid:"",
  menuflag: false,
  menus: [],
  allRoutes: [
    {
      path: "/",
      name: "layout",
      component: () => import("@/components/layouts/layout"),
      redirect: "/cashierpost",
      children: [
        // 收银台
        {
          path: "/cashierpost",
          name: "CashierPost",
          component: () => import("@/components/Cashier/CashierPost")
        },
        // 注册管理
        {
          path: "/registManag",
          name: "registManag",
          component: () => import("@/components/registManag/registManag")
        },
        // 客户管理-会员管理
        {
          path: "/member",
          name: "member",
          component: () => import("@/components/MemberManag/Member")
        },
        // 识别推送
        {
          path: "/identifypush",
          name: "identifypush",
          component: () => import("@/components/IdentifyPush/IdentifyPush")
        },
         // 企业管理 - 门店管理
         {
          path: "/shopmanag",
          name: "shopmanag",
          component: () => import("@/components/shopManag/shopManag")
        },
        // 员工管理
        {
          path: "/staffmanag",
          name: "staffmanag",
          component: () => import("@/components/StaffManag/StaffManag")
        },
        // 识别记录
        {
          path: "/identifyrecord",
          name: "identifyrecord",
          component: () => import("@/components/identifyRecord/identifyRecord")
        },
        //预警管理 - 黑名单（待审核）
        {
          path: "/blacklistnorev",
          name: "blacklistnorev",
          component: () => import("@/components/blackList/blackListNoRev")
        },
        // 黑名单（已审核）
        {
          path: "/blacklisthasrev",
          name: "blacklisthasrev",
          component: () => import("@/components/blackList/blackListHasRev")
        },
        // 会员日消费统计
        {
          path: "/consumeindays",
          name: "consumeindays",
          component: () => import("@/components/dataReports/menberReport/consumeInDays"),
          meta:{
            keepAlive:true
          }
        },
        
        // 客流统计
        {
          path: "/preinhours",
          name: "preinhours",
          component: () => import("@/components/dataReports/PassengerAnalysis/preInHours"),
          meta:{
            keepAlive:true
          }
        },
        {
          path: "/preindays",
          name: "preindays",
          component: () => import("@/components/dataReports/PassengerAnalysis/preindays"),
          meta:{
            keepAlive:true
          }
        },
        {
          path: "/preinweeks",
          name: "preinweeks",
          component: () => import("@/components/dataReports/PassengerAnalysis/preInWeeks"),
          meta:{
            keepAlive:true
          }
        },
        {
          path: "/preinmonth",
          name: "preinmonth",
          component: () => import("@/components/dataReports/PassengerAnalysis/preInMonth"),
          meta:{
            keepAlive:true
          }
        },
        {
          path: "/contrastofday",
          name: "contrastofday",
          component: () => import("@/components/dataReports/PassengerAnalysis/contrastOfDay"),
          meta:{
            keepAlive:true
          }
        },
        {
          path: "/contrastofweek",
          name: "contrastofweek",
          component: () => import("@/components/dataReports/PassengerAnalysis/contrastOfWeek"),
          meta:{
            keepAlive:true
          }
        },
        {
          path: "/contrastofmonth",
          name: "contrastofmonth",
          component: () => import("@/components/dataReports/PassengerAnalysis/contrastOfMonth"),
          meta:{
            keepAlive:true
          }
        },
        {
          path: "/contrastofhours",
          name: "contrastofhours",
          component: () => import("@/components/dataReports/PassengerAnalysis/contrastOfHours"),
          meta:{
            keepAlive:true
          }
        },
        {
          path: "/shopsindays",
          name: "shopsindays",
          component: () => import("@/components/dataReports/PassengerAnalysis/shopsInDays"),
          meta:{
            keepAlive:true
          }
        },
        {
          path: "/shopsinweeks",
          name: "shopsinweeks",
          component: () => import("@/components/dataReports/PassengerAnalysis/shopsInWeeks"),
          meta:{
            keepAlive:true
          }
        },
        {
          path: "/shopsinmonth",
          name: "shopsinmonth",
          component: () => import("@/components/dataReports/PassengerAnalysis/shopsInMonth"),
          meta:{
            keepAlive:true
          }
        },
       
        // 设备管理 -主机管理
        {
          path: "/hosts",
          name: "hosts",
          component: () => import("@/components/DeviceManag/hosts")
        },
        // 设备列表
        {
          path: "/devicelist",
          name: "devicelist",
          component: () => import("@/components/DeviceManag/DeviceList")
        },
        // 基础设备表
        {
          path: "/basicdevicelist",
          name: "basicdevicelist",
          component: () => import("@/components/DeviceManag/BasicDeviceList")
        },
        // 基础主机管理
        {
          path: "/basehosts",
          name: "basehosts",
          component: () => import("@/components/DeviceManag/baseHosts")
        },
         //设备传输日志
         {
          path: "/prequeueindev",
          name: "prequeueindev",
          component: () => import("@/components/DeviceManag/preQueueInDev")
        },
        // 账号管理
        {
          path: "/accountmanag",
          name: "accountManag",
          component: () => import("@/components/accountManag/accountManag")
        },
        // 角色管理
        {
          path: "/roles",
          name: "roles",
          component: () => import("@/components/accountManag/roles")
        },
        // 公司群组管理
        {
          path: "/companies",
          name: "companies",
          component: () => import("@/components/company/companies")
        },
        // 菜单管理
        {
          path: "/menumanager",
          name: "menumanager",
          component: () => import("@/components/accountManag/menuManager")
        },
        // 数据接口
        {
          path: "/dataport",
          name: "dataport",
          component: () => import("@/components/accountManag/dataPort")
        },
        // 账号集群
        {
          path: "/groupuser",
          name: "groupuser",
          component: () => import("@/components/accountManag/groupUser")
        },
        
        // 公司信息管理
        {
          path: "/mycompany",
          name: "mycompany",
          component: () => import("@/components/company/myCompany")
        },

        
      ]
    }
  ],
  addRoute: [],
  rootBtn: []
};

//getters的主要作用是根据state的值来变化，
//比如时间对象转换成字符串。对于不做变化的值，不需要用到getters。
// const getters={
//   getToken(state){
//     return state.token;
//   },
//   getUserName(state){
//     return state.userName;
//   }
// }

//actions 异步触发mutations的方法，避免同一方法获取的值是一样的
//const actions={}

// mutations 用来定义修改state变量的方法
const mutations = {
  setUserInfo(state, data) {
    //设置用户的token和用户名，同时存储在本地
    state.token = data.token;
    localStorage.setItem("token", data.token);
    state.userName = data.name;
    localStorage.setItem("userName", data.name);
    state.roleid = data.roleid;
    localStorage.setItem("roleid", data.roleid);
    state.comUnikey = data.comUnikey;
    localStorage.setItem("comUnikey", data.comUnikey);
    state.iscasher=data.iscasher;
    localStorage.setItem("iscasher", data.iscasher);
    state.userid=data.userid;
    localStorage.setItem("userid", data.userid);
  },
  setmMenuflag(state, bool) {
    //用来判断是否已经加载过路由了。
    state.menuflag = bool;
  },
  getUserToken(state) {
    //判断是否有用户token，如果有，再次设置vuex的token和name
    let token = localStorage.getItem("token");
    let userName = localStorage.getItem("userName");
    let roleid = localStorage.getItem("roleid");
    let comUnikey = localStorage.getItem("comUnikey");
    let iscasher = localStorage.getItem("iscasher");
    let userid = localStorage.getItem("userid");
    state.token = token;
    state.userName = userName;
    state.roleid = roleid;
    state.comUnikey = comUnikey;
    state.iscasher=iscasher;
    state.userid=userid;
  },
  getMenus(state) {
    //重新获取路由表/侧边菜单
    state.menus = JSON.parse(localStorage.getItem("slidermenu"));
    state.addRoute = [];
    let len = state.allRoutes[0].children.length;
    let c = state.allRoutes[0].children;
    let data = localStorage.getItem("menuList")
      ? JSON.parse(localStorage.getItem("menuList"))
      : [];

    if (data.length == 0) {
      state.addRoute = [];
    } else {
      data = data[0].children;
      for (let n = 0; n < data.length; n++) {
        for (let i = 0; i < len; i++) {
          if (data[n].path == c[i].path) {
            state.addRoute.push(c[i]);
            break;
          }
        }
      }
    }
    let ff = state.addRoute;
    state.addRoute = [
      {
        path: "/",
        name: "layout",
        component: () => import("@/components/layouts/layout"),
        redirect: "/welcome",
        children: [
          {
            path: "/welcome",
            name: "welcome",
            component: () => import("@/components/layouts/welcome")
          },
          ...ff
        ]
      },
      {//临时页面路由
        path: "/temporary",
        name: "temporary",
        component: () => import("@/components/404page/temporary"),
      },
      {
        path: "/login",
        name: "login",
        component: () => import("@/components/Login/login")
      },
      { path: "*", component: () => import("@/components/404page/404") }
    ];
  },
  setMenus(state, menu) {
    //通过菜单表设置动态路由/侧边菜单
    state.addRoute = [];
    state.menus = menu;
    let allroute = state.allRoutes[0].children;
    let len = allroute.length;
    for (let i of menu) {
      if (i.children.length != 0) {
        //*
        let x = i.children;
        for (let j of x) {
          if (j.children.length != 0) {
            //三级菜单才可用*
          } else {
            if (j.isroot) {
              for (let n = 0; n < len; n++) {
                if (j.url == allroute[n].path) {
                  state.addRoute.push(allroute[n]);
                  // break;
                }
              }
            }
          }
        }
      } else {
        if (i.isroot) {
          for (let n = 0; n < len; n++) {
            if (i.url == allroute[n].path) {
              state.addRoute.push(allroute[n]);
              break;
            }
          }
        }
      }
    }
    let ff = state.addRoute;
    state.addRoute = [
      {
        path: "/",
        name: "layout",
        component: () => import("@/components/layouts/layout"),
        redirect: "/welcome", //showmenber
        children: [
          {
            path: "/welcome",
            name: "welcome",
            component: () => import("@/components/layouts/welcome")
          },
          ...ff
        ]
      },
      {//临时页面路由
        path: "/temporary",
        name: "temporary",
        component: () => import("@/components/404page/temporary"),
      },
      {
        path: "/login",
        name: "login",
        component: () => import("@/components/Login/login")
      },
      { path: "*", component: () => import("@/components/404page/404") }
    ];
    localStorage.setItem("menuList", JSON.stringify(state.addRoute));
    localStorage.setItem("slidermenu", JSON.stringify(state.menus));
  },
  getRootBtn(state, menu) {
    //将所有的按钮，*有权限*的设置在同一个数组里。
    state.rootBtn = []; //清空原有的btn
    ffff(state, menu);
  },
  setCasherRoute(state){//设置收银账号路由
    state.addRoute=[
      {
        path: "/cashierpost",
        name: "CashierPost",
        component: () => import("@/components/Cashier/CashierPost")
      },
      {//临时页面路由
        path: "/temporary",
        name: "temporary",
        component: () => import("@/components/404page/temporary"),
      },
      {
        path: "/login",
        name: "login",
        component: () => import("@/components/Login/login")
      },
      { path: "*", component: () => import("@/components/404page/404")}
    ]
  }
};

function ffff(state, menu) {
  let len = menu.length;
  for (let n = 0; n < len; n++) {
    if (menu[n].children.length == 0 && menu[n].childrenBtn.length != 0) {
      //无子菜单且有权限按钮时，将有权限的按钮加到rootBtn
      let data = menu[n].childrenBtn;
      let datalen = data.length;
      for (let i = 0; i < datalen; i++) {
        if (data[i].isroot) {
          state.rootBtn.push(data[i].key);
        }
      }
    } else if (menu[n].children.length != 0) {
      let menus = menu[n].children;
      ffff(state, menus);
    }
  }
}



const store = new Vuex.Store({
  state,
  mutations
});

export default store;
