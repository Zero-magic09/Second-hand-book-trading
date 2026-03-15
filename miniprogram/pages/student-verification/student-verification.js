// 学生认证 student-verification.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    currentStatus: 'unverified', // unverified, pending, verified
    step: 1,
    majors: ['计算机', '经济管理', '机械工程', '外国语', '建筑设计', '通识教育'],
    formData: {
      name: '',
      studentId: '',
      major: '计算机'
    },
    photoUrl: '',
    canSubmit: false,
    benefits: [
      { emoji: '👤', title: '身份专属标识', desc: '主页及发布展示认证图标' },
      { emoji: '🎓', title: '解锁发布权限', desc: '认证后可发布闲置与求购' },
      { emoji: '🛡️', title: '提高交易信任', desc: '买家更愿意购买认证学子的书' }
    ]
  },

  onLoad() {
    this.setData({
      majors: app.globalData.majors || this.data.majors
    });
    this.checkStatus();
  },

  async checkStatus() {
    try {
      const res = await api.getCurrentUser();
      if (res.code === 200 || res.code === 0) {
        const user = res.data;
        // verificationStatus: 0-未认证 1-待审核 2-已认证 3-认证失败
        let status = 'unverified';
        let step = 1;

        if (user.verificationStatus === 1) {
          step = 2; // 审核中
        } else if (user.verificationStatus === 2) {
          status = 'verified';
        }

        this.setData({
          currentStatus: status,
          step: step
        });
      }
    } catch (e) {
      console.error('获取状态失败', e);
    }
  },

  onBack() {
    wx.navigateBack();
  },

  onInputChange(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`formData.${field}`]: value
    });
    this.checkCanSubmit();
  },

  onMajorChange(e) {
    const idx = e.detail.value;
    this.setData({
      'formData.major': this.data.majors[idx]
    });
  },

  checkCanSubmit() {
    const { name, studentId } = this.data.formData;
    const { photoUrl } = this.data;
    this.setData({
      canSubmit: !!(name.trim() && studentId.trim() && photoUrl)
    });
  },

  onUploadPhoto() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        this.setData({
          photoUrl: tempFilePath
        });
        this.checkCanSubmit();
      },
      fail: (err) => {
        console.error('[onUploadPhoto] 选择图片失败:', err);
      }
    });
  },

  onDeletePhoto() {
    this.setData({
      photoUrl: ''
    });
    this.checkCanSubmit();
  },

  async onSubmit() {
    if (!this.data.canSubmit) return;

    wx.showLoading({ title: '提交中...' });

    try {
      const { name, studentId } = this.data.formData;

      // 先上传图片获取URL
      let photoUrl = '';
      if (this.data.photoUrl) {
        photoUrl = await api.uploadFile(this.data.photoUrl);
      }

      const res = await api.submitVerification({
        school: '浙江大学',
        studentId: studentId,
        realName: name,
        studentIdCardUrl: photoUrl
      });

      if (res.code === 200 || res.code === 0) {
        wx.showToast({ title: '提交成功' });
        this.setData({ step: 2 });
      } else {
        throw new Error(res.message);
      }
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  }
});
