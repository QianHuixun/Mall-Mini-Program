// pages/my/collect/index.js
const app = getApp();
import http from '../../../utils/http'
var loadMoreView;
var spaceView;
// var goCartView;
Page({

	/**
	 * 页面的初始数据
	 */
	data: {
		active: "1",
		goodsData: [], //商品收藏
		goodsPage: 0,
		menuData: [], //菜谱收藏
		menuPage: 0,
		boothData: [], //摊位收藏
		boothPage: 0,
		pagesize: 12,
		isShow: false, //是否显示 规格选择dialog
		isAuto: true,
		iShidden: true,
		numData: {
			boothNum: "",
			cookfdNum: "",
			goodsNum: "",
		}//数量
	},

	/**
	 * 生命周期函数--监听页面加载
	 */
	onLoad: function (options) {
		loadMoreView = this.selectComponent("#loadMoreView");
		spaceView = this.selectComponent("#spaceView");
		// goCartView = this.selectComponent("#goCartView");
		// goCartView.getPosition();
		if(options.active){
			console.log('onLoad');
			this.setData({
				active: options.active
			});
		}
	},

	loadMoreListener: function (e) {
		console.log('loadMoreListener');
		this.loadData()
	},
	clickLoadMore: function (e) {
		console.log('clickLoadMore');
		this.loadData()
	},
	/**获取数量 */
	getNumData: function () {
		var _this = this;
		var url = "/v1/app/market/goods/collection/get/ctype/num",
			params = {};
		http.request({
			method: "POST",
			url: app.globalData.ajax_url + url,
			data: params,
			header: {
				'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
				"openid": app.globalData.openid,
				"farmer": app.globalData.location.pkey
			},
			success: function (res) {
				if (res.data.success) {
					_this.setData({
						numData: res.data.result
					});
				} else {
					wx.showToast({
						title: res.data.msg || '',
						icon: 'none'
					});
				}
			}
		});
	},
	/**
	 * 加载数据
	 */
	loadData: function () {
		console.log("active", this.data.active)
		var _this = this;
		var url = "/v1/app/market/goods/collection/query",
			params = {
				ctype: parseInt(this.data.active), //类型 0: 菜谱/ 1:商品  2:商户
				page: parseInt(this.data.active) == 0 ? this.data.menuPage : (parseInt(this.data.active) == 1 ? this.data.goodsPage : this.data.boothPage),
				pagesize: this.data.pagesize,
			};

		http.request({
			method: "POST",
			url: app.globalData.ajax_url + url,
			data: params,
			header: {
				'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
				"openid": app.globalData.openid,
				"farmer": app.globalData.location.pkey
			},
			success: function (res) {
				if (res.data.success) {
					if (_this.data.active == "0") { //菜谱         
						if (_this.data.menuPage == 0) {
							_this.setData({
								menuData: res.data.result.content,
								menuPage: ++_this.data.menuPage
							});
						} else {
							_this.setData({
								menuData: _this.data.menuData.concat(res.data.result.content),
								menuPage: ++_this.data.menuPage
							});
						}
						res.data.result.curPage = _this.data.menuPage;
						loadMoreView.loadMoreComplete(res.data);
					} else if (_this.data.active == "1") { //商品
						if (_this.data.goodsPage == 0) {
							_this.setData({
								goodsData: res.data.result.content,
								goodsPage: ++_this.data.goodsPage
							});
						} else {
							_this.setData({
								goodsData: _this.data.goodsData.concat(res.data.result.content),
								goodsPage: ++_this.data.goodsPage
							});
						}
						res.data.result.curPage = _this.data.goodsPage;
						loadMoreView.loadMoreComplete(res.data);
					} else {
						if (_this.data.boothPage == 0) {
							_this.setData({
								boothData: res.data.result.content,
								boothPage: ++_this.data.boothPage
							});
						} else {
							_this.setData({
								boothData: _this.data.boothData.concat(res.data.result.content),
								boothPage: ++_this.data.boothPage
							});
						}
						res.data.result.curPage = _this.data.boothPage;
						loadMoreView.loadMoreComplete(res.data);
					}
				} else {
					wx.showToast({
						title: res.data.msg || '',
						icon: 'none'
					});
				}
			}
		});
	},
	//回到顶部
	goTop: function (e) { // 一键回到顶部
		if (wx.pageScrollTo) {
			wx.pageScrollTo({
				scrollTop: 0,
				duration: 0
			})
		}
	},
	/**
	 * Tab标签页修改事件
	 */
	onChange(event) {
		console.log('onChange');
		this.setData({
			active: event.detail.name
		});
		this.goTop();
		this.loadData();
	},
	/**
	 * 获取当前商品的规格数量
	 */
	getSpaceNumber: function (data) {
		var url = "/v1/app/market/goods/space/totalAmount",
			pkey = data.currentTarget.dataset.pkey,
			space = data.currentTarget.dataset.space,
			that = this;
		http.request({
			method: "POST",
			url: app.globalData.ajax_url + url,
			data: {
				pkey: pkey
			},
			header: {
				'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
				"openid": app.globalData.openid,
				"farmer": app.globalData.location.pkey
			},
			success: function (res) {
				if (res.data.result == 1) {
					that.handleAddTOCart(data, pkey, space);
				} else {
					that.setData({
						isShow: true
					});
					spaceView.getData(pkey);
				}
			}
		})
	},
	/**
	 * 添加到购物车
	 */
	handleAddTOCart: function (data, pkey, space) {
		var url = "/v1/app/market/lm/member/gwc/ins",
			that = this,
			params;
		if (data.detail.hasOwnProperty('data')) {
			params = {
				goodsPkey: data.detail.goodsPkey,
				goodsNum: 1,
				space: data.detail.space,
			}
			data = data.detail.data
		} else
			params = {
				goodsPkey: pkey,
				goodsNum: 1,
				space: space,
			}
		http.request({
			method: "POST",
			url: app.globalData.ajax_url + url,
			data: params,
			header: {
				'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
				"openid": app.globalData.openid,
				"farmer": app.globalData.location.pkey
			},
			success: function (res) {
				if (res.data.code == "999") {
					that.setData({
						iShidden: false
					})
					return;
				};
				if (res.data.success) {
					// goCartView.addshopcar(data);
					app.getBuycarNum();
					wx.showToast({
						title: '已加入购物车',
						icon: "none"
					});
				} else {
					wx.showToast({
						title: res.data.msg || '',
						icon: "none"
					});
				}
			}
		});
	},
	/**
	 * 删除收藏
	 */
	handleDelete: function (data) {
		var url = "/v1/app/market/goods/collection/del",
			that = this,
			pkey = data.currentTarget.dataset.pkey,
			index = data.currentTarget.dataset.index,
			goodsData = this.data.goodsData,
			menuData = this.data.menuData;
		boothData = this.data.boothData;

		wx.showModal({
			title: '操作',
			content: '确定删除？',
			success(res) {
				if (res.confirm) {
					http.request({
						method: "POST",
						url: app.globalData.ajax_url + url,
						data: {
							pkey: pkey
						},
						header: {
							'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
							"openid": app.globalData.openid,
							"farmer": app.globalData.location.pkey
						},
						success: function (res) {
							if (res.data.success) {
								if (that.data.active == "1") {
									goodsData.splice(index, 1);
									that.setData({
										goodsData: goodsData
									})
								} else if (that.data.active == "0") {
									menuData.splice(index, 1);
									that.setData({
										menuData: menuData
									})
								} else {
									boothData.splice(index, 1);
									that.setData({
										boothData: boothData
									})
								}
							} else {
								wx.showToast({
									title: '删除失败',
									icon: "none"
								})
							}
						}
					});
				}
			}
		});
	},
	handleClick: function (data) {
		wx.navigateTo({
			url: "/pages/shouyeGroup/recipe/detail?pkey=" + data.currentTarget.dataset.pkey,
		})
	},

	handleBoothClick: function (data) {
		wx.navigateTo({
      url: `/pages/shouyeGroup/merchant/index?pkey=${data.currentTarget.dataset.pkey}&isClassify=true`
    })
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
		console.log('onshow');
		this.setData({
			menuPage: 0,
			goodsPage: 0,
			boothPage: 0,
		});
		this.loadData();
		this.getNumData();
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
		loadMoreView.loadMore()
	},
	//这里为授权会掉函数，授权后会调用此方法，请求一些需要访问权限的借口请放在这个
	//函数里面执行
	onLoadFun: function () {
		console.log('onLoadFun');
		this.loadData();
	}
})