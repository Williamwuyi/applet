const app = getApp();
const util = require("../../../util/util.js");

Page({
    data: {
        contractId: "",
        fileList: []
    },

    onLoad: function (options) {
        let that = this;
        let contractId = options.contractId;
        util._get("/app/contract/sign?contractId=" + contractId).then(ret => {
            if(ret.code == 0) {
                let fileList = ret.contract.temp_file.split(",");
                for (let i in fileList) {
                    fileList[i] = app.globalData.host + "/app/contract/preview?filename=" + fileList[i];
                }
                that.setData({
                    contractId: contractId,
                    fileList: fileList
                });
            }
        });
    },

    Scale(e) {
        let current = this.data.fileList[e.currentTarget.dataset.index];
        wx.previewImage({
            current: current,
            urls: this.data.fileList
        });
    },

    Sign() {
        wx.redirectTo({
            url: "sign?contractId=" + this.data.contractId,
        });
    }
});