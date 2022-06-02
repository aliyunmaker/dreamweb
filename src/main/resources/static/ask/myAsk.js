Ext.onReady(function () {
    // Ext.tip.QuickTipManager.init();

    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../apply/getMyAsk.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['startername', 'processtime', 'processid', 'task', 'processstate', 'processinfo', 'cond', 'opinion']
    });

    var userGrid = Ext.create('MyExt.Component.GridPanel', {
    // var userGrid = Ext.create('Ext.grid.Panel', {
        region: 'center',
        title: '我的申请列表',
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
            dataIndex: 'processid',
            header: "流程实例ID",
            width: 100
        }, {
            dataIndex: 'processstate',
            header: "流程状态",
            width: 100,
            align: 'center',
        }, {
            dataIndex: 'task',
            header: "当前节点",
            width: 160,
            align: 'center',
        }, {
            dataIndex: 'cond',
            header: "是否拒绝",
            width: 100,
            align: 'center',
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
                            MyExt.util.Ajax('../preView/getExample.do', {
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
                                            value: data.data["应用"]
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '场景',
                                            name: 'home_score',
                                            value: data.data['场景']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '产品ID',
                                            name: 'home_score',
                                            value: select[0].raw.processid
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '实例名称',
                                            name: 'home_score',
                                            value: select[0].raw.processid
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '参数1',
                                            name: 'home_score',
                                            value: select[0].raw.processid
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '参数2',
                                            name: 'home_score',
                                            value: select[0].raw.processid
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '参数3',
                                            name: 'home_score',
                                            value: select[0].raw.processid
                                        }, ],
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
            dataIndex: 'opinion',
            header: "审批拒绝意见",
            width: 200,
            align: 'center',
       }],
    });


    Ext.create('Ext.container.Viewport', {
        layout: 'border',    //使用BorderLayout的布局方式(边界布局);可以自动检测浏览器的大小变化和自动调整布局中每个部分的大小;为什么加上就没有页码了？
        items: [userGrid]
    });
    reload();

})