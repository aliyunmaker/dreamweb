ALTER TABLE `user`
    ADD COLUMN `login_method` varchar(50) NULL COMMENT '登录类型' AFTER `login_name`
;