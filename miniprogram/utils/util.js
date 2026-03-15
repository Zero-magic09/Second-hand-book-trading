/**
 * 工具函数模块
 */

// 格式化时间
const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return `${[year, month, day].map(formatNumber).join('/')} ${[hour, minute, second].map(formatNumber).join(':')}`
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : `0${n}`
}

// 格式化日期（仅日期）
const formatDate = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${year}-${formatNumber(month)}-${formatNumber(day)}`
}

// 相对时间
const getRelativeTime = dateStr => {
  const now = new Date()
  const date = new Date(dateStr)
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return formatDate(date)
}

// 价格格式化
const formatPrice = price => {
  if (typeof price !== 'number') return '0.00'
  return price.toFixed(2)
}

// 防抖函数
const debounce = (fn, delay = 300) => {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// 节流函数
const throttle = (fn, interval = 300) => {
  let lastTime = 0
  return function (...args) {
    const now = Date.now()
    if (now - lastTime >= interval) {
      lastTime = now
      fn.apply(this, args)
    }
  }
}

// 显示加载提示
const showLoading = (title = '加载中...') => {
  wx.showLoading({
    title,
    mask: true
  })
}

// 隐藏加载提示
const hideLoading = () => {
  wx.hideLoading()
}

// 显示提示
const showToast = (title, icon = 'none', duration = 2000) => {
  wx.showToast({
    title,
    icon,
    duration
  })
}

// 显示成功提示
const showSuccess = (title = '操作成功') => {
  showToast(title, 'success')
}

// 显示错误提示
const showError = (title = '操作失败') => {
  showToast(title, 'error')
}

// 确认对话框
const showConfirm = (content, title = '提示') => {
  return new Promise((resolve, reject) => {
    wx.showModal({
      title,
      content,
      success: res => {
        if (res.confirm) {
          resolve(true)
        } else {
          resolve(false)
        }
      },
      fail: reject
    })
  })
}

// 检查登录状态
const checkLogin = () => {
  const app = getApp()
  return app.globalData.isLoggedIn
}

// 跳转到登录页
const goLogin = () => {
  wx.navigateTo({
    url: '/pages/login/login'
  })
}

// 需要登录的操作
const requireLogin = (callback) => {
  if (checkLogin()) {
    callback && callback()
  } else {
    showConfirm('请先登录后再操作', '提示').then(confirm => {
      if (confirm) {
        goLogin()
      }
    })
  }
}

// 获取存储数据
const getStorage = key => {
  try {
    return wx.getStorageSync(key)
  } catch (e) {
    console.error('getStorage error:', e)
    return null
  }
}

// 设置存储数据
const setStorage = (key, data) => {
  try {
    wx.setStorageSync(key, data)
    return true
  } catch (e) {
    console.error('setStorage error:', e)
    return false
  }
}

// 移除存储数据
const removeStorage = key => {
  try {
    wx.removeStorageSync(key)
    return true
  } catch (e) {
    console.error('removeStorage error:', e)
    return false
  }
}

// 图片选择
const chooseImage = (count = 1) => {
  return new Promise((resolve, reject) => {
    wx.chooseMedia({
      count,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: res => {
        const tempFiles = res.tempFiles.map(item => item.tempFilePath)
        resolve(tempFiles)
      },
      fail: reject
    })
  })
}

// 复制到剪贴板
const copyText = text => {
  wx.setClipboardData({
    data: text,
    success: () => {
      showToast('已复制')
    }
  })
}

// 拨打电话
const makePhoneCall = phoneNumber => {
  wx.makePhoneCall({
    phoneNumber,
    fail: () => {
      copyText(phoneNumber)
      showToast('已复制号码')
    }
  })
}

// 返回上一页
const navigateBack = (delta = 1) => {
  wx.navigateBack({ delta })
}

// 页面跳转
const navigateTo = url => {
  wx.navigateTo({ url })
}

// 重定向
const redirectTo = url => {
  wx.redirectTo({ url })
}

// 切换Tab
const switchTab = url => {
  wx.switchTab({ url })
}

module.exports = {
  formatTime,
  formatDate,
  formatNumber,
  getRelativeTime,
  formatPrice,
  debounce,
  throttle,
  showLoading,
  hideLoading,
  showToast,
  showSuccess,
  showError,
  showConfirm,
  checkLogin,
  goLogin,
  requireLogin,
  getStorage,
  setStorage,
  removeStorage,
  chooseImage,
  copyText,
  makePhoneCall,
  navigateBack,
  navigateTo,
  redirectTo,
  switchTab
}
