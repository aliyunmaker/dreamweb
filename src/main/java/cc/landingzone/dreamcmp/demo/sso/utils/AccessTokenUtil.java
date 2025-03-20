package cc.landingzone.dreamcmp.demo.sso.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cc.landingzone.dreamcmp.common.utils.JsonUtils;
import cc.landingzone.dreamcmp.demo.employeelist.utils.OkHttpClientUtils;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.util.Assert;

import static cc.landingzone.dreamcmp.demo.employeelist.utils.OkHttpClientUtils.client;

/**
 * @author weijieyang.wjy
 * @date 2025/3/17
 */
public class AccessTokenUtil {

    public static final String APP_DING = "DingDing";
    public static final String APP_FEISHU = "FeiShu";

    // 钉钉
    public static final String SSO_DING_ID = "dingfydpspv6n8tjbaz0";
    public static final String SSO_DING_SECRET = "P6m_2PymH9VdyxK8LdAgUIr9O98k5p3w3yJ8c2svq07e47T5rQL43dy4e6NA46eL";

    // 飞书
    public static final String SSO_FEISHU_ID = "cli_a745676178fc100b";
    public static final String SSO_FEISHU_SECRET = "R5pEFk74U36AGGFLniqPxdHFcJRH8ggt";

    public static String getUserAccessToken(String appType, String code)
        throws Exception {
        if (appType.equals(APP_DING)) {
            return getDingAccessToken(code);

        } else if (appType.equals(APP_FEISHU)) {
            return getFeishuAccessToken(code);

        } else {
            return getAliAccessToken(code);
        }
    }

    private static String getAliAccessToken(String code) throws Exception {
        // 构建请求体
        RequestBody requestBody = new FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", code)
            .add("client_id", "app_m7erzezs3r2xwkzwcy7hb2fgfu")
            .add("client_secret", "CSDwA2deZymrA6FADDoX1XY2yNMyRF1xEH1JrqL1D31gQb")
            //.add("redirect_uri", "https://qsqnc5cn.aliyunidaas.com/login/go/app_m7erzezs3r2xwkzwcy7hb2fgfu\n")
            .build();

        String url = "https://oauth.aliyun.com/v1/token";
        String result = OkHttpClientUtils.post(url, null, JsonUtils.toJsonString(requestBody));
        JSONObject resultJson = JSONObject.parseObject(result);
        return resultJson.getString("accessToken");
    }

    private static String getDingAccessToken(String code) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("clientId", SSO_DING_ID);
        map.put("clientSecret", SSO_DING_SECRET);
        map.put("code", code);
        map.put("grantType", "authorization_code");

        String url = "https://api.dingtalk.com/v1.0/oauth2/userAccessToken";
        String result = OkHttpClientUtils.post(url, null, JsonUtils.toJsonString(map));
        JSONObject resultJson = JSONObject.parseObject(result);
        return resultJson.getString("accessToken");
    }

    private static String getFeishuAccessToken(String code) throws Exception {
        String appAccessToken = getAppAccessToken();
        Assert.hasText(code, "code can not be null!");
        Assert.hasText(appAccessToken, "appAccessToken can not be null!");

        HashMap<String, String> headMap = new HashMap<>() {{
            put("Authorization", "Bearer " + appAccessToken);
        }};
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "authorization_code");
        map.put("code", code);
        String url = "https://open.feishu.cn/open-apis/authen/v1/oidc/access_token";
        String result = OkHttpClientUtils.post(url, headMap, JsonUtils.toJsonString(map));
        JSONObject jsonResult = JSON.parseObject(result);
        if (0 != (jsonResult.getInteger("code"))) {
            throw new RuntimeException(result);
        }
        return jsonResult.getJSONObject("data").getString("access_token");
    }

    // 获取飞书app access token
    private static String getAppAccessToken() throws Exception {
        String url = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("app_id", SSO_FEISHU_ID);
        bodyMap.put("app_secret", SSO_FEISHU_SECRET);
        String result = OkHttpClientUtils.post(url, null, JsonUtils.toJsonString(bodyMap));
        JSONObject jsonResult = JSON.parseObject(result);
        if (0 != (jsonResult.getInteger("code"))) {
            throw new RuntimeException(result);
        }
        return jsonResult.getString("app_access_token");
    }

    public static void main(String[] args) throws Exception {
        String access = AccessTokenUtil.getUserAccessToken(AccessTokenUtil.APP_FEISHU, "aFKn3yDzFFbD4HzHGJ8606E0ww0FHc4B");
        System.out.println(access);
    }
}
