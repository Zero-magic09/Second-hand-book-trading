// 首页 home.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    activeTab: '推荐',
    categories: ['推荐', '计算机', '经济管理', '机械工程', '外国语', '建筑设计', '通识教育', '考研资料'],
    books: [],
    allBooks: [], // 保存所有书籍用于筛选
    searchKeyword: '',
    quickServices: [
      { emoji: '📱', label: '扫码卖书', bgClass: 'bg-sky' },
      { emoji: '📚', label: '全部分类', bgClass: 'bg-blue' },
      { emoji: '❤️', label: '心愿单', bgClass: 'bg-rose' },
      { emoji: '🏆', label: '贡献榜', bgClass: 'bg-amber' }
    ]
  },

  onLoad() {
    this.loadBooks();
  },

  onShow() {
    // 检查登录状态
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 0
      });
    }
  },

  loadBooks(showLoading = true) {
    const { activeTab, searchKeyword } = this.data;

    if (showLoading) {
      wx.showLoading({ title: '加载中...' });
    }

    return api.getBooks({
      category: activeTab,
      keyword: searchKeyword
    }).then(res => {
      if (showLoading) {
        wx.hideLoading();
      }
      console.log('[首页] API响应:', res); // 添加日志

      if (res.code === 0) {
        // 缓存书籍数据到全局，供详情页使用
        app.globalData.cachedBooks = res.data;
        this.setData({
          allBooks: res.data,
          books: res.data
        });

        // 如果没有数据，显示提示
        if (res.data.length === 0) {
          wx.showToast({
            title: '暂无数据',
            icon: 'none'
          });
        }
      } else {
        // 如果返回格式不对，使用本地数据
        console.log('[首页] API返回格式异常，使用本地数据');
        const allBooks = app.globalData.mockBooks || [];
        this.setData({
          allBooks: allBooks,
          books: allBooks
        });
      }
    }).catch(err => {
      if (showLoading) {
        wx.hideLoading();
      }
      console.error('加载书籍失败:', err);
      // 失败时使用本地数据
      const allBooks = app.globalData.mockBooks || [];
      this.setData({
        allBooks: allBooks,
        books: allBooks
      });

      wx.showToast({
        title: '网络异常，使用本地数据',
        icon: 'none'
      });
    });
  },

  // 搜索输入
  onSearchInput(e) {
    const keyword = e.detail.value;
    this.setData({ searchKeyword: keyword });
    this.filterBooks();
  },

  // 搜索确认
  onSearchConfirm(e) {
    const keyword = e.detail.value;
    this.setData({ searchKeyword: keyword });
    this.loadBooks();
  },

  // 清空搜索
  onClearSearch() {
    this.setData({ searchKeyword: '' });
    this.loadBooks();
  },

  // 筛选书籍
  filterBooks() {
    const { allBooks, activeTab, searchKeyword } = this.data;
    let filtered = allBooks;

    // 分类筛选
    if (activeTab && activeTab !== '推荐') {
      filtered = filtered.filter(book =>
        book.category === activeTab || book.major === activeTab
      );
    }

    // 关键词搜索
    if (searchKeyword && searchKeyword.trim()) {
      const kw = searchKeyword.toLowerCase().trim();
      filtered = filtered.filter(book =>
        (book.title && book.title.toLowerCase().includes(kw)) ||
        (book.author && book.author.toLowerCase().includes(kw)) ||
        (book.isbn && book.isbn.includes(kw)) ||
        (book.category && book.category.toLowerCase().includes(kw)) ||
        (book.major && book.major.toLowerCase().includes(kw))
      );
    }

    this.setData({ books: filtered });
  },

  onCategoryTap(e) {
    const category = e.currentTarget.dataset.category;
    this.setData({ activeTab: category, searchKeyword: '' });
    this.loadBooks();
  },

  onBookTap(e) {
    const book = e.currentTarget.dataset.book;
    wx.navigateTo({
      url: `/pages/book-detail/book-detail?id=${book.id}`
    });
  },

  onPullDownRefresh() {
    this.setData({ searchKeyword: '', activeTab: '推荐' });
    this.loadBooks(false).then(() => {
      wx.stopPullDownRefresh();
      wx.showToast({
        title: '刷新成功',
        icon: 'success'
      });
    });
  }
});
