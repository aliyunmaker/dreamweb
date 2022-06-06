DROP TABLE IF EXISTS `example`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `myAsk`;

CREATE TABLE `product` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `productid` varchar(100) COMMENT '产品id',
                            `application` varchar(100) NOT NULL COMMENT '应用',
                            `scenes` varchar(100) NOT NULL COMMENT '场景',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`productid`),
                             UNIQUE KEY `application-scenes` (`application`, `scenes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `product` (`productid`, `application`, `scenes`, `gmt_create`)
VALUES
('prod-bp165aqz2kx5df','application1','日常',now()),
('prod-bp1n4yee2544b1','application1','预发',now()),
('prod-bp1c6y7y2wj453','application1','线上',now()),
('25','application2','日常',now()),
('prod-bp1qbazd242511','application2','预发',now()),
('6','application2','线上',now()),
('65','application3','日常',now()),
('63','application3','预发',now()),
('7','application3','线上',now()),
('9','application3','scenes1',now());


CREATE TABLE `example`(
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `examplename` varchar(100) NOT NULL COMMENT '实例名称',
                            `productid` varchar(100) NOT NULL COMMENT '产品id',
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
                            `processInfo` varchar(100) NOT NULL COMMENT '流程信息',
                            `cond` varchar(50) COMMENT '是否审批通过',
                            `processDefinitionId` varchar(100) NOT NULL COMMENT '流程定义ID',
                            `opinion` varchar(500) COMMENT '审批拒绝意见',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY (`processId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 
