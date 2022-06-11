Ext.onReady(function () {
    // Ext.tip.QuickTipManager.init();

    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../apply/getMyAsk.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['startername', 'processtime', 'processid', 'task', 'processstate', 'cond', 'opinion']
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
            }
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
            hidden: true,
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
            dataIndex: 'opinion',
            header: "审批拒绝意见",
            width: 200,
            align: 'center',
       }],
    });

//    userGrid.renderer(3, getColor);
////    userGrid.setRenderer(3, getColor);
////    userGrid.setRenderer(4, getColor);
//    function getColor(val) {
//      if (val != "") {
//          return '<font color=blue></font><span style="color:red;">' + Ext.util.Format.usMoney(val) + '</span>';
//      }
//    }

    Ext.create('Ext.container.Viewport', {
        layout: 'border',    //使用BorderLayout的布局方式(边界布局);可以自动检测浏览器的大小变化和自动调整布局中每个部分的大小;为什么加上就没有页码了？
        items: [userGrid]
    });
    reload();

})