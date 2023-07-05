package cc.landingzone.dreamweb;

import com.alibaba.fastjson.JSON;
import com.aliyun.sls20201230.Client;
import com.aliyun.sls20201230.models.CreateProjectRequest;
import com.aliyun.tag20180828.models.TagResourcesRequest;
import com.aliyun.teautil.models.RuntimeOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Sls {

    public static void main(String[] args) throws Exception {
        String accountId = System.getenv("ALIBABA_CLOUD_ACCOUNT_ID");
        String accessKeyId = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");

        String projectName = "dreamwebtest";
        // 创建logProject
        Client client = createSlsClient(accessKeyId,accessKeySecret);
        RuntimeOptions runtime = new RuntimeOptions();
        CreateProjectRequest createProjectRequest = new CreateProjectRequest()
                .setDescription("description")
                .setProjectName(projectName);
        Map<String, String> headers = new HashMap<>();
        client.createProjectWithOptions(createProjectRequest, headers, runtime);

        // 添加标签
        com.aliyun.tag20180828.Client tagClient = createTagClient(accessKeyId, accessKeySecret);
        Map<String, String> tags = new HashMap<>();
        tags.put("application", "application1");
        tags.put("environmentType", "product");
        String tagStr = JSON.toJSONString(tags);
        List<String> resourceArn = new ArrayList<>();
        resourceArn.add("acs:log:*:" + accountId + ":project/" + projectName);
        TagResourcesRequest tagResourcesRequest = new TagResourcesRequest()
                .setResourceARN(resourceArn)
                .setTags(tagStr)
                .setRegionId("cn-hangzhou");
        tagClient.tagResourcesWithOptions(tagResourcesRequest, runtime);
    }

    public static Client createSlsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("cn-hangzhou.log.aliyuncs.com");
        return new Client(config);
    }

    public static com.aliyun.tag20180828.Client createTagClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "tag.aliyuncs.com";
        return new com.aliyun.tag20180828.Client(config);
    }

}