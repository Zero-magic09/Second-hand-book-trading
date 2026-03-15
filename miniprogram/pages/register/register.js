const api = require('../../utils/api.js');
const app = getApp();

Page({
    data: {
        school: '',
        studentId: '',
        username: '',
        phoneNumber: '',
        vCode: '',
        captchaCode: '',
        canSubmit: false
    },

    onLoad() {
        this.generateCaptcha();
    },

    // 生成随机4位数字验证码
    generateCaptcha() {
        const code = Math.floor(1000 + Math.random() * 9000).toString();
        this.setData({ captchaCode: code });
    },

    // 点击刷新验证码
    refreshCaptcha() {
        this.generateCaptcha();
        wx.showToast({ title: '已刷新', icon: 'none', duration: 800 });
    },

    onSchoolInput(e) {
        this.setData({ school: e.detail.value });
        this.checkCanSubmit();
    },

    onStudentIdInput(e) {
        this.setData({ studentId: e.detail.value });
        this.checkCanSubmit();
    },

    onUsernameInput(e) {
        this.setData({ username: e.detail.value });
        this.checkCanSubmit();
    },

    onPhoneInput(e) {
        const phoneNumber = e.detail.value;
        this.setData({ phoneNumber });
        this.checkCanSubmit();
    },

    onCodeInput(e) {
        this.setData({ vCode: e.detail.value });
        this.checkCanSubmit();
    },

    checkCanSubmit() {
        const { school, studentId, username, phoneNumber, vCode } = this.data;
        this.setData({
            canSubmit: school.length > 0 && studentId.length > 0 && username.length > 0 && phoneNumber.length === 11 && vCode.length === 4
        });
    },

    onRegister() {
        const { school, studentId, username, phoneNumber, vCode, captchaCode } = this.data;

        if (!school) {
            wx.showToast({ title: '请输入学校名称', icon: 'none' });
            return;
        }

        if (!studentId) {
            wx.showToast({ title: '请输入学号', icon: 'none' });
            return;
        }

        if (!username) {
            wx.showToast({ title: '请输入用户名', icon: 'none' });
            return;
        }

        if (phoneNumber.length !== 11) {
            wx.showToast({ title: '请输入11位手机号', icon: 'none' });
            return;
        }

        if (vCode !== captchaCode) {
            wx.showToast({ title: '验证码错误', icon: 'none' });
            this.generateCaptcha();
            this.setData({ vCode: '' });
            return;
        }

        wx.showLoading({ title: '注册中...' });

        api.post('/auth/register', {
            phone: phoneNumber,
            verifyCode: vCode,
            nickname: username,
            school: school,
            studentId: studentId
        }).then(res => {
            wx.hideLoading();
            if (res.code === 200) {
                wx.showToast({ title: '注册成功，请登录', icon: 'success' });

                setTimeout(() => {
                    // 注册成功，跳转回登录页
                    wx.reLaunch({ url: '/pages/login/login' });
                }, 1500);
            } else {
                wx.showToast({ title: res.message || '注册失败', icon: 'none' });
            }
        }).catch(err => {
            wx.hideLoading();
            wx.showToast({ title: '网络请求失败', icon: 'none' });
            console.error(err);
        });
    }
});
