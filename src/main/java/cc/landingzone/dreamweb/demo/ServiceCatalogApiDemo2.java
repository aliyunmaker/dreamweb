package cc.landingzone.dreamweb.demo;

import java.util.HashMap;
import java.util.Map;

import cc.landingzone.dreamweb.utils.JsonUtils;
import com.aliyun.servicecatalog20210901.Client;
import com.aliyun.servicecatalog20210901.models.AssociatePrincipalWithPortfolioRequest;
import com.aliyun.servicecatalog20210901.models.AssociateProductWithPortfolioRequest;
import com.aliyun.servicecatalog20210901.models.CreateConstraintRequest;
import com.aliyun.servicecatalog20210901.models.CreateConstraintResponse;
import com.aliyun.servicecatalog20210901.models.CreatePortfolioRequest;
import com.aliyun.servicecatalog20210901.models.CreatePortfolioResponse;
import com.aliyun.servicecatalog20210901.models.CreateProductRequest;
import com.aliyun.servicecatalog20210901.models.CreateProductRequest.CreateProductRequestProductVersionParameters;
import com.aliyun.servicecatalog20210901.models.CreateProductResponse;
import com.aliyun.servicecatalog20210901.models.CreateProductResponseBody;
import com.aliyun.servicecatalog20210901.models.CreateTemplateRequest;
import com.aliyun.servicecatalog20210901.models.CreateTemplateResponse;
import com.aliyun.teaopenapi.models.Config;

public class ServiceCatalogApiDemo2 {

    // 请替换成您的AccessKey ID
    private static final String ACCESS_KEY_ID = "<your_access_key_id>";
    // 请替换成您的AccessKey Secret
    private static final String ACCESS_KEY_SECRET = "<yourt_access_key_secret>";
    // 地域
    private static final String REGION = "cn-hangzhou";

    private static final String TEMPLATE_BODY = "{\"ROSTemplateFormatVersion\":\"2015-09-01\","
        + "\"Transform\":\"Aliyun::Terraform-v1.1\",\"Workspace\":{\"main.tf\":\"data "
        + "\\\"alicloud_caller_identity\\\" \\\"current\\\" {\\n}\\n\\nresource \\\"alicloud_ram_role\\\" "
        + "\\\"default\\\" {\\n  name        = var.role_name\\n  document    = <<EOF\\n  {\\n    \\\"Statement\\\": "
        + "[\\n      {\\n        \\\"Action\\\": \\\"sts:AssumeRole\\\",\\n        \\\"Effect\\\": \\\"Allow\\\",\\n "
        + "       \\\"Principal\\\": {\\n          \\\"RAM\\\": [\\n            \\\"acs:ram::${data"
        + ".alicloud_caller_identity.current.account_id}:root\\\"\\n          ]\\n        }\\n      }\\n    ],\\n    "
        + "\\\"Version\\\": \\\"1\\\"\\n  }\\n  EOF\\n  description = \\\"created by terraform\\\"\\n  force       = "
        + "true\\n}\\n\\noutput \\\"role_arn\\\" {\\n  value       = alicloud_ram_role.default.arn\\n  description = "
        + "\\\"The role arn.\\\"\\n}\\n\\noutput \\\"document\\\" {\\n  value       = alicloud_ram_role.default"
        + ".document\\n  description = \\\"Authorization strategy of the role.\\\"\\n}\\n\",\"variables"
        + ".tf\":\"variable \\\"role_name\\\" {\\n  type = string\\n  description = <<EOT\\n  {\\n    \\\"Label\\\": "
        + "\\\"RAM角色名称\\\"\\n  }\\n  EOT\\n}\"}}";
    private static final String LOCAL_ROLE_NAME = "TerraformExecutionRole";
    private static final long RAM_USER_ID = 244771116036371524L;

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.setAccessKeyId(ACCESS_KEY_ID);
        config.setAccessKeySecret(ACCESS_KEY_SECRET);
        config.setRegionId(REGION);
        Client client = new Client(config);

        String portfolioId = createPortfolio(client);
        String templateUrl = createTemplate(client);
        CreateProductResponseBody createProductResponse = createProduct(client, templateUrl);
        String productId = createProductResponse.getProductId();
        String productVersionId = createProductResponse.getProductVersionId();
        boolean associateProductSuccess = associateProductWithPortfolio(client, portfolioId, productId);
        String constraintId = createConstraint(client, portfolioId, productId);
        boolean associatePrincipalSuccess = associatePrincipalWithPortfolio(client, portfolioId);

        System.out.println("portfolioId: " + portfolioId);
        System.out.println("templateUrl: " + templateUrl);
        System.out.println("productId: " + productId);
        System.out.println("productVersionId: " + productVersionId);
        System.out.println("associateProdSuccess: " + associateProductSuccess);
        System.out.println("constraintId: " + constraintId);
        System.out.println("associatePrincipalSuccess: " + associatePrincipalSuccess);
    }

    public static String createPortfolio(Client client) throws Exception {
        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setPortfolioName("测试产品组合");
        request.setProviderName("贝熊");
        request.setDescription("通过API创建");

        CreatePortfolioResponse response = client.createPortfolio(request);
        return response.getBody().getPortfolioId();
    }

    public static String createTemplate(Client client) throws Exception {
        CreateTemplateRequest request = new CreateTemplateRequest();
        request.setTemplateType("RosTerraformTemplate");
        request.setTemplateBody(TEMPLATE_BODY);

        CreateTemplateResponse response = client.createTemplate(request);
        return response.getBody().getTemplateUrl();
    }

    public static CreateProductResponseBody createProduct(Client client, String templateUrl) throws Exception {
        CreateProductRequestProductVersionParameters productVersionParameters = new CreateProductRequestProductVersionParameters();
        productVersionParameters.setProductVersionName("1.0");
        productVersionParameters.setTemplateType("RosTerraformTemplate");
        productVersionParameters.setTemplateUrl(templateUrl);
        productVersionParameters.setDescription("通过API创建");

        CreateProductRequest request = new CreateProductRequest();
        request.setProductName("测试产品");
        request.setProviderName("贝熊");
        request.setDescription("通过API创建");
        request.setProductType("Ros");
        request.setProductVersionParameters(productVersionParameters);

        CreateProductResponse response = client.createProduct(request);
        return response.getBody();
    }

    public static boolean associateProductWithPortfolio(Client client, String portfolioId, String productId) throws Exception {
        AssociateProductWithPortfolioRequest request = new AssociateProductWithPortfolioRequest();
        request.setPortfolioId(portfolioId);
        request.setProductId(productId);

        client.associateProductWithPortfolio(request);
        return true;
    }

    public static String createConstraint(Client client, String portfolioId, String productId) throws Exception {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("LocalRoleName", LOCAL_ROLE_NAME);

        CreateConstraintRequest request = new CreateConstraintRequest();
        request.setPortfolioId(portfolioId);
        request.setProductId(productId);
        request.setConstraintType("Launch");
        request.setConfig(JsonUtils.toJsonString(configMap));
        request.setDescription("Launch as local role " + LOCAL_ROLE_NAME);

        CreateConstraintResponse response = client.createConstraint(request);
        return response.getBody().getConstraintId();
    }

    public static boolean associatePrincipalWithPortfolio(Client client, String portfolioId) throws Exception {
        AssociatePrincipalWithPortfolioRequest request = new AssociatePrincipalWithPortfolioRequest();
        request.setPortfolioId(portfolioId);
        request.setPrincipalType("RamUser");
        request.setPrincipalId(String.valueOf(RAM_USER_ID));

        client.associatePrincipalWithPortfolio(request);
        return true;
    }
}