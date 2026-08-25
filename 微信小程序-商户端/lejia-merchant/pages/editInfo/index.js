// pages/editInfo/index.js
import http from '../../utils/http'
import Toast from '@vant/weapp/toast/toast';
var app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
        editName: false,
        editNameFocus: false,
        edit: false,
        focus: false,
        infoList: [{
                name: '手机号码',
                key: 'mobile',
                show: true,
                value: '',
                error: '',
                focus: false,
            },
            {
                name: '银行账户名称',
                key: 'bankname',
                show: true,
                value: '',
                focus: false,
            },
            {
                name: '银行卡号',
                key: 'bankcard',
                show: true,
                value: '',
                focus: false,
            },
            {
                name: '开户支行名称',
                key: 'bankBranchName',
                show: true,
                value: '',
                focus: false,
            },
            {
                name: '开户行大额行号',
                key: 'bankNo',
                show: true,
                value: '',
                focus: false,
            },
            {
                name: '地址',
                key: 'addr',
                show: true,
                value: '',
                focus: false,
            },
        ],
        name: '',
        background: [],
        videoLink: '',
        params: null,
        avatar: null,
        hasUpload: true,
        showVideo: false,
        disabled: true
    },
    getData() {
        var that = this,
            url = "/v2/app/vendor/get"
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            header: {
                "Content-Type": 'application/x-www-form-urlencoded',
                "openid": app.globalData.openid
            },
            success: function (res) {
                console.log(res);
                const result = res.data.result

                let avatar = null,
                    videoLink = null,
                    background = [],
                    hasUpload = false;
                // console.log(result);

                let list = that.data.infoList
                if (result) {
                    list.forEach(item => {
                        item.value = result[item.key]
                    })
                }
                if (result.files && result.files.length > 0) {
                    result.files.forEach(item => {
                        if (item.type === 'HEAD_ICON') {
                            avatar = item.url
                        }
                        if (item.type === 'PROPAGANDA') {
                            background.push(item);
                        }
                        if (item.type === 'VIDEO') {
                            videoLink = item.url
                            console.log(videoLink);
                        }
                    })
                    if (background && background.length >= 5) {
                        hasUpload = true
                    }
                }
                that.setData({
                    infoList: list,
                    name: result.name,
                    params: result,
                    background: background,
                    avatar,
                    videoLink,
                    hasUpload
                })
                console.log(that.data);
            }
        })
    },
    /**
     * @desc 获取运营端是否统一配置
     */
    getConfig() {
        let that = this;
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + '/v2/app/vendor/isUnified',
            data: {},
            header: {
                "Content-Type": 'application/json',
                "openid": app.globalData.openid
            },
            success: function (res) {
                that.setData({
                    disabled: res.data.result
                });
                console.log(that.data.disabled);
            }
        })
    },
    /**
     * 点击头像修改头像
     */
    onEditAvatar() {
        if (this.data.disabled) {
            return
        }
        const that = this
        wx.chooseImage({
            count: 1,
            success: function (res) {
                console.log(res.tempFilePaths);
                const imgurl = res.tempFilePaths[0]
                const imgFile = res.tempFiles[0]
                console.log('imgFile', imgFile);
                if (imgFile.size > 5242880) {
                    wx.showToast({
                        title: '上传的图片大小不要超过5MB',
                        icon: 'none',
                        duration: 2000
                    })
                    return
                }
                that.onUpload(imgurl, imgFile)
            }
        })
    },
    onUpload(imgurl, imgFile) {
        const that = this,
            url = "/v1/app/vendor/uploadImage";
        wx.uploadFile({
            filePath: imgurl,
            name: 'file',
            url: app.globalData.ajax_url + url,
            header: {
                "Content-Type": 'application/xml',
                "openid": app.globalData.openid
            },
            success: function (res) {
                that.changeHeadicon(JSON.parse(res.data).result.url);
            },
            fail(err) {
                wx.showToast({
                    title: '头像更改失败，请重试',
                    icon: 'none',
                    duration: 2000
                });
            }
        })
    },
    /**
     * @desc 改变商户头像
     */
    changeHeadicon(imgUrl) {
        const that = this,
            url = "/v2/app/vendor/upd",
            params = this.data.params;
        let hasheadIcon = false;
        for (let i in params.files) {
            let item = params.files[i];
            if (item.type == 'HEAD_ICON') {
                hasheadIcon = true;
                params.files[i].url = imgUrl;
                break;
            }
        }
        if (!hasheadIcon) {
            params.files.push({
                url: imgUrl,
                type: "HEAD_ICON"
            });
        }
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            header: {
                "Content-Type": 'application/json',
                "openid": app.globalData.openid
            },
            success: function (res) {
                console.log(res);
                wx.showToast({
                    title: '头像更改成功',
                    icon: 'none',
                    duration: 2000
                })
                that.getData();
            },
            fail(err) {
                wx.showToast({
                    title: '头像更改失败，请重试',
                    icon: 'none',
                    duration: 2000
                });
            }
        })
    },
    /**
     * 点击编辑名字
     */
    onEidtName() {
        if (this.data.disabled) {
            return
        }
        this.setData({
            editName: true,
            editNameFocus: true
        })
        // console.log(this.data.editName);
    },
    /**
     * 编辑名字失焦
     */
    onEditNameBlur() {
        this.setData({
            editName: false,
            editNameFocus: false
        })
    },
    onEditNameConfirm(event) {
        const name = event.detail
        const that = this,
            url = "/v2/app/vendor/upd",
            params = this.data.params;
        params.name = name
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            header: {
                "Content-Type": 'application/json',
                "openid": app.globalData.openid
            },
            success: function (res) {
                console.log(res);
                that.getData()
            }
        })
    },
    /** 点击单元格切换输出框 */
    onCellClick(event) {
        if (this.data.disabled) {
            return
        }
        let list = this.data.infoList
        const key = event.target.dataset.key
        const obj = list.find(item => item.key === key)
        // console.log(obj);
        obj.show = false
        obj.focus = true
        // console.log(this.data.infoList);
        this.setData({
            infoList: list
        })
    },
    /** 输入框失焦时还原单元格显示 */
    onBlur(event) {
        // console.log(event);
        let list = this.data.infoList
        const key = event.target.dataset.key
        const obj = list.find(item => item.key === key)
        obj.show = true
        obj.focus = false
        this.setData({
            infoList: list
        })
    },
    /** 输入框提交 */
    onConfirm(event) {
        const key = event.target.dataset.key
        const value = event.detail
        let list = this.data.infoList
        console.log('key', key);
        // 手机验证
        if (key === 'mobile') {
            const reg = /^[1][3,4,5,7,8,9][0-9]{9}$/
            console.log(reg.test(value));
            if (!reg.test(value)) {
                // // console.log('手机验证失败');
                // const found = list.find(item => {
                //     return item.key === key
                // })
                // found.error = '请输入正确的手机号码'
                // return
                Toast.fail('请输入正确的手机号码')
                return
            }
        }
        // 开始提交
        const that = this,
            url = "/v2/app/vendor/upd",
            params = this.data.params;
        params[key] = value
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            header: {
                "Content-Type": 'application/json',
                "openid": app.globalData.openid
            },
            success: function (res) {
                if (!res.success) {
                    wx.showToast({
                        title: res.data.msg,
                        icon: 'none',
                        duration: 2000
                    })
                }
                that.getData()
            }
        })
    },
    /** 查看视频 */
    onLookVideo() {
        const that = this;
        var videoContext = wx.createVideoContext('videoId', this);
        this.setData({
            showVideo: true
        });
        videoContext.requestFullScreen();
        setTimeout(function () {
            videoContext.play()
        }, 500);
        this.videoContext.play();
    },
    /** 视频退出全屏 */
    leaveVideo: function (e) {
        if (!e.detail.fullScreen) {
            var videoContext = wx.createVideoContext('videoId', this);
            videoContext.pause();
            this.setData({
                videoLink: null,
                showVideo: false
            });
        }
    },
    onClose() {
        this.videoContext.pause()
        this.setData({
            showVideo: false
        })
    },
    /** 点击上传图片 */
    onUploadImg() {
        const that = this
        const imgList = this.data.background
        console.log(imgList.length);
        const count = 5 - imgList.length
        wx.chooseImage({
            count: count,
            success: function (res) {
                console.log(res);
                let imgurls = res.tempFilePaths;
                let imgfiles = res.tempFiles;

                let message = [];
                console.log(imgfiles);
                for (let i = imgfiles.length - 1; i >= 0; i--) {
                    let item = imgfiles[i];
                    if (item.size > 5242880) {
                        imgfiles.splice(i, 1);
                        imgurls.splice(i, 1);
                        message.push(i);
                    }
                }
                if (!imgurls.length) {
                    wx.showToast({
                        title: '所有图片的大小都超过5MB，未上传',
                        icon: 'none',
                        duration: 2000
                    })
                    return
                }
                wx.showLoading({
                    title: '图片上传中'
                });
                that.onUploadSwiperImg(imgurls, message)
            }
        })
    },
    onUploadSwiperImg(imgurl, message) {
        console.log(imgurl)
        const that = this,
            params = this.data.params,
            url = "/v1/app/vendor/uploadImage";
        wx.uploadFile({
            filePath: imgurl[0],
            name: 'file',
            url: app.globalData.ajax_url + url,
            header: {
                "Content-Type": 'application/xml',
                "openid": app.globalData.openid
            },
            success: function (res) {
                // that.changeHeadicon(JSON.parse(res.data).result.url );
                // that.addSwiper(JSON.parse(res.data).result.url );
                params.files.push({
                    url: JSON.parse(res.data).result.url,
                    type: 'PROPAGANDA'
                })
                that.setData({
                    params,
                })
                imgurl.shift()
                if (imgurl && imgurl.length > 0) {
                    that.onUploadSwiperImg(imgurl,message)
                } else {
                    that.addSwiper(message)
                }
            },
            fail(err) {
                wx.showToast({
                    title: '上传图片失败，请重试',
                    icon: 'none',
                    duration: 2000
                });
            }
        })
    },
    inputFormat(e) {
        console.log(e);
        if (e.currentTarget.dataset.key == 'bankcard') {
            this.setData({
                ['infoList[2].value']: e.detail.replace(/[^\d]/g, '')
            })
        }
    },
    addSwiper(message) {

        const that = this,
            url = "/v2/app/vendor/upd",
            params = this.data.params;
        // params.files.push({
        //     url: imgUrl,
        //     type: 'PROPAGANDA'
        // })
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            header: {
                "Content-Type": 'application/json',
                "openid": app.globalData.openid
            },
            success: function (res) {
                wx.hideLoading();
                if (message.length) {
                    wx.showToast({
                        title: '图片上传成功,有部分图片大小超过5mb,未上传',
                        icon: 'none',
                        duration: 2000
                    })
                } else {
                    wx.showToast({
                        title: '图片上传成功',
                        icon: 'none',
                        duration: 2000
                    })
                }
                that.getData();
            },
            fail(err) {
                wx.showToast({
                    title: '图片上传失败，请重试',
                    icon: 'none',
                    duration: 2000
                });
            }
        })
    },
    /** 图片超过5张后点击图片切换图片 */
    onChangeSwiperImg(event) {
        // console.log(event);
        if (this.data.disabled) {
            return
        }
        const {
            hasUpload
        } = this.data
        const {
            pkey
        } = event.target.dataset
        const that = this
        if (hasUpload) {
            wx.chooseImage({
                count: 1,
                success: function (res) {
                    console.log(res.tempFilePaths);
                    const imgurl = res.tempFilePaths[0];
                    const imgFile = res.tempFiles[0]
                    if (imgFile.size > 5242880) {
                        wx.showToast({
                            title: '上传的图片大小不要超过5MB',
                            icon: 'none',
                            duration: 2000
                        })
                        return
                    }
                    that.changeSwiperImg(imgurl, pkey)
                }
            })
        }
    },
    changeSwiperImg(imgurl, pkey) {
        const that = this,
            url = "/v1/app/vendor/uploadImage";
        wx.uploadFile({
            filePath: imgurl,
            name: 'file',
            url: app.globalData.ajax_url + url,
            header: {
                "Content-Type": 'application/xml',
                "openid": app.globalData.openid
            },
            success: function (res) {
                // that.changeHeadicon(JSON.parse(res.data).result.url );
                that.changeSwiper(JSON.parse(res.data).result.url, pkey);
            },
            fail(err) {
                wx.showToast({
                    title: '上传图片失败，请重试',
                    icon: 'none',
                    duration: 2000
                });
                const imgFile = res.tempFiles[0]
                console.log('imgFile', imgFile);
                if (imgFile.size > 5242880) {
                    wx.showToast({
                        title: '上传的图片大小不要超过5MB',
                        icon: 'none',
                        duration: 2000
                    })
                    return
                }
            }
        })
    },
    changeSwiper(imgUrl, pkey) {
        const that = this,
            url = "/v2/app/vendor/upd",
            params = this.data.params;
        // params.files.push({
        //     url: imgUrl,
        //     type: 'PROPAGANDA'
        // })
        const found = params.files.find(item => {
            return item.pkey === pkey
        })
        found.url = imgUrl
        http.request({
            method: "POST",
            url: app.globalData.ajax_url + url,
            data: params,
            header: {
                "Content-Type": 'application/json',
                "openid": app.globalData.openid
            },
            success: function (res) {
                console.log(res);
                wx.showToast({
                    title: '图片上传成功',
                    icon: 'none',
                    duration: 2000
                })
                that.getData();
            },
            fail(err) {
                wx.showToast({
                    title: '图片上传失败，请重试',
                    icon: 'none',
                    duration: 2000
                });
            }
        })
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.getData();
        this.getConfig();
        this.videoContext = wx.createVideoContext('videoId');
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    // onShareAppMessage: function () {
    //     return {
    //         title: '菜篮商户',
    //         path: '/pages/introduce/introduce',
    //       }
    // }
})