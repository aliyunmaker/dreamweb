delete from product;
delete from product_version;
delete from user_product_associate;

insert into `product` (`servicecatalog_product_id`, `product_name`, `gmt_create`)
VALUES
('prod-bp18r7q127u45k','DEMO-创建VPC+ECS',now()),
('prod-bp1vj7xk28q4b6','DEMO-创建ECS（选择已有VPC）',now()),
('prod-bp1p27wj2c94fg','DEMO-创建RAM角色',now());


insert into `product_version` (`product_id`, `app`, `environment`, `servicecatalog_product_version_id`, `gmt_create`)
VALUES
(1,'demo-app','线上', 'pv-bp1wendz2e962y', now()),
(1,'demo-app','预发','pv-bp1du5jv2ub7w8',now()),

(2,'demo-app','线上','pv-bp1r85bt2647un',now()),
(2,'demo-app','预发','pv-bp1fc5f22rt72g',now()),

(3,'demo-app','线上','pv-bp1xc5md2767vm',now());


insert into `user_product_associate` (`product_id`, `user_id`, `servicecatalog_portfolio_id`, `gmt_create`)
VALUES
(1, 1,'port-bp1yt7582gn4p7',now()),
(2, 1,'port-bp1yt7582gn4p7',now()),
(3, 1,'port-bp1yt7582gn4p7',now());