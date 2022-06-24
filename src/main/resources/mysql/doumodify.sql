DROP TABLE IF EXISTS `provisioned_product`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `myAsk`;

CREATE TABLE `product` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `productid` varchar(100) COMMENT '产品id',
                            `application` varchar(100) NOT NULL COMMENT '应用',
                            `scenes` varchar(100) NOT NULL COMMENT '场景',
                            `productname` varchar(100) NOT NULL COMMENT '产品名称',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`productid`),
                             UNIQUE KEY `application-scenes` (`application`, `scenes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `product` (`productid`, `application`, `scenes`,`productname`, `gmt_create`)
VALUES
('prod-bp165aqz2kx5df','application1','日常','asdasda',now()),
('prod-bp1n4yee2544b1','application1','预发','DEMO-创建ECS（选择VPC）',now()),
('prod-bp1c6y7y2wj453','application1','线上','DEMO-创建ECS（选择VPC）',now()),
('25','application2','日常','dfgqdasdas',now()),
('prod-bp1qbazd242511','application2','预发','sdffedxx',now()),
('6','application2','线上','dfgewasda',now()),
('65','application3','日常','sdfrrfe',now()),
('63','application3','预发','sfsdasd',now()),
('prod-bp18r7q127u45k','application3','线上','adfsfwefq',now()),
('9','application3','scenes1','dfwefwefef',now());


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
                             UNIQUE KEY (`examplename`),
                             FOREIGN KEY(productid) REFERENCES product(productid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `myAsk` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `starterName` varchar(100) NOT NULL COMMENT '申请人',
                            `processTime` varchar(100) NOT NULL COMMENT '申请时间',
                            `processId` varchar(100) NOT NULL COMMENT '流程实例ID',
                            `task` varchar(100) COMMENT '当前节点',
                            `processState` varchar(100) NOT NULL COMMENT '流程状态',
                            `parameters` varchar(1000) NOT NULL COMMENT '流程信息',
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

DROP TABLE IF EXISTS `userproduct`;
CREATE TABLE `userproduct` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `productid` varchar(100) NOT NULL COMMENT '产品ID',
                            `username` varchar(100) NOT NULL COMMENT '用户名',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `userproduct` (`productid`, `username`, `gmt_create`)
VALUES
('prod-bp18r7q127u45k','admin',now()),
('prod-bp18r7q127u45k','dou',now()),
('prod-bp1c6y7y2wj453','admin',now()),
('prod-bp1c6y7y2wj453','dou',now());