Ext.onReady(function () {
  Ext.tip.QuickTipManager.init();

  var oss = 'https://dream-web.oss-cn-hangzhou.aliyuncs.com/static/image/icon/';
  var alignStyle = ' style="vertical-align:middle"';

  var reload = function () {
    resourceViewStore.load();
  };

  var resourceViewStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../resourceView/listAccountResourceInfo.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['accountId', 'displayName', 'resourceCount', "resourceCountDeleted"]
  });

  // resourceViewStore.on('beforeload', function (store, options) {
  //   options.params = Ext.apply(options.params || {}, searchForm.getForm().getValues());
  // })

  var resourceViewGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: '资源视图',
    store: resourceViewStore,
    columns: [{
      dataIndex: 'accountId',
      header: '子账号UID',
      flex: 1,
      renderer: function (value) {
        return '<span style="line-height:24px;font-size:16px;">' + value + '</span>';
      }
    }, {
      dataIndex: 'displayName',
      header: '子账号名称',
      flex: 1,
      renderer: function (value) {
        return '<span style="line-height:24px;font-size:16px;">' + value + '</span>';
      }
    }, {
      dataIndex: 'resourceCount',
      header: '运行中的资源列表',
      flex: 2
    }, {
      dataIndex: 'resourceCountDeleted',
      header: '已删除的资源列表',
      flex: 2
    }],
    tbar: [{
      xtype: 'button',
      text: '刷新',
      iconCls: 'MyExt-refresh',
      handler: function () {
        reload();
      }
    }]
  });

  // var form_textRegionId = Ext.create('Ext.form.field.Text', {
  //   labelWidth: 60,
  //   columnWidth: 0.3,
  //   style: 'margin-left: 10px',
  //   fieldLabel: 'Region ID',
  //   name: 'regionId',
  //   allowBlank: false
  // })
  //
  // var form_textAccessKeyId = Ext.create('Ext.form.field.Text', {
  //   labelWidth: 80,
  //   columnWidth: 0.35,
  //   style: 'margin-left: 15px',
  //   fieldLabel: 'AccessKey ID',
  //   name: 'accessKeyId',
  //   allowBlank: false
  // })
  //
  // var form_textAccessKeySecret = Ext.create('Ext.form.field.Text', {
  //   labelWidth: 100,
  //   columnWidth: 0.35,
  //   style: 'margin-left: 15px',
  //   fieldLabel: 'AccessKey Secret',
  //   name: 'accessKeySecret',
  //   allowBlank: false
  // })
  //
  // var searchForm = Ext.create('Ext.form.Panel', {
  //   region: 'north',
  //   frame: true,
  //   bodyStyle: 'padding:15px',
  //   defaultType: 'textfield',
  //   buttonAlign: 'left',
  //   height: 130,
  //   items: [{
  //     layout: 'column',
  //     xtype: 'fieldset',
  //     title: '查询条件',
  //     items: [form_textRegionId, form_textAccessKeyId, form_textAccessKeySecret]
  //   }],
  //   buttons: [{
  //     text: '搜索',
  //     style: 'margin-left:10px; margin-bottom:10px',
  //     handler: function () {
  //       reload();
  //     }
  //   }]
  // });

  Ext.create('Ext.container.Viewport', {
    layout: 'border',
    items: [resourceViewGrid]
  });

  reload();
});
