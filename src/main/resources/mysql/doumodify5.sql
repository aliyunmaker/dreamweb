drop table IF EXISTS `productversion`;
create TABLE `productversion` (
                            `id` int(11) NOT NULL AUTO_INCREMENT comment '主键',
                            `productid` varchar(100) comment '产品id',
                            `application` varchar(100) NOT NULL comment '应用',
                            `scenes` varchar(100) NOT NULL comment '场景',
                            `productversionid` varchar(100) NOT NULL comment '产品版本ID',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON update CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`productversionid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into `productversion` (`productid`, `application`, `scenes`, `productversionid`, `gmt_create`)
VALUES
('prod-bp1c6y7y2wj453','application1','日常', 'pv-bp15gfhv2px6th', now()),
('prod-bp1c6y7y2wj453','application1','预发','pv-bp11vd4m26h6uh',now()),
('prod-bp1c6y7y2wj453','application1','线上','pv-bp151yxr2we4jw',now()),

('prod-bp1qbazd242511','application2','预发','asdasdasassd',now()),
('prod-bp18r7q127u45k','application2','线上','pv-bp1wendz2e962y',now()),
('prod-bp1p27wj2c94fg','application2','日常','pv-bp1zymve23b54q',now()),

('prod-bp1p27wj2c94fg','application3','预发','pv-bp1z87gw25a4zf',now()),
('prod-bp18r7q127u45k','application3','线上','pv-bp15e79d2614pw',now()),
('prod-bp18r7q127u45k','application3','日常','pv-bp1bjeut29963a',now());