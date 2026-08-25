const app = getApp()
const defaultheader = {
    'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
    "ascription": app.globalData.ascription,
    "openid": app.globalData.openid,
}
function request(request) {
    const header = {
        ...defaultheader,
        ...request.header
    }
    wx.request({
        method: request.method,
        url: request.url,
        data: request.data,
        header: header,
        success: request.success
    })
}
module.exports = {
    request
}