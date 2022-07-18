drop table IF EXISTS `provisioned_product`;
drop table IF EXISTS `product_version`;
drop table IF EXISTS `application`;
drop table IF EXISTS `user_product_associate`;
drop table IF EXISTS `product`;

create TABLE `product` (
                            `id` int(11) NOT NULL AUTO_INCREMENT comment '主键',
                            `servicecatalog_product_id` varchar(100) comment '服务目录的产品ID',
                            `product_name` varchar(100) NOT NULL comment '产品名称',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON update CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_servicecatalog_product_id` (`servicecatalog_product_id`),
                             UNIQUE KEY `uk_product_name` (`product_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create TABLE `product_version` (
                            `id` int(11) NOT NULL AUTO_INCREMENT comment '主键',
                            `servicecatalog_product_version_id` varchar(100) NOT NULL comment '服务目录的产品版本ID',
                            `product_id` int(11) comment '产品ID',
                            `app` varchar(100) NOT NULL comment '应用',
                            `environment` varchar(100) NOT NULL comment '环境',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON update CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_servicecatalog_product_version_id` (`servicecatalog_product_version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create TABLE `provisioned_product`(
                            `id` int(11) NOT NULL AUTO_INCREMENT comment '主键',
                            `servicecatalog_provisioned_product_id` varchar(100) NOT NULL comment '服务目录的产品实例ID',
                            `provisioned_product_name` varchar(100) NOT NULL comment '实例名称',
                            `product_id` int(11) comment '产品ID',
                            `role_id` int(11) NOT NULL comment '角色ID',
                            `starter_id` int(11) comment '实例申请人ID',
                            `status` varchar(50) NOT NULL comment '实例状态',
                            `parameter` varchar(1000) comment '申请参数',
                            `outputs` varchar(2000) comment '输出',
                            `create_time` varchar(100) comment '产品创建时间',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON update CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_servicecatalog_provisioned_product_id` (`servicecatalog_provisioned_product_id`),
                             UNIQUE KEY `uk_provisioned_product_name` (`provisioned_product_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create TABLE `application` (
                         `id` int(11) NOT NULL AUTO_INCREMENT comment '主键',
                         `process_id` varchar(100) comment '流程实例ID',
                         `starter_id` int(11) NOT NULL comment '申请人ID',
                         `role_id` int(11) NOT NULL comment '角色ID',
                         `product_version_id` int(11) comment '产品版本ID',
                         `product_id` int(11) NOT NULL comment '产品ID',
                         `provisioned_product_name` varchar(100) NOT NULL comment '产品实例名称',
                         `create_time` varchar(100) NOT NULL comment '申请创建时间',
                         `task` varchar(100) comment '申请当前节点',
                         `process_state` varchar(100) NOT NULL comment '流程状态',
                         `plan_result` varchar(1000) comment '预检结果',
                         `parameters` varchar(1000) NOT NULL comment '流程信息',
                         `servicecatalog_plan_id` varchar(100) NOT NULL comment '启动计划ID',
                         `region` varchar(100) comment '地域',
                         `cond` varchar(50) comment '是否审批通过',
                         `process_definition_id` varchar(100) NOT NULL comment '流程定义ID',
                         `opinion` varchar(500) comment '审批拒绝意见',
                         `gmt_create` datetime DEFAULT NULL,
                         `gmt_modified` timestamp NULL DEFAULT NULL ON update CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_process_id` (`process_id`),
                         UNIQUE KEY `uk_servicecatalog_plan_id` (`servicecatalog_plan_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create TABLE `user_product_associate` (
                            `id` int(11) NOT NULL AUTO_INCREMENT comment '主键',
                            `product_id` int(11) NOT NULL comment '产品ID',
                            `user_id` int(11) NOT NULL comment '用户ID',
                            `servicecatalog_portfolio_id` varchar(100) NOT NULL comment '服务目录的产品组合ID',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON update CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_user_id_product_id` (`user_id`, `product_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;