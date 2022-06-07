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
                        `login_method` varchar(50) DEFAULT NULL COMMENT '登录类型',
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



# Dump of table system_config
# ------------------------------------------------------------

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
('allowSolutionDemo','false','解决方案Demo开关',TRUE,now()),
('region','','日志服务地区配置',TRUE,now()),
('useVpc','false','是否使用私网地址',TRUE,now());

UNLOCK TABLES;


# Dump of table sls_config
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sls_config`;

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



# Dump of table solution_config
# ------------------------------------------------------------

DROP TABLE IF EXISTS `solution_config`;

CREATE TABLE `solution_config` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `name` varchar(100) NOT NULL COMMENT '解决方案名称',
                            `intro` varchar(5000) DEFAULT NULL COMMENT '解决方案简介',
                            `web_config` varchar(1000) NOT NULL COMMENT '解决方案网页配置',
                            `creator` varchar(100) NOT NULL COMMENT '解决方案创建人',
                            `module` varchar(100) NOT NULL COMMENT '解决方案所属模块',
                            `customer_num` int(11) NOT NULL DEFAULT 0 COMMENT '客户数量',
                            `is_mvp` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是MVP',
                            `has_terraform` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否有Terraform脚本',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `solution_config` WRITE;

INSERT INTO `solution_config` (`name`, `intro`, `web_config`, `creator`, `module`, `gmt_create`)
VALUES
('自建IDP实现多账号SSO', 'IdP由集团管理，SLA无法满足云运维所需要的响应速度，IdP非标准化无法直接和阿里云配置。本方案通过自建IDP，一次性配置完成多账号的SSO，多账号登录态能在IDP侧统一切换，延续一致的试用体验，延续现有账号管理体系，不存在单独的个人云账号，避免离职等场景的安全隐患',
'{"SSO自动化配置":"/ssoConfig/ssoConfig.html"}', '程超', '身份权限', now()), 
('企业级公有云统一日志', '随着企业信息化的发展，面向云的IT基础设施越来越普遍，如何在庞大的IT基础设施面前管理软件的日志，成为IT稳定性保障的关键环节。本方案主要介绍如何使用SLS对阿里云、专有IDC以及第三方云场景下，进行企业级的日志管理',
'{"SLS自动化配置":"/slsAutoConfig/slsAutoConfig.html", "统一日志查看":"/slsView/slsView.html"}', '青弦', '运维管理', now());

UNLOCK TABLES;

DROP TABLE IF EXISTS `example`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `myAsk`;

CREATE TABLE `product` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `productid` varchar(100) COMMENT '产品id',
                            `application` varchar(100) NOT NULL COMMENT '应用',
                            `scenes` varchar(100) NOT NULL COMMENT '场景',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`productid`),
                             UNIQUE KEY `application-scenes` (`application`, `scenes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `product` (`productid`, `application`, `scenes`, `gmt_create`)
VALUES
('prod-bp165aqz2kx5df','application1','日常',now()),
('prod-bp1n4yee2544b1','application1','预发',now()),
('prod-bp1c6y7y2wj453','application1','线上',now()),
('25','application2','日常',now()),
('prod-bp1qbazd242511','application2','预发',now()),
('6','application2','线上',now()),
('65','application3','日常',now()),
('63','application3','预发',now()),
('7','application3','线上',now()),
('9','application3','scenes1',now());


CREATE TABLE `provisioned_product`(
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `examplename` varchar(100) NOT NULL COMMENT '实例名称',
                            `productid` varchar(100) NOT NULL COMMENT '产品id',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`examplename`),
                             FOREIGN KEY(productid) REFERENCES product(productid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `myAsk` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `starterName` varchar(100) NOT NULL COMMENT '申请人',
                            `processTime` varchar(100) NOT NULL COMMENT '申请时间',
                            `processId` varchar(100) NOT NULL COMMENT '流程实例ID',
                            `task` varchar(100) COMMENT '当前节点',
                            `processState` varchar(100) NOT NULL COMMENT '流程状态',
                            `processInfo` varchar(100) NOT NULL COMMENT '流程信息',
                            `cond` varchar(50) COMMENT '是否审批通过',
                            `processDefinitionId` varchar(100) NOT NULL COMMENT '流程定义ID',
                            `opinion` varchar(500) COMMENT '审批拒绝意见',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY (`processId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
