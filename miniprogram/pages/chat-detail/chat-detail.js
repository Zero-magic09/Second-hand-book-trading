// 聊天详情 chat-detail.js
const app = getApp();

Page({
  data: {
    session: {},
    messages: [
      { id: '1', senderId: 'other', content: '同学你好，请问二手书还在吗？', timestamp: '14:20', isMine: false },
      { id: '2', senderId: 'me', content: '在的，书很新，没有划线。', timestamp: '14:22', isMine: true },
      { id: '3', senderId: 'other', content: '可以紫金港校区面交吗？', timestamp: '14:23', isMine: false }
    ],
    inputValue: '',
    quickActions: ['最低多少？', '还在吗？', '什么时候面交？', '发张实拍图'],
    scrollToView: ''
  },

  onLoad(options) {
    const session = app.globalData.currentChat || app.globalData.mockChats[0];

    // 如果有相关书籍信息，设置到 data 中
    let relatedBook = null;
    if (session && session.relatedBook) {
      relatedBook = session.relatedBook;
    }

    this.setData({ session, relatedBook });

    // 设置导航栏标题为聊天对象名字
    if (session && session.otherPartyName) {
      wx.setNavigationBarTitle({ title: session.otherPartyName });
    }

    // 滚动到最新消息
    setTimeout(() => {
      this.scrollToBottom();
    }, 100);
  },

  onViewBook() {
    const bookId = this.data.relatedBook ? this.data.relatedBook.id : null;
    if (bookId) {
      wx.navigateTo({
        url: `/pages/book-detail/book-detail?id=${bookId}`
      });
    }
  },

  onBack() {
    wx.navigateBack();
  },

  onInput(e) {
    this.setData({ inputValue: e.detail.value });
  },

  onQuickAction(e) {
    const text = e.currentTarget.dataset.text;
    this.setData({ inputValue: text });
  },

  onSend() {
    const content = this.data.inputValue.trim();
    if (!content) return;

    const newMsg = {
      id: Date.now().toString(),
      senderId: 'me',
      content: content,
      timestamp: this.getCurrentTime(),
      isMine: true
    };

    this.setData({
      messages: [...this.data.messages, newMsg],
      inputValue: ''
    });

    this.scrollToBottom();

    // 模拟自动回复
    this.simulateReply();
  },

  simulateReply() {
    setTimeout(() => {
      const replyMsg = {
        id: Date.now().toString(),
        senderId: 'other',
        content: '我收到了您的消息，稍后回复您。', // 简单的自动回复
        timestamp: this.getCurrentTime(),
        isMine: false
      };

      this.setData({
        messages: [...this.data.messages, replyMsg]
      });

      this.scrollToBottom();
    }, 1000); // 1秒后回复
  },

  getCurrentTime() {
    const now = new Date();
    return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
  },

  scrollToBottom() {
    const messages = this.data.messages;
    if (messages.length > 0) {
      this.setData({
        scrollToView: 'msg-' + messages[messages.length - 1].id
      });
    }
  }
});
