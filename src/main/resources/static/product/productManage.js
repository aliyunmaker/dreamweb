Ext.onReady(function () {
    var reload = function () {
        productStore.load();
    };

    var reload2 = function () {
        userProductStore.load();
    }

    var reload3 = function () {
        userRoleStore.load();
    }

    var productStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../product/searchProduct.do',
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
                formWindow2.changeFormUrlAndShow('../userProduct/addUserProduct.do');
            }
        }, {
            text: '修改',
            iconCls: 'MyExt-modify',
            handler: function () {
                var select = MyExt.util.SelectGridModel(userProductGrid);
                if (!select) {
                    return;
                }
                formWindow2.changeFormUrlAndShow('../userProduct/updateUserProduct.do');
                formWindow2.getFormPanel().getForm().loadRecord(select[0]);
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
        title: '角色列表',
        store: userRoleStore,
        height: 400,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'userGroupId',
            header: "用户组ID",
            width: 100
        }, {
            dataIndex: 'roleType',
            header: "类型",
            width: 80
        }, {
            dataIndex: 'roleName',
            header: "角色名称",
            width: 140
        }, {
            dataIndex: 'roleValue',
            header: "value",
            flex: 1
        }],
        tbar: [{
            text: '选择',
            iconCls: 'MyExt-confirm',
            handler: function () {
                var select = MyExt.util.SelectGridModel(userRoleGrid, true);
                if (!select) {
                    return;
                }
                MyExt.util.MessageConfirm('是否确定选择', function () {
                    MyExt.util.Ajax('../userRole/roleSelect.do', {
                        id: select[0].data["id"],
                    }, function (data) {
                        MyExt.Msg.alert('选择成功!');
                    });
                });
            }
        }],
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

    var formWindow2 = new MyExt.Component.FormWindow({
        title: '操作',
        width: 400,
        height: 320,
        formItems: [{
            name: 'id',
            hidden: true
        }, {
            fieldLabel: '产品ID',
            name: 'productId',
            allowBlank: false
        }, {
            fieldLabel: '产品名称',
            name: 'productName',
            allowBlank: false
        }, {
            fieldLabel: '用户名',
            name: 'userName',
            allowBlank: false
        }, {
            fieldLabel: '产品组合ID',
            name: 'portfolioId',
            allowBlank: false
        }],
        submitBtnFn: function () {
            var form = formWindow2.getFormPanel().getForm();
            if (form.isValid()) {
                MyExt.util.Ajax(formWindow2.getFormPanel().url, {
                    formString: Ext.JSON.encode(form.getValues())
                }, function (data) {
                    formWindow2.hide();
                    reload2();
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