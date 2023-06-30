package cc.landingzone.dreamweb;

import cc.landingzone.dreamweb.common.CommonConstants;
import com.aliyun.tea.*;

public class LogCcapi {
    public static void main(String[] args_) throws Exception {
        String accessKeyId = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");

        com.aliyun.cloudcontrol20220830.Client client = createClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        String requestPath = "/api/v1/providers/Aliyun/products/SLS/resources/Project";
        java.util.Map<String, String> body = TeaConverter.buildMap(
            new TeaPair("Description", "cctest"),
            new TeaPair("ProjectName", "cctest-project")
        );
        com.aliyun.cloudcontrol20220830.models.CreateResourceRequest createResourceRequest = new com.aliyun.cloudcontrol20220830.models.CreateResourceRequest()
                .setRegionId("cn-hangzhou")
                .setBody(body);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        client.createResourceWithOptions(requestPath, createResourceRequest, headers, runtime);
    }

    public static com.aliyun.cloudcontrol20220830.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "cloudcontrol.aliyuncs.com";
        return new com.aliyun.cloudcontrol20220830.Client(config);
    }
}