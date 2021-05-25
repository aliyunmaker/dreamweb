ALTER TABLE `user`
    ADD COLUMN `login_method` varchar(50) NULL COMMENT '登录类型' AFTER `login_name`
;



# 数据订正：登录方式为null的记录
# ------------------------------------------------------------
# 1. 登录过的用户，将登录方式设置为登录记录中的登录方式
UPDATE user u, login_record lr
SET u.login_method = lr.login_method
WHERE u.login_method IS NULL AND u.login_name = lr.login_name;

# 2. 没有登录过的用户，将登录方式设置为"NORMAL_LOGIN"
UPDATE user
SET login_method="NORMAL_LOGIN"
WHERE login_method IS NULL AND login_name NOT IN (SELECT DISTINCT login_name
                                                  FROM login_record);