Ext.onReady(function () {
    // Ext.tip.QuickTipManager.init();

    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../task/getMyTaskList.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['startername', 'processtime', 'tasktime', 'processinfo', 'taskid', 'taskname', 'processid']
    });

    var userGrid = Ext.create('MyExt.Component.GridPanel', {
    // var userGrid = Ext.create('Ext.grid.Panel', {
        region: 'center',
        title: '待处理任务列表',
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
            // dataIndex: 'processinfo',
            // header: "申请内容",
            // width: 160,
            // align: 'center',
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
                            // win = new Ext.Window({
                            //     title:'详细信息',
                            //     layout:'form',
                            //     width:400,
                            //     closeAction:'close',
                            //     target : document.getElementById('buttonId'),
                            //     plain: true,
                            //     items: [{
                            //         xtype : 'displayfield',
                            //         fieldLabel: '应用',
                            //         name: 'home_score',
                            //         value: yingyong
                            //     }, {
                            //         xtype : 'displayfield',
                            //         fieldLabel: '场景',
                            //         name: 'home_score',
                            //         value: changjing
                            //     }, {
                            //         xtype : 'displayfield',
                            //         fieldLabel: '产品ID',
                            //         name: 'home_score',
                            //         value: select[0].raw.processid
                            //     }, {
                            //         xtype : 'displayfield',
                            //         fieldLabel: '实例名称',
                            //         name: 'home_score',
                            //         value: select[0].raw.processid
                            //     }, {
                            //         xtype : 'displayfield',
                            //         fieldLabel: '参数1',
                            //         name: 'home_score',
                            //         value: select[0].raw.processid
                            //     }, {
                            //         xtype : 'displayfield',
                            //         fieldLabel: '参数2',
                            //         name: 'home_score',
                            //         value: select[0].raw.processid
                            //     }, {
                            //         xtype : 'displayfield',
                            //         fieldLabel: '参数3',
                            //         name: 'home_score',
                            //         value: select[0].raw.processid
                            //     }, ],
                            //     buttons: [{
                            //        text: '确认',
                            //        handler: function(){
                            //            win.hide();
                            //         }
                            //     }],
                            //     buttonAlign: 'center',
                            //  });
                            // win.show();
                        }
                    });
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', id);
            }
        }, {
            dataIndex: 'taskid',
            header: "任务ID",
            width: 100,
        }, {
            dataIndex: 'taskname',
            header: "任务名称",
            width: 100,
        }, {
            dataIndex: 'processid',
            header: "流程实例ID",
            width: 100,
            align: 'center',
        }],
        tbar: [{
            text: '通过',
            iconCls: 'MyExt-confirm',
            handler: function (o) {
                var grid=o.ownerCt.ownerCt;//ownerCt 获取父节点  ，最终获取grid
	        	var select=grid.getSelectionModel().getSelection(); //获取选中的数组对象
                if (select.length == 0) {
                    Ext.Msg.alert('提示','请选择要通过的任务（可多选）');
                }else{
                    var taskids = [];
                    Ext.Array.each(select,function(record){
                        taskids.push(record.raw.taskid)
                    });
                    var taskid = JSON.stringify(taskids);
                    MyExt.util.MessageConfirm('是否确定通过', function () {
                        MyExt.util.Ajax('../task/complete.do', {
                            taskid: taskid,
                        }, function (data) {
                            reload();
                            MyExt.Msg.alert('通过成功!');
                        });
                    });
                }
            }
        }, {
            text: '拒绝',
            iconCls: 'MyExt-cancel',
            handler: function () {
                var select = MyExt.util.SelectGridModel(userGrid, true);
                if (!select) {
                    return;
                }
                win = new Ext.Window({
                    title:'处理',
                    layout:'form',
                    width:700,
                    closeAction:'close',
                    target : document.getElementById('buttonId'),
                    plain: true,
                    items: [{
                       xtype : 'textfield',
                       fieldLabel: '请给出拒绝意见'
                    }],
                    buttons: [{
                       text: '确认拒绝',
                       handler: function(){
                            MyExt.util.MessageConfirm('是否确定拒绝', function () {
                                MyExt.util.Ajax('../task/reject.do', {
                                    taskid: select[0].raw.taskid,
                                    opinion: win.items.items[0].rawValue
                                }, function (data) {
                                    win.hide();
                                    reload();
                                    MyExt.Msg.alert('拒绝成功!');
                                });
                            });
                        }
                    },{
                       text: '取消',
                       handler: function(){
                           win.hide();
                        }
                    }],
                    buttonAlign: 'center',
                 });

                 win.show();
            }
        }],
    });


    Ext.create('Ext.container.Viewport', {
        layout: 'border',    //使用BorderLayout的布局方式(边界布局);可以自动检测浏览器的大小变化和自动调整布局中每个部分的大小;为什么加上就没有页码了？
        items: [userGrid]
    });
    reload();

})