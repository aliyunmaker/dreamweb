## 概述

本项目是云管 CMP 系统的 Demo，用来演示 CMP 系统和阿里云的集成，包含：SSO、资源供给、日志与监控等功能。

```diff
- 特别注意：该项目只是演示 Demo（Code Sample），不建议您直接在线上环境使用。
```

## 环境准备

### 单点登录证书生成

```shell
#create the keypair
openssl req -new -x509 -days 3652 -nodes -out saml.crt -keyout saml.pem

#convert the private key to pkcs8 format
openssl pkcs8 -topk8 -inform PEM -outform DER -in saml.pem -out saml.pkcs8 -nocrypt
```

将生成的saml.crt saml.pem  saml.pkcs8 三个文件放在src/main/resources/ssocert/ 目录下

### 启动类(springboot)
cc.landingzone.dreamweb.DreamwebApplication

### 配置
application.properties

```
#spring.profiles.active=prod

# thymeleaf
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.check-template-location=true
spring.thymeleaf.suffix=.html
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.mode=HTML
spring.thymeleaf.cache=false
spring.thymeleaf.servlet.content-type=text/html

logging.level.cc.landingzone.dreamweb.dao=INFO

server.servlet.encoding.charset=utf-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

# dreamweb
dreamweb.env_online=false
# company account
dreamweb.aliyun_accesskeyid=
dreamweb.aliyun_accesskeysecret=
dreamweb.idp_entityid=
dreamweb.logout_success_url=<your_logout_success_url>
dreamcmp.scim_key=
dreamcmp.login_username= 
dreamcmp.login_password= 
dreamcmp.dkms_instance_id= 


# product account
dreamcmp.aliyun_accesskeyid=
dreamcmp.aliyun_accesskeysecret=
# test account
dreamcmp.aliyun_testaccount_accesskeyid=
dreamcmp.aliyun_testaccount_accesskeysecret=

dreamcmp.scim_key=
dreamcmp.idp_entityid=

dreamcmp.login_user={"admin":"admin","guest":"guest"}

# kms instance id
dreamcmp.dkms_instance_id=

# workshop
dreamcmp.workshop.ecs_instance_role=
# oss
dreamcmp.workshop.oss_region=
dreamcmp.workshop.oss_bucket=
# sls
dreamcmp.workshop.sls_project=
dreamcmp.workshop.sls_logstore=
# tvm
dreamcmp.workshop.assume_role_arn=
```

