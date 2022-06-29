drop table IF EXISTS `product`;
create TABLE `product` (
                            `id` int(11) NOT NULL AUTO_INCREMENT comment '主键',
                            `productid` varchar(100) comment '产品id',
                            `application` varchar(100) NOT NULL comment '应用',
                            `scenes` varchar(100) NOT NULL comment '场景',
                            `productname` varchar(100) NOT NULL comment '产品名称',
                            `productversionid` varchar(100) NOT NULL comment '产品版本ID',
                            `portfolioid` varchar(100) NOT NULL comment '产品组合ID',
                            `gmt_create` datetime DEFAULT NULL,
                            `gmt_modified` timestamp NULL DEFAULT NULL ON update CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`productversionid`),
                             UNIQUE KEY `productversionid-portfolioid` (`productversionid`, `portfolioid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into `product` (`productid`, `application`, `scenes`,`productname`, `productversionid`, `portfolioid`, `gmt_create`)
VALUES
('prod-bp1c6y7y2wj453','application1','日常','asdasda', 'pv-bp15gfhv2px6th', 'port-bp1u5d8h21c62y', now()),
('prod-bp1c6y7y2wj453','application1','预发','DEMO-创建ECS（选择VPC）','pv-bp11vd4m26h6uh','port-bp193yjz2qv4zu',now()),
('prod-bp1c6y7y2wj453','application1','线上','DEMO-创建ECS（选择VPC）','pv-bp151yxr2we4jw','port-bp193yjz2qv4zu',now()),
('25','application2','日常','dfgqdasdas','asdad','assdsa',now()),
('prod-bp1qbazd242511','application2','预发','sdffedxx','asdasdasassd','asdasdas',now()),
('6','application2','线上','dfgewasda','asdasdasdas','sadasd',now()),
('63','application3','预发','sfsdasd','asdasdasd','asdsadas',now()),
('prod-bp18r7q127u45k','application3','线上','DEMO-创建ECS','pv-bp15e79d2614pw','port-bp1yt7582gn4p7',now()),
('9','application3','scenes1','dfwefwefef','asdasdas','asdsada',now());