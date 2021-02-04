## 通过token实现自动登录

本文介绍了如何通过token实现企业客户平台到DreamWeb系统的自动登录。

### 登录原理

1. 用户在企业客户平台登录后，企业客户平台通过在DreamWeb系统的登录地址后面添加token参数，生成一个跳转地址；
2. 用户点击该地址，跳转到DreamWeb系统，系统自动解析token，识别用户身份，帮助用户自动登录。

注：

1. Token是经过BASE64编码后的字符串，token本身包含4个部分，分别是：

	| 字段名称 | 字段类型 | 字段含义 | 备注 |
	| ---- | ---- | ---- | ---- | ---- |
	| accessKeyId | String | 通过DreamWeb系统申请的AccessKey ID | 用于标识企业客户平台 |
	| timestamp | Long | 当前系统时间，Unix风格的13位时间戳，例如：1612137600000 | 用于判断token是否过期 |
	| loginName | String | 用户的登录名 | 用户在企业中的唯一标识 |
	| signature | String | 签名 | 计算得到的签名字符串 |

2. Token在生成后30分钟内有效，并且只能使用一次，用户登出后如需再次登录，需要企业客户平台重新生成token和跳转地址。

### 如何生成跳转地址

#### 1. 准备工作

企业客户平台需要在DreamWeb系统中申请API账号，API账号包含一组AccessKey ID和AccessKey Secret。AccessKey ID用于标识企业客户平台，AccessKey Secret是用于计算签名字符串以及服务端验证签名字符串的密钥，必须严格保密。

#### 2. 生成token

本小节介绍了如何通过代码生成token。

【生成token的步骤】

1. 创建参数表，填写accessKeyId、timestamp、loginName的值；
2. 按照参数名称的字典顺序对请求中所有参数（不包括signature参数）进行排序；
3. 将参数的名称和值用"="进行连接, 得到如同"key=value"的字符串；
4. 将上一步中"="连接得到的参数组合按顺序依次用"&"进行连接, 得到如同"key1=value1&key2=value2..."的待签名字符串；
4. 按照RFC2104的定义，计算待签名字符串的HMAC值，使用的哈希算法是SHA1，对计算结果进行HEX编码，得到签名字符串，计算过程中需要用到AccessKey Secret；
5. 将签名拼接到待签名字符串的后面（key1=value1&...&signature={signature}），得到完整的参数字符串；
6. 对参数字符串进行BASE64编码，得到的结果即为自动登录所需的token。

```
accessKeySecret: {accessKeySecret}
needSignature: accessKeyId={accessKeyId}&loginName={loginName}&timestamp={timestamp}

signature = Hex(HMAC-SHA1(accessKeySecret, UTF8_Byte(needSignature)))
token = BASE64(needSignature&signature={signature})

示例:
accessKeySecret: testAccessKeySecret
needSignature: accessKeyId=testAccessKeyId&loginName=testLoginName&timestamp=1612137600000

计算后的signature: e50f4c7c4704586a8c42bb892277186fa5eef153
计算后的token: YWNjZXNzS2V5SWQ9dGVzdEFjY2Vzc0tleUlkJmxvZ2luTmFtZT10ZXN0TG9naW5OYW1lJnRpbWVzdGFtcD0xNjEyMTM3NjAwMDAwJnNpZ25hdHVyZT1lNTBmNGM3YzQ3MDQ1ODZhOGM0MmJiODkyMjc3MTg2ZmE1ZWVmMTUz

```

【代码中的变量说明】

| 变量名称 | 变量类型 | 变量含义 | 占位符 |
| ---- | ---- | ---- | ---- | ---- | 
| ACCESS\_KEY_ID | String | 通过DreamWeb系统申请的AccessKey ID | \<your\_dreamWeb_accessKeyId> |
| ACCESS\_KEY_SECRET | String | 通过DreamWeb系统申请的AccessKey Secret | \<your\_dreamWeb_accessKeySecret> |
| loginName | String | 用户的登录名 | \<your\_user_loginName> |

【代码示例】

AutoLoginDemo.class

```java
package cc.landingzone.dreamweb.demo;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import cc.landingzone.dreamweb.utils.SignatureUtils;

/**
 * Demo: 企业客户平台跳转到DreamWeb, 通过token实现自动登录<br/>
 *
 * <br/>
 * <p>
 *     本样例中通过代码生成自动登录所需的token<br/>
 *     <br/>
 *     企业客户平台还需要按照以下格式拼接成实际可以跳转的地址：<br/>
 *     scheme://主机名[:端口号]+/autoLogin?token={token}<br/>
 *     例如: http://localhost:8080/autoLogin?token={token}
 * </p>
 *
 * @author merc-bottle
 * @date 2021/02/03
 */
public class AutoLoginDemo {

    /**
     * 通过DreamWeb系统申请的 AccessKey ID, 用于标识企业客户平台
     */
    private static final String ACCESS_KEY_ID = "testAccessKeyId";
    /**
     * 通过DreamWeb系统申请的 AccessKey Secret, 用于计算签名字符串以及服务端验证签名字符串, 必须严格保密
     */
    private static final String ACCESS_KEY_SECRET = "testAccessKeySecret";

    public static void main(String[] args) {
        // 用户的loginName
        String loginName = "testLoginName";
        String token = buildAutoLoginToken(loginName);
        System.out.println(token);
    }

    /**
     * 生成自动登录需要的Token
     *
     * @param loginName
     * @return
     */
    public static String buildAutoLoginToken(String loginName) {
        // 创建参数表
        Map<String, String> paramMap = new HashMap<>();
        // AccessKey ID
        paramMap.put("accessKeyId", ACCESS_KEY_ID);
        // 13位时间戳
        paramMap.put("timestamp", "1612137600000");
        // 用户的loginName
        paramMap.put("loginName", loginName);

        // 按照参数名称的字典顺序对请求中所有参数(不包括signature参数)进行排序
        String[] keyArray = paramMap.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);

        // 将参数的名称和值用"="进行连接, 得到如同"key=value"的字符串
        // 将"="连接得到的参数组合按顺序依次用"&"进行连接, 得到如同"key1=value1&key2=value2..."的待签名字符串
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (String key : keyArray) {
            if (!isFirst) {
                stringBuilder.append("&");
            } else {
                isFirst = false;
            }
            stringBuilder.append(key).append("=").append(paramMap.get(key));
        }
        String needSign = stringBuilder.toString();

        // 计算待签名字符串的HMAC值，使用的哈希算法是SHA1
        // 对计算结果进行HEX编码，得到签名字符串
        // 计算过程中需要用到AccessKey Secret
        String signature = SignatureUtils.generateSignature(needSign, ACCESS_KEY_SECRET);
        System.out.println(signature);

        // 拼接签名, 得到完整的参数字符串
        String params = needSign + "&signature=" + signature;

        // 对参数字符串进行BASE64编码, 得到token
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(params.getBytes(StandardCharsets.UTF_8));
        return token;
    }
}
```

SignatureUtils.class

```java
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class SignatureUtils {

    private static Logger logger = LoggerFactory.getLogger(SignatureUtils.class);

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final long ONE_SECOND = 1000L;
    private static final long ONE_MINUTE = 60 * ONE_SECOND;
    private static final long THIRTY_MINUTES = 30 * ONE_MINUTE;

    /**
     * 计算签名
     *
     * @param needSign 待签名字符串
     * @param accessKeySecret 通过DreamWeb系统申请的AccessKey Secret
     * @return
     */
    public static String generateSignature(String needSign, String accessKeySecret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec signingKey = new SecretKeySpec(accessKeySecret.getBytes(DEFAULT_CHARSET), mac.getAlgorithm());
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(needSign.getBytes(DEFAULT_CHARSET));
            return toHexString(rawHmac);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 把二进制转化为小写的十六进制
     *
     * @param bytes
     * @return
     */
    public static String toHexString(final byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
```

#### 3. 拼接跳转地址

企业客户平台还需要按照以下格式，将DreamWeb系统的登录地址，和上一步中得到的token，拼接成实际可以跳转的地址：

```
格式: (http/https)://{DreamWeb系统的域名}[:端口号]/autoLogin?token={token}

示例: https://dreamweb.xx.com/autoLogin?token=YWNjZXNzS2V5SWQ9dGVzdEFjY2Vzc0tleUlkJmxvZ2luTmFtZT10ZXN0TG9naW5OYW1lJnRpbWVzdGFtcD0xNjEyMTM3NjAwMDAwJnNpZ25hdHVyZT1lNTBmNGM3YzQ3MDQ1ODZhOGM0MmJiODkyMjc3MTg2ZmE1ZWVmMTUz
```