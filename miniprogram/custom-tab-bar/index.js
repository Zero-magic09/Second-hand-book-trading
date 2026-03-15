Component({
  data: {
    selected: 0,
    list: [
      {
        pagePath: "/pages/home/home",
        text: "首页",
        icon: "🏠"
      },
      {
        pagePath: "/pages/wanted/wanted",
        text: "求购区",
        icon: "📢"
      },
      {
        pagePath: "/pages/publish/publish",
        text: "发布",
        icon: "➕"
      },
      {
        pagePath: "/pages/message/message",
        text: "消息",
        icon: "💬"
      },
      {
        pagePath: "/pages/me/me",
        text: "我的",
        icon: "👤"
      }
    ]
  },

  methods: {
    switchTab(e) {
      const data = e.currentTarget.dataset;
      const url = data.path;
      wx.switchTab({ url });
    }
  }
});
