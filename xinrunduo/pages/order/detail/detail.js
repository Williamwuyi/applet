const app = getApp();
const util = require("../../../util/util.js");
const onfire = require("../../../util/onfire.js");

Page({
    data: {
        orderId: "",
        detail: null
    },

    onLoad: function(options) {
        let that = this;
        let orderId = options.orderId;
        that.setData({
            orderId: orderId
        });
        that.LoadDetail();
    },

    LoadDetail() {
        let that = this;
        util._get("/app/order/detail?orderId=" + that.data.orderId).then(ret => {
            if (ret.code == 0) {
                that.setData({
                    detail: ret.detail
                });
                if(!ret.detail.sign_status) {
                    that.OnContractSign();
                }
            } else {
                util._msg(ret.msg);
            }
        });
    },

    OnContractSign() {
        onfire.on("contract-sign", () => {
            LoadDetail();
        });
    },

    Confirm() {
        let detail = this.data.detail;
        if (!detail.sign_status) {
            wx.navigateTo({
                url: "../../contract/sign/view?contractId=" + detail.contract_id,
            });
        } else {
            util._post("/app/order/confirm", {
                orderId: this.data.orderId
            });
        }
    }
})