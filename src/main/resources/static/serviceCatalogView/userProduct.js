Ext.onReady(function () {
    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../userProduct/searchUserProductByUserName.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'servicecatalogProductId', 'productName']
    });


    var userGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '产品列表',
        store: userStore,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'servicecatalogProductId',
            header: "产品ID",
            width: 400
        }, {
            dataIndex: 'productName',
            header: "产品名称",
            width: 400
        }, {
            text: '申请',
            xtype: 'gridcolumn',
            width: 250,
            align: 'center',
            renderer: function (value, metaData, record) {
                var id = record.data["id"];
                var servicecatalogProductId = record.data["servicecatalogProductId"];
                var name = record.data["productName"];
                var nameUrl = encodeURIComponent(name);
                Ext.defer(function () {
                    Ext.widget('button', {
                        renderTo: servicecatalogProductId,
                        width: 170,
                        text: '申请实例',
                        handler: function () {
                            var select = MyExt.util.SelectGridModel(userGrid, true);
                            MyExt.util.Ajax('../userRole/getRoleId.do', {

                                }, function (data) {
                                    var roleId = data.data;
                                    if(roleId != null) {
                                        window.location.href = "http://localhost:8080/serviceCatalogView/serviceCatalogView.html?"+"productId="+id+"&productName="+nameUrl + "&roleId="+ roleId;
                                    } else {
                                        alert("您还未选择角色！");
                                        window.location.href = "http://localhost:8080/metadata/metadataManage.html";
                                    }
                                });
                        }
                    });
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', servicecatalogProductId);
            }
        }]
    });

    reload();

    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        items: [userGrid]
    });

})