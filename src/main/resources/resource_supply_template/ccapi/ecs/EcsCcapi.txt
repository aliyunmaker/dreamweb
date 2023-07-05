package cc.landingzone.dreamweb;

import com.aliyun.tea.TeaConverter;
import com.aliyun.tea.TeaPair;

public class EcsCcapi {
    public static void main(String[] args_) throws Exception {
        String accessKeyId = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");

        com.aliyun.cloudcontrol20220830.Client client = createClient(accessKeyId, accessKeySecret);
        String requestPath = "/api/v1/providers/Aliyun/products/ECS/resources/Instance";
        java.util.Map<String, Object> body = TeaConverter.buildMap(
                new TeaPair("InstanceType", "ecs.n2.small"),
                new TeaPair("SecurityGroupId", "sg-bp103rdxtizwmfam0tfa"),
                new TeaPair("VpcAttributes", TeaConverter.buildMap(
                        new TeaPair("VSwitchId", "vsw-bp1tipegxihb0brq0qy61")
                )),
                new TeaPair("SystemDisk", TeaConverter.buildMap(
                        new TeaPair("Category", "cloud_ssd")
                )),
                new TeaPair("DataDisk", java.util.Arrays.asList(
                        TeaConverter.buildMap(
                                new TeaPair("Category", "cloud_ssd"),
                                new TeaPair("Size", 100)
                        )
                )),
                new TeaPair("HostName", "ECS-test"),
                new TeaPair("Tags", java.util.Arrays.asList(
                        TeaConverter.buildMap(
                                new TeaPair("TagKey", "application"),
                                new TeaPair("TagValue", "application1")
                        ),
                        TeaConverter.buildMap(
                                new TeaPair("TagKey", "environmentType"),
                                new TeaPair("TagValue", "product")
                        )
                )),
                new TeaPair("Password", "ECS@test1234"),
                new TeaPair("InternetMaxBandwidthOut", 10),
                new TeaPair("ImageId", "ubuntu_18_04_64_20G_alibase_20190624.vhd"),
                new TeaPair("PaymentType", "PostPaid"),
                new TeaPair("Status", "Running")
        );
        com.aliyun.cloudcontrol20220830.models.CreateResourceRequest createResourceRequest = new com.aliyun.cloudcontrol20220830.models.CreateResourceRequest()
                .setBody(body)
                .setRegionId("cn-hangzhou");
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
