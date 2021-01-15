Ext.onReady(function () {
    // Ext.tip.QuickTipManager.init();

    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../user/searchUser.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'loginName', 'name', 'email', 'comment', 'role', 'phone', 'gmtCreate', 'unionid']
    });

    userStore.on('beforeload', function (store, options) {
        options.params = Ext.apply(options.params || {}, searchForm.getForm().getValues());
    });

    var searchForm = Ext.create('Ext.form.Panel', {
        region: 'north',
        frame: true,
        height: 80,
        bodyStyle: 'padding:15px 0px 0px 10px',
        fieldDefaults: {
            labelWidth: 30
        },
        defaults: {
            width: 300
        },
        defaultType: 'textfield',
        buttonAlign: 'left',
        items: [{
            fieldLabel: '搜索',
            width: 600,
            emptyText: '登录名|姓名|手机号码|ID(举例: id_2,或者空格2)(管理员:=admin)',
            name: 'simpleSearch',
            enableKeyEvents: true,
            listeners: {
                keypress: function (thiz, e) {
                    if (e.getKey() == Ext.EventObject.ENTER) {
                        userGrid.getPageToolbar().moveFirst();
                    }
                }
            }
        }]
    });

    var detailWin = null;
    var userGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '用户列表',
        store: userStore,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'loginName',
            header: "登录名",
            width: 250
        }, {
            dataIndex: 'name',
            header: "名称",
            width: 120
        }, {
            dataIndex: 'email',
            header: "邮箱",
            width: 200,
            hidden: true
        }, {
            dataIndex: 'phone',
            header: "手机号码",
            width: 100,
        }, {
            dataIndex: 'role',
            header: "角色",
            width: 60,
            align: 'center',
            renderer: function (value) {
                if (value === "ROLE_GUEST") {
                    return '<img src="https://ichengchao.oss-cn-hangzhou.aliyuncs.com/static/image/icon/user_guest.png" />';
                } else if (value === "ROLE_ADMIN") {
                    return '<img src="https://ichengchao.oss-cn-hangzhou.aliyuncs.com/static/image/icon/user_admin.png" />';
                } else {
                    return value;
                }
            }
        }, {
            header: "创建时间",
            dataIndex: 'gmtCreate',
            // align: 'center',
            width: 150
        }, {
            header: "unionid",
            dataIndex: 'unionid',
            flex: 1
        }, {
            header: "备注",
            dataIndex: 'comment',
            flex: 1
        }],
        tbar: [{
            text: '增加',
            iconCls: 'MyExt-add',
            handler: function () {
                formWindow.changeFormUrlAndShow('../user/addUser.do');
            }
        }, {
            text: '修改',
            iconCls: 'MyExt-modify',
            handler: function () {
                var select = MyExt.util.SelectGridModel(userGrid);
                if (!select) {
                    return;
                }
                formWindow.changeFormUrlAndShow('../user/updateUser.do');
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
                    MyExt.util.Ajax('../user/deleteUser.do', {
                        id: select[0].data["id"],
                    }, function (data) {
                        reload();
                        MyExt.Msg.alert('删除成功!');
                    });
                });
            }
        }, {
            text: '设为管理员',
            iconCls: 'MyExt-admin',
            handler: function () {
                var select = MyExt.util.SelectGridModel(userGrid, true);
                if (!select) {
                    return;
                }
                MyExt.util.Ajax('../user/assignRoleAdmin.do', {
                    loginName: select[0].data["loginName"],
                }, function (data) {
                    reload();
                    MyExt.Msg.alert('设置成功!');
                });
            }
        }, {
            text: '取消管理员',
            iconCls: 'MyExt-cancel',
            handler: function () {
                var select = MyExt.util.SelectGridModel(userGrid, true);
                if (!select) {
                    return;
                }
                MyExt.util.Ajax('../user/cancelRoleAdmin.do', {
                    loginName: select[0].data["loginName"],
                }, function (data) {
                    reload();
                    MyExt.Msg.alert('设置成功!');
                });
            }
        }],
        listeners: {
            itemdblclick: function (grid, record) {
                if (detailWin == null) {
                    var detailTextArea = new Ext.form.field.TextArea({
                        autoScroll: true,
                        readOnly: true,
                        name: 'value',
                        margin: 0
                    });
                    detailWin = Ext.create('Ext.window.Window', {
                        maximizable: true,
                        layout: 'fit',
                        width: 400,
                        height: 300,
                        bodyBorder: false,
                        closeAction: 'hide',
                        border: 0,
                        title: '详情',
                        items: [detailTextArea],
                        setTextAreaValue: function (content) {
                            detailTextArea.setValue(content);
                            this.show();
                        }
                    });
                }
                detailWin
                    .setTextAreaValue(record.get('loginName') + "\n" + record.get('name') +
                        "\n---------------------------------------\n" +
                        MyExt.util.formatToJson(record.get('comment'), false));
            }
        }
    });

    var formWindow = new MyExt.Component.FormWindow({
        title: '操作',
        width: 400,
        height: 320,
        formItems: [{
            name: 'id',
            hidden: true
        }, {
            fieldLabel: '登录名(*)',
            name: 'loginName',
            allowBlank: false
        }, {
            fieldLabel: '显示名(*)',
            name: 'name',
            allowBlank: false
        }, {
            fieldLabel: '密码',
            name: 'password'
        }, {
            fieldLabel: '手机',
            name: 'phone'
        }, {
            fieldLabel: '备注',
            name: 'comment'
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
        items: [searchForm, userGrid]
    });
    reload();

})