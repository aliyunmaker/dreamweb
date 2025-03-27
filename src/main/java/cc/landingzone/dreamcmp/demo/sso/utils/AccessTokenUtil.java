package cc.landingzone.dreamcmp.demo.sso.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cc.landingzone.dreamcmp.common.utils.JsonUtils;
import cc.landingzone.dreamcmp.demo.employeelist.utils.OkHttpClientUtils;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * @author weijieyang.wjy
 * @date 2025/3/17
 */
@Configuration
public class AccessTokenUtil {

    @Value("${dreamcmp.workshop.sso.dingding_id}")
    private static String sso_ding_id;

    @Value("${dreamcmp.workshop.sso.dingding_secret}")
    private static String sso_ding_secret;

    @Value("${dreamcmp.workshop.sso.feishu_id}")
    private static String sso_feishu_id;

    @Value("${dreamcmp.workshop.sso.feishu_secrert}")
    private static String sso_feishu_secret;

    public static final String APP_DING = "DingDing";
    public static final String APP_FEISHU = "FeiShu";

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
        map.put("clientId", sso_ding_id);
        map.put("clientSecret", sso_ding_secret);
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
        bodyMap.put("app_id", sso_feishu_id);
        bodyMap.put("app_secret", sso_feishu_secret);
        String result = OkHttpClientUtils.post(url, null, JsonUtils.toJsonString(bodyMap));
        JSONObject jsonResult = JSON.parseObject(result);
        if (0 != (jsonResult.getInteger("code"))) {
            throw new RuntimeException(result);
        }
        return jsonResult.getString("app_access_token");
    }
}
