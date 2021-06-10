Ext.onReady(function () {
  Ext.tip.QuickTipManager.init();

  var oss = 'https://ichengchao.oss-cn-hangzhou.aliyuncs.com/static/image/icon/';
  var alignStyle = ' style="vertical-align:middle"';

  var reload = function () {
    slsConfigStore.load();
  };

  Ext.MessageBox.buttonText.ok = '确定';
  Ext.MessageBox.buttonText.yes = '确定';
  Ext.MessageBox.buttonText.no = '取消';
  Ext.MessageBox.buttonText.cancel = '取消';

  var slsConfigStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../slsConfig/getSlsConfigByOwnerId.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['id', 'configName', 'configValue', 'comment', 'gmtCreate']
  });

  var slsConfigGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: '系统配置列表',
    store: slsConfigStore,
    columns: [{
      dataIndex: 'id',
      header: 'ID',
      hidden: true
    }, {
      dataIndex: 'configName',
      header: '配置名',
      flex: 1
    }, {
      dataIndex: 'configValue',
      header: '配置值',
      flex: 2
    }, {
      dataIndex: 'comment',
      header: '配置说明',
      flex: 2
    }],
    tbar: [{
      text: '修改',
      iconCls: 'MyExt-modify',
      handler: function () {
        var select = MyExt.util.SelectGridModel(slsConfigGrid);
        if (!select) {
          return;
        }
        slsConfigFormWindow.changeFormUrlAndShow("../slsConfig/updateSlsConfig.do");
        slsConfigFormWindow.getFormPanel().getForm().loadRecord(select[0]);
      }
    }, {
      text: '增加',
      iconCls: 'MyExt-add',
      handler: function () {
        slsConfigFormWindow.changeFormUrlAndShow('../slsConfig/addSlsConfig.do');
      }
    }, {
      text: '删除',
        iconCls: 'MyExt-delete',
        handler: function () {
          var select = MyExt.util.SelectGridModel(slsConfigGrid, true);
          if (!select) {
            return;
          }
        MyExt.util.MessageConfirm('是否确定删除', function () {
        MyExt.util.Ajax('../slsConfig/deleteSlsConfigById.do', {
          id: select[0].data["id"],
        }, function (data) {
          reload();
          MyExt.Msg.alert('删除成功!');
          });
        });
      }
    }]
  });

  var LABELWIDTH = 60;

  var slsConfigFormWindow = new MyExt.Component.FormWindow({
    title: '操作',
    width: 400,
    height: 240,
    formItems: [{
      name: 'id',
      hidden: true
    }, {
      xtype: 'textfield',
      fieldLabel: '配置名',
      labelWidth: LABELWIDTH,
      name: 'configName',
      allowBlank: false
    }, {
      xtype: 'textarea',
      fieldLabel: '配置值',
      labelWidth: LABELWIDTH,
      name: 'configValue',
      allowBlank: false
    }, {
      xtype: 'textarea',
      fieldLabel: '配置说明',
      labelWidth: LABELWIDTH,
      name: 'comment'
    }],
    submitBtnFn: function () {
      var form = slsConfigFormWindow.getFormPanel().getForm();
      if (form.isValid()) {
        var formValue = form.getValues();
        MyExt.util.Ajax(slsConfigFormWindow.getFormPanel().url, {
          id: formValue['id'],
          configName: formValue['configName'],
          configValue: formValue['configValue'],
          comment: formValue['comment']
        }, function (data) {
          if (data.success) {
            slsConfigFormWindow.hide();
            reload();
            MyExt.Msg.alert('操作成功!');
          } else {
            MyExt.Msg.alert(data.errorMsg);
          }
        });
      }
    }
  });

  Ext.create('Ext.container.Viewport', {
    layout: 'border',
    items: [slsConfigGrid]
  });

  reload();
});
