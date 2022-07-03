Ext.onReady(function () {

    var reload = function () {
        userStore.load();
    };

    function test() {
        // MyExt.util.Ajax('../apply/updateProcess.do', {
        //     planId: "222",
        // }, function (data) {
        //     console.log(data);
        // });
        Ext.Ajax.request({
            url: '../apply/updateProcess.do',
            success: function(response, opts) {
                result = Ext.decode(response.responseText);
                console.dir(result);
            },
            failure: function(response, opts) {
                console.log('server-side failure with status code ' + response.status);
            }
        });

    }

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../apply/getMyAsk.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['starterName', 'processTime', 'processId', 'task', 'processState', 'cond', 'opinion', 'planId', 'planResult']
    });

    var userGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '我的申请列表',
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
            dataIndex: 'processId',
            header: "流程实例ID",
            width: 150
        }, {
            dataIndex: 'planId',
            header: "启动计划ID",
            width: 150
        }, {
            dataIndex: 'processState',
            header: "流程状态",
            width: 100,
            align: 'center',
            renderer: function(v) {
                if(v == '已拒绝') {
                    return "<span style='color:red;'>已拒绝</span>";
                }
                else if(v == '已通过') {
                    return "<span style='color:green;'>已通过</span>";
                }
                else if(v == '审批中') {
                    return "<span style='color:black;'>审批中</span>";
                }
                else if(v == '预检中') {
                    return "<span style='color:blue;'>预检中</span>";
                }
                else if(v == '预检失败') {
                    return "<span style='color:red;'>预检失败</span>";
                }
            }
        }, {
            dataIndex: 'cond',
            header: "是否拒绝",
            width: 100,
            align: 'center',
            hidden: true,
        }, {
            text: '申请内容',
            xtype: 'gridcolumn',
            width: 150,
            align: 'center',
            renderer: function (value,metaData, record, rowIndex, columnIndex,cellmeta) {
                var planId = record.raw.planId;
                var id = planId + 'shenqingneirong';
                Ext.defer(function () {
                    Ext.widget('button', {
                        renderTo: id,
                        width: 110,
                        text: '详细信息',
                        handler: function () {
                            var select = MyExt.util.SelectGridModel(userGrid, true);
                            MyExt.util.Ajax('../task/getInfo.do', {
                                    planId: planId,
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
                                    win = new Ext.Window({
                                        title:'详细信息',
                                        layout:'fit',
                                        width:500,
                                        closeAction:'close',
                                        target : document.getElementById('buttonId'),
                                        plain: true,
                                        items: [form],
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
            text: '预检结果',
            width: 140,
            align: 'center',
            renderer: function (value, metaData, record) {
                var planId = record.raw.planId;
                var id = planId + 'yujianjieguo';
                var planResult = record.data.planResult;
                Ext.defer(function () {
                    Ext.widget('button', {
                        renderTo: id,
                        width: 110,
                        text: '预检结果',
                        handler: function () {
                            planResultTest = Ext.create('Ext.form.field.TextArea', {
                                labelAlign:'right',
                                width: "100%",
                                name: 'planResult',
                                value: planResult,
                                rows:20,
                                width: '100%',
                                readOnly:true,
                            });
                            win = new Ext.Window({
                                title:'预检结果详细信息',
                                layout:'form',
                                width:500,
                                closeAction:'close',
                                target : document.getElementById('buttonId'),
                                plain: true,
                                items: [planResultTest],
                                buttons: [{
                                    text: '确认',
                                    handler: function(){
                                        win.hide();
                                    }
                                }],
                                buttonAlign: 'center',
                            });
                            win.show();
                        }
                    });
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', id);
            }
        }, {
            dataIndex: 'opinion',
            header: "审批意见",
            width: 200,
       }],
    });

    Ext.create('Ext.container.Viewport', {
        layout: 'border',    //使用BorderLayout的布局方式(边界布局);可以自动检测浏览器的大小变化和自动调整布局中每个部分的大小
        items: [userGrid]
    });
    reload();
    //定时刷新store
    var task={
        run:
            // MyExt.util.Ajax('../apply/updateProcess.do', {
            //         planId:"2",
            //     }, function (data) {
            //         console.log(data);
            // });
            // $.ajax({
            //     url: "../../apply/updateProcess.do",
            //     success: function (result) {
            //         console.log(result);
            //     }
            // });
            test,
            // reload();//直接reload
        interval:3000 //3秒
    }
    Ext.TaskManager.start(task);
})