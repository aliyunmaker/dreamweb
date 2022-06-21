package cc.landingzone.dreamweb.demo;

import java.util.ArrayList;
import java.util.List;

import cc.landingzone.dreamweb.utils.JsonUtils;
import com.aliyun.servicecatalog20210901.Client;
import com.aliyun.servicecatalog20210901.models.ListPortfoliosRequest;
import com.aliyun.servicecatalog20210901.models.ListPortfoliosRequest.ListPortfoliosRequestFilters;
import com.aliyun.servicecatalog20210901.models.ListPortfoliosResponse;
import com.aliyun.servicecatalog20210901.models.ListPortfoliosResponseBody;
import com.aliyun.servicecatalog20210901.models.ListPortfoliosResponseBody.ListPortfoliosResponseBodyPortfolioDetails;
import com.aliyun.servicecatalog20210901.models.ListProductsAsAdminRequest;
import com.aliyun.servicecatalog20210901.models.ListProductsAsAdminResponse;
import com.aliyun.servicecatalog20210901.models.ListProductsAsAdminResponseBody;
import com.aliyun.servicecatalog20210901.models.ListProductsAsAdminResponseBody.ListProductsAsAdminResponseBodyProductDetails;
import com.aliyun.servicecatalog20210901.models.ListProductsAsEndUserRequest;
import com.aliyun.servicecatalog20210901.models.ListProductsAsEndUserResponse;
import com.aliyun.servicecatalog20210901.models.ListProductsAsEndUserResponseBody;
import com.aliyun.servicecatalog20210901.models.ListProductsAsEndUserResponseBody.ListProductsAsEndUserResponseBodyProductSummaries;
import com.aliyun.teaopenapi.models.Config;

public class ServiceCatalogApiDemo3 {

    // 请替换成您的AccessKey ID
//    private static final String ACCESS_KEY_ID = "<your_access_key_id>";
    private static final String SecurityToken = "CAIS7AF1q6Ft5B2yfSjIr5ffJYiApuZ02q2OSBP0okJgWrhmn5fumzz2IH9OfndrBuwZt/w3mW1W6Pcclqp/TIId0NE27jAovPpt6gqET9frma7ctM4p6vCMHWyUFGSIvqv7aPn4S9XwY+qkb0u++AZ43br9c0fJPTXnS+rr76RqddMKRAK1QCNbDdNNXGtYpdQdKGHaOITGUHeooBKJUBQ241Mh2TMhsPvlmpDF0HeE0g2mkN1yjp/qP52pY/NrOJpCSNqv1IR0DPGeinQOsEcWq/4u1PEZp2mW4Mv/ClVU4hPDP+3JqM2Uy4a1xW2fcRqAAV0hWA402RE8s8C/mb++b02pYTfLu+cjy35DTYdcesmL76mKlWfYoDMcY/UgJ5uggpGafFlEgTkPj7D63ZA7PQNnn0mRFLxNGuluzMdMrzL3vAN8MBrdRViaZ5+hVujFmsC306zPXBSIzWzVuK9aY4UlP1vxjJZNUfsSX3LuRjG/";
    // 请替换成您的AccessKey Secret
    private static final String ACCESS_KEY_SECRET = "GUa7q8Txu18TPTLjUSHbGf92joJstNc2kyFRyhoVzZEV";
    // 地域
    private static final String ACCESS_KEY_ID = "STS.NTjn24K9UmoeJ5EFB5V7JpREy";

    private static final String REGION = "cn-hangzhou";

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.setAccessKeyId(ACCESS_KEY_ID);
        config.setAccessKeySecret(ACCESS_KEY_SECRET);
        config.setRegionId(REGION);
        config.setSecurityToken(SecurityToken);
        Client client = new Client(config);

        List<ListPortfoliosResponseBodyPortfolioDetails> portfolioDetails = listPortfolios(client);
        List<ListProductsAsAdminResponseBodyProductDetails> productDetails = listProductsAsAdmin(client);
        List<ListProductsAsEndUserResponseBodyProductSummaries> productSummaries = listProductsAsEndUser(client);
        System.out.println(JsonUtils.toJsonString(portfolioDetails) + "\n");
        System.out.println(JsonUtils.toJsonString(productDetails) + "\n");
        System.out.println(JsonUtils.toJsonString(productSummaries));
    }

    public static List<ListPortfoliosResponseBodyPortfolioDetails> listPortfolios(Client client) throws Exception {
        List<ListPortfoliosRequestFilters> filters = new ArrayList<ListPortfoliosRequestFilters>() {{
            ListPortfoliosRequestFilters filter = new ListPortfoliosRequestFilters();
            filter.setKey("FullTextSearch");
            filter.setValue("API");
            add(filter);
        }};

        int pageNumber = 1;
        int pageSize = 2;

        ListPortfoliosRequest request = new ListPortfoliosRequest();
        request.setFilters(filters);
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        request.setSortBy("CreateTime");
        request.setSortOrder("Desc");

        ListPortfoliosResponse response = client.listPortfolios(request);
        ListPortfoliosResponseBody responseBody = response.getBody();
        int total = responseBody.getTotalCount();

        List<ListPortfoliosResponseBodyPortfolioDetails> portfolioDetails = new ArrayList<>();
        portfolioDetails.addAll(responseBody.getPortfolioDetails());
        while (pageNumber * pageSize < total) {
            pageNumber++;
            request.setPageNumber(pageNumber);
            response = client.listPortfolios(request);
            responseBody = response.getBody();
            portfolioDetails.addAll(responseBody.getPortfolioDetails());
        }

        return portfolioDetails;
    }

    public static List<ListProductsAsAdminResponseBodyProductDetails> listProductsAsAdmin(Client client) throws Exception {
        int pageNumber = 1;
        int pageSize = 2;

        ListProductsAsAdminRequest request = new ListProductsAsAdminRequest();
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        request.setSortBy("CreateTime");
        request.setSortOrder("Desc");

        ListProductsAsAdminResponse response = client.listProductsAsAdmin(request);
        ListProductsAsAdminResponseBody responseBody = response.getBody();
        int total = responseBody.getTotalCount();

        List<ListProductsAsAdminResponseBodyProductDetails> productDetails = new ArrayList<>();
        productDetails.addAll(responseBody.getProductDetails());
        while (pageNumber * pageSize < total) {
            pageNumber++;
            request.setPageNumber(pageNumber);
            response = client.listProductsAsAdmin(request);
            responseBody = response.getBody();
            productDetails.addAll(responseBody.getProductDetails());
        }

        return productDetails;
    }

    public static List<ListProductsAsEndUserResponseBodyProductSummaries> listProductsAsEndUser(Client client) throws Exception {
        int pageNumber = 1;
        int pageSize = 2;

        ListProductsAsEndUserRequest request = new ListProductsAsEndUserRequest();
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        request.setSortBy("CreateTime");
        request.setSortOrder("Desc");

        ListProductsAsEndUserResponse response = client.listProductsAsEndUser(request);
        ListProductsAsEndUserResponseBody responseBody = response.getBody();
        int total = responseBody.getTotalCount();

        List<ListProductsAsEndUserResponseBodyProductSummaries> productSummaries = new ArrayList<>();
        productSummaries.addAll(responseBody.getProductSummaries());
        while (pageNumber * pageSize < total) {
            pageNumber++;
            request.setPageNumber(pageNumber);
            response = client.listProductsAsEndUser(request);
            responseBody = response.getBody();
            productSummaries.addAll(responseBody.getProductSummaries());
        }

        return productSummaries;
    }
}