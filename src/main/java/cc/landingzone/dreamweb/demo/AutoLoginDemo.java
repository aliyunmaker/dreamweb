package cc.landingzone.dreamweb.demo;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import cc.landingzone.dreamweb.utils.SignatureUtils;

/**
 * Demo: 通过token实现企业客户平台到DreamWeb系统的自动登录<br/>
 *
 * <br/>
 * <p>
 *     本样例中通过代码生成自动登录所需的token<br/>
 *     <br/>
 *     将DreamWeb系统的登录地址, 和上一步中得到的token, 拼接成实际可以跳转的地址: <br/>
 *     (http/https)://{DreamWeb系统的域名}[:端口号]/autoLogin?token={token}
 * </p>
 *
 * @author merc-bottle
 * @date 2021/02/03
 */
public class AutoLoginDemo {

    /**
     * 通过DreamWeb系统申请的 AccessKey ID, 用于标识企业客户平台
     */
    private static final String ACCESS_KEY_ID = "<your_dreamWeb_accessKeyId>";
    /**
     * 通过DreamWeb系统申请的 AccessKey Secret, 用于计算签名字符串以及服务端验证签名字符串, 必须严格保密
     */
    private static final String ACCESS_KEY_SECRET = "<your_dreamWeb_accessKeySecret>";

    public static void main(String[] args) {
        // 用户的loginName
        String loginName = "<your_user_loginName>";
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
        paramMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
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

        // 计算待签名字符串的HMAC值, 使用的哈希算法是SHA1
        // 对计算结果进行HEX编码, 得到签名字符串
        // 计算过程中需要用到AccessKey Secret
        String signature = SignatureUtils.generateSignature(needSign, ACCESS_KEY_SECRET);

        // 拼接签名, 得到完整的参数字符串
        String params = needSign + "&signature=" + signature;

        // 对参数字符串进行BASE64编码, 得到token
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(params.getBytes(StandardCharsets.UTF_8));
        return token;
    }
}
