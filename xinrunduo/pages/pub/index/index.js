const app = getApp();

Component({
    options: {
        addGlobalClass: true,
    },
    data: {
        StatusBar: app.globalData.StatusBar,
        CustomBar: app.globalData.CustomBar,
        Custom: app.globalData.Custom
    },
});