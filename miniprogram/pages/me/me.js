const api = require('../../utils/api.js');
const app = getApp();

Page({
  data: {
    userInfo: {
      nickname: '',
      avatarUrl: '',
      school: '',
      verificationStatus: 0
    },
    showEditModal: false,
    editForm: {
      nickname: '',
      avatarUrl: ''
    },
    stats: [
      { label: '正在出售', value: 0, colorClass: 'text-emerald', tab: 'publish-records' },
      { label: '已售出', value: 0, colorClass: 'text-blue', tab: 'publish-records' },
      { label: '收藏', value: 0, colorClass: 'text-amber', tab: 'home' }
    ],
    menuItems: [
      { emoji: '🛍️', label: '我的订单', tab: 'orders', bgClass: 'bg-indigo' },
      { emoji: '📋', label: '发布记录', tab: 'publish-records', bgClass: 'bg-emerald' },
      { emoji: '🛡️', label: '学生认证', tab: 'student-verification', bgClass: 'bg-blue' },
      { emoji: '📤', label: '分享小程序', tab: 'share', bgClass: 'bg-cyan' }
    ]
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 4 });
    }
    this.loadUserInfo();
  },

  onPullDownRefresh() {
    this.loadUserInfo(() => {
      wx.stopPullDownRefresh();
      wx.showToast({ title: '刷新成功', icon: 'none' });
    });
  },

  loadUserInfo(callback) {
    const token = wx.getStorageSync('token');
    const userInfo = app.globalData.userInfo;

    if (userInfo) {
      this.setData({
        userInfo: {
          nickname: userInfo.nickname || '未设置昵称',
          avatarUrl: api.processImageUrl(userInfo.avatarUrl),
          school: userInfo.school || '未设置学校',
          verificationStatus: userInfo.verificationStatus || 0
        },
        stats: [
          { label: '正在出售', value: userInfo.sellingCount || 0, colorClass: 'text-emerald', tab: 'publish-records' },
          { label: '已售出', value: userInfo.soldCount || 0, colorClass: 'text-blue', tab: 'publish-records' },
          { label: '收藏', value: userInfo.favoriteCount || 0, colorClass: 'text-amber', tab: 'home' }
        ]
      });
    }

    if (token) {
      this.fetchUserInfo(callback);
    } else if (callback) {
      callback();
    }
  },

  fetchUserInfo(callback) {
    api.get('/users/me').then(res => {
      if (res.code === 200) {
        const userInfo = res.data;
        app.globalData.userInfo = userInfo;
        this.setData({
          userInfo: {
            nickname: userInfo.nickname || '未设置昵称',
            avatarUrl: api.processImageUrl(userInfo.avatarUrl),
            school: userInfo.school || '未设置学校',
            verificationStatus: userInfo.verificationStatus || 0
          },
          stats: [
            { label: '正在出售', value: userInfo.sellingCount || 0, colorClass: 'text-emerald', tab: 'publish-records' },
            { label: '已售出', value: userInfo.soldCount || 0, colorClass: 'text-blue', tab: 'publish-records' },
            { label: '收藏', value: userInfo.favoriteCount || 0, colorClass: 'text-amber', tab: 'home' }
          ]
        });
      }
    }).catch(err => {
      console.error('获取用户信息失败', err);
    }).finally(() => {
      if (callback) callback();
    });
  },

  onAvatarTap() {
    this.setData({
      showEditModal: true,
      editForm: {
        nickname: this.data.userInfo.nickname,
        avatarUrl: this.data.userInfo.avatarUrl
      }
    });
  },

  closeEditModal() {
    this.setData({ showEditModal: false });
  },

  onNicknameInput(e) {
    this.setData({ 'editForm.nickname': e.detail.value });
  },

  chooseAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.uploadAvatar(res.tempFilePaths[0]);
      }
    });
  },

  uploadAvatar(filePath) {
    wx.showLoading({ title: '上传中...' });
    api.uploadFile(filePath).then(imageUrl => {
      wx.hideLoading();
      this.setData({
        'editForm.avatarUrl': api.processImageUrl(imageUrl)
      });
      wx.showToast({ title: '头像上传成功', icon: 'success' });
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '上传失败', icon: 'none' });
      console.error(err);
    });
  },

  saveProfile() {
    const { nickname, avatarUrl } = this.data.editForm;

    if (!nickname.trim()) {
      wx.showToast({ title: '请输入昵称', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '保存中...' });

    // 提取相对路径
    let relativeAvatarUrl = avatarUrl;
    if (avatarUrl.includes('/uploads/')) {
      relativeAvatarUrl = '/uploads/' + avatarUrl.split('/uploads/')[1];
    }

    api.put('/users/me', {
      nickname: nickname,
      avatarUrl: relativeAvatarUrl
    }).then(res => {
      wx.hideLoading();
      if (res.code === 200) {
        const updatedUser = res.data;
        app.globalData.userInfo = updatedUser;

        this.setData({
          userInfo: {
            nickname: updatedUser.nickname,
            avatarUrl: api.processImageUrl(updatedUser.avatarUrl),
            school: updatedUser.school || '未设置学校',
            verificationStatus: updatedUser.verificationStatus || 0
          },
          showEditModal: false
        });

        wx.showToast({ title: '保存成功', icon: 'success' });
      } else {
        wx.showToast({ title: res.message || '保存失败', icon: 'none' });
      }
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '保存失败', icon: 'none' });
      console.error(err);
    });
  },

  onStatTap(e) {
    const tab = e.currentTarget.dataset.tab;
    if (tab === 'home') {
      wx.switchTab({ url: '/pages/home/home' });
    } else {
      wx.navigateTo({ url: `/pages/${tab}/${tab}` });
    }
  },

  onMenuTap(e) {
    const item = e.currentTarget.dataset.item;

    if (item.tab === 'share') {
      // 触发分享
      return;
    }

    wx.navigateTo({ url: `/pages/${item.tab}/${item.tab}` });
  },

  onLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout();
          wx.reLaunch({
            url: '/pages/login/login'
          });
        }
      }
    });
  },

  onShareAppMessage() {
    return {
      title: '校园书环 - 让知识流动起来',
      path: '/pages/home/home'
    };
  }
});
