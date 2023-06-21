package cc.landingzone.dreamweb.demo.resourcesupply;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.ServiceHelper;
import cc.landingzone.dreamweb.demo.akapply.AkApplyUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.sls20201230.Client;
import com.aliyun.sls20201230.models.CreateProjectRequest;
import com.aliyun.tag20180828.models.TagResourcesRequest;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResourceSupplyUtil {

    public static Logger logger = LoggerFactory.getLogger(AkApplyUtil.class);

    public static void main(String[] args) {
        createLogProject("logprojectjiaapitest2", "test");
    }

    public static void createECSInstance() {
        try {

        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void createOSSBucket(String bucketName) {
        try {

        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void createLogProject(String projectName,String description) {
        try {
            Client client = ServiceHelper.createSlsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
            CreateProjectRequest createProjectRequest = new CreateProjectRequest()
                    .setDescription(description)
                    .setProjectName(projectName);
            RuntimeOptions runtime = new RuntimeOptions();
            Map<String, String> headers = new HashMap<>();
            client.createProjectWithOptions(createProjectRequest, headers, runtime);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void attachTagToResource(String applicationName, String environment, String resourceType,
                                           List<String> resourceNameList) {
        try {
            Map<String,String> tags = new HashMap<>();
            tags.put(CommonConstants.APPLICATION_TAG_KEY, applicationName);
            tags.put(CommonConstants.ENVIRONMENT_TYPE_TAG_KEY, environment);
            String tagStr = JSON.toJSONString(tags);
            com.aliyun.tag20180828.Client client = ServiceHelper.createTagClient
                    (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
            TagResourcesRequest tagResourcesRequest = new TagResourcesRequest()
                    .setResourceARN(ServiceHelper.getResourceArn(resourceType, resourceNameList, CommonConstants.Aliyun_UserId))
                    .setTags(tagStr)
                    .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
            RuntimeOptions runtime = new RuntimeOptions();
            client.tagResourcesWithOptions(tagResourcesRequest, runtime);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


}