// 结算页 checkout.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    book: {},
    isPaying: false,
    paySuccess: false,
    isExistingOrder: false, // 新增：是否为支付现有订单模式
    orderId: null, // 新增：现有订单的ID
    selectedPaymentMethod: 1 // 1-微信支付, 2-支付宝
  },

  onLoad(options) {
    if (options.mode === 'pay_existing' && options.id) {
      this.setData({
        isExistingOrder: true,
        orderId: options.id
      });

      wx.showLoading({ title: '加载订单信息' });
      api.getOrderDetail(options.id).then(res => {
        wx.hideLoading();
        if (res.code === 0 || res.code === 200) {
          const order = res.data;
          // 构造页面所需数据格式
          const book = {
            id: order.book.id,
            title: order.book.title,
            price: order.price, // 使用订单实际成交价
            images: order.book.images ? order.book.images.map(img => api.processImageUrl(img)) : [api.processImageUrl(null)],
            sellerName: order.seller.nickname
          };

          this.setData({ book });
        } else {
          wx.showToast({ title: '加载失败', icon: 'none' });
          setTimeout(() => wx.navigateBack(), 1500);
        }
      }).catch(err => {
        wx.hideLoading();
        wx.showToast({ title: '网络错误', icon: 'none' });
        setTimeout(() => wx.navigateBack(), 1500);
      });

    } else {
      const book = app.globalData.currentBook || app.globalData.mockBooks[0];
      this.setData({ book });
    }
  },

  selectPayment(e) {
    const method = parseInt(e.currentTarget.dataset.method);
    this.setData({ selectedPaymentMethod: method });
  },

  onCancelPay() {
    // 无论是现有订单还是新订单，点击取消都直接返回上一页
    // 不创建新订单
    wx.navigateBack();
  },

  onPay() {
    if (this.data.isPaying) return;

    this.setData({ isPaying: true });

    // 如果是现有订单支付
    if (this.data.isExistingOrder) {
      // 实际上后端payOrder接口可能不需要paymentMethod，因为订单创建时已定。
      // 但如果支持支付时修改方式，需要后端支持。
      // 当前后端 payOrder 只接受 orderId。
      // 假设支付方式在创建时已定，但在前端我们允许用户选，只是这里仅作模拟展示。
      // 或者如果需要在支付时更新支付方式，需要额外接口。
      // 暂时保持原样，只负责调用支付。
      api.post(`/orders/${this.data.orderId}/pay`).then(res => {
        if (res.code === 200 || res.code === 0) {
          this.handlePaySuccess();
        } else {
          this.handlePayFail(res.message);
        }
      }).catch(err => {
        this.handlePayFail('网络错误');
      });
      return;
    }

    // 如果是新创建订单支付
    const orderData = {
      bookId: this.data.book.id,
      paymentMethod: this.data.selectedPaymentMethod, // 使用选择的支付方式
      address: '校内自提' // 暂时默认，后续可扩展
    };

    api.post('/orders', orderData).then(res => {
      if (res.code === 200 || res.code === 0) {
        // 获取创建后的订单ID，立即支付
        const orderId = res.data.id;
        api.post(`/orders/${orderId}/pay`).then(payRes => {
          if (payRes.code === 200 || payRes.code === 0) {
            this.handlePaySuccess();
          } else {
            // 支付失败但订单已创建，跳转订单页
            wx.showToast({ title: '支付失败，订单已保存', icon: 'none' });
            setTimeout(() => {
              wx.redirectTo({ url: '/pages/orders/orders' });
            }, 1500);
          }
        });
      } else {
        this.handlePayFail(res.message);
      }
    }).catch(err => {
      this.handlePayFail('支付请求失败');
    });
  },

  handlePaySuccess() {
    this.setData({
      isPaying: false,
      paySuccess: true
    });

    // 跳转到订单页
    setTimeout(() => {
      wx.redirectTo({
        url: '/pages/orders/orders'
      });
    }, 1500);
  },

  handlePayFail(msg) {
    wx.showToast({
      title: msg || '支付失败',
      icon: 'none'
    });
    this.setData({ isPaying: false });
  }
});
