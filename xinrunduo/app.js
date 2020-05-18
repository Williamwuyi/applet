//app.js
App({
    onLaunch: function() {
        wx.getSystemInfo({
            success: e => {
                this.globalData.StatusBar = e.statusBarHeight;
                let custom = wx.getMenuButtonBoundingClientRect();
                this.globalData.Custom = custom;
                this.globalData.CustomBar = custom.bottom + custom.top - e.statusBarHeight;
            }
        });

        let token = this.GetToken();
        if (token) {
            this.verify(token);
        } else {
            this.login();
        }

    },
    verify(token) {
        wx.request({
            url: this.globalData.host + "/app/login/verify",
            header: {
                "content-type": "application/x-www-form-urlencoded",
                "Authorization": token
            },
            data: {
                token: token
            },
            success: res => {
                let ret = res.data;
                if (ret.code == 0) {
                    wx.setStorageSync("labor", ret.labor);
                }else if(ret.code == 40003 || ret.code == 40005) {
                    this.login();
                }
            }
        });
    },
    login() {
        wx.login({
            success: res => {
                wx.request({
                    url: this.globalData.host + "/app/login",
                    method: "post",
                    header: {
                        "content-type": "application/x-www-form-urlencoded"
                    },
                    data: {
                        code: res.code
                    },
                    success: res => {
                        let ret = res.data;
                        if (ret.code == 0) {
                            wx.setStorageSync("token", ret.token);
                            wx.setStorageSync("labor", ret.labor);
                        }
                    }
                });
            }
        });
    },
    isUserReg() {
        let labor = wx.getStorageSync("labor");
        return labor && labor.labor_id != null;
    },
    isUserIdentify() {
        let labor = wx.getStorageSync("labor");
        return labor && labor.idcard_front != null;
    },
    GetToken() {
        return wx.getStorageSync("token");
    },
    GetArea(parentId) {
        let that = this;
        return new Promise(function (resolve, reject) {
            wx.request({
                url: that.globalData.host + "/app/pub/area",
                header: {
                    "Authorization": that.GetToken()
                },
                data: {
                    parentId: parentId
                },
                success: res => {
                    let ret = res.data;
                    if(ret.code == 0) {
                        resolve(ret.areas);
                    }else {
                        reject(res);
                    }
                },
                fail: res => {
                    reject(res);
                }
            })
        })
    },
    globalData: {
        host: "http://192.168.20.105:99"
        // host: "http://192.168.0.10:99"
    }

});