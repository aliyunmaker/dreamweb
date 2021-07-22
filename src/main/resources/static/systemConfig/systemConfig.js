Ext.onReady(function () {
  Ext.tip.QuickTipManager.init();

  var oss = 'https://dream-web.oss-cn-hangzhou.aliyuncs.com/static/image/icon/';
  var alignStyle = ' style="vertical-align:middle"';

  var reload = function () {
    systemConfigStore.load();
  };

  Ext.MessageBox.buttonText.ok = '确定';
  Ext.MessageBox.buttonText.yes = '确定';
  Ext.MessageBox.buttonText.no = '取消';
  Ext.MessageBox.buttonText.cancel = '取消';

  var setUnEditable = function (name) {
    systemConfigFormWindow.getFormPanel().getForm().findField(name).setReadOnly(true);
    systemConfigFormWindow.getFormPanel().getForm().findField(name).setFieldStyle('background: white; color: #bbbbbb;');
  }

  var setEditable = function(name) {
    systemConfigFormWindow.getFormPanel().getForm().findField(name).setReadOnly(false);
    systemConfigFormWindow.getFormPanel().getForm().findField(name).setFieldStyle('background: white; color: black;');
  }

  var systemConfigStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../systemConfig/listSystemConfig.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['id', 'configName', 'configValue', 'comment', 'gmtCreate', { name: 'changeable', type: 'string' }]
  });

  var systemConfigGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: '系统配置列表',
    store: systemConfigStore,
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
        var select = MyExt.util.SelectGridModel(systemConfigGrid);
        if (!select) {
          return;
        }
        setUnEditable('configName');
        if (select[0].data['changeable'] != 'true') {
          setUnEditable('configValue');
          setUnEditable('comment');
        }else {
          setEditable('configValue');
          setEditable('comment');
        }
        systemConfigFormWindow.changeFormUrlAndShow("../systemConfig/updateSystemConfig.do");
        systemConfigFormWindow.getFormPanel().getForm().loadRecord(select[0]);
      }
    }, {
      text: '更新系统密钥',
      iconCls: 'MyExt-refresh',
      handler: function () {
        Ext.MessageBox.confirm('提示', '<b><font color=\'red\'>是否确定更新?</font></b>', function (btn) {
          if (btn == 'yes') {
            MyExt.util.Ajax('../rsakey/updateRSAKey.do', {}, function (data) {
              if (data.success) {
                Ext.MessageBox.alert('提示', '更新成功，点击确定返回登录页', function () {
                  window.top.location.href = "../logout";
                });
              } else {
                MyExt.Msg.alert("更新失败");
              }
            })
          }
        })
      }
    }]
  });

  var LABELWIDTH = 60;

  var systemConfigFormWindow = new MyExt.Component.FormWindow({
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
      var form = systemConfigFormWindow.getFormPanel().getForm();
      if (form.isValid()) {
        var formValue = form.getValues();
        MyExt.util.Ajax(systemConfigFormWindow.getFormPanel().url, {
          id: formValue['id'],
          configName: formValue['configName'],
          configValue: formValue['configValue'],
          comment: formValue['comment']
        }, function (data) {
          if (data.success) {
            systemConfigFormWindow.hide();
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
    items: [systemConfigGrid]
  });

  reload();
});
