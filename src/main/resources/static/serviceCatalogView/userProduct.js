Ext.onReady(function () {
    var reload = function () {
        userStore.load();
    };

    var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
        dataUrl: '../product/searchUserProduct.do',
        rootFlag: 'data',
        pageSize: 200,
        fields: ['id', 'productId', 'productName']
    });


    var detailRole = null;
    var userGrid = Ext.create('MyExt.Component.GridPanel', {
        region: 'center',
        title: '产品列表',
        store: userStore,
        columns: [{
            dataIndex: 'id',
            header: 'ID',
            hidden: true
        }, {
            dataIndex: 'productId',
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
                var id = record.data["productId"];
                var name = record.data["productName"];
                var nameUrl = encodeURIComponent(name);
                Ext.defer(function () {
                    Ext.widget('button', {
                        renderTo: id,
                        width: 170,
                        text: '申请实例',
                        handler: function () {
                            var select = MyExt.util.SelectGridModel(userGrid, true);
                            MyExt.util.Ajax('../userRole/getRoleIdByUserName.do', {

                                }, function (data) {
                                    var roleId = data.data;
                                    if(roleId != null) {
                                        window.location.href = "http://localhost:8080/serviceCatalogView/serviceCatalogView.html?"+"productId="+id+"&productName="+nameUrl + "&roleId="+ roleId;
                                    } else {
                                        alert("您还未选择角色！");
                                        window.location.href = "http://localhost:8080/product/productManage.html";
                                    }
                                });
                        }
                    });
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', id);
            }
        }]
    });

    reload();

    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        items: [userGrid]
    });

})

function isTrue(isSuccess) {
    return (isSuccess === "true" || isSuccess === true);
}