package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ProvisionedProductDao;
import cc.landingzone.dreamweb.model.*;
import cc.landingzone.dreamweb.utils.JsonUtils;
import com.aliyun.servicecatalog20210901.Client;
import com.aliyun.servicecatalog20210901.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 操作产品实例
 *
 * @author: laodou
 * @createDate: 2022/6/21
 */
@Service
public class ProvisionedProductService {

    private static Logger logger = LoggerFactory.getLogger(ServiceCatalogViewService.class);

    @Autowired
    private ProvisionedProductDao provisionedProductDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ServiceCatalogViewService serviceCatalogViewService;

    @Transactional
    public String getUserName (String exampleId) {
        return provisionedProductDao.getUserName(exampleId);
    }

    @Transactional
    public Integer getRoleId (String exampleId) {
        return provisionedProductDao.getRoleId(exampleId);
    }

    @Transactional
    public Integer getExampleId (String exampleName) {
        return provisionedProductDao.getExampleId(exampleName);
    }

    @Transactional
    public String getProductId(String exampleId) { return provisionedProductDao.getProductIdByExampleId(exampleId); }

    @Transactional
    public void saveExample (ProvisionedProduct provisionedProduct) {
        provisionedProductDao.saveExample(provisionedProduct);
    }

    @Transactional
    public List<String> listExampleId () {
        return provisionedProductDao.listExampleId();
    }

    @Transactional
    public List<ProvisionedProduct> listExample(Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        List<ProvisionedProduct> list = provisionedProductDao.listExample(map);
        if (null != page) {
            if (null != page.getStart() && null != page.getLimit()) {
                Integer total = provisionedProductDao.getExampleTotal(map);
                page.setTotal(total);
            } else {
                page.setTotal(list.size());
            }
        }
        return list;
    }




    /**
         * 新增一条产品实例记录
         *
         * @param:
         * @return
         * @throws Exception
         */
    public void saveProvisionedProduct(Client client, String planId, Map<String, Object> example) throws Exception {

        ProvisionedProduct provisionedProduct = new ProvisionedProduct();

        String provisionedProductStatus;
        String lastTaskId;

        GetProvisionedProductPlanRequest request = new GetProvisionedProductPlanRequest();
        request.setPlanId(planId);
        GetProvisionedProductPlanResponse response = client.getProvisionedProductPlan(request);
        String provisionedProductId = response.getBody().getPlanDetail().provisionedProductId;

        GetProvisionedProductResponseBody.GetProvisionedProductResponseBodyProvisionedProductDetail provisionedProductDetail = getProvisionedProduct(client, provisionedProductId);
        provisionedProductStatus = provisionedProductDetail.getStatus();   //实例状态
        provisionedProduct.setStatus(provisionedProductStatus);

        String time = provisionedProductDetail.createTime;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //设置时区UTC
        df.setTimeZone(TimeZone.getTimeZone("UTC")); //格式化，转当地时区时间
        Date timeData = df.parse(time);
        df.applyPattern("yyyy-MM-dd HH:mm:ss"); //默认时区
        df.setTimeZone(TimeZone.getDefault());
        provisionedProduct.setStartTime(df.format(timeData));// 实例创建时间

        lastTaskId = provisionedProductDetail.getLastTaskId();

        provisionedProduct.setExampleName(provisionedProductDetail.getProvisionedProductName());//实例名称
        provisionedProduct.setProductName(provisionedProductDetail.getProductName());//产品名称
        provisionedProduct.setProductId(provisionedProductDetail.getProductId());//产品ID
        provisionedProduct.setExampleId(provisionedProductId);//实例ID
        provisionedProduct.setRoleId((Integer) example.get("角色ID"));//角色ID
        provisionedProduct.setStartName((String) example.get("申请人"));//申请人

        GetTaskResponseBody.GetTaskResponseBodyTaskDetail taskDetail = getTask(client, lastTaskId);
        provisionedProduct.setParameter(JsonUtils.toJsonString(taskDetail.getParameters()));//申请参数
        if ("Available".equals(provisionedProductStatus)) {
//            provisionedProduct.setParameter(JsonUtils.toJsonString(taskDetail.getParameters()));//申请参数
            provisionedProduct.setOutputs(JsonUtils.toJsonString(taskDetail.getOutputs()));//输出
        } else {
            logger.error(taskDetail.getStatusMessage());
        }

        saveExample(provisionedProduct);// 将产品实例信息存入数据库
    }

    /**
         * 更新产品实例状态
         *
         * @param:
         * @return
         * @throws Exception
         */
    public void updateProvisionedProduct(Client client, String provisionedProductId) {
        try {
            String provisionedProductStatus;
            String lastTaskId;

            GetProvisionedProductResponseBody.GetProvisionedProductResponseBodyProvisionedProductDetail provisionedProductDetail = getProvisionedProduct(client, provisionedProductId);
            provisionedProductStatus = provisionedProductDetail.getStatus();   //实例状态

            if(provisionedProductStatus != "UnderChange") {
                provisionedProductDao.updateStatus(provisionedProductStatus, provisionedProductId);
                lastTaskId = provisionedProductDetail.getLastTaskId();

                GetTaskResponseBody.GetTaskResponseBodyTaskDetail taskDetail = getTask(client, lastTaskId);
                if ("Available".equals(provisionedProductStatus)) {
                    String parameter = JsonUtils.toJsonString(taskDetail.getParameters());
                    provisionedProductDao.updateParameter(parameter, provisionedProductId);
                    String outputs = JsonUtils.toJsonString(taskDetail.getOutputs());
                    provisionedProductDao.updateOutputs(outputs, provisionedProductId);
                } else {
                    logger.error(taskDetail.getStatusMessage());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
         * 启动产品计划
         *
         * @param:
         * @return
         * @throws Exception
         */
    public void executePlan(com.aliyun.servicecatalog20210901.Client client, String planId) throws Exception {
        ExecuteProvisionedProductPlanRequest request = new ExecuteProvisionedProductPlanRequest();
        request.setPlanId(planId);

        ExecuteProvisionedProductPlanResponse response = client.executeProvisionedProductPlan(request);
    }

//    /**
//         * 启动产品
//         *
//         * @param: 终端、输入、实例map
//         * @return 产品实例ID
//         * @throws Exception
//         */
//    public String launchProduct(com.aliyun.servicecatalog20210901.Client client, Map<String, String> inputs, Map<String, Object> example) throws Exception {
//
//        List<LaunchProductRequest.LaunchProductRequestParameters> parameters = inputs.entrySet().stream()
//                .map(entry -> {
//                    LaunchProductRequest.LaunchProductRequestParameters parameter = new LaunchProductRequest.LaunchProductRequestParameters();
//                    parameter.setParameterKey(entry.getKey());
//                    parameter.setParameterValue(entry.getValue());
//                    return parameter;
//                })
//                .collect(Collectors.toList());
//
//        LaunchProductRequest request = new LaunchProductRequest();
//
//         request.setProductId("prod-bp18r7q127u45k");
//        // System.out.println((String) example.get("产品ID"));
////        request.setProductId((String) example.get("产品ID"));
//
//
//        System.out.println((String) example.get("版本ID"));
//
//        request.setPortfolioId("port-bp1yt7582gn4p7");// 产品组合ID，控制启动选项
//        request.setProductVersionId("pv-bp15e79d2614pw");// 产品版本ID
//
//        request.setProvisionedProductName((String) example.get("实例名称"));
//
//        request.setStackRegionId("cn-shanghai");
//        System.out.println((String) example.get("地域"));
//
//        request.setParameters(parameters);
//
//        LaunchProductResponse response = client.launchProduct(request);
//        return response.getBody().getProvisionedProductId();
//    }

    /**
         * 获取产品实例详细信息
         *
         * @param: 终端、实例ID
         * @return 产品实例详细信息
         * @throws Exception
         */
    public GetProvisionedProductResponseBody.GetProvisionedProductResponseBodyProvisionedProductDetail getProvisionedProduct(com.aliyun.servicecatalog20210901.Client client, String provisionedProductId)
            throws Exception {

        GetProvisionedProductRequest request = new GetProvisionedProductRequest();
        request.setProvisionedProductId(provisionedProductId);

        GetProvisionedProductResponse response = client.getProvisionedProduct(request);
        return  response.getBody().getProvisionedProductDetail();
    }

    /**
         * 获取任务详细信息以便查询产品实例参数和输出
         *
         * @param: 任务ID
         * @return 任务详细信息
         * @throws Exception
         */
    public GetTaskResponseBody.GetTaskResponseBodyTaskDetail getTask(com.aliyun.servicecatalog20210901.Client client, String taskId) throws Exception {
        GetTaskRequest request = new GetTaskRequest();
        request.setTaskId(taskId);

        GetTaskResponse response = client.getTask(request);
        return response.getBody().getTaskDetail();
    }

    /**
         * 定时任务查询"UnderChange"状态产品实例并更新状态
         *
         *
         * @throws Exception
         */
    @Scheduled(cron = "0/5 * * * * ?")
    public void updateExample() {
        try {
            List<String> exampleIds = listExampleId();
            if (exampleIds != null) {
                for (String exampleId : exampleIds) {
                    // 创建终端
                    String region = "cn-hangzhou";
                    String userName = getUserName(exampleId);
                    Integer roleId = getRoleId(exampleId);
                    User user = userService.getUserByLoginName(userName);
                    UserRole userRole = userRoleService.getUserRoleById(roleId);
                    String productId = getProductId(exampleId);
                    Client client = serviceCatalogViewService.createClient(region, user, userRole, productId);
                    // 查询并更新数据库，还是调用getProvisionedProduct和getTask接口
                    updateProvisionedProduct(client, exampleId);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
