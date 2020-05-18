const app = getApp();
const onfire = require("../../util/onfire.js");

Page({
    data: {
        PageCur: "pub"
    },
    NavChange(e) {
        let cur = e.currentTarget.dataset.cur;
        if (cur != "pub") {
            if (!app.isUserReg()) {
                wx.navigateTo({
                    url: "../labor/register/register",
                });
                return;
            }
        }
        this.setData({
            PageCur: cur
        });
    },
    onLoad() {
        let that = this;
        that.OnLaborUpdate();
        that.OnOrderChange();
    },
    OnLaborUpdate() {
        //人员信息完善事件
        onfire.on("labor-update", () => {
            that.selectComponent("#labor").reloadInfo();
        });
    },
    OnOrderChange() {
        //订单状态更改事件
        onfire.on("order-change", (stauts) => {

        });
    }
})