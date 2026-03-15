// 书籍详情 book-detail.js
const app = getApp();
const api = require('../../utils/api.js');

// 移除本地冗余逻辑，使用 api.js 中的统一处理

Page({
  data: {
    book: {},
    discount: '0',
    defaultImage: 'https://picsum.photos/seed/book/400/500',
    isFavorited: false,
    isOwner: false
  },

  onLoad(options) {
    const bookId = options.id;
    this.loadBook(bookId);
    this.checkFavoriteStatus(bookId);
  },



  checkFavoriteStatus(bookId) {
    const token = wx.getStorageSync('token');
    if (!token) return;

    api.get(`/favorites/${bookId}/check`).then(res => {
      if (res.code === 200) {
        this.setData({ isFavorited: res.data });
      }
    }).catch(err => {
      console.error('检查收藏状态失败', err);
    });
  },

  onFavorite() {
    const token = wx.getStorageSync('token');
    if (!token) {
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }

    const bookId = this.data.book.id;
    const isFavorited = this.data.isFavorited;

    if (isFavorited) {
      // 取消收藏
      api.delete(`/favorites/${bookId}`).then(res => {
        if (res.code === 200) {
          this.setData({ isFavorited: false });
          wx.showToast({ title: '已取消收藏', icon: 'none' });
        }
      });
    } else {
      // 添加收藏
      api.post(`/favorites/${bookId}`).then(res => {
        if (res.code === 200) {
          this.setData({ isFavorited: true });
          wx.showToast({ title: '收藏成功', icon: 'success' });
        }
      });
    }
  },

  loadBook(id) {
    wx.showLoading({ title: '加载中...' });

    console.log('=== 详情页加载书籍 ID:', id, '===');

    // 优先从全局数据获取（首页点击进来时已经有数据）
    const cachedBooks = app.globalData.cachedBooks || [];
    console.log('=== 缓存书籍数量:', cachedBooks.length, '===');

    let book = cachedBooks.find(b => String(b.id) === String(id));

    // 如果缓存中有书籍且包含学校信息，直接使用
    if (book && book.sellerSchool) {
      console.log('=== 从缓存找到书籍:', book.title, '===');
      console.log('=== 图片URL:', book.images, '===');
      this.setBookData(book);
      wx.hideLoading();
    } else {
      // 否则从后端API获取
      this.fetchBook(id);
    }
  },

  fetchBook(id) {
    api.get('/books/' + id).then(res => {
      wx.hideLoading();
      if (res.code === 200 && res.data) {
        const book = this.formatBook(res.data);
        this.setBookData(book);
      } else {
        this.showError();
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('获取书籍详情失败:', err);
      // 尝试使用本地模拟数据
      const mockBooks = app.globalData.mockBooks || [];
      let book = mockBooks.find(b => String(b.id) === String(id));
      if (book) {
        // 确保模拟数据也有默认值
        if (!book.sellerSchool) book.sellerSchool = '未知学校';
        this.setBookData(book);
      } else {
        this.showError();
      }
    });
  },

  formatBook(data) {
    // 处理图片URL
    let images = [];
    if (data.images) {
      try {
        const imgList = typeof data.images === 'string' ? JSON.parse(data.images) : data.images;
        images = (Array.isArray(imgList) ? imgList : [imgList]).map(img => api.processImageUrl(img));
      } catch (e) {
        images = [api.processImageUrl(data.images)];
      }
    }
    if (images.length === 0) {
      images = ['https://picsum.photos/seed/book' + data.id + '/400/500'];
    }

    return {
      id: data.id,
      title: data.title,
      author: data.author,
      price: data.price,
      originalPrice: data.originalPrice,
      condition: data.condition,
      category: data.category,
      major: data.category,
      images: images,
      description: data.description,
      sellerId: data.sellerId,
      sellerName: data.sellerNickname || '匿名用户',
      sellerSchool: data.sellerSchool || '未知学校',
      sellerAvatar: api.processImageUrl(data.sellerAvatar) || 'https://picsum.photos/seed/avatar/100/100'
    };
  },

  setBookData(book) {
    console.log('=== setBookData 原始图片:', book.images, '===');

    // 确保图片URL是完整的
    if (book.images && book.images.length > 0) {
      book.images = book.images.map(img => {
        if (img && !img.startsWith('http')) {
          return api.processImageUrl(img);
        }
        return img;
      });
    } else {
      book.images = ['https://picsum.photos/seed/book' + book.id + '/400/500'];
    }

    console.log('=== setBookData 处理后图片:', book.images, '===');

    const discount = book.originalPrice > 0
      ? ((book.price / book.originalPrice) * 10).toFixed(1)
      : '0';

    // 检查是否为发布者本人
    const currentUserId = app.globalData.userId || wx.getStorageSync('userId');
    const isOwner = String(currentUserId) === String(book.sellerId);

    this.setData({ book, discount, isOwner });
  },

  showError() {
    wx.showToast({
      title: '书籍不存在',
      icon: 'none'
    });
    setTimeout(() => {
      wx.navigateBack();
    }, 1500);
  },

  onImageError() {
    // 图片加载失败时使用默认图片
    const book = this.data.book;
    book.images = [this.data.defaultImage];
    this.setData({ book });
  },

  onBack() {
    wx.navigateBack();
  },

  onShare() {
    // 分享功能
  },

  onChat() {
    if (this.data.isOwner) {
      wx.showToast({ title: '不能与自己聊天', icon: 'none' });
      return;
    }
    const book = this.data.book;
    const sellerId = book.sellerId;
    const sellerName = book.sellerName;
    const sellerAvatar = book.sellerAvatar;

    // 检查是否已有该用户的聊天记录
    const chats = app.globalData.mockChats || [];
    let chat = chats.find(c => c.otherPartyName === sellerName);

    if (!chat) {
      // 创建新聊天
      chat = {
        id: 'chat_' + Date.now(),
        otherPartyName: sellerName,
        otherPartyAvatar: sellerAvatar,
        lastMessage: '你好，我对这本书感兴趣',
        time: '刚刚',
        unreadCount: 0,
        relatedBook: {
          id: book.id,
          title: book.title,
          price: book.price,
          image: book.images[0]
        }
      };
      app.globalData.mockChats.unshift(chat); // 添加到开头
    }

    app.globalData.currentChat = chat;
    wx.navigateTo({
      url: `/pages/chat-detail/chat-detail?id=${chat.id}`
    });
  },

  onBuy() {
    if (this.data.isOwner) {
      wx.showToast({ title: '不能购买自己的商品', icon: 'none' });
      return;
    }
    // 保存当前书籍到全局
    app.globalData.currentBook = this.data.book;
    wx.navigateTo({
      url: '/pages/checkout/checkout'
    });
  },

  onShareAppMessage() {
    return {
      title: this.data.book.title,
      path: `/pages/book-detail/book-detail?id=${this.data.book.id}`
    };
  }
});
