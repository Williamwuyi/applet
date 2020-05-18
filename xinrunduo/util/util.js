const app = getApp();

const request = (method, url, data) => {
    return new Promise((resolve, reject) => {
        _showLoading();
        wx.request({
            url: app.globalData.host + url,
            method: method,
            data: data,
            header: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Authorization": wx.getStorageSync("token") || ""
            },
            success: res => {
                let ret = res.data;
                if (ret.code == 0) {
                    resolve(ret);
                } else if (ret.code == 40004) {
                    wx.navigateTo({
                        url: "/page/labor/register/register",
                    });
                } else {
                    //如果出现异常则弹出dialog
                    wx.showModal({
                        title: "系统提示",
                        content: ret.msg,
                        confirmColor: '#118EDE',
                        showCancel: false,
                        success: function(res) {
                            if (res.confirm) {}
                        }
                    });
                }
            },
            fail: res => {
                wx.showToast({
                    title: "服务器暂时无法连接",
                    icon: "loading",
                    duration: 2000
                });
                reject(res);
            },
            complete: () => {

                setTimeout(function() {
                    _hideLoading();
                }, 100);
            }
        });
    });
}

const _get = (url, data) => {
    return request("GET", url, data);
}

const _post = (url, data) => {
    return request("POST", url, data);
}

const toast = (icon, msg, callback) => {
    wx.showToast({
        title: msg,
        icon: icon,
        duration: 1500,
        mask: true,
        success: () => {
            callback && callback();
        }
    });
}

const _msg = (msg, callback) => {
    toast("none", msg, callback);
}

const _ok = (msg, callback) => {
    toast("success", msg, callback);
}

const _showLoading = () => {
    wx.showLoading({
        title: "加载中",
        mask: true
    });
}

const _hideLoading = () => {
    wx.hideLoading();
}

module.exports = {
    _get,
    _post,
    _msg,
    _ok,
    _showLoading,
    _hideLoading
}