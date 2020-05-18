const app = getApp();
const util = require("../../../util/util.js");

Page({
    data: {
        formToken: "",
        idCardFront: "",
        idCardBack: "",
        idCardInfo: null,
        frontUploaded: false,
        backUploaded: false
    },

    onLoad() {
        let that = this;
        util._get("/app/labor/edit").then(ret => {
            if (ret.code == 0) {
                that.setData({
                    formToken: ret.param.form_token
                });

                wx.setStorageSync("laborNation", ret.param.labor_nation);
                wx.setStorageSync("laborEduLevel", ret.param.labor_edu_level);
                wx.setStorageSync("laborPolitics", ret.param.labor_politics);
            } else {
                util._msg("初始化失败！", () => {
                    wx.navigateBack({
                        delta: -1
                    });
                });
            }
        });
    },

    upload(side, filePath) {
        let that = this;
        util._showLoading();
        wx.uploadFile({
            url: app.globalData.host + "/app/labor/identify/idCardVerify",
            filePath: filePath,
            name: "idcard",
            header: {
                "Authorization": app.GetToken()
            },
            formData: {
                form_token: that.data.formToken,
                side: side
            },
            success: res => {
                let ret = JSON.parse(res.data);
                util._msg(ret.msg, () => {
                    if (ret.code == 0) {
                        if (side == "front") {
                            that.setData({
                                idCardFront: filePath,
                                frontUploaded: true,
                                idCardInfo: ret.info
                            });
                        }
                        if (side == "back") {
                            that.setData({
                                idCardBack: filePath,
                                backUploaded: true
                            });
                        }
                    }
                });
            },
            complete: () => {
                util._hideLoading();
            }
        });
    },

    uploadFront() {
        let that = this;
        wx.chooseImage({
            count: 1,
            sizeType: ["original"],
            sourceType: ["album", "camera"],
            success: res => {
                let tempFiles = res.tempFilePaths;
                that.upload("front", tempFiles[0]);
            }
        });
    },

    uploadBack() {
        let that = this;
        wx.chooseImage({
            count: 1,
            sizeType: ["original"],
            sourceType: ["album", "camera"],
            success: res => {
                let tempFiles = res.tempFilePaths;
                that.upload("back", tempFiles[0]);
            }
        });
    },

    submit() {
        let that = this;
        wx.setStorageSync("idCardInfo", that.data.idCardInfo);
        wx.redirectTo({
            url: "../info/edit?formToken=" + that.data.formToken,
        });
    }
});