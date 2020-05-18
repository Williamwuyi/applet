const app = getApp();
const util = require("../../../util/util.js");
const onfire = require("../../../util/onfire.js");

Page({
    data: {
        idCardInfo: null,
        formToken: "",
        laborEduLevelIndex: null,
        rangeLaborEduLevel: [],
        laborPoliticsIndex: null,
        rangeLaborPolitics: [],
        laborNationIndex: null,
        rangeLaborNation: [],
        areaName: "请选择工作地区",
        areaId: 0,
        areaArray: [[], [], []],
        areaIndex: [0, 0, 0],
        objectAreaArray: [[], [], []]
    },

    onLoad(option) {
        let that = this;
        let idCardInfo = wx.getStorageSync("idCardInfo");
        let formToken = option.formToken;
        if (idCardInfo == null || formToken == null) {
            util._msg("初始化失败！", () => {
                wx.navigateBack({
                    delta: -1
                });
            });
        }
        that.setData({
            idCardInfo: idCardInfo,
            formToken: formToken,
            rangeLaborEduLevel: wx.getStorageSync("laborEduLevel"),
            rangeLaborPolitics: wx.getStorageSync("laborPolitics"),
            rangeLaborNation: wx.getStorageSync("laborNation")
        });
        that.InitArea(that.data.areaId);
    },

    PickerLaborEduLevelChange(e) {
        this.setData({
            laborEduLevelIndex: e.detail.value
        });
    },
    PickerLaborPoliticsChange(e) {
        this.setData({
            laborPoliticsIndex: e.detail.value
        });
    },
    PickerLaborNationChange(e) {
        this.setData({
            laborNationIndex: e.detail.value
        });
    },

    //地区选择
    LoadArea(areaId, level) {
        let that = this;
        app.GetArea(areaId).then(res => {
            res.forEach(item => {
                that.data.areaArray[level].push(item.area_name);
                that.data.objectAreaArray[level].push(item);
            });
            that.setData({
                ["areaArray[" + level + "]"]: that.data.areaArray[level],
                ["objectAreaArray[" + level + "]"]: that.data.objectAreaArray[level]
            });
        });
    },
    InitArea(areaId) {
        let that = this;
        app.GetArea(areaId).then(city => {
            city.forEach(item => {
                that.data.areaArray[0].push(item.area_name);
                that.data.objectAreaArray[0].push(item);
            });
            that.setData({
                ["areaArray[0]"]: that.data.areaArray[0],
                ["objectAreaArray[0]"]: that.data.objectAreaArray[0]
            });
            app.GetArea(city[0].area_id).then(county => {
                county.forEach(item => {
                    that.data.areaArray[1].push(item.area_name);
                    that.data.objectAreaArray[1].push(item);
                });
                that.setData({
                    ["areaArray[1]"]: that.data.areaArray[1],
                    ["objectAreaArray[1]"]: that.data.objectAreaArray[1]
                });
                app.GetArea(county[0].area_id).then(village => {
                    village.forEach(item => {
                        that.data.areaArray[2].push(item.area_name);
                        that.data.objectAreaArray[2].push(item);
                    });
                    that.setData({
                        ["areaArray[2]"]: that.data.areaArray[2],
                        ["objectAreaArray[2]"]: that.data.objectAreaArray[2]
                    });
                });
            });
        });
    },
    AreaChange(e) {
        let that = this;
        let areaName = "";
        let lastAreas = that.data.objectAreaArray[2];
        for (let i = 0; i <= 2; i++) {
            let areas = that.data.objectAreaArray[i];
            areaName += areas[e.detail.value[i]].area_name + "-";
        }
        areaName = areaName.substring(0, areaName.length - 1);
        that.setData({
            areaIndex: e.detail.value,
            areaId: lastAreas[e.detail.value[2]].area_id,
            areaName: areaName
        });
    },
    AreaColumnChange(e) {
        let that = this;
        let data = {
            areaArea: this.data.areaArea,
            areaIndex: this.data.areaIndex
        };
        data.areaIndex[e.detail.column] = e.detail.value;
        switch (e.detail.column) {
            case 0:
                let city = that.data.objectAreaArray[0][e.detail.value];
                that.setData({
                    ['areaIndex[0]']: e.detail.value,
                    ['areaArray[1]']: [],
                    ['objectAreaArray[1]']: [],
                    ['areaArray[2]']: [],
                    ['objectAreaArray[2]']: []
                });
                app.GetArea(city.area_id).then(county => {
                    county.forEach(item => {
                        that.data.areaArray[1].push(item.area_name);
                        that.data.objectAreaArray[1].push(item);
                    });
                    that.setData({
                        ['areaArray[1]']: that.data.areaArray[1],
                        ['objectAreaArray[1]']: that.data.objectAreaArray[1]
                    });
                    app.GetArea(county[0].area_id).then(village => {
                        village.forEach(item => {
                            that.data.areaArray[2].push(item.area_name);
                            that.data.objectAreaArray[2].push(item);
                        });
                        that.setData({
                            ['areaArray[2]']: that.data.areaArray[2],
                            ['objectAreaArray[2]']: that.data.objectAreaArray[2]
                        });
                    });
                });
                break;
            case 1:
                let county = that.data.objectAreaArray[1][e.detail.value];
                that.setData({
                    ['areaIndex[1]']: e.detail.value,
                    ['areaArray[2]']: [],
                    ['objectAreaArray[2]']: []
                });
                that.LoadArea(county.area_id, 2);
                break;
            case 2:

                break;
        }
    },
    Submit(e) {
        let that = this;
        let formData = {};
        let idCardInfo = that.data.idCardInfo;
        formData.form_token = that.data.formToken;
        formData["labor.realname"] = idCardInfo.realname;
        formData["labor.paper_number"] = idCardInfo.number;
        formData["labor.age"] = idCardInfo.age;
        formData["labor.sex"] = idCardInfo.sex == "女" ? 0 : 1;
        formData["labor.nation"] = that.data.rangeLaborNation[that.data.laborNationIndex].dict_item_id;
        formData["labor.politics"] = that.data.rangeLaborPolitics[that.data.laborNationIndex].dict_item_id;
        formData["labor.edu_level"] = that.data.rangeLaborEduLevel[that.data.laborEduLevelIndex].dict_item_id;
        formData["labor.area_id"] = that.data.areaId;
        util._post("/app/labor/edit", formData).then(ret => {
            util._msg(ret.msg, () => {
                if(ret.code == 0) {
                    onfire.fire("labor-update");
                    wx.removeStorageSync("idCardInfo");
                    wx.removeStorageSyns("laborEduLevel");
                    wx.removeStorageSyns("laborPolitics");
                    wx.removeStorageSyns("laborNation");
                    wx.navigateBack({
                        delta: -1
                    });

                }
            });
        });
        return false;
    }
});