const api = require('../../utils/api.js');
const app = getApp();

Page({
  data: {
    username: '',
    phoneNumber: '',
    vCode: '',
    captchaCode: '',
    canSubmit: false
  },

  onLoad() {
    this.generateCaptcha();
  },

  // 生成随机4位数字验证码
  generateCaptcha() {
    const code = Math.floor(1000 + Math.random() * 9000).toString();
    this.setData({ captchaCode: code });
  },

  // 点击刷新验证码
  refreshCaptcha() {
    this.generateCaptcha();
    wx.showToast({ title: '已刷新', icon: 'none', duration: 800 });
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value });
    this.checkCanSubmit();
  },

  onPhoneInput(e) {
    const phoneNumber = e.detail.value;
    this.setData({ phoneNumber });
    this.checkCanSubmit();
  },

  onCodeInput(e) {
    this.setData({ vCode: e.detail.value });
    this.checkCanSubmit();
  },

  checkCanSubmit() {
    const { username, phoneNumber, vCode } = this.data;
    this.setData({
      canSubmit: username.length > 0 && phoneNumber.length === 11 && vCode.length === 4
    });
  },

  onMobileLogin() {
    const { username, phoneNumber, vCode, captchaCode } = this.data;

    if (!username) {
      wx.showToast({ title: '请输入用户名', icon: 'none' });
      return;
    }

    if (phoneNumber.length !== 11) {
      wx.showToast({ title: '请输入11位手机号', icon: 'none' });
      return;
    }

    if (vCode !== captchaCode) {
      wx.showToast({ title: '验证码错误', icon: 'none' });
      this.generateCaptcha();
      this.setData({ vCode: '' });
      return;
    }

    wx.showLoading({ title: '登录中...' });

    api.post('/auth/phone-login', {
      phone: phoneNumber,
      verifyCode: vCode
    }).then(res => {
      wx.hideLoading();
      if (res.code === 200) {
        wx.showToast({ title: '登录成功', icon: 'success' });

        const loginData = res.data;
        wx.setStorageSync('token', loginData.token);
        wx.setStorageSync('userId', loginData.user.id);
        app.globalData.userInfo = loginData.user;
        app.globalData.userId = loginData.user.id;
        app.globalData.isLoggedIn = true;

        setTimeout(() => {
          wx.switchTab({ url: '/pages/home/home' });
        }, 1500);
      } else {
        wx.showToast({ title: res.message || '登录失败', icon: 'none' });
      }
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '网络请求失败', icon: 'none' });
      console.error(err);
    });
  }
});
