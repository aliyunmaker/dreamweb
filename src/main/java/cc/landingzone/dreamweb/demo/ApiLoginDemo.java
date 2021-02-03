package cc.landingzone.dreamweb.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cc.landingzone.dreamweb.utils.SignatureUtils;

/**
 * Demo: 客户平台跳转到dreamWeb, 通过API实现免登<br/>
 *
 * <br/>
 * <p>
 *     本样例中通过代码生成 API登录所需的URI(以下称URI)<br/>
 *     然后按照以下格式拼接成实际的地址：<br/>
 *     scheme://主机名[:端口号]+URI，例如: http://localhost:8080/apiLogin?key1=value1&key2=value2...
 * </p>
 *
 * @author merc-bottle
 * @date 2021/02/03
 */
public class ApiLoginDemo {

    /**
     * 通过dreamWeb申请的 AccessKey ID, 用于标识用户, 会在参数中传递
     */
    private static final String ACCESS_KEY_ID = "<your_dreamWeb_accessKeyId>";
    /**
     * 通过dreamWeb申请的 AccessKey Secret, 用于验证用户的密钥, 必须保密
     */
    private static final String ACCESS_KEY_SECRET = "<your_dreamWeb_accessKeySecret>";

    public static void main(String[] args) {
        // 用户的loginName
        String loginName = "<your_user_loginName>";
        String apiLoginUri = buildApiLoginRequestUri(loginName);
        System.out.println(apiLoginUri);
    }

    /**
     * 生成API登录需要的URI
     */
    public static String buildApiLoginRequestUri(String loginName) {
        // 创建参数表
        Map<String, String> params = new HashMap<>();
        // AccessKey ID
        params.put("accessKeyId", ACCESS_KEY_ID);
        // 13位时间戳
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        // 用户的loginName
        params.put("loginName", loginName);

        // 按照参数名称的字典顺序对请求中所有参数(不包括signature参数本身)进行排序
        String[] keyArray = params.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);

        // 将参数名称和值用"="进行连接,得到形如"key=value"的字符串
        // 将"="连接得到的参数组合按顺序依次用"&"进行连接,得到形如"key1=value1&key2=value2..."的字符串
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (String key : keyArray) {
            if (!isFirst) {
                stringBuilder.append("&");
            } else {
                isFirst = false;
            }
            stringBuilder.append(key).append("=").append(params.get(key));
        }
        String needSignature = stringBuilder.toString();

        // 使用AccessKey Secret计算签名
        String signature = SignatureUtils.generateSignature(needSignature, ACCESS_KEY_SECRET);
        return "/apiLogin?" + needSignature + "&signature=" + signature;
    }
}
