const app = getApp();
const util = require("../../../util/util.js");

Component({
    options: {
        addGlobalClass: true,
    },
    data: {
        CustomBar: app.globalData.CustomBar,
        TabIndex: 0,
        TabNav: ["待接单", "待开始", "进行中", "已完成"],
        TabParam: [{
            status: "confirm",
            loaded: false,
        }, {
            status: "pended",
            loaded: false,
        }, {
            status: "active",
            loaded: false,
        }, {
            status: "finish",
            loaded: false,
        }, ],
        confirmPage: {},
        pendedPage: {},
        activePage: {},
        finishPage: {},
    },
    attached() {
        let that = this;
        that.loadData();
    },
    methods: {
        loadData(target) {
            if (!app.isUserIdentify()) {
                return;
            }
            let that = this;
            let tabIndex = target || that.data.TabIndex;
            let tabParam = that.data.TabParam;
            let current = tabParam[tabIndex];
            if (current.loaded) {
                return;
            }
            util._get("/app/order/list", {
                page: 1,
                limit: 10,
                status: current.status
            }).then(ret => {
                let page = ret.page;
                switch (current.status) {
                    case "confirm":
                        that.setData({
                            confirmPage: ret.page
                        });
                        break;
                    case "pended":
                        that.setData({
                            pendedPage: ret.page
                        });
                        break;
                    case "active":
                        that.setData({
                            activePage: ret.page
                        });
                        break;
                    case "finish":
                        that.setData({
                            finishPage: ret.page
                        });
                        break;
                }
                current.loaded = true;
                tabParam[tabIndex] = current;
                this.setData({
                    TabIndex: tabIndex,
                    TabParam: tabParam
                });
            });
        },

        tabSelect(e) {
            this.setData({
                TabIndex: e.currentTarget.dataset.id,
                scrollLeft: (e.currentTarget.dataset.id - 1) * 60
            });
            this.loadData();
        },

        Confirm(e) {
            let that = this;
            let index = e.currentTarget.dataset.index;
            let order = that.data.confirmPage.list[index];
            wx.navigateTo({
                url: "../order/detail/detail?orderId=" + order.order_id,
            });
        },
        Proof(e) {

        }
    }
});