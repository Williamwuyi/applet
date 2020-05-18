const app = getApp();
const util = require("../../../util/util.js");
const onfire = require("../../../util/onfire.js");

Page({
    data: {
        context: null,
        contractId: ""
    },
    onLoad: function(options) {
        let contractId = options.contractId;
        let context = wx.createCanvasContext("canvas");
        context.beginPath();
        context.setStrokeStyle("#333333");
        context.setLineWidth(4);
        context.setLineCap("round");
        context.setLineJoin("round");
        this.setData({
            context: context,
            contractId: contractId
        });
    },
    bindtouchstart(e) {
        this.data.context.moveTo(e.changedTouches[0].x, e.changedTouches[0].y)
    },
    /**记录移动点，刷新绘制 */
    bindtouchmove(e) {
        this.data.context.lineTo(e.changedTouches[0].x, e.changedTouches[0].y);
        this.data.context.stroke();
        this.data.context.draw(true);
        this.data.context.moveTo(e.changedTouches[0].x, e.changedTouches[0].y);
    },
    /**清空画布 */
    clearDraw() {
        this.data.context.draw();
        this.data.context.setStrokeStyle("#333333");
        this.data.context.setLineWidth(4);
        this.data.context.setLineCap("round");
        this.data.context.setLineJoin("round");
    },
    /**导出图片 */
    getImage() {
        let that = this;

        util._showLoading();
        that.data.context.draw(false, wx.canvasToTempFilePath({
            x: 0,
            y: 0,
            canvasId: "canvas",
            success: res => {
                that.Upload(res.tempFilePath);
            },
            fail: () => {
                util._msg("图片创建失败！");
            }
        }));
    },
    Upload(tempFilePath) {
        let that = this;
        wx.uploadFile({
            url: app.globalData.host + "/app/contract/sign",
            filePath: tempFilePath,
            name: "sign",
            header: {
                "Authorization": app.GetToken()
            },
            formData: {
                contractId: that.data.contractId
            },
            success: res => {
                let ret = JSON.parse(res.data);
                util._msg(ret.msg, () => {
                    if (ret.code == 0) {
                        console.log(ret);
                    }
                });
            },
            complete: () => {
                util._hideLoading();
            }
        });
    }
});