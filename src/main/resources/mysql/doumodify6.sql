DROP TABLE IF EXISTS `userrole`;
CREATE TABLE `userrole` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `roleid` int(11) NOT NULL COMMENT '角色ID',
                            `username` varchar(100) NOT NULL COMMENT '用户名',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `user-role` (`username`, `roleid`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;