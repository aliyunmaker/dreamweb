CREATE TABLE `rsakey` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `key_name` varchar(100) NOT NULL COMMENT '密钥名',
                            `public_key` varchar(1200) DEFAULT NULL COMMENT '公钥',
                            `private_key` varchar(1200) DEFAULT NULL COMMENT '私钥',
                            `comment` varchar(1000) DEFAULT NULL,
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `key_name` (`key_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;