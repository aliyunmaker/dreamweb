Ext.onReady(function () {
    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../provisionedProduct/searchProvisionedProduct.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'exampleName', 'productId', 'exampleId', 'roleId', 'startName', 'status', 'parameter', 'outputs', 'productName','startTime']
    });

    var userGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '产品实例列表',
        store: userStore,
        height: 50,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'productId',
            header: '产品ID',
            width: 160
        }, {
            dataIndex: 'productName',
            header: "产品名称",
            width: 150
        }, {
            dataIndex: 'exampleId',
            header: "实例ID",
            width: 150
        }, {
            dataIndex: 'exampleName',
            header: "实例名称",
            width: 250,
        }, {
            dataIndex: 'startName',
            header: "申请人",
            width: 100,
        }, {
            dataIndex: 'status',
            header: "实例状态",
            width: 100,
            align: 'center',
            renderer: function(v) {
                if(v == 'Error') {
                    return "<span style='color:red;'>Error</span>";
                }
                else if(v == 'Available') {
                    return "<span style='color:green;'>Available</span>";
                }
                else if(v == 'UnderChange') {
                    return "<span style='color:black;'>UnderChange</span>";
                }
            }
        }, {
            dataIndex: 'startTime',
            header: "创建时间",
            width: 150
        }, {
            text: '申请参数',
            width: 140,
            align: 'center',
            renderer: function (value, metaData, record) {
                var id = record.data.exampleId;
                Ext.defer(function () {
                    Ext.widget('button', {
                        renderTo: id,
                        width: 110,
                        text: '参数详情',
                        handler: function () {
                            var parameter = JSON.stringify(JSON.parse(record.data.parameter), null, 4);
                            parameterTest = Ext.create('Ext.form.field.TextArea', {
                                labelAlign:'right',
                                width: "100%",
                                name: 'parameter',
                                value: parameter,
                                allowBlank: false,
                                rows:20,
                                width: '100%',
                                readOnly:true,
                            });
                            win = new Ext.Window({
                                title:'申请参数详细信息',
                                layout:'form',
                                width:400,
                                closeAction:'close',
                                target : document.getElementById('buttonId'),
                                plain: true,
                                items: [parameterTest],
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
            text: '实例输出',
            width: 140,
            align: 'center',
            renderer: function (value, metaData, record,rowIndex,store) {
            var id = record.data.exampleId + 'shilishuchu';
            Ext.defer(function () {
                Ext.widget('button', {
                    renderTo: id,
                    width: 110,
                    text: '实例输出',
                    handler: function () {
                        var outputs;
                        if(record.data.outputs == null) {
                            outputs = "";
                        } else if(record.data.status == "Error"){
                            outputs = record.data.outputs;
                        } else {
                            outputs = JSON.stringify(JSON.parse(record.data.outputs), null, 4);
                        }
                        outputsTest = Ext.create('Ext.form.field.TextArea', {
                            labelAlign:'right',
                            width: "100%",
                            name: 'outputs',
                            value: outputs,
                            allowBlank: false,
                            rows:20,
                            width: '100%',
                            readOnly:true,
                        });
                        win = new Ext.Window({
                            title:'实例输出详细信息',
                            layout:'form',
                            width:500,
                            closeAction:'close',
                            target : document.getElementById('buttonId'),
                            plain: true,
                            items: [outputsTest],
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
    }],
});

    Ext.create('Ext.container.Viewport', {
        layout: 'border',    //使用BorderLayout的布局方式(边界布局);可以自动检测浏览器的大小变化和自动调整布局中每个部分的大小;
        items: [userGrid]
    });

    //定时刷新store
    var task={
        run:function(){
            reload();//直接reload
        },
    interval:5000 //5秒
    }
    Ext.TaskManager.start(task);

})