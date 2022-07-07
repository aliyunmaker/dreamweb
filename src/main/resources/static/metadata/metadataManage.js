Ext.onReady(function () {
    var reload = function () {
        productStore.load();
    };

    var reload2 = function () {
        userProductStore.load();
    }

    var reload3 = function () {
        userRoleCurrentStore.load();
    }


    var productStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../product/searchProductVersion.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'productId', 'application', 'scenes', 'productName', 'productVersionId']
    });

    var userProductStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../userProduct/searchUserProduct.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'productId', 'userName', 'portfolioId', 'productName']
    });

    var userRoleCurrentStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../userRole/getRoleCurrent.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'userGroupId', 'roleType', 'roleName', 'roleValue']
      });

    var userRoleStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../userRole/getRolesByUser.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'userGroupId', 'roleType', 'roleName', 'roleValue']
    });


    var productGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '产品列表',
        store: productStore,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'productId',
            header: "产品ID",
            width: 160
        }, {
            dataIndex: 'productName',
            header: "产品名称",
            width: 170
        }, {
            dataIndex: 'application',
            header: "应用",
            width: 90
        }, {
            dataIndex: 'scenes',
            header: "环境",
            width: 60
        }, {
            dataIndex: 'productVersionId',
            header: "产品版本ID",
            width: 160
        }],
        tbar: [{
            text: '增加',
            iconCls: 'MyExt-add',
            handler: function () {
                formWindow.changeFormUrlAndShow('../product/addProduct.do');
            }
        }, {
            text: '修改',
            iconCls: 'MyExt-modify',
            handler: function () {
                var select = MyExt.util.SelectGridModel(productGrid);
                if (!select) {
                    return;
                }
                formWindow.changeFormUrlAndShow('../product/updateProduct.do');
                formWindow.getFormPanel().getForm().loadRecord(select[0]);
            }
        }, {
            text: '删除',
            iconCls: 'MyExt-delete',
            handler: function () {
                var select = MyExt.util.SelectGridModel(productGrid, true);
                if (!select) {
                    return;
                }
                MyExt.util.MessageConfirm('是否确定删除', function () {
                    MyExt.util.Ajax('../product/deleteProduct.do', {
                        id: select[0].data["id"],
                    }, function (data) {
                        reload();
                        MyExt.Msg.alert('删除成功!');
                    });
                });
            }
        }],
    });

    var userProductGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '权限列表',
        store: userProductStore,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'productId',
            header: "产品ID",
            width: 160
        }, {
            dataIndex: 'productName',
            header: "产品名称",
            width: 170
        }, {
            dataIndex: 'userName',
            header: "用户名",
            width: 70
        }, {
            dataIndex: 'portfolioId',
            header: "产品组合ID",
            width: 160,
            flex: 1
        }],
        tbar: [{
            text: '增加',
            iconCls: 'MyExt-add',
            handler: function () {
                  userProductFormWindow.changeFormUrlAndShow('');
            }
        }, {
            text: '修改',
            iconCls: 'MyExt-modify',
            handler: function () {
                var select = MyExt.util.SelectGridModel(userProductGrid);
                if (!select) {
                    return;
                }
                userProductFormWindow2.changeFormUrlAndShow('');
                userProductFormWindow2.getFormPanel().getForm().loadRecord(select[0]);
            }
        }, {
            text: '删除',
            iconCls: 'MyExt-delete',
            handler: function () {
                var select = MyExt.util.SelectGridModel(userProductGrid, true);
                if (!select) {
                    return;
                }
                MyExt.util.MessageConfirm('是否确定删除', function () {
                    MyExt.util.Ajax('../userProduct/deleteUserProduct.do', {
                        id: select[0].data["id"],
                    }, function (data) {
                        reload2();
                        MyExt.Msg.alert('删除成功!');
                    });
                });
            }
        }],
    });

    var userRoleGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'south',
        split: true,
        title: '当前使用角色',
        store: userRoleCurrentStore,
        height: 300,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'userGroupId',
            header: "用户组ID",
            width: 70
        }, {
            dataIndex: 'roleType',
            header: "类型",
            width: 80
        }, {
            dataIndex: 'roleName',
            header: "角色名称",
            width: 100
        }, {
            dataIndex: 'roleValue',
            header: "value",
            flex: 1
        }],
        tbar: [{
            text: '角色配置',
            iconCls: 'MyExt-modify',
            handler: function () {
                userRoleStore.load();
                var userRoleInfo = Ext.create('MyExt.Component.GridPanel', {
                    // title: '角色',
                    store: userRoleStore,
                    hasBbar: false,
                    height: 200,
                    columns: [{
                        dataIndex: 'id',
                        header: 'ID',
                        hidden: true
                    }, {
                        dataIndex: 'userGroupId',
                        header: "用户组ID",
                        width: 70
                    }, {
                        dataIndex: 'roleType',
                        header: "类型",
                        width: 80
                    }, {
                        dataIndex: 'roleName',
                        header: "角色名称",
                        width: 100
                    }, {
                        dataIndex: 'roleValue',
                        header: "value",
                        flex: 1
                    }],
                    tbar: [{
                        text: '选择',
                        iconCls: 'MyExt-confirm',
                        handler: function () {
                            var select = MyExt.util.SelectGridModel(userRoleInfo, true);
                            if (!select) {
                                return;
                            }
                            MyExt.util.MessageConfirm('是否确定选择', function () {
                                MyExt.util.Ajax('../userRole/roleSelect.do', {
                                    id: select[0].data["id"],
                                }, function (data) {
                                    MyExt.Msg.alert('选择成功!');
                                    win.hide();
                                    reload3();
                                });
                            });
                        }
                    }]
                });
                win = new Ext.Window({
                    title:'可选角色信息',
                    layout:'form',
                    width:900,
                    closeAction:'close',
                    plain: true,
                    items: [userRoleInfo],
                    buttons: [{
                        text: '关闭',
                        handler: function(){
                            win.hide();
                        }
                    }],
                    buttonAlign: 'center',
                });
                win.show();
            }
        }],
        listeners: {}
    });

    var formWindow = new MyExt.Component.FormWindow({
        title: '操作',
        width: 400,
        height: 320,
        formItems: [{
            name: 'id',
            hidden: true
        }, {
            fieldLabel: '应用',
            name: 'application',
            allowBlank: false
        }, {
            fieldLabel: '环境',
            name: 'scenes',
            allowBlank: false
        }, {
            fieldLabel: '产品ID',
            name: 'productId',
            allowBlank: false
        }, {
            fieldLabel: '产品名称',
            name: 'productName',
            allowBlank: false
        }, {
            fieldLabel: '产品版本ID',
            name: 'productVersionId',
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

    var userProductFormWindow2 = new MyExt.Component.FormWindow({
            title: '修改权限',
            width: 500,
            height: 320,
            formItems: [{
              name: 'id',
              hidden: true
            }, {
              xtype: 'autocombobox',
              fieldLabel: '产品',
              store: Ext.create('MyExt.Component.SimpleJsonStore', {
                dataUrl: '../product/searchProduct.do',
                pageSize: 10,
                fields: ['id', 'productName', 'productId']
              }),
              displayField: 'productId',
              name: 'productId',
              listConfig: {
                getInnerTpl: function () {
                  return '{productName}[{productId}]';
                }
              },
            }, {
               xtype: 'autocombobox',
               fieldLabel: '用户',
               store: Ext.create('MyExt.Component.SimpleJsonStore', {
                 dataUrl: '../user/searchUser.do',
                 pageSize: 10,
                 fields: ['id', 'loginName', 'name']
               }),
               displayField: 'loginName',
               name: 'userName',
               listConfig: {
                 getInnerTpl: function () {
                   return '{loginName}[{name}]';
                 }
               },
             }, {
              fieldLabel: '产品组合ID',
              name: 'portfolioId',
              allowBlank: false
            }],
            submitBtnFn: function () {
                var form = userProductFormWindow2.getFormPanel().getForm();
              if (form.isValid()) {
                MyExt.util.Ajax('../userProduct/updateUserProduct.do', {
                  id: form.getValues().id,
                  productId: form.getValues().productId,
                  userName: form.getValues().userName,
                  portfolioId: form.getValues().portfolioId
                }, function (data) {
                  userProductFormWindow2.hide();
                  userProductStore.load();
                  MyExt.Msg.alert('操作成功!');
                });
              }
            }
          });

    var userProductFormWindow = new MyExt.Component.FormWindow({
        title: '增加权限',
        width: 500,
        height: 320,
        formItems: [{
          name: 'id',
          hidden: true
        }, {
          xtype: 'autocombobox',
          emptyText: '产品名称（产品ID）',
          fieldLabel: '产品',
          store: Ext.create('MyExt.Component.SimpleJsonStore', {
            dataUrl: '../product/searchProduct.do',
            pageSize: 10,
            fields: ['id', 'productName', 'productId']
          }),
          displayField: 'productName',
          displayTpl: Ext.create('Ext.XTemplate',
            '<tpl for=".">',
            '{productName}[{productId}]',
            '</tpl>'
          ),
          valueField: 'id',
          name: 'productId',
          listConfig: {
            getInnerTpl: function () {
              return '{productName}[{productId}]';
            }
          },
        }, {
           xtype: 'autocombobox',
           emptyText: '登录名（姓名）',
           fieldLabel: '用户',
           store: Ext.create('MyExt.Component.SimpleJsonStore', {
             dataUrl: '../user/searchUser.do',
             pageSize: 10,
             fields: ['id', 'loginName', 'name']
           }),
           displayField: 'loginName',
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
         }, {
          fieldLabel: '产品组合ID',
          name: 'portfolioId',
          allowBlank: false
        }],
        submitBtnFn: function () {
            var form = userProductFormWindow.getFormPanel().getForm();
          if (form.isValid()) {
            MyExt.util.Ajax('../userProduct/addUserProduct.do', {
              productId: form.getValues().productId,
              userId: form.getValues().userId,
              portfolioId: form.getValues().portfolioId
            }, function (data) {
              userProductFormWindow.hide();
              userProductStore.load();
              MyExt.Msg.alert('操作成功!');
            });
          }
        }
      });

    reload();
    reload2();
    reload3();

    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        items: [{
          layout: 'border',
          border: false,
          split: true,
          region: 'west',
          width: 660,
          items: [productGrid]
        }, {
          layout: 'border',
          region: 'center',
          border: false,
          items: [userProductGrid, userRoleGrid]
        }]
    });

})