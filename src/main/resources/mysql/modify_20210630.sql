CREATE TABLE `solution_config` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `name` varchar(100) NOT NULL COMMENT '解决方案名称',
                            `intro` varchar(5000) DEFAULT NULL COMMENT '解决方案简介',
                            `web_config` varchar(1000) NOT NULL COMMENT '解决方案网页配置',
                            `creator` varchar(100) NOT NULL COMMENT '解决方案创建人',
                            `version` varchar(100) NOT NULL DEFAULT 'V0' COMMENT '解决方案版本',
                            `module` varchar(100) NOT NULL COMMENT '解决方案所属模块',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `solution_config` WRITE;

INSERT INTO `solution_config` (`name`, `intro`, `web_config`, `creator`, `version`, `module`, `gmt_create`)
VALUES
('自建IDP实现多账号SSO', 'IdP由集团管理，SLA无法满足云运维所需要的响应速度，IdP非标准化无法直接和阿里云配置。本方案通过自建IDP，一次性配置完成多账号的SSO，多账号登录态能在IDP侧统一切换，延续一致的试用体验，延续现有账号管理体系，不存在单独的个人云账号，避免离职等场景的安全隐患',
'{"SSO自动化配置":"/ssoConfig/ssoConfig.html"}', '程超', 'V0', '身份权限', now()), 
('企业级公有云统一日志', '随着企业信息化的发展，面向云的IT基础设施越来越普遍，如何在庞大的IT基础设施面前管理软件的日志，成为IT稳定性保障的关键环节。本方案主要介绍如何使用SLS对阿里云、专有IDC以及第三方云场景下，进行企业级的日志管理',
'{"SLS自动化配置":"/slsAutoConfig/slsAutoConfig.html", "统一日志查看":"/slsView/slsView.html"}', '青弦', 'V0', '运维管理', now());


UNLOCK TABLES;