# 环境准备

### 数据库
database: dreamdb
create sql: src/main/resources/mysql/database_dreamdb.sql

### 单点登录证书生成

```shell
#create the keypair
openssl req -new -x509 -days 3652 -nodes -out saml.crt -keyout saml.pem

#convert the private key to pkcs8 format
openssl pkcs8 -topk8 -inform PEM -outform DER -in saml.pem -out saml.pkcs8 -nocrypt
```

将生成的saml.crt saml.pem  saml.pkcs8 三个文件放在src/main/resources/ssocert/ 目录下


### 阿里云AK
在src/main/resources/目录下新建dreamweb.properties,内容如下
```properties
env_online=false
aliyun_accesskeyid=<your_aliyun_accesskeyid>
aliyun_accesskeysecret=<your_aliyun_accesskeysecret>
```

### 启动类(springboot)
cc.landingzone.dreamweb.DreamwebApplication
