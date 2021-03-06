# ************************************************************
# Host: 127.0.0.1 (MySQL 5.7.29)
# Database: dreamweb
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `unionid` varchar(100) DEFAULT NULL COMMENT '微信的统一id',
                        `login_name` varchar(100) NOT NULL DEFAULT '' COMMENT '登录名',
                        `name` varchar(200) DEFAULT '',
                        `email` varchar(200) DEFAULT NULL,
                        `password` varchar(100) DEFAULT NULL,
                        `role` varchar(100) DEFAULT NULL COMMENT '角色',
                        `phone` varchar(100) DEFAULT NULL COMMENT '手机号码',
                        `comment` varchar(1000) DEFAULT NULL,
                        `gmt_create` datetime DEFAULT NULL,
                        `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `login_name` (`login_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;

INSERT INTO `user` (`id`, `unionid`, `login_name`, `name`, `email`, `password`, `role`, `phone`, `comment`, `gmt_create`, `gmt_modified`)
VALUES
(1,NULL,'admin','管理员',NULL,'304213573cbe4ea1304a1d630e3e7322','ROLE_ADMIN',NULL,NULL,NULL,'2021-01-15 13:56:52'),
(2,NULL,'test','测试',NULL,'6791018a83aecc125f4e150ac6acefa9','ROLE_GUEST','11111222','','2021-01-15 14:00:06','2021-01-15 14:00:19');

/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user_group
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_group`;

CREATE TABLE `user_group` (
                              `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                              `name` varchar(200) NOT NULL DEFAULT '',
                              `gmt_create` datetime DEFAULT NULL,
                              `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table user_group_associate
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_group_associate`;

CREATE TABLE `user_group_associate` (
                                        `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                        `user_id` int(11) NOT NULL,
                                        `user_group_id` int(11) NOT NULL,
                                        `gmt_create` datetime DEFAULT NULL,
                                        `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `u_user_user_group` (`user_group_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table user_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
                             `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                             `user_group_id` int(11) NOT NULL,
                             `role_name` varchar(100) NOT NULL,
                             `role_value` varchar(200) DEFAULT NULL,
                             `role_type` varchar(100) DEFAULT NULL,
                             `gmt_create` datetime DEFAULT NULL,
                             `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table api_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `api_user`;

CREATE TABLE `api_user` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `accessKeyId` varchar(100) NOT NULL,
                        `accessKeySecret` varchar(100) NOT NULL,
                        `comment` varchar(1000) DEFAULT NULL,
                        `valid` tinyint(1) NOT NULL COMMENT '是否生效',
                        `gmt_create` datetime DEFAULT NULL,
                        `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `accessKeyId` (`accessKeyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table login_record
# ------------------------------------------------------------

DROP TABLE IF EXISTS `login_record`;

CREATE TABLE `login_record` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `client_ip_addr` varchar(100) NOT NULL COMMENT '客户端IP地址',
                            `login_name` varchar(100) NOT NULL COMMENT '登录名',
                            `login_method` varchar(100) NOT NULL COMMENT '登录方式',
                            `comment` varchar(1000) DEFAULT NULL,
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
