Ext
    .onReady(function () {
        Ext.tip.QuickTipManager.init();

        var store = Ext.create('Ext.data.TreeStore', {
            root: {
                expanded: true,
                children: [{
                    text: "<font style='font-weight: bold;'>功能列表</font>",
                    iconCls: 'MyExt-menu-sub',
                    expanded: true,
                    children: [{
                        text: "个人中心",
                        leaf: true,
                        href: 'welcome/welcome.html',
                        hrefTarget: 'mainFrame'
                    }, {
                        text: "用户管理",
                        leaf: true,
                        href: 'user/userManage.html',
                        hrefTarget: 'mainFrame'
                    }, {
                        text: "用户组管理",
                        leaf: true,
                        href: 'userGroup/userGroupManage.html',
                        hrefTarget: 'mainFrame'
                    }, {
                      text: "资源视图",
                      leaf: true,
                      href: 'resourceView/resourceView.html',
                      hrefTarget: 'mainFrame'
                    }, {
                        text: "注销",
                        leaf: true,
                        iconCls: 'MyExt-logout',
                        href: 'logout'
                    }]
                }, {
                    text: "<font style='font-weight: bold;'>系统工具</font>",
                    iconCls: 'MyExt-menu-sub',
                    expanded: true,
                    children: [{
                        text: "账号生成器",
                        leaf: true,
                        href: 'tools/tools.html',
                        hrefTarget: 'mainFrame'
                    }, {
                        text: "API密钥管理",
                        leaf: true,
                        href: 'apiUser/apiUser.html',
                        hrefTarget: 'mainFrame'
                    }, {
                      text: "登录记录",
                      leaf: true,
                      href: 'loginRecord/loginRecord.html',
                      hrefTarget: 'mainFrame'
                    }]
                }]
            }
        });

        var tree_panel = Ext.create('Ext.tree.Panel', {
            frame: false,
            border: false,
            region: "center",
            store: store,
            rootVisible: false
        });

        var header_panel = Ext
            .create(
                'Ext.panel.Panel',
                {
                    region: "north",
                    height: 68,
                    frame: false,
                    border: false,
                    bodyStyle: 'background:rgb(223,233,246)',
                    html: 'loading...'
                });

        var main_panel = Ext.create('Ext.panel.Panel', {
            // title : 'Hello',
            frame: false,
            border: false,
            region: "center",
            html: '<iframe name="mainFrame" frameborder="0" width="100%" height="100%" src="welcome/welcome.html"/>'
        });

        Ext.create('Ext.container.Viewport', {
            layout: 'border',
            items: [{
                layout: 'border',
                region: 'west',
                // frame : false,
                // border : false,
                split: true,
                width: 160,
                margins: '0 0 0 0',
                items: [header_panel, tree_panel]
            }, main_panel]
        });

        MyExt.util.Ajax("system/getIndexLogoPage.do", null, function (data) {
            header_panel.body.update(data.data);
        });

    });