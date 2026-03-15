// 求购区 wanted.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    wantedList: [],
    loading: false,
    hasMore: true,
    page: 0,
    size: 20
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 1 });
    }
    this.loadWantedList();
  },

  // 加载求购列表
  async loadWantedList() {
    if (this.data.loading) return;

    this.setData({ loading: true });

    try {
      const res = await api.getWantedList({
        status: 0, // 求购中
        page: this.data.page,
        size: this.data.size
      });

      if ((res.code === 0 || res.code === 200) && res.data) {
        const newPosts = (res.data.content || []).map(post => ({
          id: post.id,
          title: post.title,
          author: post.author || '',
          budget: post.maxPrice ? post.maxPrice.toString() : '面议',
          major: post.category || '其他',
          user: post.userNickname || '匿名用户',
          time: this.formatTime(post.createdAt),
          avatar: api.processImageUrl(post.userAvatar),
          description: post.description,
          userId: post.userId
        }));

        this.setData({
          wantedList: this.data.page === 0 ? newPosts : [...this.data.wantedList, ...newPosts],
          hasMore: newPosts.length === this.data.size
        });
      }
    } catch (error) {
      console.error('加载求购列表失败:', error);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 格式化时间
  formatTime(createdAt) {
    if (!createdAt) return '刚刚';

    const now = new Date();
    const created = new Date(createdAt);
    const diff = now - created;

    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 1) return '刚刚';
    if (minutes < 60) return `${minutes}分钟前`;
    if (hours < 24) return `${hours}小时前`;
    if (days < 7) return `${days}天前`;

    return createdAt.split('T')[0];
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.setData({ page: 0, wantedList: [] });
    this.loadWantedList().finally(() => {
      wx.stopPullDownRefresh();
      wx.showToast({
        title: '刷新成功',
        icon: 'success'
      });
    });
  },

  // 上拉加载更多
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ page: this.data.page + 1 });
      this.loadWantedList();
    }
  },

  onPublishWanted() {
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

    wx.navigateTo({
      url: '/pages/publish-wanted/publish-wanted'
    });
  },

  async onHaveBook(e) {
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

    const item = e.currentTarget.dataset.item;

    wx.showModal({
      title: '提示',
      content: '已通知对方，请等待回复',
      showCancel: false,
      confirmText: '我知道了',
      confirmColor: '#10b981'
    });
  },

  // 查看求购详情
  onWantedDetail(e) {
    // 详情页已移除，点击不执行任何操作
  },

  // 阻止事件冒泡
  stopPropagation(e) {
    e.stopPropagation();
  }
});
