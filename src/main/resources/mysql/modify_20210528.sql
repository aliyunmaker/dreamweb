DROP TABLE IF EXISTS `rsakey`;
DROP TABLE IF EXISTS `system_config`;

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

LOCK TABLES `system_config` WRITE;

INSERT INTO `system_config` (`config_name`, `config_value`, `comment`, `changeable`, `gmt_create`)
VALUES
('allowWechatLogin','true','是否允许通过微信登录',TRUE,now()),
('allowLDAP','false','是否允许通过LDAP登录',TRUE,now()),
('loginPageTitle','无限梦想','登录页标题',TRUE,now()),
('allowSolutionDemo','true','解决方案Demo开关',TRUE,now());

UPDATE `system_config`
SET `comment` = '系统密钥'
WHERE `config_name` = 'systemRSAKey';

UNLOCK TABLES;