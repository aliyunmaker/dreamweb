Ext.onReady(function () {
  Ext.tip.QuickTipManager.init();

  var oss = 'https://dream-web.oss-cn-hangzhou.aliyuncs.com/static/image/icon/';
  var alignStyle = ' style="vertical-align:middle"';

  var reload = function () {
    loginRecordStore.load();
  };

  var loginRecordStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../loginRecord/listLoginRecord.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['id', 'clientIpAddr', 'loginName', 'loginMethod', 'comment', 'gmtCreate']
  });

  var loginRecordGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: '登录记录列表',
    store: loginRecordStore,
    columns: [{
      dataIndex: 'id',
      header: 'ID',
      hidden: true
    }, {
      dataIndex: 'gmtCreate',
      header: '登录时间',
      flex: 1
    },  {
      dataIndex: 'clientIpAddr',
      header: '客户端IP地址',
      flex: 1
    }, {
      dataIndex: 'loginName',
      header: '登录名',
      flex: 1
    }, {
      dataIndex: 'loginMethod',
      header: '登录方式',
      flex: 1,
      renderer: function (value) {
        if (value === "NORMAL_LOGIN") {
          return '用户名密码登录';
        } else if (value === "WEIXIN_LOGIN") {
          return '微信登录';
        } else if (value === "AUTO_LOGIN") {
          return '通过token自动登录';
        } else {
          return value;
        }
      }
    }, {
      dataIndex: 'comment',
      header: '备注',
      flex: 2
    }]
  });

  Ext.create('Ext.container.Viewport', {
    layout: 'border',
    items: [loginRecordGrid]
  });

  reload();
});
