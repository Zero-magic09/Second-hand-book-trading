// 编辑书籍 publish-edit.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    isScanning: false,
    isAnalyzing: false,
    conditions: ['全新', '九成新', '八成新', '较旧'],
    majors: ['计算机', '经济管理', '机械工程', '外国语', '建筑设计', '通识教育', '考研资料'],
    majorIndex: 0,
    images: [], // 已选择的图片
    bookData: {
      title: '',
      author: '',
      publisher: '',
      isbn: '',
      price: '',
      originalPrice: '',
      description: '',
      major: '计算机',
      condition: ''
    },
    editingId: null // 编辑模式下的书籍ID
  },

  onLoad(options) {
    if (options && options.id) {
      this.setData({ editingId: options.id });
      this.loadBookDetail(options.id);
      wx.setNavigationBarTitle({ title: '编辑商品' });
    }
  },

  onShow() {
    // 不需要特殊的 onShow 逻辑
  },

  loadBookDetail(id) {
    wx.showLoading({ title: '加载中...' });
    api.getBookDetail(id).then(res => {
      wx.hideLoading();
      if (res.code === 0 || res.code === 200) {
        const book = res.data;

        // 处理图片
        let images = [];
        if (book.images) {
          try {
            const imgList = typeof book.images === 'string' ? JSON.parse(book.images) : book.images;
            images = (Array.isArray(imgList) ? imgList : [imgList]).map(img => api.processImageUrl(img));
          } catch {
            images = [api.processImageUrl(book.images)];
          }
        }

        // 查找专业索引
        const majorIndex = this.data.majors.findIndex(m => m === book.category);

        this.setData({
          images: images,
          majorIndex: majorIndex >= 0 ? majorIndex : 0,
          bookData: {
            title: book.title,
            author: book.author,
            publisher: book.publisher,
            isbn: book.isbn,
            price: book.price,
            originalPrice: book.originalPrice,
            description: book.description,
            major: book.category, // 使用 category 作为 major
            condition: book.condition || book.bookCondition
          }
        });
      } else {
        wx.showToast({ title: '加载失败', icon: 'none' });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error(err);
      wx.showToast({ title: '网络错误', icon: 'none' });
    });
  },

  onInputChange(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`bookData.${field}`]: value
    });
  },

  onConditionSelect(e) {
    const condition = e.currentTarget.dataset.condition;
    this.setData({
      'bookData.condition': condition
    });
  },

  onChooseImage() {
    const currentCount = this.data.images.length;
    const maxCount = 1; // Limit to 1 image
    if (currentCount >= maxCount) {
      wx.showToast({ title: '最多选择1张图片', icon: 'none' });
      return;
    }
    wx.chooseMedia({
      count: maxCount - currentCount,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newImages = res.tempFiles.map(file => file.tempFilePath);
        this.setData({
          images: [...this.data.images, ...newImages]
        });
      }
    });
  },

  // 删除图片
  onDeleteImage(e) {
    const index = e.currentTarget.dataset.index;
    const images = this.data.images;
    images.splice(index, 1);
    this.setData({ images });
  },

  // 预览图片
  onPreviewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({
      current: url,
      urls: this.data.images
    });
  },

  onScanCode() {
    this.setData({ isScanning: true });

    wx.scanCode({
      onlyFromCamera: false,
      scanType: ['barCode'],
      success: (res) => {
        this.setData({
          isScanning: false,
          isAnalyzing: true
        });

        // 模拟 AI 识别
        setTimeout(() => {
          this.setData({
            isAnalyzing: false,
            'bookData.title': '算法导论',
            'bookData.author': 'Cormen',
            'bookData.isbn': res.result || '9787111407010',
            'bookData.originalPrice': '128'
          });

          wx.showToast({
            title: '识别成功',
            icon: 'success'
          });
        }, 1500);
      },
      fail: () => {
        this.setData({ isScanning: false });
        // 模拟扫码结果
        this.setData({ isAnalyzing: true });
        setTimeout(() => {
          this.setData({
            isAnalyzing: false,
            'bookData.title': '算法导论',
            'bookData.author': 'Cormen',
            'bookData.isbn': '9787111407010',
            'bookData.originalPrice': '128'
          });
        }, 1500);
      }
    });
  },

  onMajorChange(e) {
    const index = e.detail.value;
    this.setData({
      majorIndex: index,
      'bookData.major': this.data.majors[index]
    });
  },

  async onSubmit() {
    if (!app.globalData.userId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      setTimeout(() => { wx.navigateTo({ url: '/pages/login/login' }); }, 1000);
      return;
    }

    if (!this.data.bookData.title) {
      wx.showToast({
        title: '请输入书名',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '发布中...' });

    try {
      // 1. 上传图片
      let imageUrls = [];
      if (this.data.images.length > 0) {
        wx.showLoading({ title: '正在处理图片...' });
        const uploadTasks = this.data.images.map(path => {
          // 如果是网络图片（以 http 开头）或相对路径（以 / 开头），则无需上传
          if (path.startsWith('http') || path.startsWith('/')) {
            return Promise.resolve(path);
          }
          // 否则认为是本地临时文件，需要上传
          return api.uploadFile(path);
        });
        imageUrls = await Promise.all(uploadTasks);
      }

      // 2. 提交书籍数据
      wx.showLoading({ title: this.data.editingId ? '正在更新...' : '正在发布...' });
      const bookData = {
        ...this.data.bookData,
        category: this.data.bookData.major, // Map major to category for backend
        images: imageUrls
      };

      let res;
      if (this.data.editingId) {
        res = await api.updateBook(this.data.editingId, bookData);
      } else {
        res = await api.publishBook(bookData);
      }

      if (res.code === 200 || res.code === 0) {
        wx.hideLoading();
        wx.showToast({
          title: this.data.editingId ? '更新成功' : '发布成功',
          icon: 'success'
        });

        if (this.data.editingId) {
          // 编辑模式下，延迟返回
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);
        } else {
          // 发布模式下，刷新页面（重置数据）
          this.setData({
            images: [],
            bookData: {
              title: '',
              author: '',
              publisher: '',
              isbn: '',
              price: '',
              originalPrice: '',
              description: '', // Fix: was missing comma in original check if needed
              major: this.data.majors[0],
              condition: '',
              category: ''
            },
            majorIndex: 0
          });
        }
      } else {
        throw new Error(res.message || (this.data.editingId ? '更新失败' : '发布失败'));
      }
    } catch (err) {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '发布失败',
        icon: 'none'
      });
      console.error(err);
    }
  }
});
