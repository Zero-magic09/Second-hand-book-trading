const api = require('../../utils/api.js');

Page({
  data: {
    orderType: 'buy', // 'buy' or 'sell'
    filter: '全部',
    orders: []
  },

  onLoad() {
    this.loadOrders();
  },

  onShow() {
    // 每次显示页面时刷新数据，因为可能有新订单或状态变更
    this.loadOrders();
  },

  // 状态映射：后端状态码 -> 前端显示文本
  getStatusText(status) {
    const map = {
      0: '待付款',
      1: '待发货',
      2: '待收货',
      3: '已完成',
      4: '已取消'
    };
    return map[status] || '未知状态';
  },

  // 前端筛选 -> 后端状态码
  getStatusFromFilter(filter) {
    const map = {
      '全部': null,
      '待发货': 1,
      '待收货': 2,
      '已完成': 3
    };
    return map[filter] !== undefined ? map[filter] : null;
  },

  loadOrders() {
    wx.showLoading({ title: '加载中' });
    const status = this.getStatusFromFilter(this.data.filter);

    let promise;
    if (this.data.orderType === 'buy') {
      promise = api.getBuyerOrders(status);
    } else {
      promise = api.getSellerOrders(status);
    }

    promise.then(res => {
      wx.hideLoading();
      if (res.code === 0 || res.code === 200) {
        const orders = res.data.content.map(item => {
          // 适配前端 WXML 字段
          return {
            id: item.id,
            title: item.book.title,
            price: item.price,
            status: this.getStatusText(item.status),
            statusCode: item.status, // 保留原始状态码用于逻辑判断
            time: item.createdAt.replace('T', ' ').substring(0, 16),
            image: api.processImageUrl(item.book.images ? item.book.images[0] : null), // TODO: 处理书籍图片
            partner: this.data.orderType === 'buy' ? item.seller.nickname : item.buyer.nickname,
            bookId: item.book.id
          };
        }).filter(item => item.statusCode !== 0); // 过滤掉待付款订单
        this.setData({ orders });
      } else {
        wx.showToast({ title: '加载失败', icon: 'none' });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error(err);
      wx.showToast({ title: '网络错误', icon: 'none' });
    });
  },

  onContact() {
    wx.showToast({
      title: '请耐心等待回复',
      icon: 'none'
    });
  },

  onBack() {
    wx.navigateBack();
  },

  onPayOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    // 不再不需要 globalData传递，直接传ID
    wx.navigateTo({
      url: '/pages/checkout/checkout?mode=pay_existing&id=' + orderId
    });
  },

  onRemindDelivery(e) {
    wx.showToast({
      title: '已提醒卖家发货',
      icon: 'success'
    });
  },

  onRemindPickup(e) {
    wx.showToast({
      title: '对方已收到通知',
      icon: 'success'
    });
  },

  onDeliverOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    const that = this;
    wx.showModal({
      title: '确认发货',
      content: '确认已将书籍交付给买家？',
      success(res) {
        if (res.confirm) {
          wx.showLoading({ title: '处理中' });
          api.deliverOrder(orderId).then(res => {
            wx.hideLoading();
            if (res.code === 0 || res.code === 200) {
              wx.showToast({ title: '发货成功' });
              that.loadOrders(); // 刷新列表
            } else {
              wx.showToast({ title: res.message || '操作失败', icon: 'none' });
            }
          }).catch(err => {
            wx.hideLoading();
            wx.showToast({ title: '网络错误', icon: 'none' });
          });
        }
      }
    });
  },

  onConfirmReceipt(e) {
    const orderId = e.currentTarget.dataset.id;
    const that = this;
    wx.showModal({
      title: '确认收货',
      content: '确认已收到书籍且无误？',
      success(res) {
        if (res.confirm) {
          wx.showLoading({ title: '处理中' });
          api.receiveOrder(orderId).then(res => {
            wx.hideLoading();
            if (res.code === 0 || res.code === 200) {
              wx.showToast({ title: '交易完成' });
              that.loadOrders(); // 刷新列表
            } else {
              wx.showToast({ title: res.message || '操作失败', icon: 'none' });
            }
          }).catch(err => {
            wx.hideLoading();
            wx.showToast({ title: '网络错误', icon: 'none' });
          });
        }
      }
    });
  },

  switchOrderType(e) {
    const type = e.currentTarget.dataset.type;
    if (this.data.orderType === type) return;

    this.setData({
      orderType: type,
      filter: '全部',
      orders: []
    });
    this.loadOrders();
  },

  onFilterChange(e) {
    const filter = e.currentTarget.dataset.filter;
    if (this.data.filter === filter) return;

    this.setData({ filter });
    this.loadOrders();
  }
});
