Ext.onReady(function () {
  // Ext.tip.QuickTipManager.init();

  var reload = function () {
    userGroupStore.load();
  };

  var userGroupStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../userGroup/getAllUserGroups.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['id', 'name']
  });

  var userRoleStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../userRole/getUserRolesByGroupId.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['id', 'userGroupId', 'roleType', 'roleName', 'roleValue']
  });

  var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../userGroup/getUsersByUserGroupId.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['id', 'loginName', 'name']
  });



  userRoleStore.on('beforeload', function (store, options) {
    var select = MyExt.util.SelectGridModel(userGroupGrid, true);
    if (!select) {
      return;
    }
    options.params = Ext.apply(options.params || {}, {
      userGroupId: select[0].data['id']
    });
  });

  userStore.on('beforeload', function (store, options) {
    var select = MyExt.util.SelectGridModel(userGroupGrid, true);
    if (!select) {
      return;
    }
    options.params = Ext.apply(options.params || {}, {
      userGroupId: select[0].data['id']
    });
  });

  var detailWin = null;
  var userGroupGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: '用户组列表',
    store: userGroupStore,
    columns: [{
      dataIndex: 'id',
      header: 'ID',
      hidden: true
    }, {
      dataIndex: 'name',
      header: "名称",
      flex: 1
    }],
    tbar: [{
      text: '增加',
      iconCls: 'MyExt-add',
      handler: function () {
        formWindow.changeFormUrlAndShow('../userGroup/addUserGroup.do');
      }
    }, {
      text: '修改',
      iconCls: 'MyExt-modify',
      handler: function () {
        var select = MyExt.util.SelectGridModel(userGroupGrid, true);
        if (!select) {
          return;
        }
        formWindow.changeFormUrlAndShow('../userGroup/updateUserGroup.do');
        formWindow.getFormPanel().getForm().loadRecord(select[0]);
      }
    }, {
      text: '删除',
      iconCls: 'MyExt-delete',
      handler: function () {
        var select = MyExt.util.SelectGridModel(userGroupGrid, true);
        if (!select) {
          return;
        }
        MyExt.util.MessageConfirm('是否确定删除', function () {
          MyExt.util.Ajax('../userGroup/deleteUserGroup.do', {
            id: select[0].data["id"],
          }, function (data) {
            reload();
            MyExt.Msg.alert('删除成功!');
          });
        });
      }
    }],
    listeners: {
      itemclick: function (grid, record) {
        var select = MyExt.util.SelectGridModel(grid, true);
        userStore.removeAll();
        userRoleStore.removeAll();
        if (!select) {
          return;
        }
        userStore.load();
        userRoleStore.load();
      },
      itemdblclick: function (grid, record) {
      }
    }
  });

  var userRoleGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'south',
    split: true,
    height: 400,
    title: '角色列表',
    store: userRoleStore,
    columns: [{
      dataIndex: 'id',
      header: 'ID',
      hidden: true
    }, {
      dataIndex: 'userGroupId',
      header: 'userGroupId',
      hidden: true
    }, {
      dataIndex: 'roleType',
      header: '类型',
      width: 80
    }, {
      header: "角色名称",
      dataIndex: 'roleName',
      width: 140
    }, {
      header: "value",
      dataIndex: 'roleValue',
      flex: 1
    }],
    tbar: [{
      text: '增加',
      iconCls: 'MyExt-add',
      handler: function () {
        var select = MyExt.util.SelectGridModel(userGroupGrid, true);
        if (!select) {
          return;
        }
        userRoleFormWindow.changeFormUrlAndShow('../userRole/addUserRole.do');
      }
    }, {
      text: '修改',
      iconCls: 'MyExt-modify',
      handler: function () {
        var select = MyExt.util.SelectGridModel(userRoleGrid, true);
        if (!select) {
          return;
        }
        userRoleFormWindow.changeFormUrlAndShow('../userRole/updateUserRole.do');
        userRoleFormWindow.getFormPanel().getForm().loadRecord(select[0]);
      }
    }, {
      text: '删除',
      iconCls: 'MyExt-delete',
      handler: function () {
        var select = MyExt.util.SelectGridModel(userRoleGrid, true);
        if (!select) {
          return;
        }
        MyExt.util.MessageConfirm('是否确定删除', function () {
          MyExt.util.Ajax('../userRole/deleteUserRole.do', {
            id: select[0].data["id"],
          }, function (data) {
            userRoleStore.load();
            MyExt.Msg.alert('删除成功!');
          });
        });
      }
    }],
    listeners: {}
  });


  var userGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: '用户列表',
    store: userStore,
    hasBbar: false,
    columns: [{
      dataIndex: 'id',
      header: 'ID',
      hidden: true
    }, {
      header: "登录名",
      dataIndex: 'loginName',
      width: 250
    }, {
      header: "姓名",
      dataIndex: 'name',
      flex: 1
    }],
    tbar: [{
      text: '选取',
      iconCls: 'MyExt-add',
      handler: function () {
        var select = MyExt.util.SelectGridModel(userGroupGrid, true);
        if (!select) {
          return;
        }
        userFormWindow.changeFormUrlAndShow('');
      }
    }, {
      text: '移除',
      iconCls: 'MyExt-delete',
      handler: function () {
        var select = MyExt.util.SelectGridModel(userGroupGrid, true);
        if (!select) {
          return;
        }

        var userSelect = MyExt.util.SelectGridModel(userGrid, true);
        if (!userSelect) {
          return;
        }
        MyExt.util.MessageConfirm('是否确定移除', function () {
          MyExt.util.Ajax('../userGroup/deleteUserGroupAssociate.do', {
            userGroupId: select[0].data["id"],
            userId: userSelect[0].data["id"]
          }, function (data) {
            userStore.load();
            MyExt.Msg.alert('移除成功!');
          });
        });
      }
    }],
    listeners: {}
  });



  var formWindow = new MyExt.Component.FormWindow({
    title: '操作',
    width: 400,
    height: 200,
    formItems: [{
      name: 'id',
      hidden: true
    }, {
      fieldLabel: '分组名(*)',
      name: 'name',
      allowBlank: false
    }],
    submitBtnFn: function () {
      var form = formWindow.getFormPanel().getForm();
      if (form.isValid()) {
        MyExt.util.Ajax(formWindow.getFormPanel().url, {
          formString: Ext.JSON.encode(form.getValues())
        }, function (data) {
          formWindow.hide();
          reload();
          MyExt.Msg.alert('操作成功!');
        });
      }
    }
  });


  var userFormWindow = new MyExt.Component.FormWindow({
    title: '选取用户',
    width: 500,
    height: 150,
    formItems: [{
      name: 'id',
      hidden: true
    }, {
      xtype: 'autocombobox',
      emptyText: '登录名|姓名|手机号码|ID(举例: id_2,或者空格2)',
      fieldLabel: '用户',
      // getValueByIdUrl: '../user/getUserById.do',
      queryParam: 'simpleSearch',
      store: Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../user/searchUser.do',
        pageSize: 10,
        fields: ['id', 'loginName', 'name']
      }),
      displayField: 'loginName',
      // template for the content inside text field
      displayTpl: Ext.create('Ext.XTemplate',
        '<tpl for=".">',
        '{loginName}[{name}]',
        '</tpl>'
      ),
      valueField: 'id',
      name: 'userId',
      listConfig: {
        getInnerTpl: function () {
          return '{loginName}[{name}]';
        }
      },
      hideTrigger: true
    }],
    submitBtnFn: function () {
      var select = MyExt.util.SelectGridModel(userGroupGrid, true);
      if (!select) {
        return;
      }
      var form = userFormWindow.getFormPanel().getForm();
      if (form.isValid()) {
        MyExt.util.Ajax('../userGroup/addUserGroupAssociate.do', {
          userId: form.getValues().userId,
          userGroupId: select[0].data["id"]
        }, function (data) {
          userFormWindow.hide();
          userStore.load();
          MyExt.Msg.alert('操作成功!');
        });
      }
    }
  });


  var userRoleFormWindow = new MyExt.Component.FormWindow({
    title: '操作',
    width: 600,
    height: 250,
    formItems: [{
      name: 'id',
      hidden: true
    }, {
      name: 'userGroupId',
      hidden: true
    }, {
      xtype: 'fieldcontainer',
      fieldLabel: '类型(*)',
      defaultType: 'radiofield',
      defaults: {
        flex: 1
      },
      layout: 'hbox',
      items: [{
        boxLabel: 'Aliyun',
        name: 'roleType',
        inputValue: 'aliyun',
        checked: true
      }, {
        boxLabel: 'Aliyun-User',
        name: 'roleType',
        inputValue: 'aliyun_user'
      }, {
        boxLabel: 'AWS',
        name: 'roleType',
        inputValue: 'aws'
      }, {
        boxLabel: 'AWS-User',
        name: 'roleType',
        inputValue: 'aws_user'
      }]
    }, {
      fieldLabel: '角色名称(*)',
      name: 'roleName',
      allowBlank: false
    }, {
      fieldLabel: '角色值(*)',
      name: 'roleValue',
      xtype: 'textarea',
      allowBlank: false
    }],
    submitBtnFn: function () {
      var select = MyExt.util.SelectGridModel(userGroupGrid, true);
      if (!select) {
        return;
      }
      var form = userRoleFormWindow.getFormPanel().getForm();
      if (form.isValid()) {
        MyExt.util.Ajax(userRoleFormWindow.getFormPanel().url, {
          formString: Ext.JSON.encode(form.getValues()),
          userGroupId: select[0].data["id"]
        }, function (data) {
          userRoleFormWindow.hide();
          userRoleStore.load();
          MyExt.Msg.alert('操作成功!');
        });
      }
    }
  });




  Ext.create('Ext.container.Viewport', {
    layout: 'border',
    items: [{
      layout: 'border',
      border: false,
      split: true,
      region: 'west',
      width: 500,
      items: [userGroupGrid]
    }, {
      layout: 'border',
      region: 'center',
      border: false,
      items: [userRoleGrid, userGrid]
    }]
  });

  reload();

})