Ext.onReady(function () {
  Ext.tip.QuickTipManager.init();

  var oss = 'https://ichengchao.oss-cn-hangzhou.aliyuncs.com/static/image/icon/';
  var alignStyle = ' style="vertical-align:middle"';

  var reload = function () {
    solutionConfigStore.load();
  };

  Ext.MessageBox.buttonText.ok = '确定';
  Ext.MessageBox.buttonText.yes = '确定';
  Ext.MessageBox.buttonText.no = '取消';
  Ext.MessageBox.buttonText.cancel = '取消';

  var setUnEditable = function (name) {
    solutionConfigFormWindow.getFormPanel().getForm().findField(name).setReadOnly(true);
    solutionConfigFormWindow.getFormPanel().getForm().findField(name).setFieldStyle('background: white; color: #bbbbbb;');
  }

  var setEditable = function(name) {
    solutionConfigFormWindow.getFormPanel().getForm().findField(name).setReadOnly(false);
    solutionConfigFormWindow.getFormPanel().getForm().findField(name).setFieldStyle('background: white; color: black;');
  }

  var solutionConfigStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../solutionConfig/listSolutionConfig.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['id', 'name', 'intro', 'webConfig', 'creator', 'version', 'module']
  });

  var solutionConfigGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: '系统配置列表',
    store: solutionConfigStore,
    columns: [{
      dataIndex: 'id',
      header: 'ID',
      hidden: true
    }, {
      dataIndex: 'name',
      header: '配置名',
      flex: 1
    }, {
      dataIndex: 'intro',
      header: '配置值',
      flex: 2
    }, {
      dataIndex: 'webConfig',
      header: '配置说明',
      flex: 2
    }],
    tbar: [{
      text: '修改',
      iconCls: 'MyExt-modify',
      handler: function () {
        var select = MyExt.util.SelectGridModel(solutionConfigGrid);
        if (!select) {
          return;
        }
        setUnEditable('name');
        if (select[0].data['changeable'] != 'true') {
          setUnEditable('intro');
          setUnEditable('webConfig');
        }else {
          setEditable('intro');
          setEditable('webConfig');
        }
        solutionConfigFormWindow.changeFormUrlAndShow("../solutionConfig/updateSolutionConfig.do");
        solutionConfigFormWindow.getFormPanel().getForm().loadRecord(select[0]);
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

  var solutionConfigFormWindow = new MyExt.Component.FormWindow({
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
      name: 'name',
      allowBlank: false
    }, {
      xtype: 'textarea',
      fieldLabel: '配置值',
      labelWidth: LABELWIDTH,
      name: 'intro',
      allowBlank: false
    }, {
      xtype: 'textarea',
      fieldLabel: '配置说明',
      labelWidth: LABELWIDTH,
      name: 'webConfig'
    }],
    submitBtnFn: function () {
      var form = solutionConfigFormWindow.getFormPanel().getForm();
      if (form.isValid()) {
        var formValue = form.getValues();
        MyExt.util.Ajax(solutionConfigFormWindow.getFormPanel().url, {
          id: formValue['id'],
          name: formValue['name'],
          intro: formValue['intro'],
          webConfig: formValue['webConfig']
        }, function (data) {
          if (data.success) {
            solutionConfigFormWindow.hide();
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
    items: [solutionConfigGrid]
  });

  reload();
});
