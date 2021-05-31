DROP TABLE IF EXISTS `rsakey`;

CREATE TABLE `system_config` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `config_name` varchar(100) NOT NULL COMMENT '配置名',
                            `config_value` varchar(5000) DEFAULT NULL COMMENT '配置',
                            `comment` varchar(1000) DEFAULT NULL,
                            `changeable` tinyint(1) NOT NULL DEFAULT TRUE COMMENT '是否可修改',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `config_name` (`config_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;