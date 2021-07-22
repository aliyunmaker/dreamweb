Ext.onReady(function () {
  Ext.tip.QuickTipManager.init();

  var oss = 'https://dream-web.oss-cn-hangzhou.aliyuncs.com/static/image/icon/';
  var alignStyle = ' style="vertical-align:middle"';

  var reload = function () {
    apiUserStore.load();
  };

  var apiUserStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../apiUser/listApiUser.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['id', 'accessKeyId', 'accessKeySecret', 'accessKeyId', 'comment', {name: 'valid', type: 'string'}, 'gmtCreate']
  });

  var apiUserGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: 'API用户列表',
    store: apiUserStore,
    columns: [{
      dataIndex: 'id',
      header: 'ID',
      hidden: true
    }, {
      dataIndex: 'gmtCreate',
      header: '创建时间',
      flex: 1.5
    }, {
      dataIndex: 'accessKeyId',
      header: 'accessKeyId',
      flex: 1.5
    }, {
      dataIndex: 'accessKeySecret',
      header: 'accessKeySecret',
      flex: 3
    }, {
      dataIndex: 'comment',
      header: '备注',
      flex: 2.5
    }, {
      dataIndex: 'valid',
      header: '开关',
      flex: 1.5,
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
        apiUserFormWindow.changeFormUrlAndShow("../apiUser/addApiUser.do")
      }
    }, {
      text: '修改',
      iconCls: 'MyExt-modify',
      handler: function () {
        var select = MyExt.util.SelectGridModel(apiUserGrid);
        if (!select) {
          return;
        }
        apiUserFormWindow.changeFormUrlAndShow("../apiUser/updateApiUser.do")
        apiUserFormWindow.getFormPanel().getForm().loadRecord(select[0]);
      }
    }, {
      text: '删除',
      iconCls: 'MyExt-delete',
      handler: function () {
        var select = MyExt.util.SelectGridModel(apiUserGrid);
        if (!select) {
          return;
        }
        MyExt.util.MessageConfirm('是否确定删除', function () {
          MyExt.util.Ajax('../apiUser/deleteApiUser.do', {
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

  var apiUserFormWindow = new MyExt.Component.FormWindow({
    title: '操作',
    width: 400,
    height: 120,
    formItems: [{
      name: 'id',
      hidden: true
    }, {
      fieldLabel: '备注',
      labelWidth: 40,
      name: 'comment',
      allowBlank: false,
      emptyText: '备注'
    }, {
      fieldLabel: '生效',
      labelWidth: 40,
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
      var form = apiUserFormWindow.getFormPanel().getForm();
      if (form.isValid()) {
        var formValue = form.getValues();
        MyExt.util.Ajax(apiUserFormWindow.getFormPanel().url, {
          id: formValue['id'],
          comment: formValue['comment'],
          valid: formValue['valid']
        }, function (data) {
          if (data.success) {
            apiUserFormWindow.hide();
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
    items: [apiUserGrid]
  });

  reload();
});
