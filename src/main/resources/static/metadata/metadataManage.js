Ext.onReady(function () {
    var reload = function () {
        productVersionStore.load();
    };

    var reload2 = function () {
        userProductStore.load();
    }

    var reload3 = function () {
        userRoleCurrentStore.load();
    }

    var reload4 = function () {
        productStore.load();
    }

    var productVersionStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../productVersion/searchProductVersion.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'servicecatalogProductId', 'app', 'environment', 'productName', 'servicecatalogProductVersionId']
    });

    var productStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../product/searchProduct.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'servicecatalogProductId', 'productName']
    });

    var userProductStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../userProduct/searchUserProductAssociate.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'servicecatalogProductId', 'productName', 'loginName', 'servicecatalogPortfolioId']
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


    var productVersionGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '产品版本管理',
        store: productVersionStore,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'servicecatalogProductId',
            header: "服务目录产品ID",
            width: 160
        }, {
            dataIndex: 'servicecatalogProductVersionId',
            header: "服务目录产品版本ID",
            width: 160
        }, {
            dataIndex: 'productName',
            header: "产品名称",
            width: 170
        }, {
            dataIndex: 'app',
            header: "应用",
            width: 90
        }, {
            dataIndex: 'environment',
            header: "环境",
            width: 60,
            flex: 1
        }],
        tbar: [{
            text: '增加',
            iconCls: 'MyExt-add',
            handler: function () {
                formWindow2.changeFormUrlAndShow('../productVersion/addProductVersion.do');
            }
        }, {
            text: '修改',
            iconCls: 'MyExt-modify',
            handler: function () {
                var select = MyExt.util.SelectGridModel(productVersionGrid);
                if (!select) {
                    return;
                }
                formWindow.changeFormUrlAndShow('../productVersion/updateProductVersion.do');
                formWindow.getFormPanel().getForm().loadRecord(select[0]);
            }
        }, {
            text: '删除',
            iconCls: 'MyExt-delete',
            handler: function () {
                var select = MyExt.util.SelectGridModel(productVersionGrid, true);
                if (!select) {
                    return;
                }
                MyExt.util.MessageConfirm('是否确定删除', function () {
                    MyExt.util.Ajax('../productVersion/deleteProductVersion.do', {
                        id: select[0].data["id"],
                    }, function (data) {
                        reload();
                        MyExt.Msg.alert('删除成功!');
                    });
                });
            }
        }],
    });

    var productGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'south',
        title: '产品管理',
        store: productStore,
        height: 400,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'servicecatalogProductId',
            header: "服务目录产品ID",
            width: 160
        }, {
            dataIndex: 'productName',
            header: "产品名称",
            width: 200,
            flex: 1
        }],
        tbar: [{
            text: '增加',
            iconCls: 'MyExt-add',
            handler: function () {
                productFormWindow.changeFormUrlAndShow('../product/addProduct.do');
            }
        }, {
            text: '修改',
            iconCls: 'MyExt-modify',
            handler: function () {
                var select = MyExt.util.SelectGridModel(productGrid);
                if (!select) {
                    return;
                }
                productFormWindow.changeFormUrlAndShow('../product/updateProduct.do');
                productFormWindow.getFormPanel().getForm().loadRecord(select[0]);
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
                        reload2();
                        reload4();
                        MyExt.Msg.alert('删除成功!');
                    });
                });
            }
        }],
    });

    var userProductGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '权限管理',
        store: userProductStore,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'servicecatalogProductId',
            header: "服务目录产品ID",
            width: 160
        }, {
            dataIndex: 'servicecatalogPortfolioId',
            header: "服务目录产品组合ID",
            width: 160
        }, {
            dataIndex: 'loginName',
            header: "用户名",
            width: 70
        }, {
            dataIndex: 'productName',
            header: "产品名称",
            width: 170,
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
        height: 400,
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
                    title: '可选角色信息',
                    layout: 'form',
                    width: 900,
                    closeAction: 'close',
                    plain: true,
                    items: [userRoleInfo],
                    buttons: [{
                        text: '关闭',
                        handler: function () {
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
        title: '修改产品版本',
        width: 500,
        height: 320,
        formItems: [{
            name: 'id',
            hidden: true
        }, {
            fieldLabel: '应用',
            name: 'app',
            allowBlank: false
        }, {
            fieldLabel: '环境',
            name: 'environment',
            allowBlank: false
        }, {
            fieldLabel: '产品版本ID',
            name: 'servicecatalogProductVersionId',
            allowBlank: false
        }, {
            xtype: 'autocombobox',
            fieldLabel: '产品',
            queryParam: 'simpleSearch',
            store: Ext.create('MyExt.Component.SimpleJsonStore', {
                dataUrl: '../product/searchProduct.do',
                pageSize: 10,
                fields: ['id', 'productName', 'servicecatalogProductId']
            }),
            displayField: 'productName',
            valueField: 'productName',
            name: 'productName',
            listConfig: {
                getInnerTpl: function () {
                    return '{productName}[{servicecatalogProductId}]';
                }
            },
        }],
        submitBtnFn: function () {
            var form = formWindow.getFormPanel().getForm();
            if (form.isValid()) {
                MyExt.util.Ajax(formWindow.getFormPanel().url, {
                    id: form.getValues().id,
                    app: form.getValues().app,
                    environment: form.getValues().environment,
                    servicecatalogProductVersionId: form.getValues().servicecatalogProductVersionId,
                    productName: form.getValues().productName
                }, function (data) {
                    formWindow.hide();
                    reload();
                    MyExt.Msg.alert('修改成功!');
                });
            }
        }
    });

    var formWindow2 = new MyExt.Component.FormWindow({
        title: '增加产品版本',
        width: 500,
        height: 320,
        formItems: [{
            name: 'id',
            hidden: true
        }, {
            fieldLabel: '应用',
            name: 'app',
            allowBlank: false
        }, {
            fieldLabel: '环境',
            name: 'environment',
            allowBlank: false
        }, {
            fieldLabel: '产品版本ID',
            name: 'servicecatalogProductVersionId',
            allowBlank: false
        }, {
            xtype: 'autocombobox',
            emptyText: '产品名称（产品ID）',
            fieldLabel: '产品',
            queryParam: 'simpleSearch',
            store: Ext.create('MyExt.Component.SimpleJsonStore', {
                dataUrl: '../product/searchProduct.do',
                pageSize: 10,
                fields: ['id', 'productName', 'servicecatalogProductId']
            }),
            displayField: 'productName',
            valueField: 'id',
            name: 'productId',
            listConfig: {
                getInnerTpl: function () {
                    return '{productName}[{servicecatalogProductId}]';
                }
            },
        }],
        submitBtnFn: function () {
            var form = formWindow2.getFormPanel().getForm();
            if (form.isValid()) {
                MyExt.util.Ajax(formWindow2.getFormPanel().url, {
                    formString: Ext.JSON.encode(form.getValues())
                }, function (data) {
                    formWindow2.hide();
                    reload();
                    MyExt.Msg.alert('增加成功!');
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
            queryParam: 'simpleSearch',
            store: Ext.create('MyExt.Component.SimpleJsonStore', {
                dataUrl: '../product/searchProduct.do',
                pageSize: 10,
                fields: ['id', 'productName', 'servicecatalogProductId']
            }),
            displayField: 'productName',
            valueField: 'productName',
            name: 'productName',
            listConfig: {
                getInnerTpl: function () {
                    return '{productName}[{servicecatalogProductId}]';
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
            valueField: 'loginName',
            name: 'loginName',
            listConfig: {
                getInnerTpl: function () {
                    return '{loginName}[{name}]';
                }
            },
        }, {
            fieldLabel: '产品组合ID',
            name: 'servicecatalogPortfolioId',
            allowBlank: false
        }],
        submitBtnFn: function () {
            var form = userProductFormWindow2.getFormPanel().getForm();
            if (form.isValid()) {
                MyExt.util.Ajax('../userProduct/updateUserProduct.do', {
                    id: form.getValues().id,
                    productName: form.getValues().productName,
                    loginName: form.getValues().loginName,
                    servicecatalogPortfolioId: form.getValues().servicecatalogPortfolioId
                }, function (data) {
                    userProductFormWindow2.hide();
                    userProductStore.load();
                    MyExt.Msg.alert('修改成功!');
                });
            }
        }
    });

    var userProductFormWindow = new MyExt.Component.FormWindow({
        title: '增加权限',
        width: 500,
        height: 320,
        formItems: [
            {
                name: 'id',
                hidden: true
            }, {
                xtype: 'autocombobox',
                emptyText: '产品名称（产品ID）',
                fieldLabel: '产品',
                queryParam: 'simpleSearch',
                store: Ext.create('MyExt.Component.SimpleJsonStore', {
                    dataUrl: '../product/searchProduct.do',
                    pageSize: 10,
                    fields: ['id', 'productName', 'servicecatalogProductId']
                }),
                displayField: 'productName',
                displayTpl: Ext.create('Ext.XTemplate',
                    '<tpl for=".">',
                    '{productName}[{servicecatalogProductId}]',
                    '</tpl>'
                ),
                valueField: 'id',
                name: 'productId',
                listConfig: {
                    getInnerTpl: function () {
                        return '{productName}[{servicecatalogProductId}]';
                    }
                },
            }, {
                xtype: 'autocombobox',
                emptyText: '登录名（姓名）',
                fieldLabel: '用户',
                queryParam: 'simpleSearch',
                store: Ext.create('MyExt.Component.SimpleJsonStore', {
                    dataUrl: '../user/searchUser.do',
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
                name: 'servicecatalogPortfolioId',
                allowBlank: false
            }],
        submitBtnFn: function () {
            var form = userProductFormWindow.getFormPanel().getForm();
            if (form.isValid()) {
                MyExt.util.Ajax('../userProduct/addUserProduct.do', {
                    formString: Ext.JSON.encode(form.getValues())
                }, function (data) {
                    userProductFormWindow.hide();
                    userProductStore.load();
                    MyExt.Msg.alert('增加成功!');
                });
            }
        }
    });


    var productFormWindow = new MyExt.Component.FormWindow({
        title: '操作',
        width: 500,
        height: 320,
        formItems: [{
            name: 'id',
            hidden: true
        }, {
            fieldLabel: '产品ID',
            name: 'servicecatalogProductId',
            allowBlank: false
        }, {
            fieldLabel: '产品名称',
            name: 'productName',
            allowBlank: false
        }],
        submitBtnFn: function () {
            var form = productFormWindow.getFormPanel().getForm();
            if (form.isValid()) {
                MyExt.util.Ajax(productFormWindow.getFormPanel().url, {
                    formString: Ext.JSON.encode(form.getValues())
                }, function (data) {
                    productFormWindow.hide();
                    reload();
                    reload2();
                    reload4();
                    MyExt.Msg.alert('操作成功!');
                });
            }
        }
    });


    reload();
    reload2();
    reload3();
    reload4();

    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        items: [{
            layout: 'border',
            border: false,
            split: true,
            region: 'west',
            width: 680,
            items: [productVersionGrid, productGrid]
        }, {
            layout: 'border',
            region: 'center',
            border: false,
            items: [userProductGrid, userRoleGrid]
        }]
    });

})