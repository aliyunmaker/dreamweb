DROP TABLE IF EXISTS `provisioned_product`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `myAsk`;
DROP TABLE IF EXISTS `userproduct`;

create TABLE `product` (
                            `id` int(11) NOT NULL AUTO_INCREMENT comment '主键',
                            `productid` varchar(100) comment '产品id',
                            `application` varchar(100) NOT NULL comment '应用',
                            `scenes` varchar(100) NOT NULL comment '场景',
                            `productname` varchar(100) NOT NULL comment '产品名称',
                            `productversionid` varchar(100) NOT NULL comment '产品版本ID',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON update CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`productversionid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into `product` (`productid`, `application`, `scenes`,`productname`, `productversionid`, `gmt_create`)
VALUES
('prod-bp1c6y7y2wj453','application1','日常','asdasda', 'pv-bp15gfhv2px6th', now()),
('prod-bp1c6y7y2wj453','application1','预发','DEMO-创建ECS（选择VPC）','pv-bp11vd4m26h6uh',now()),
('prod-bp1c6y7y2wj453','application1','线上','DEMO-创建ECS（选择VPC）','pv-bp151yxr2we4jw',now()),

('prod-bp1qbazd242511','application2','预发','sdffedxx','asdasdasassd',now()),
('prod-bp18r7q127u45k','application2','线上','DEMO-创建VPC+ECS','pv-bp1wendz2e962y',now()),
('prod-bp1p27wj2c94fg','application2','日常','DEMO-创建RAM角色','pv-bp1zymve23b54q',now()),

('prod-bp1p27wj2c94fg','application3','预发','DEMO-创建RAM角色','pv-bp1z87gw25a4zf',now()),
('prod-bp18r7q127u45k','application3','线上','DEMO-创建VPC+ECS','pv-bp15e79d2614pw',now()),
('prod-bp18r7q127u45k','application3','日常','DEMO-创建VPC+ECS','pv-bp1bjeut29963a',now());


CREATE TABLE `provisioned_product`(
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `examplename` varchar(100) NOT NULL COMMENT '实例名称',
                            `productid` varchar(100) NOT NULL COMMENT '产品ID',
                            `productname` varchar(100) NOT NULL COMMENT '产品名称',
                            `exampleid` varchar(100) NOT NULL COMMENT '实例ID',
                            `roleid` int(11) NOT NULL COMMENT '角色ID',
                            `startname` varchar(100) NOT NULL COMMENT '实例申请人',
                            `status` varchar(100) NOT NULL COMMENT '实例状态',
                            `parameter` varchar(1000)COMMENT '申请参数',
                            `outputs` varchar(1000) COMMENT '输出',
                            `starttime` varchar(100) COMMENT '创建时间',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`examplename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `myAsk` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `starterName` varchar(100) NOT NULL COMMENT '申请人',
                            `roleId` int(11) NOT NULL COMMENT '角色ID',
                            `application` varchar(100) NOT NULL COMMENT '应用',
                            `scene` varchar(100) NOT NULL COMMENT '环境',
                            `processTime` varchar(100) NOT NULL COMMENT '申请时间',
                            `processId` varchar(100) COMMENT '流程实例ID',
                            `exampleName` varchar(100) COMMENT '实例名称',
                            `task` varchar(100) COMMENT '当前节点',
                            `processState` varchar(100) NOT NULL COMMENT '流程状态',
                            `parameters` varchar(1000) NOT NULL COMMENT '流程信息',
                            `productId` varchar(100) NOT NULL COMMENT '产品ID',
                            `planId` varchar(100) NOT NULL COMMENT '启动计划ID',
                            `region` varchar(100) COMMENT '地域',
                            `versionid` varchar(100) COMMENT '版本ID',
                            `cond` varchar(50) COMMENT '是否审批通过',
                            `processDefinitionId` varchar(100) NOT NULL COMMENT '流程定义ID',
                            `opinion` varchar(500) COMMENT '审批拒绝意见',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY (`processId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `userproduct` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `productid` varchar(100) NOT NULL COMMENT '产品ID',
                            `username` varchar(100) NOT NULL COMMENT '用户名',
                            `portfolioid` varchar(100) NOT NULL COMMENT '产品组合ID',
                            `productname` varchar(100) NOT NULL COMMENT '产品名称',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `user-product` (`username`, `productid`),
                            UNIQUE KEY `user-product-portfolio` (`username`, `productid`, `portfolioid`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `userproduct` (`productid`, `productname`, `username`, `portfolioid`, `gmt_create`)
VALUES
('prod-bp18r7q127u45k','DEMO-创建VPC+ECS','admin','port-bp1yt7582gn4p7',now()),
('prod-bp18r7q127u45k','DEMO-创建VPC+ECS','dou','port-bp1yt7582gn4p7',now()),
('prod-bp1c6y7y2wj453','创建ECS','admin','port-bp1yt7582gn4p7',now()),
('prod-bp1c6y7y2wj453','创建ECS','dou','port-bp1yt7582gn4p7',now()),
('prod-bp1p27wj2c94fg','创建RAM角色','admin','port-bp1yt7582gn4p7',now()),
('prod-bp1p27wj2c94fg','创建RAM角色','dou','port-bp1yt7582gn4p7',now());