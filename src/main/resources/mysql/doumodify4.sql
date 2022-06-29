DROP TABLE IF EXISTS `userproduct`;
CREATE TABLE `userproduct` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `productid` varchar(100) NOT NULL COMMENT '产品ID',
                            `username` varchar(100) NOT NULL COMMENT '用户名',
                            `productname` varchar(100) NOT NULL COMMENT '产品名称',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `user-product` (`username`, `productid`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `userproduct` (`productid`, `productname`, `username`, `gmt_create`)
VALUES
('prod-bp18r7q127u45k','DEMO-创建VPC+ECS','admin',now()),
('prod-bp18r7q127u45k','DEMO-创建VPC+ECS','dou',now()),
('prod-bp1c6y7y2wj453','创建ECS','admin',now()),
('prod-bp1c6y7y2wj453','创建ECS','dou',now()),
('prod-bp1yfyzf2dp4x7','创建RAM角色','admin',now()),
('prod-bp1yfyzf2dp4x7','创建RAM角色','dou',now());