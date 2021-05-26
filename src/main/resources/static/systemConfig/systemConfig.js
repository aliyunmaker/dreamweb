Ext.onReady(function () {
  Ext.tip.QuickTipManager.init();

  var oss = 'https://ichengchao.oss-cn-hangzhou.aliyuncs.com/static/image/icon/';
  var alignStyle = ' style="vertical-align:middle"';

  var reload = function () {
    systemConfigStore.load();
  };

  var systemConfigStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../systemConfig/listSystemConfig.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['id', 'configName', 'configValue', 'comment', 'gmtCreate', { name: 'valid', type: 'string' }, { name: 'changeable', type: 'string' }]
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
      dataIndex: 'gmtCreate',
      header: '创建时间',
      flex: 1.25
    }, {
      dataIndex: 'configName',
      header: '配置名',
      flex: 1.25
    }, {
      dataIndex: 'configValue',
      header: '配置',
      flex: 4
    }, {
      dataIndex: 'comment',
      header: '备注',
      flex: 1.5
    }, {
      dataIndex: 'valid',
      header: '开关',
      flex: 0.5,
      renderer: function (value) {
        if (value === "true") {
          return '<img src="' + oss + 'green.png"' + alignStyle + '/>';
        } else {
          return '<img src="' + oss + 'gray.png"' + alignStyle + '/>';
        }
      }
    }],
    tbar: [{
      text: '新增',
      iconCls: 'MyExt-add',
      handler: function () {
        addSystemConfigFormWindow.changeFormUrlAndShow("../systemConfig/addSystemConfig.do")
      }
    }, {
      text: '修改',
      iconCls: 'MyExt-modify',
      handler: function () {
        var select = MyExt.util.SelectGridModel(systemConfigGrid);
        if (!select) {
          return;
        }
        if (select[0].data['changeable'] != 'true') {
          MyExt.Msg.alert("该项不可修改");
          return;
        }
        updateSystemConfigFormWindow.changeFormUrlAndShow("../systemConfig/updateSystemConfig.do")
      }
    }, {
      text: '删除',
      iconCls: 'MyExt-delete',
      handler: function () {
        var select = MyExt.util.SelectGridModel(systemConfigGrid);
        if (!select) {
          return;
        }
        if (select[0].data['changeable'] != 'true') {
          MyExt.Msg.alert("该项不可删除");
          return;
        }
        MyExt.util.MessageConfirm('是否确定删除', function () {
          MyExt.util.Ajax('../systemConfig/deleteSystemConfig.do', {
            id: select[0].data['id']
          }, function (data) {
            if (data.success) {
              reload();
              MyExt.Msg.alert('删除成功!');
            } else {
              MyExt.Msg.alert(data.errorMsg);
            }
          })
        })
      }
    }]
  });


  var addSystemConfigFormWindow = new MyExt.Component.FormWindow({
    title: '操作-新增',
    width: 400,
    height: 250,
    formItems: [{
      name: 'id',
      hidden: true
    }, {
      fieldLabel: '配置名',
      labelWidth: 50,
      name: 'configName',
      allowBlank: false,
      emptyText: '配置名'
    }, {
      xtype: 'textarea',
      fieldLabel: '配置',
      labelWidth: 50,
      name: 'configValue',
      allowBlank: false,
      emptyText: '配置值'
    }, {
      fieldLabel: '备注',
      labelWidth: 50,
      name: 'comment',
      emptyText: '备注'
    }, {
      fieldLabel: '可修改',
      labelWidth: 50,
      xtype: 'radiogroup',
      allowBlank: false,
      columns: 2,
      items: [{
        boxLabel: "是",
        id: 'changeable0',
        name: "changeable",
        inputValue: "true",
        checked: true
      }, {
        boxLabel: "否",
        id: 'changeable1',
        name: "changeable",
        inputValue: "false"
      }]
    }, {
      fieldLabel: '生效',
      labelWidth: 50,
      xtype: 'radiogroup',
      allowBlank: false,
      columns: 2,
      items: [{
        boxLabel: "是",
        id: 'valid0',
        name: "valid",
        inputValue: "true",
        checked: true
      }, {
        boxLabel: "否",
        id: 'valid1',
        name: "valid",
        inputValue: "false"
      }]
    }],
    submitBtnFn: function () {
      var form = addSystemConfigFormWindow.getFormPanel().getForm();
      if (form.isValid()) {
        var formValue = form.getValues();
        MyExt.util.Ajax(addSystemConfigFormWindow.getFormPanel().url, {
          id: formValue['id'],
          configName: formValue['configName'],
          configValue: formValue['configValue'],
          comment: formValue['comment'],
          changeable: formValue['changeable'],
          valid: formValue['valid']
        }, function (data) {
          if (data.success) {
            addSystemConfigFormWindow.hide();
            reload();
            MyExt.Msg.alert('操作成功!');
          } else {
            MyExt.Msg.alert(data.errorMsg);
          }
        });
      }
    }
  });

  
  var updateSystemConfigFormWindow = new MyExt.Component.FormWindow({
    title: '操作-更新',
    width: 400,
    height: 220,
    formItems: [{
      name: 'id',
      hidden: true
    }, {
      xtype: 'textarea',
      fieldLabel: '配置',
      labelWidth: 50,
      name: 'configValue',
      allowBlank: false,
      emptyText: '配置值'
    }, {
      fieldLabel: '备注',
      labelWidth: 50,
      name: 'comment',
      emptyText: '备注'
    }, {
      fieldLabel: '可修改',
      labelWidth: 50,
      xtype: 'radiogroup',
      allowBlank: false,
      columns: 2,
      items: [{
        boxLabel: "是",
        id: 'changeable0',
        name: "changeable",
        inputValue: "true",
        checked: true
      }, {
        boxLabel: "否",
        id: 'changeable1',
        name: "changeable",
        inputValue: "false"
      }]
    }, {
      fieldLabel: '生效',
      labelWidth: 50,
      xtype: 'radiogroup',
      allowBlank: false,
      columns: 2,
      items: [{
        boxLabel: "是",
        id: 'valid0',
        name: "valid",
        inputValue: "true",
        checked: true
      }, {
        boxLabel: "否",
        id: 'valid1',
        name: "valid",
        inputValue: "false"
      }]
    }],
    submitBtnFn: function () {
      var form = updateSystemConfigFormWindow.getFormPanel().getForm();
      if (form.isValid()) {
        var formValue = form.getValues();
        MyExt.util.Ajax(updateSystemConfigFormWindow.getFormPanel().url, {
          id: formValue['id'],
          configValue: formValue['configValue'],
          comment: formValue['comment'],
          changeable: formValue['changeable'],
          valid: formValue['valid']
        }, function (data) {
          if (data.success) {
            updateSystemConfigFormWindow.hide();
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
