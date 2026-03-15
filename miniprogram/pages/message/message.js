// 消息列表 message.js
const app = getApp();

Page({
  data: {
    chatList: []
  },

  onLoad() {
    this.loadChats();
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 3 });
    }
    this.loadChats();
  },

  loadChats() {
    const mockChats = app.globalData.mockChats;

    this.setData({
      chatList: mockChats
    });
  },

  onPullDownRefresh() {
    // 模拟重新加载
    setTimeout(() => {
      this.loadChats();
      wx.stopPullDownRefresh();
      wx.showToast({
        title: '刷新成功',
        icon: 'success'
      });
    }, 500);
  },

  onChatTap(e) {
    const chat = e.currentTarget.dataset.chat;
    // 将聊天信息存储到全局，供详情页使用
    app.globalData.currentChat = chat;
    wx.navigateTo({
      url: `/pages/chat-detail/chat-detail?id=${chat.id}`
    });
  }
});
