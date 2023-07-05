# 环境准备

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
dreamweb.aliyun_userid=
dreamweb.aliyun_sso_userid=
dreamweb.aws_userid=
dreamweb.idp_entityid=
dreamweb.logout_success_url=<your_logout_success_url>
```

