DROP TABLE IF EXISTS `product`;

CREATE TABLE `product` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `productid` varchar(100) NOT NULL COMMENT '产品id',
                            `application` varchar(100) NOT NULL COMMENT '应用',
                            `scenes` varchar(100) NOT NULL COMMENT '场景',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY (`productid`),
                             UNIQUE KEY `application-scenes` (`application`, `scenes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `product` (`productid`, `application`, `scenes`)
VALUES
('prod-bp165aqz2kx5df','application1','日常'),
('prod-bp1n4yee2544b1','application1','预发'),
('prod-bp1c6y7y2wj453','application1','线上'),
('25','application2','日常'),
('77','application2','预发'),
('6','application2','线上'),
('65','application3','日常'),
('63','application3','预发'),
('7','application3','线上'),
('9','application3','scenes1');
