DROP TABLE IF EXISTS `product`;

create TABLE `product` (
                            `id` int(11) NOT NULL AUTO_INCREMENT comment '主键',
                            `productid` varchar(100) comment '产品id',
                            `productname` varchar(100) NOT NULL comment '产品名称',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON update CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`productid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into `product` (`productid`, `productname`, `gmt_create`)
VALUES
('prod-bp1c6y7y2wj453','DEMO-创建ECS（选择VPC）',now()),
('prod-bp1qbazd242511','sdffedxx',now()),
('prod-bp18r7q127u45k','DEMO-创建VPC+ECS',now()),
('prod-bp1p27wj2c94fg','DEMO-创建RAM角色',now());