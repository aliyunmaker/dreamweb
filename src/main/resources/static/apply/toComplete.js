Ext.onReady(function () {

    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../task/getMyTaskList.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['starterName', 'processTime', 'taskTime', 'taskId', 'taskName', 'processId', 'planId']
    });

    var userGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '待处理任务列表',
        store: userStore,
        columns: [{
            dataIndex: 'starterName',
            header: '申请人',
            width: 100
        }, {
            dataIndex: 'processTime',
            header: "申请时间",
            width: 150
        }, {
            dataIndex: 'planId',
            header: "启动计划ID",
            width: 150
        }, {
            text: '申请内容',
            xtype: 'gridcolumn',
            width: 107,
            align: 'center',
            renderer: function (value, metaData, record) {
                var id = record.raw.planId;
                metaData.tdAttr = 'data-qtip="查看当前申请内容详情"';
                Ext.defer(function () {
                    Ext.widget('button', {
                         renderTo: id,
                        width: 100,
                        text: '详细信息',
                        handler: function () {
                            var select = MyExt.util.SelectGridModel(userGrid, true);
                            MyExt.util.Ajax('../task/getInfo.do', {
                                    planId: id,
                                }, function (data) {
                                    var parameters = JSON.stringify(JSON.parse(data.data["参数信息"]), null, 4);
                                    var form = new Ext.form.FormPanel({
                                        defaultType:'textfield',
                                        items: [{
                                            xtype : 'displayfield',
                                            fieldLabel: '应用',
                                            name: 'home_score',
                                            value: data.data['应用']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '环境',
                                            name: 'home_score',
                                            value: data.data['环境']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '地域',
                                            name: 'home_score',
                                            value: data.data['地域']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '产品ID',
                                            name: 'home_score',
                                            value: data.data['产品ID']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '产品名称',
                                            name: 'home_score',
                                            value: data.data['产品名称']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '产品版本ID',
                                            name: 'home_score',
                                            value: data.data['版本ID']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '产品组合ID',
                                            name: 'home_score',
                                            value: data.data['产品组合ID']
                                        }, {
                                            xtype : 'displayfield',
                                            fieldLabel: '实例名称',
                                            name: 'home_score',
                                            value: data.data['实例名称']
                                        }, {
                                            xtype : 'textarea',
                                            fieldLabel: '参数信息',
                                            width: 400,
                                            name: 'home_score',
                                            value: parameters,
                                            rows:10,
                                            readOnly:true
                                        }]
                                    });
                                    var win = new Ext.Window({
                                        layout:'fit',
                                        title:'详细信息',
                                        target : document.getElementById('buttonId'),
                                        width:500,
                                        items:[form],         //嵌入表单;
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
            dataIndex: 'taskId',
            header: "任务ID",
            width: 100,
        }, {
            dataIndex: 'processId',
            header: "流程实例ID",
            width: 100,
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
                    var taskIds = [];
                    var processIds = [];
                    var planIds = [];
                    Ext.Array.each(select,function(record){
                        taskIds.push(record.raw.taskId);
                        processIds.push(record.raw.processId);
                        planIds.push(record.raw.planId);
                    });
                    var taskId = JSON.stringify(taskIds);
                    var processId = JSON.stringify(processIds);
                    var planId = JSON.stringify(planIds);
                    MyExt.util.MessageConfirm('是否确定通过', function () {
                        MyExt.util.Ajax('../task/complete.do', {
                            taskId: taskId,
                            processId: processId,
                            planId: planIds
                        }, function (data) {
                            reload();
                            if (data.data == 1)
                                MyExt.Msg.alert('通过成功!');
                            else {
                                MyExt.Msg.alert('审批通过，开始创建产品实例!');
                            }
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
                                    taskId: select[0].raw.taskId,
                                    processId: select[0].raw.processId,
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