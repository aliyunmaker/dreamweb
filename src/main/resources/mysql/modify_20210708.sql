ALTER TABLE `solution_config`
    ADD COLUMN `customer_num` int(11) NOT NULL DEFAULT 0 COMMENT '客户数量' AFTER `module`,
    ADD COLUMN `is_mvp` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是MVP' AFTER `customer_num`,
    DROP COLUMN `version`;