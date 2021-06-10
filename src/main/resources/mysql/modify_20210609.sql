CREATE TABLE `sls_config` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `config_name` varchar(100) NOT NULL COMMENT '配置名',
                            `config_value` varchar(5000) DEFAULT NULL COMMENT '配置',
                            `config_owner_id` int(11) NOT NULL COMMENT '所有者',
                            `comment` varchar(1000) DEFAULT NULL,
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `config_name` (`config_name`),
                            UNIQUE KEY `config_owner_id` (`config_owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
