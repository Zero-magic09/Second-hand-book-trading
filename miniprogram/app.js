// app.js
App({
  globalData: {
    userInfo: null,
    isLoggedIn: false,
    majors: ['计算机', '经济管理', '机械工程', '外国语', '建筑设计', '通识教育', '考研资料'],
    mockBooks: [
      {
        id: '1',
        title: '算法导论 (原书第3版)',
        author: 'Thomas H. Cormen',
        price: 45,
        originalPrice: 128,
        condition: '九成新',
        category: '专业教材',
        major: '计算机',
        images: ['https://picsum.photos/seed/book1/400/500'],
        publisher: '机械工业出版社',
        isbn: '9787111407010',
        sellerId: 'user1',
        sellerName: '张同学',
        sellerSchool: '计算机学院',
        sellerAvatar: 'https://picsum.photos/seed/avatar1/100/100',
        description: '书里只有少量铅笔勾画，非常整洁。',
        createdAt: '2小时前',
        status: 'active'
      },
      {
        id: '2',
        title: '经济学原理',
        author: '曼昆',
        price: 35,
        originalPrice: 88,
        condition: '八成新',
        category: '专业教材',
        major: '经济管理',
        images: ['https://picsum.photos/seed/book2/400/500'],
        publisher: '北京大学出版社',
        isbn: '9787301252413',
        sellerId: 'user2',
        sellerName: '李同学',
        sellerSchool: '经济管理学院',
        sellerAvatar: 'https://picsum.photos/seed/avatar2/100/100',
        description: '老师划的重点都在。',
        createdAt: '5小时前',
        status: 'active'
      },
      {
        id: '3',
        title: '考研英语词汇',
        author: '新东方',
        price: 15,
        originalPrice: 48,
        condition: '全新',
        category: '考研资料',
        major: '外国语',
        images: ['https://picsum.photos/seed/book3/400/500'],
        publisher: '新东方出版社',
        isbn: '9787560592812',
        sellerId: 'user3',
        sellerName: '王同学',
        sellerSchool: '外国语学院',
        sellerAvatar: 'https://picsum.photos/seed/avatar3/100/100',
        description: '买多了，全新没拆封。',
        createdAt: '10小时前',
        status: 'active'
      }
    ],
    mockChats: [
      {
        id: '2',
        otherPartyName: '张三',
        otherPartyAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix',
        time: '2025-12-21',
        lastMessage: '同学，这本书还在吗？可以便宜点吗？',
        unreadCount: 0
      },
      {
        id: '3',
        otherPartyName: '李四',
        otherPartyAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Aneka',
        time: '2025-12-16',
        lastMessage: '好的，下午图书馆门口见。',
        unreadCount: 2
      },
      {
        id: '4',
        otherPartyName: '王五',
        otherPartyAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Bob',
        time: '2025-12-20',
        lastMessage: '好的，谢谢！',
        unreadCount: 0
      },
      {
        id: '5',
        otherPartyName: '赵六',
        otherPartyAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Chloe',
        time: '2025-12-18',
        lastMessage: '书还可以便宜点吗？诚心想要。',
        unreadCount: 0
      },
      {
        id: '6',
        otherPartyName: '钱七',
        otherPartyAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=David',
        time: '2025-12-15',
        lastMessage: '已经发货了吗？麻烦发一下单号。',
        unreadCount: 3
      },
      {
        id: '7',
        otherPartyName: '孙八',
        otherPartyAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Emily',
        time: '2025-12-10',
        lastMessage: '这本书是正版吗？',
        unreadCount: 0
      }
    ],
    mockWantedList: [
      {
        id: 'w1',
        title: '《离散数学及其应用》',
        budget: '20-40',
        major: '计算机',
        user: '周同学',
        time: '1小时前',
        avatar: 'https://picsum.photos/seed/w1/100/100',
        description: '求购离散数学教材，最好有配套习题解答'
      },
      {
        id: 'w2',
        title: '托福精选词汇（红宝书）',
        budget: '10-20',
        major: '外国语',
        user: '韩同学',
        time: '4小时前',
        avatar: 'https://picsum.photos/seed/w2/100/100',
        description: '备考托福，需要词汇书'
      },
      {
        id: 'w3',
        title: '考研政治核心考点',
        budget: '15',
        major: '通识教育',
        user: '陈同学',
        time: '昨天',
        avatar: 'https://picsum.photos/seed/w3/100/100',
        description: '求购考研政治核心考点，品相较好即可'
      }
    ]
  },

  onLaunch() {
    // 检查登录状态
    const isLoggedIn = wx.getStorageSync('isLoggedIn');
    if (isLoggedIn) {
      this.globalData.isLoggedIn = true;
    }

    // 尝试从缓存恢复用户信息
    const userId = wx.getStorageSync('userId');
    if (userId) {
      this.globalData.userId = userId;
    }
    const token = wx.getStorageSync('token');
    if (token) {
      // Token exists but we might need to validate it or fetch user info if userInfo is null
      // API calls will handle 401 if token is expired
    }
  },

  // 登录方法
  login() {
    this.globalData.isLoggedIn = true;
    wx.setStorageSync('isLoggedIn', true);
  },

  // 登出方法
  logout() {
    this.globalData.isLoggedIn = false;
    wx.removeStorageSync('isLoggedIn');
  },

  // 检查登录状态
  checkLogin() {
    if (!this.globalData.isLoggedIn) {
      wx.navigateTo({
        url: '/pages/login/login'
      });
      return false;
    }
    return true;
  }
});
