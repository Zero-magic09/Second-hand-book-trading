// 登录页 login.js
const app = getApp();

Page({
  data: {
    agreed: false,
    isMobileView: false,
    phoneNumber: '',
    vCode: '',
    countdown: 0,
    canSendCode: false,
    canSubmit: false
  },

  onLoad() {
    // 如果已登录，跳转首页
    if (app.globalData.isLoggedIn) {
      wx.switchTab({ url: '/pages/home/home' });
    }
  },

  toggleAgreed() {
    this.setData({ agreed: !this.data.agreed });
    this.checkCanSubmit();
  },

  showMobileView() {
    this.setData({ isMobileView: true });
  },

  hideMobileView() {
    this.setData({ isMobileView: false });
  },

  onPhoneInput(e) {
    const phoneNumber = e.detail.value.replace(/\D/g, '');
    this.setData({
      phoneNumber,
      canSendCode: phoneNumber.length === 11
    });
    this.checkCanSubmit();
  },

  onCodeInput(e) {
    const vCode = e.detail.value.replace(/\D/g, '');
    this.setData({ vCode });
    this.checkCanSubmit();
  },

  checkCanSubmit() {
    const { agreed, phoneNumber, vCode } = this.data;
    this.setData({
      canSubmit: agreed && phoneNumber.length === 11 && vCode.length === 6
    });
  },

  onSendCode() {
    if (!this.data.canSendCode || this.data.countdown > 0) return;

    this.setData({ countdown: 60 });

    const timer = setInterval(() => {
      if (this.data.countdown <= 1) {
        clearInterval(timer);
        this.setData({ countdown: 0 });
      } else {
        this.setData({ countdown: this.data.countdown - 1 });
      }
    }, 1000);

    wx.showToast({
      title: '验证码已发送',
      icon: 'success'
    });
  },

  goMobileLogin() {
    wx.navigateTo({ url: '/pages/mobile-login/mobile-login' });
  },

  goRegister() {
    wx.navigateTo({ url: '/pages/register/register' });
  },

  onWeChatLogin() {
    // 微信登录
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        app.globalData.userInfo = res.userInfo;
        this.doLogin();
      },
      fail: () => {
        // 直接登录
        this.doLogin();
      }
    });
  },

  onMobileLogin() {
    if (!this.data.canSubmit) return;
    this.doLogin();
  },

  doLogin() {
    app.login();
    wx.showToast({
      title: '登录成功',
      icon: 'success'
    });

    setTimeout(() => {
      wx.switchTab({
        url: '/pages/home/home'
      });
    }, 1500);
  }
});
