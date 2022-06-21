package cc.landingzone.dreamweb.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cc.landingzone.dreamweb.utils.JsonUtils;
import com.aliyun.servicecatalog20210901.Client;
import com.aliyun.servicecatalog20210901.models.GetProvisionedProductRequest;
import com.aliyun.servicecatalog20210901.models.GetProvisionedProductResponse;
import com.aliyun.servicecatalog20210901.models.GetProvisionedProductResponseBody.GetProvisionedProductResponseBodyProvisionedProductDetail;
import com.aliyun.servicecatalog20210901.models.GetTaskRequest;
import com.aliyun.servicecatalog20210901.models.GetTaskResponse;
import com.aliyun.servicecatalog20210901.models.GetTaskResponseBody.GetTaskResponseBodyTaskDetail;
import com.aliyun.servicecatalog20210901.models.LaunchProductRequest;
import com.aliyun.servicecatalog20210901.models.LaunchProductRequest.LaunchProductRequestParameters;
import com.aliyun.servicecatalog20210901.models.LaunchProductResponse;
import com.aliyun.teaopenapi.models.Config;

public class ServiceCatalogApiDemo {

    // 请替换成您的AccessKey ID
    private static final String SecurityToken = "CAIS7gF1q6Ft5B2yfSjIr5fYPMyNqI8ZhqTaaHTlgnMwfccfrZDvtDz2IH9OfndrBuwZt/w3mW1W6Pcclqx6R5pEQxRgjVeTEswFnzm6aq/t5uaXj9Vd+rDHdEGXDxnkprywB8zyUNLafNq0dlnAjVUd6LDmdDKkLTfHWN/z/vwBVNkMWRSiZjdrHcpfIhAYyPUXLnzML/2gQHWI6yjydBM05FQl0D4vufrmnZfEt0Pk4QekmrNPlePYOYO5asRgBpB7Xuqu0fZ+Hqi7i3EKsUYRq/sp1fQcpGqZ4IDDGTtY7xCHNa/Y9cA1PPSe72SZ3gsuGoABe/GRDXR5qIZgzJfDI7NO2Ub+N2fUPshBrtuE2CYwFXOaGjzLcuF4e/TIHUVZJ+C4+3bRxt/hsRs6mxTOzOtBzD4RAjwrR1KynrTMoMspCxkKYM1QoOyJCX2OmLLF1ULneduu2VyYAZJop7ccOruIhbVpTueAbz5pTWD+x638vAQ=";
    private static final String ACCESS_KEY_SECRET = "9nvvcaiQNzvTNUS9wufMP84FtMxTNtYmLLaD7sf6H3yF";
    private static final String ACCESS_KEY_ID = "STS.NTmwv9EP81f1jRTfseqH3BUDV";

    //    private static final String ACCESS_KEY_ID = "<your_access_key_id>";
    // 请替换成您的AccessKey Secret
//    private static final String ACCESS_KEY_SECRET = "<yourt_access_key_secret>";
    // 地域
    private static final String REGION = "cn-hangzhou";

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.setAccessKeyId(ACCESS_KEY_ID);
        config.setAccessKeySecret(ACCESS_KEY_SECRET);
        config.setSecurityToken(SecurityToken);
        config.setRegionId(REGION);
        Client client = new Client(config);

        String provisionedProductId = launchProduct(client);

        String provisionedProductStatus;
        String lastTaskId;
        do {
            Thread.sleep(3000);
            GetProvisionedProductResponseBodyProvisionedProductDetail provisionedProductDetail = getProvisionedProduct(client, provisionedProductId);
            provisionedProductStatus = provisionedProductDetail.getStatus();
            lastTaskId = provisionedProductDetail.getLastTaskId();
            System.out.printf("ProvisionedProductId %s is %s%n", provisionedProductId, provisionedProductStatus);
        } while ("UnderChange".equals(provisionedProductStatus));

        GetTaskResponseBodyTaskDetail taskDetail = getTask(client, lastTaskId);
        if ("Available".equals(provisionedProductStatus)) {
            System.out.println("Parameters: " + JsonUtils.toJsonString(taskDetail.getParameters()));
            System.out.println("Outputs: " + JsonUtils.toJsonString(taskDetail.getOutputs()));
        } else {
            System.out.println("Error Message: " + taskDetail.getStatusMessage());
        }
    }

    public static String launchProduct(Client client) throws Exception {

        Map<String, String> inputs = new HashMap<>();
        inputs.put("zone_id", "cn-shanghai-l");
        inputs.put("vpc_cidr_block", "172.16.0.0/12");
        inputs.put("vswitch_cidr_block", "172.16.0.0/21");
        inputs.put("ecs_instance_type", "ecs.s6-c1m1.small");

        List<LaunchProductRequestParameters> parameters = inputs.entrySet().stream()
            .map(entry -> {
                LaunchProductRequestParameters parameter = new LaunchProductRequestParameters();
                parameter.setParameterKey(entry.getKey());
                parameter.setParameterValue(entry.getValue());
                return parameter;
            })
            .collect(Collectors.toList());

        LaunchProductRequest request = new LaunchProductRequest();
        //request.setPortfolioId("port-bp1yt7582gn4p7");
        request.setProductId("prod-bp18r7q127u45k");
        request.setProductVersionId("pv-bp15e79d2614pw");
        request.setProvisionedProductName("nuoya_test_" + System.currentTimeMillis());
        request.setStackRegionId("cn-shanghai");
        request.setParameters(parameters);

        LaunchProductResponse response = client.launchProduct(request);
        return response.getBody().getProvisionedProductId();
    }

    public static GetProvisionedProductResponseBodyProvisionedProductDetail getProvisionedProduct(Client client, String provisionedProductId)
        throws Exception {

        GetProvisionedProductRequest request = new GetProvisionedProductRequest();
        request.setProvisionedProductId(provisionedProductId);

        GetProvisionedProductResponse response = client.getProvisionedProduct(request);
        return  response.getBody().getProvisionedProductDetail();
    }

    public static GetTaskResponseBodyTaskDetail getTask(Client client, String taskId) throws Exception {
        GetTaskRequest request = new GetTaskRequest();
        request.setTaskId(taskId);

        GetTaskResponse response = client.getTask(request);
        return response.getBody().getTaskDetail();
    }
}