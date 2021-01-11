// 图片上传的过程1.监测有无图片;2.无图片出现图片上传icon
// 点击icon选择本地图片(图片上传到缓存,返回一缓存里的地址)
// 3.将缓存中的图片上传(upload)到服务器,返回图片存储的地址
// 4.通过异步,将地址复制给要表单;
// 封装一下图片上传到服务器过程的函数,使用异步,在上传完毕获取到
// 注意这个type="file"
function uploadPic (url,filePath,name){
  return new Promise((resolve,reject) => {
    wx.uploadFile({
      // url: getApp().globalData.baseurl+url,
      // url:'http://192.168.10.132:8001/org/person/imgUpload',
      // url:'http://192.168.10.146:8003/person/imgUpload',
      url:url,
      filePath: filePath,
      name: name,
      header: { "Content-Type": "multipart/form-data" },
      success(res){
        // wx.uploads的成功返回值是一个string类型,所以此处需要进行转换
        const data = JSON.parse(res.data).datas;
          resolve(data)
        // console.log(data)
      }
    })
  })
}
module.exports = {
  uploadPic
};
