const util = require("../../../util/util.js");

Page({
    data: {
        countDown: 60,
        countDownText: "获取验证码",
        countDownStart: false,
        mobile: "",
        verifyCode: ""
    },

    onLoad: function(options) {

    },

    mobile(e) {
        this.setData({
            mobile: e.detail.value
        });
    },
    verifyCode(e) {
        this.setData({
            verifyCode: e.detail.value
        });
    },

    send() {
        let that = this;
        if (that.data.countDownStart) {
            return;
        }
        if (!that.data.mobile) {
            wx.showToast({
                title: "请输入手机号",
                icon: "none",
                duration: 1000
            });
            return;
        }
        that.setData({
            countDownStart: true
        });
        util._post("app/register/sms", {
            mobile: that.data.mobile.replace(/\s*/g, "")
        }).then(ret => {
            if (ret.code == 0) {
                wx.showToast({
                    title: ret.msg,
                    icon: "success",
                    duration: 1500
                });
                let countDown = that.data.countDown;
                let countDownText = that.data.countDownText;
                let timer = setInterval(() => {
                    countDown--;
                    countDownText = countDown + "秒";
                    that.setData({
                        countDown: countDown,
                        countDownText: countDownText
                    });
                    if (countDown <= 0) {
                        clearInterval(timer);
                        that.setData({
                            countDown: 60,
                            countDownText: "获取验证码",
                            countDownStart: false
                        });
                    }
                }, 1000);
            } else {
                wx.showToast({
                    title: ret.msg,
                    icon: "none",
                    duration: 1500
                });
            }
        });
    },

    login() {
        let that = this;
        let mobile = that.data.mobile.replace(/\s*/g, "");
        let verifyCode = that.data.verifyCode.replace(/\s*/g, "");
        if (!mobile || !verifyCode) {
            util.msg("请输入手机号和验证码！");
            return;
        }
        util._post("app/register/register", {
            mobile: mobile,
            verifyCode: verifyCode
        }).then(ret => {
            if (ret.code == 0) {
                util._ok(ret.msg, () => {
                    wx.setStorageSync("labor", ret.labor);
                    wx.navigateBack({
                        delta: -1
                    });
                });
            } else {
                util.msg(ret.msg);
            }
        });
    }
})