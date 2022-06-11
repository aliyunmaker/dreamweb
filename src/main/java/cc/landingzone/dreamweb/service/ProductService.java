package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ProductDao;
import cc.landingzone.dreamweb.model.*;

import cc.landingzone.dreamweb.utils.JsonUtils;
import com.aliyun.servicecatalog20210901.models.*;
import com.aliyun.servicecatalog20210901.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


import java.util.*;
import java.util.stream.Collectors;


@Component
public class ProductService {
    @Autowired
    private ProductDao productDao;

    @Autowired
    private PreViewService preViewService;

    @Transactional
    public List<String> getApplication () {
        return productDao.getApplication();
    }

    @Transactional
    public List<String> getScenes (String application) {
        return productDao.getScenes(application);
    }

    @Transactional
    public String getProductId (String application, String scene) {
        return productDao.getProductId(application, scene);
    }

    @Transactional
    public String getProductName (String productId) {
        return productDao.getProductName(productId);
    }

    @Transactional
    public Integer getExampleId (String exampleName) {
        return productDao.getExampleId(exampleName);
    }

    @Transactional
    public void addExample (Provisioned_product provisioned_product) {
        productDao.addExample(provisioned_product);
    }

    @Transactional
    public List<Provisioned_product> searchExample(Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        List<Provisioned_product> list = productDao.searchExample(map);
        if (null != page) {
            if (null != page.getStart() && null != page.getLimit()) {
                Integer total = productDao.searchExampleTotal(map);
                page.setTotal(total);
            } else {
                page.setTotal(list.size());
            }
        }
        System.out.println(list.toString());
        return list;
    }

    @Transactional
    public List<Product> searchProduct(Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        List<Product> list = productDao.searchProduct(map);
        if (null != page) {
            if (null != page.getStart() && null != page.getLimit()) {
                Integer total = productDao.searchProductTotal(map);
                page.setTotal(total);
            } else {
                page.setTotal(list.size());
            }
        }
        return list;
    }

    @Transactional
    public void addProduct(Product product) {
        Product product2 = getProductByProductId(product.getProductid());
        if (product2 != null) {
            throw new IllegalArgumentException("此产品ID(" + product2.getProductid()+ ")已存在");
        }
        productDao.addProduct(product);
    }

    public Product getProductByProductId(String productId) {
        Assert.hasText(productId, "产品ID不能为空!");
        return productDao.getProductByProductId(productId);
    }

    public Product getProductById(Integer id) {
        return productDao.getProductById(id);
    }

    @Transactional
    public void updateProduct(Product product) {
        Assert.notNull(product, "数据不能为空!");
        Assert.hasText(product.getProductid(), "产品ID不能为空!");
        productDao.updateProduct(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        productDao.deleteProduct(id);
    }


    public void launchProduct(Client client, String provisionedProductId, Map<String, Object> example) throws Exception {
//        Client client = preViewService.createClient(region, user, userRole);
//
//        String provisionedProductId = launchProduct(client);
//        System.out.println("ProvisionedProductId: " + provisionedProductId);

        Provisioned_product provisioned_product = new Provisioned_product();

        String provisionedProductStatus;
        String lastTaskId;


        GetProvisionedProductResponseBody.GetProvisionedProductResponseBodyProvisionedProductDetail provisionedProductDetail = getProvisionedProduct(client, provisionedProductId);
        provisionedProductStatus = provisionedProductDetail.getStatus();   //实例状态
        provisioned_product.setStatus(provisionedProductStatus);

        lastTaskId = provisionedProductDetail.getLastTaskId();

        System.out.println(provisionedProductDetail.getProvisionedProductName()); //实例名称
        provisioned_product.setExamplename(provisionedProductDetail.getProvisionedProductName());
        System.out.println(provisionedProductDetail.getProductName());//产品名称
        provisioned_product.setProductname(provisionedProductDetail.getProductName());
        System.out.println(provisionedProductDetail.getProductId());//产品ID
        provisioned_product.setProductid(provisionedProductDetail.getProductId());
        // provisionedProductId //实例ID
        provisioned_product.setExampleid(provisionedProductId);
        //角色ID
        provisioned_product.setRoleid((Integer) example.get("角色ID"));
        //申请人
        provisioned_product.setStartname((String) example.get("申请人"));

        System.out.printf("ProvisionedProductId %s is %s%n", provisionedProductId, provisionedProductStatus);
//        } while ("UnderChange".equals(provisionedProductStatus));

    GetTaskResponseBody.GetTaskResponseBodyTaskDetail taskDetail = getTask(client, lastTaskId);
    if ("Available".equals(provisionedProductStatus)) {
        System.out.println("Parameters: " + JsonUtils.toJsonString(taskDetail.getParameters()));//申请参数
        provisioned_product.setParameter(JsonUtils.toJsonString(taskDetail.getParameters()));
        System.out.println("Outputs: " + JsonUtils.toJsonString(taskDetail.getOutputs()));//输出
        provisioned_product.setOutputs(JsonUtils.toJsonString(taskDetail.getOutputs()));
    } else {
        System.out.println("Error Message: " + taskDetail.getStatusMessage());
    }

        ///////////////////////////分界线//////////////////////

// //        do {
//            Thread.sleep(100000);
//            GetProvisionedProductResponseBody.GetProvisionedProductResponseBodyProvisionedProductDetail provisionedProductDetail = getProvisionedProduct(client, provisionedProductId);
//            provisionedProductStatus = provisionedProductDetail.getStatus();   //实例状态
//            provisioned_product.setStatus(provisionedProductStatus);

//            lastTaskId = provisionedProductDetail.getLastTaskId();

//            System.out.println(provisionedProductDetail.getProvisionedProductName()); //实例名称
//            provisioned_product.setExamplename(provisionedProductDetail.getProvisionedProductName());
//            System.out.println(provisionedProductDetail.getProductName());//产品名称
//            provisioned_product.setProductname(provisionedProductDetail.getProductName());
//            System.out.println(provisionedProductDetail.getProductId());//产品ID
//            provisioned_product.setProductid(provisionedProductDetail.getProductId());
//            // provisionedProductId //实例ID
//            provisioned_product.setExampleid(provisionedProductId);
//            //角色ID
//            provisioned_product.setRoleid((Integer) example.get("角色ID"));
//            //申请人
//            provisioned_product.setStartname((String) example.get("申请人"));

//            System.out.printf("ProvisionedProductId %s is %s%n", provisionedProductId, provisionedProductStatus);
// //        } while ("UnderChange".equals(provisionedProductStatus));

//        GetTaskResponseBody.GetTaskResponseBodyTaskDetail taskDetail = getTask(client, lastTaskId);
//        if ("Available".equals(provisionedProductStatus)) {
//            System.out.println("Parameters: " + JsonUtils.toJsonString(taskDetail.getParameters()));//申请参数
//            provisioned_product.setParameter(JsonUtils.toJsonString(taskDetail.getParameters()));
//            System.out.println("Outputs: " + JsonUtils.toJsonString(taskDetail.getOutputs()));//输出
//            provisioned_product.setOutputs(JsonUtils.toJsonString(taskDetail.getOutputs()));
//        } else {
//            System.out.println("Error Message: " + taskDetail.getStatusMessage());
//        }

        addExample(provisioned_product);
    }

    public static String launchProduct(com.aliyun.servicecatalog20210901.Client client, Map<String, String> inputs, Map<String, Object> example) throws Exception {

//        Map<String, String> inputs = new HashMap<>();
//        inputs.put("zone_id", "cn-shanghai-l");
//        inputs.put("vpc_cidr_block", "172.16.0.0/12");
//        inputs.put("vswitch_cidr_block", "172.16.0.0/21");
//        inputs.put("ecs_instance_type", "ecs.s6-c1m1.small");

        List<LaunchProductRequest.LaunchProductRequestParameters> parameters = inputs.entrySet().stream()
                .map(entry -> {
                    LaunchProductRequest.LaunchProductRequestParameters parameter = new LaunchProductRequest.LaunchProductRequestParameters();
                    parameter.setParameterKey(entry.getKey());
                    parameter.setParameterValue(entry.getValue());
                    return parameter;
                })
                .collect(Collectors.toList());

        LaunchProductRequest request = new LaunchProductRequest();
//        request.setPortfolioId("port-bp1yt7582gn4p7");
        request.setProductId("prod-bp18r7q127u45k");
//        request.setProductId((String) example.get("产品ID"));
        request.setProductVersionId("pv-bp15e79d2614pw");
        request.setProvisionedProductName((String) example.get("实例名称"));
        request.setStackRegionId("cn-shanghai");
        request.setParameters(parameters);

        LaunchProductResponse response = client.launchProduct(request);
        return response.getBody().getProvisionedProductId();
    }

    public static GetProvisionedProductResponseBody.GetProvisionedProductResponseBodyProvisionedProductDetail getProvisionedProduct(com.aliyun.servicecatalog20210901.Client client, String provisionedProductId)
            throws Exception {

        GetProvisionedProductRequest request = new GetProvisionedProductRequest();
        request.setProvisionedProductId(provisionedProductId);

        GetProvisionedProductResponse response = client.getProvisionedProduct(request);
        return  response.getBody().getProvisionedProductDetail();
    }

    public static GetTaskResponseBody.GetTaskResponseBodyTaskDetail getTask(com.aliyun.servicecatalog20210901.Client client, String taskId) throws Exception {
        GetTaskRequest request = new GetTaskRequest();
        request.setTaskId(taskId);

        GetTaskResponse response = client.getTask(request);
        return response.getBody().getTaskDetail();
    }

}
