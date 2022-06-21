Ext.onReady(function () {
    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../provisionedProduct/searchProvisionedProduct.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'examplename', 'productid', 'exampleid', 'roleid', 'startname', 'status', 'parameter', 'outputs', 'productname','starttime']
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
            dataIndex: 'productid',
            header: '产品ID',
            width: 160
        }, {
            dataIndex: 'productname',
            header: "产品名称",
            width: 150
        }, {
            dataIndex: 'exampleid',
            header: "实例ID",
            width: 150
        }, {
            dataIndex: 'examplename',
            header: "实例名称",
            width: 300,
        }, {
            dataIndex: 'startname',
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
            dataIndex: 'starttime',
            header: "创建时间",
            width: 150
        }, {
            text: '申请参数' + "&nbsp;&nbsp;&nbsp;&nbsp;"+ '|'+ "&nbsp;&nbsp;&nbsp;&nbsp;" + '实例输出',
            width: 220,
            height: 25,
            align: 'center',
            renderer: function (value, metaData, record,rowIndex,columnLndex,cellmeta,store) {
            metaData.tdAttr = 'data-qtip="查看当前申请参数详情"';
            var id = record.id;
            Ext.defer(function () {


                Ext.widget('toolbar', {
                    renderTo: id,
                    dock: 'top',
                    style: 'margin:0',
                    ui: 'footer',
                    height: 25,
                    width: 220,
                    items: [
                        {
                            xtype: 'button',
                            width: 100,
                            height: 25,
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
                        },
                        {
                            xtype: 'button',
                            width: 100,
                            height: 25,
                            text: '输出详情',
                            handler: function () {
                                var outputs = JSON.stringify(JSON.parse(record.data.outputs), null, 4);
                                var id = record.id;
                                outputsTest = Ext.create('Ext.form.field.TextArea', {
                                    labelAlign:'right',
                                    width: "100%",
                                    name: 'outputs',
                                    // renderTo: id, 
                                    value: outputs,
                                    allowBlank: false,
                                    rows:20,
                                    width: '100%',
                                    readOnly:true,
                                });
                                win = new Ext.Window({
                                    title:'实例输出详细信息',
                                    layout:'form',
                                    width:400,
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

                        }]
                    });
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', record.id);
            }
        }
        ],
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
    interval:30000 //30秒
    }
    Ext.TaskManager.start(task);

})