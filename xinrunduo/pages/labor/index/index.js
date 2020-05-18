const app = getApp();
const util = require("../../../util/util.js");

Component({
    options: {
        addGlobalClass: true,
    },
    data: {
        labor: null,
        identify: false,
        starCount: 365,
        forksCount: 15685.55,
        visitTotal: 12,
    },
    attached() {
        let that = this;
        that.setData({
            labor: wx.getStorageSync("labor"),
            identify: app.isUserIdentify()
        });
        
    },
    methods: {
        reloadInfo() {
            util._get("/app/labor/info").then(ret => {
                if(ret.code == 0) {
                    let labor = ret.labor;
                    this.setData({
                        labor: labor,
                        identify: labor.idcard_front != null
                    });
                    wx.setStorageSync("labor", labor);
                }
            });
        },
        laborInfo() {
            if(this.data.identify) {
                wx.navigateTo({
                    url: "/pages/labor/info/info",
                });
            } else {
                wx.navigateTo({
                    url: "/pages/labor/identify/identify",
                });
            }
        },
    }
})