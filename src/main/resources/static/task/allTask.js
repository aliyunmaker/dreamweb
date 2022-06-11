Ext.onReady(function () {
    // Ext.tip.QuickTipManager.init();

    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../task/getAllTaskList.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['startername', 'processtime', 'tasktime', 'taskid', 'taskname', 'processid', 'assignee']
    });

    var userGrid = Ext.create('MyExt.Component.GridPanel', {
    // var userGrid = Ext.create('Ext.grid.Panel', {
        region: 'center',
        title: '所有任务列表',
        store: userStore,
        columns: [{
            dataIndex: 'startername',
            header: '申请人',
            width: 100
        }, {
            dataIndex: 'processtime',
            header: "申请时间",
            width: 150
        }, {
            dataIndex: 'tasktime',
            header: "任务创建时间",
            width: 150
        }, {
            text: '申请内容',
            xtype: 'gridcolumn',
            width: 160,
            align: 'center',
            renderer: function (value, metaData, record) {
                var id = record.raw.processid;
                metaData.tdAttr = 'data-qtip="查看当前申请内容详情"';
                Ext.defer(function () {
                    Ext.widget('button', {
                        renderTo: id,
                        // height: 20,
                        width: 100,
                        // style:"margin-left:5px;background:blue;",
                        text: '详细信息',
                        handler: function () {
                            var select = MyExt.util.SelectGridModel(userGrid, true);
                            MyExt.util.Ajax('../task/getInfo.do', {
                                    processid: id,
                                }, function (data) {
                                    // console.log(data.data);
                                    // console.log(data.data["应用"]);
                                    // console.log(data.data['场景']);
                                    win = new Ext.Window({
                                        title:'详细信息',
                                        layout:'form',
                                        width:400,
                                        closeAction:'close',
                                        target : document.getElementById('buttonId'),
                                        plain: true,
                                        items: [{
                                            xtype : 'displayfield',
                                            fieldLabel: '应用',
                                            name: 'home_score',
                                            value: data.data['应用']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '场景',
                                            name: 'home_score',
                                            value: data.data['场景']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '产品ID',
                                            name: 'home_score',
                                            value: data.data['产品ID']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '实例名称',
                                            name: 'home_score',
                                            value: data.data['实例名称']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '参数信息',
                                            name: 'home_score',
                                            value: data.data['参数信息']
                                        }],
                                        buttons: [{
                                           text: '确认',
                                           handler: function(){
                                               win.hide();
                                            }
                                        }],
                                        buttonAlign: 'center',
                                     });
                                    win.show();
                                });
                        }
                    });
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', id);
            }
        }, {
            dataIndex: 'taskid',
            header: "任务ID",
            width: 200,
            hidden: true
        }, {
            dataIndex: 'taskname',
            header: "任务名称",
            width: 100,
        }, {
            dataIndex: 'processid',
            header: "流程实例ID",
            width: 100,
            align: 'center',
        }, {
            dataIndex: 'assignee',
            header: "待办理人",
            width: 100,
            align: 'center',
        }],
        // listeners: {
        //     itemdblclick: function (grid, record) {
        //         if (detailRole == null) {
        //             var detailTextArea = new Ext.form.field.TextArea({
        //                 autoScroll: true,
        //                 readOnly: true,
        //                 name: 'value',
        //                 margin: 0
        //             });
        //             detailRole = Ext.create('Ext.window.Window', {
        //                 maximizable: true,
        //                 layout: 'fit',
        //                 width: 400,
        //                 height: 300,
        //                 bodyBorder: false,
        //                 closeAction: 'hide',
        //                 border: 0,
        //                 title: '详情',
        //                 items: [detailTextArea],
        //                 setTextAreaValue: function (content) {
        //                     detailTextArea.setValue(content);
        //                     this.show();
        //                 }
        //             });
        //         }
        //         detailRole
        //             .setTextAreaValue(record.get('loginName') + "\n" + record.get('name') +
        //                 "\n---------------------------------------\n" +
        //                 MyExt.util.formatToJson(record.get('comment'), false));
        //     }
        // }
    });


    Ext.create('Ext.container.Viewport', {
        layout: 'border',    //使用BorderLayout的布局方式(边界布局);可以自动检测浏览器的大小变化和自动调整布局中每个部分的大小;为什么加上就没有页码了？
        items: [userGrid]
    });
    reload();

})