/**
 * API 请求模块
 */

// 后端 API 地址
// 本地开发：使用 localhost
// 手机调试：使用电脑局域网IP（如 192.168.1.xxx）
// 注意：微信开发者工具需要勾选"不校验合法域名"

// 根据运行环境自动选择地址
const getBaseUrl = () => {
  const systemInfo = wx.getSystemInfoSync()
  // 开发者工具使用 localhost，真机使用局域网IP
  if (systemInfo.platform === 'devtools') {
    return 'http://localhost:8080/api'
  } else {
    // 手机调试时，请替换为你电脑的实际IP
    // 自动获取本机IP地址（示例）
    return 'http://localhost:8080/api'
  }
}

const BASE_URL = getBaseUrl()

// 处理图片URL，转换为完整路径
const processImageUrl = (url) => {
  if (!url || url === 'null' || url === 'undefined') {
    return 'https://picsum.photos/seed/book/400/500' // 默认图片
  }
  // 已经是完整URL
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }

  const rootUrl = BASE_URL.endsWith('/api') ? BASE_URL.substring(0, BASE_URL.length - 4) : BASE_URL;

  // 相对路径转换为完整URL
  if (url.startsWith('/')) {
    return rootUrl + url
  }
  // 其他情况使用默认图片
  return 'https://picsum.photos/seed/' + url + '/400/500'
}

// 请求封装
const request = (url, options = {}) => {
  return new Promise((resolve, reject) => {
    const app = getApp()
    const fullUrl = BASE_URL + url
    console.log('[请求]', options.method || 'GET', fullUrl, options.data)

    // 获取用户ID，优先从app.globalData获取，如果没有则尝试从本地存储获取
    let userId = app.globalData.userId;
    if (!userId) {
      userId = wx.getStorageSync('userId');
      if (userId) {
        app.globalData.userId = userId;
      }
    }

    wx.request({
      url: fullUrl,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + (wx.getStorageSync('token') || ''),
        'X-User-Id': userId || (app.globalData.userInfo ? app.globalData.userInfo.id : '') || 'anonymous',
        ...options.header
      },
      success: res => {
        console.log('[响应]', fullUrl, res.statusCode, res.data)
        if (res.statusCode === 200) {
          resolve(res.data)
        } else if (res.statusCode === 401) {
          wx.navigateTo({
            url: '/pages/login/login'
          })
          reject(new Error('未授权，请先登录'))
        } else {
          reject(new Error(res.data.message || '请求失败'))
        }
      },
      fail: err => {
        console.error('Request failed:', err)
        // 失败时使用本地数据作为备用
        if (options.method === 'GET' && url === '/wanted') {
          // 移除本地mock数据，强制使用后端数据
          console.warn('后端请求失败 /wanted');
        }
        reject(err)
      }
    })
  })
}

// GET 请求
const get = (url, data) => request(url, { method: 'GET', data })

// POST 请求
const post = (url, data) => request(url, { method: 'POST', data })

// PUT 请求
const put = (url, data) => request(url, { method: 'PUT', data })

// DELETE 请求
const del = (url, data) => request(url, { method: 'DELETE', data })

// ================== 书籍 API ==================

// 获取我发布的书籍
const getMyPublishedBooks = (params = {}) => {
  return request('/books/my', {
    method: 'GET',
    data: {
      page: params.page || 0,
      size: params.size || 20
    }
  });
}

// 获取书籍列表（调用后端接口）
const getBooks = (params = {}) => {
  return new Promise((resolve, reject) => {
    request('/books', {
      method: 'GET',
      data: {
        status: params.status || 0, // 默认获取在售书籍
        category: params.category !== '推荐' ? params.category : '',
        keyword: params.keyword || '',
        page: params.page || 0,
        size: params.size || 20
      }
    }).then(res => {
      console.log('[getBooks] 响应数据:', res) // 添加详细日志

      // 后端返回 code: 200 表示成功
      if ((res.code === 0 || res.code === 200) && res.data) {
        // 转换数据格式适配前端
        const books = (res.data.content || []).map(book => {
          console.log('[getBooks] 处理书籍数据:', book) // 详细日志

          // 处理图片URL
          let images = []
          if (book.images) {
            try {
              // images 可能是JSON字符串或数组
              const imgList = typeof book.images === 'string' ? JSON.parse(book.images) : book.images
              images = (Array.isArray(imgList) ? imgList : [imgList]).map(img => processImageUrl(img))
            } catch (e) {
              console.log('[getBooks] 图片处理错误:', e)
              images = [processImageUrl(book.images)]
            }
          }
          if (images.length === 0) {
            images = ['https://picsum.photos/seed/book' + book.id + '/400/500']
          }

          return {
            id: book.id,
            title: book.title,
            author: book.author,
            price: book.price,
            originalPrice: book.originalPrice,
            condition: book.bookCondition || book.condition,
            category: book.category,
            major: book.category,
            images: images,
            publisher: book.publisher,
            isbn: book.isbn,
            sellerId: book.sellerId,
            sellerName: book.sellerNickname || '匿名用户',
            sellerAvatar: processImageUrl(book.sellerAvatar) || 'https://picsum.photos/seed/avatar/100/100',
            description: book.description,
            createdAt: book.createdAt,
            status: book.status
          }
        })

        console.log('[getBooks] 转换后的书籍列表:', books) // 详细日志

        resolve({
          code: 0,
          data: books,
          total: res.data.totalElements,
          message: 'success'
        })
      } else {
        console.log('[getBooks] 响应格式不符合预期:', res)
        resolve({
          code: 0,
          data: [],
          message: res.message || 'success'
        })
      }
    }).catch(err => {
      console.error('获取书籍失败，使用本地数据:', err)
      // 失败时使用本地模拟数据
      const app = getApp()
      let books = app.globalData.mockBooks || []

      if (params.category && params.category !== '推荐' && params.category !== '全部') {
        books = books.filter(b => b.category === params.category || b.major === params.category)
      }

      if (params.keyword) {
        const keyword = params.keyword.toLowerCase()
        books = books.filter(b =>
          b.title.toLowerCase().includes(keyword) ||
          (b.author && b.author.toLowerCase().includes(keyword))
        )
      }

      resolve({
        code: 0,
        data: books,
        message: 'success (本地数据)'
      })
    })
  })
}

// 获取书籍详情
// 获取书籍详情
const getBookDetail = (id) => {
  return request(`/books/${id}`, {
    method: 'GET'
  });
}


// 获取聊天列表
const getChatList = () => {
  return new Promise(resolve => {
    const app = getApp()
    const chats = app.globalData.mockChats || []

    setTimeout(() => {
      resolve({
        code: 0,
        data: chats,
        message: 'success'
      })
    }, 200)
  })
}

// 发送消息
const sendMessage = (chatId, content) => {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 0,
        data: {
          id: 'msg_' + Date.now(),
          content,
          time: new Date().toISOString(),
          isSelf: true
        },
        message: 'success'
      })
    }, 100)
  })
}

// 获取订单详情
const getOrderDetail = (id) => {
  return request(`/orders/${id}`, {
    method: 'GET'
  });
}

// 获取买家订单
const getBuyerOrders = (status) => {
  const data = {};
  if (status !== undefined && status !== null) {
    data.status = status;
  }
  return request('/orders/buy', {
    method: 'GET',
    data: data
  });
}

// 卖家发货
const deliverOrder = (id) => {
  return request(`/orders/${id}/deliver`, {
    method: 'POST'
  });
}

// 获取卖家订单
const getSellerOrders = (status) => {
  const data = {};
  if (status !== undefined && status !== null) {
    data.status = status;
  }
  return request('/orders/sell', {
    method: 'GET',
    data: data
  });
}

// 发布书籍
const publishBook = (bookData) => {
  return post('/books', bookData);
}

// 删除书籍
const deleteBook = (id) => {
  return del(`/books/${id}`);
}

// 更新书籍状态
const updateBookStatus = (id, status) => {
  return put(`/books/${id}/status?status=${status}`);
}

// 更新书籍详情
const updateBook = (id, bookData) => {
  return put(`/books/${id}`, bookData);
}

// 发布求购
const publishWanted = (wantedData) => {
  return new Promise((resolve, reject) => {
    request('/wanted', {
      method: 'POST',
      data: wantedData
    }).then(res => {
      if (res.code === 0 || res.code === 200) {
        resolve(res);
      } else {
        reject(new Error(res.message || '发布失败'));
      }
    }).catch(err => {
      console.error('发布求购失败:', err);
      // 失败时使用本地数据作为备用
      setTimeout(() => {
        resolve({
          code: 0,
          data: {
            id: 'wanted_' + Date.now(),
            ...wantedData
          },
          message: '发布成功（本地）'
        });
      }, 500);
    });
  });
}

// 获取求购列表
const getWantedList = (params = {}) => {
  return new Promise((resolve, reject) => {
    request('/wanted', {
      method: 'GET',
      data: {
        status: params.status || 0,
        category: params.category || '',
        keyword: params.keyword || '',
        page: params.page || 0,
        size: params.size || 20
      }
    }).then(res => {
      if (res.code === 0 || res.code === 200) {
        resolve(res);
      } else {
        reject(new Error(res.message || '获取求购列表失败'));
      }
    }).catch(err => {
      console.error('获取求购列表失败:', err);
      // 失败时使用本地数据作为备用
      const app = getApp();
      const mockWanted = app.globalData.mockWantedList || [
        {
          id: 'w1',
          title: '高等数学同济第七版',
          budget: '30',
          major: '计算机',
          user: '李同学',
          time: '2小时前',
          avatar: 'https://picsum.photos/seed/w1/100/100'
        }
      ];

      resolve({
        code: 0,
        data: {
          content: mockWanted,
          totalElements: mockWanted.length
        },
        message: 'success (本地数据)'
      });
    });
  });
}

// 获取求购详情
const getWantedDetail = (id) => {
  return new Promise((resolve, reject) => {
    request(`/wanted/${id}`, {
      method: 'GET'
    }).then(res => {
      if (res.code === 0 || res.code === 200) {
        resolve(res);
      } else {
        reject(new Error(res.message || '获取求购详情失败'));
      }
    }).catch(err => {
      console.error('获取求购详情失败:', err);
      // 失败时使用本地数据作为备用
      const mockDetail = {
        id: id,
        title: '《离散数学及其应用》',
        author: 'Kenneth H. Rosen',
        category: '计算机',
        maxPrice: 40,
        description: '求购离散数学教材，最好有配套习题解答。需要最新版，品相较好。',
        userNickname: '周同学',
        userAvatar: 'https://picsum.photos/seed/w1/100/100',
        createdAt: '2024-01-15T14:30:00',
        status: 0
      };

      resolve({
        code: 0,
        data: mockDetail,
        message: 'success (本地数据)'
      });
    });
  });
}

// 学生认证
const submitVerification = (data) => {
  return request('/users/verification', {
    method: 'POST',
    data: data
  });
}

// 获取当前用户信息
const getCurrentUser = () => {
  return request('/users/me', {
    method: 'GET'
  });
}

// 上传文件
const uploadFile = (filePath) => {
  return new Promise((resolve, reject) => {
    const app = getApp();

    // 获取用户ID
    let userId = app.globalData.userId;
    if (!userId) {
      userId = wx.getStorageSync('userId');
      if (userId) {
        app.globalData.userId = userId;
      }
    }

    wx.uploadFile({
      url: BASE_URL + '/upload/image',
      filePath: filePath,
      name: 'file',
      header: {
        'X-User-Id': userId || 'anonymous'
      },
      success(res) {
        // uploadFile返回的是字符串，需要解析
        try {
          const data = JSON.parse(res.data);
          if (data.code === 0 || data.code === 200) {
            resolve(data.url);
          } else {
            reject(new Error(data.message || '上传失败'));
          }
        } catch (e) {
          reject(new Error('上传响应解析失败'));
        }
      },
      fail(err) {
        reject(err);
      }
    });
  });
}

// 用户登录
const login = (code) => {
  // ... existing login code ...
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 0,
        data: {
          token: 'mock_token_' + Date.now(),
          userInfo: {
            id: 'user_1',
            nickName: '校园用户',
            avatarUrl: '/images/avatar-placeholder.png',
            isVerified: false
          }
        },
        message: '登录成功'
      })
    }, 500)
  })
}

// 买家确认收货
const receiveOrder = (id) => {
  return request(`/orders/${id}/receive`, {
    method: 'POST'
  });
}

module.exports = {
  request,
  processImageUrl,
  get,
  post,
  put,
  del,
  getBooks,
  getMyPublishedBooks,
  getBookDetail,
  getWantedList,
  getChatList,
  sendMessage,
  getOrderDetail,
  deliverOrder,
  receiveOrder, // 导出新方法
  getBuyerOrders,
  getSellerOrders,
  publishBook,
  deleteBook,
  updateBook,
  updateBookStatus,
  publishWanted,
  uploadFile,
  submitVerification,
  getCurrentUser,
  login
}
