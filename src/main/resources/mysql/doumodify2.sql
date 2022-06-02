DROP TABLE IF EXISTS `example`;

CREATE TABLE `example`(
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `examplename` varchar(100) NOT NULL COMMENT '实例名称',
                            `productid` varchar(100) NOT NULL COMMENT '产品id',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`examplename`),
                             FOREIGN KEY(productid) REFERENCES product(productid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
