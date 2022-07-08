DROP TABLE IF EXISTS `properties`;
CREATE TABLE `properties` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `k` varchar(100) NOT NULL COMMENT '键',
                            `v` varchar(100) NOT NULL COMMENT '值',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `key-value` (`k`, `v`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;