// 发布求购 publish-wanted.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    majors: [],
    formData: {
      title: '',
      author: '',
      maxPrice: '',
      category: '计算机',
      description: ''
    },
    submitting: false
  },

  onLoad() {
    this.setData({
      majors: app.globalData.majors
    });
  },

  onInputChange(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`formData.${field}`]: value
    });
  },

  onMajorChange(e) {
    const idx = e.detail.value;
    this.setData({
      'formData.category': this.data.majors[idx]
    });
  },

  async onPublish() {
    if (!app.globalData.userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateTo({
          url: '/pages/login/login'
        });
      }, 1000);
      return;
    }

    if (!this.data.formData.title) {
      wx.showToast({
        title: '请输入书名',
        icon: 'none'
      });
      return;
    }

    if (this.data.submitting) return;

    this.setData({ submitting: true });
    wx.showLoading({ title: '发布中...' });

    try {
      // 构建请求数据
      const requestData = {
        title: this.data.formData.title,
        author: this.data.formData.author || null,
        description: this.data.formData.description || null,
        category: this.data.formData.category,
        maxPrice: this.data.formData.maxPrice ? parseFloat(this.data.formData.maxPrice) : null
      };

      // 调用后端API
      const res = await api.post('/wanted', requestData);

      if (res.code === 0 || res.code === 200) {
        wx.hideLoading();
        wx.showToast({
          title: '发布成功',
          icon: 'success'
        });

        // 清空表单
        this.setData({
          formData: {
            title: '',
            author: '',
            maxPrice: '',
            category: this.data.majors[0],
            description: ''
          }
        });

        setTimeout(() => {
          // 返回上一页（求购列表）并刷新
          const pages = getCurrentPages();
          const prevPage = pages[pages.length - 2];
          if (prevPage && prevPage.route === 'pages/wanted/wanted') {
            prevPage.setData({ page: 0, wantedList: [] });
            prevPage.loadWantedList();
          }
          wx.navigateBack();
        }, 1500);
      } else {
        throw new Error(res.message || '发布失败');
      }
    } catch (error) {
      console.error('发布求购失败:', error);
      wx.hideLoading();
      wx.showToast({
        title: error.message || '发布失败',
        icon: 'none'
      });
    } finally {
      this.setData({ submitting: false });
    }
  },


});
