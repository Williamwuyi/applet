Page({
    data: {
        labor: null
    },

    onLoad: function(options) {
        let labor = wx.getStorageSync("labor");
        this.setData({
            labor: labor
        });
    }
})