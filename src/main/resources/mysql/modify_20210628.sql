DELETE FROM `system_config`
WHERE `config_name` = 'stsHost';

INSERT INTO `system_config` (`config_name`, `config_value`, `comment`, `changeable`, `gmt_create`)
VALUES
('useVpc','false','是否使用私网地址',TRUE,now());