Ext.onReady(function () {
    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../product/searchProduct.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'productId', 'application', 'scenes', 'productName', 'productVersionId']
    });


    var detailRole = null;
    var userGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '产品列表',
        store: userStore,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'productId',
            header: "产品ID",
            width: 200
        }, {
            dataIndex: 'productName',
            header: "产品名称",
            width: 200
        }, {
            dataIndex: 'application',
            header: "应用",
            width: 200
        }, {
            dataIndex: 'scenes',
            header: "环境",
            width: 200
        }, {
            dataIndex: 'productVersionId',
            header: "产品版本ID",
            width: 200
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
                var select = MyExt.util.SelectGridModel(userGrid);
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
                var select = MyExt.util.SelectGridModel(userGrid, true);
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


    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        items: [userGrid]
    });
    reload();

})