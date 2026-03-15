const api = require('../../utils/api.js');

Page({
  data: {
    filter: '全部',
    records: []
  },

  onLoad() {
    this.loadRecords();
  },

  onShow() {
    this.loadRecords();
  },

  loadRecords() {
    wx.showLoading({ title: '加载中' });
    api.getMyPublishedBooks({ page: 0, size: 50 }).then(res => {
      wx.hideLoading();
      if (res.code === 0 || res.code === 200) {
        const records = res.data.content.map(book => {
          let statusText = '未知';
          let statusClass = '';
          // 0-在售 1-已预订 2-已售出 3-已下架
          switch (book.status) {
            case 0: statusText = '出售中'; statusClass = 'selling'; break;
            case 1: statusText = '被预订'; statusClass = 'reserved'; break;
            case 2: statusText = '已售出'; statusClass = 'sold'; break;
            case 3: statusText = '已下架'; statusClass = 'off-shelf'; break;
          }

          return {
            id: book.id,
            title: book.title,
            price: book.price,
            originalPrice: book.originalPrice,
            image: book.images && book.images.length > 0 ? api.processImageUrl(book.images[0]) : api.processImageUrl(null),
            status: statusText,
            statusClass: statusClass, // 用于样式
            statusCode: book.status,
            views: book.viewCount || 0,
            time: book.createdAt.replace('T', ' ').substring(0, 10)
          };
        });

        // 简单的前端筛选
        const filteredRecords = this.filterRecords(records, this.data.filter);
        this.setData({ records: filteredRecords, allRecords: records });
      } else {
        wx.showToast({ title: '加载失败', icon: 'none' });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error(err);
      wx.showToast({ title: '网络错误', icon: 'none' });
    });
  },

  filterRecords(records, filter) {
    if (filter === '全部') return records;
    return records.filter(r => r.status === filter);
  },

  onBack() {
    wx.navigateBack();
  },

  onFilterChange(e) {
    const filter = e.currentTarget.dataset.filter;
    this.setData({ filter });

    if (this.data.allRecords) {
      const filtered = this.filterRecords(this.data.allRecords, filter);
      this.setData({ records: filtered });
    } else {
      this.loadRecords();
    }
  },

  onDelete(e) {
    const id = e.currentTarget.dataset.id;
    const status = e.currentTarget.dataset.status;

    if (status !== 0) {
      wx.showToast({
        title: '只能删除出售中的商品',
        icon: 'none'
      });
      return;
    }

    const that = this;
    wx.showModal({
      title: '确认删除',
      content: '删除后无法恢复，是否确认？',
      success(res) {
        if (res.confirm) {
          wx.showLoading({ title: '删除中' });
          api.deleteBook(id).then(res => {
            wx.hideLoading();
            if (res.code === 0 || res.code === 200) {
              wx.showToast({ title: '已删除' });
              // 重新加载
              that.loadRecords();
            } else {
              wx.showToast({ title: res.message || '删除失败', icon: 'none' });
            }
          }).catch(err => {
            wx.hideLoading();
            wx.showToast({ title: '网络错误', icon: 'none' });
          });
        }
      }
    });
  },

  onEdit(e) {
    const id = e.currentTarget.dataset.id;
    // 跳转到独立的编辑页面
    wx.navigateTo({
      url: '/pages/publish-edit/publish-edit?id=' + id
    });
  },

  onPolish(e) {
    wx.showToast({
      title: '擦亮成功，曝光度 +10',
      icon: 'none'
    });
  },

  onRepublish(e) {
    const id = e.currentTarget.dataset.id;
    const that = this;
    wx.showModal({
      title: '确认上架',
      content: '确认重新上架该书籍？',
      success(res) {
        if (res.confirm) {
          wx.showLoading({ title: '处理中' });
          api.updateBookStatus(id, 0).then(res => {
            wx.hideLoading();
            if (res.code === 0 || res.code === 200) {
              wx.showToast({ title: '已重新上架' });
              that.loadRecords();
            } else {
              wx.showToast({ title: res.message || '操作失败', icon: 'none' });
            }
          }).catch(err => {
            wx.hideLoading();
            wx.showToast({ title: '网络错误', icon: 'none' });
          });
        }
      }
    });
  }
});
