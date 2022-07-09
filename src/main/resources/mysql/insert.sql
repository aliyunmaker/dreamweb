insert into `product` (`servicecatalog_product_id`, `product_name`, `gmt_create`)
VALUES
('prod-bp1c6y7y2wj453','DEMO-创建ECS（选择VPC）',now()),
('prod-bp1qbazd242511','sdffedxx',now()),
('prod-bp18r7q127u45k','DEMO-创建VPC+ECS',now()),
('prod-bp1p27wj2c94fg','DEMO-创建RAM角色',now());


insert into `product_version` (`product_id`, `app`, `environment`, `servicecatalog_product_version_id`, `gmt_create`)
VALUES
(1,'application1','日常', 'pv-bp15gfhv2px6th', now()),
(1,'application1','预发','pv-bp11vd4m26h6uh',now()),
(1,'application1','线上','pv-bp151yxr2we4jw',now()),

(2,'application2','预发','asdasdasassd',now()),
(3,'application2','线上','pv-bp1wendz2e962y',now()),
(4,'application2','日常','pv-bp1zymve23b54q',now()),

(4,'application3','预发','pv-bp1z87gw25a4zf',now()),
(3,'application3','线上','pv-bp15e79d2614pw',now()),
(3,'application3','日常','pv-bp1bjeut29963a',now());


-- INSERT INTO `userproduct` (`productid`, `productname`, `username`, `portfolioid`, `gmt_create`)
-- VALUES
-- ('prod-bp18r7q127u45k','DEMO-创建VPC+ECS','admin','port-bp1yt7582gn4p7',now()),
-- ('prod-bp18r7q127u45k','DEMO-创建VPC+ECS','dou','port-bp1yt7582gn4p7',now()),
-- ('prod-bp1c6y7y2wj453','创建ECS','admin','port-bp1yt7582gn4p7',now()),
-- ('prod-bp1c6y7y2wj453','创建ECS','dou','port-bp1yt7582gn4p7',now()),
-- ('prod-bp1p27wj2c94fg','创建RAM角色','admin','port-bp1yt7582gn4p7',now()),
-- ('prod-bp1p27wj2c94fg','创建RAM角色','dou','port-bp1yt7582gn4p7',now());