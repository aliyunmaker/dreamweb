DROP TABLE IF EXISTS `provisioned_product`;
CREATE TABLE `provisioned_product`(
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `examplename` varchar(100) NOT NULL COMMENT '实例名称',
                            `productid` varchar(100) NOT NULL COMMENT '产品ID',
                            `productname` varchar(100) NOT NULL COMMENT '产品名称',
                            `exampleid` varchar(100) NOT NULL COMMENT '实例ID',
                            `roleid` int(11) NOT NULL COMMENT '角色ID',
                            `startname` varchar(100) NOT NULL COMMENT '实例申请人',
                            `status` varchar(100) NOT NULL COMMENT '实例状态',
                            `parameter` varchar(1000) COMMENT '申请参数',
                            `outputs` varchar(2000) COMMENT '输出',
                            `starttime` varchar(100) COMMENT '创建时间',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`examplename`),
                             FOREIGN KEY(productid) REFERENCES product(productid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;