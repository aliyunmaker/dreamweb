package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.*;
import cc.landingzone.dreamweb.service.*;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *
 * 对服务目录页面请求进行处理
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Controller
@RequestMapping("/serviceCatalogView")
public class ServiceCatalogViewController extends BaseController{

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductVersionService productVersionService;

    @Autowired
    private ProvisionedProductService provisionedProductService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private ServiceCatalogViewService serviceCatalogViewService;

    @Autowired
    private UserProductService userProductService;

    /**
         * 获取产品ID对应的使用应用
         *
         *
         * @return 应用列表
         * @throws Exception
         */
    @GetMapping("/getApplications.do")
    public void getApplication(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String productId = request.getParameter("productId");

        List<String> applicationList = productVersionService.listApplication(productId);
        result.setTotal(applicationList.size());
        result.setData(applicationList);

        outputToJSON(response, result);
    }

    /**
         * 用户 + 产品ID 获取产品组合ID
         *
         *
         * @return 
         * @throws Exception
         */
    @GetMapping("/getPortfolioId.do")
    public void getPortfolioId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String productId = request.getParameter("productId");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String portfolioId = userProductService.getPortfolioId(productId, username);
        result.setData(portfolioId);
        outputToJSON(response, result);
    }

    /**
         * 获取某应用对应的所有使用场景
         *
         * @param: 应用名称
         * @return 场景列表
         * @throws Exception
         */
    @GetMapping("/getScenes.do")
    public void getScenes(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String getApplication = request.getParameter("select_Application");
            String productId = request.getParameter("productId");
            List<String> scenesList = productVersionService.listScenes(productId, getApplication);
            result.setTotal(scenesList.size());
            result.setData(scenesList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    /**
         * 根据应用及场景获取指定产品ID
         *
         * @param: 应用名称、场景名称
         * @return 产品ID
         * @throws Exception
         */
    @GetMapping("/getProductVersionId.do")
    public void getProductVersionId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String getApplication = request.getParameter("select_Application");
            Assert.hasText(getApplication, "应用不能为空！");
            String getScene = request.getParameter("select_Scene");
            Assert.hasText(getScene, "场景不能为空！");
            String getProductId = request.getParameter("productId");
            Assert.hasText(getProductId, "产品Id不能为空！");
            String productVersionId = productVersionService.getProductVersionId(getProductId, getApplication, getScene);
            Assert.hasText(productVersionId, "未找到对应productVersionId！");
            result.setData(productVersionId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    /**
         * 根据产品ID查询产品名称并生成产品实例名称
         *
         * @param: 产品ID
         * @return 实例名称
         * @throws Exception
         */
    @GetMapping("/getExampleName.do")
    public void getExampleName(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        Random r = new Random();
        String productId = request.getParameter("productId");
        String productName = productService.getProductName(productId);
        
        Integer exampleRandom = r.nextInt(10000000);
        String exampleName = productName + "-" + exampleRandom;
        Integer id = provisionedProductService.getExampleId(exampleName);
        while (id != null) {
            exampleRandom = r.nextInt(10000000);
            exampleName = productName + "-" + exampleRandom;
            id = provisionedProductService.getExampleId(exampleName);
        }
        result.setData(exampleName);
        outputToJSON(response, result);

    }

/**
     * 获取免密登录URL
     *
     * @param: 产品ID、角色ID、实例名称
     * @return URL
     * @throws Exception
     */
    @GetMapping("/getNonLoginPreUrl.do")
    public void getNonLoginPreUrl(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String productId = request.getParameter("productId");
            Assert.hasText(productId, "产品号不能为空!");
            String roleIdStr = request.getParameter("roleId");
            Assert.hasText(roleIdStr, "roleId不能为空!");
            String exampleName = request.getParameter("exampleName");
            Assert.hasText(exampleName, "实例名称不能为空！");

            String productVersionId = request.getParameter("productVersionId");
            Assert.hasText(productVersionId, "实例名称不能为空！");
            String portfolioId = request.getParameter("portfolioId");
            Assert.hasText(portfolioId, "实例名称不能为空！");
            String regionSelect = request.getParameter("region");
            Assert.hasText(regionSelect, "实例名称不能为空！");


            Integer roleId = Integer.valueOf(roleIdStr);
            String region = systemConfigService.getStringValueFromCache("region");

            // 获取当前用户信息以及所需要使用的ram角色信息
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(userName);
            UserRole userRole = userRoleService.getUserRoleById(roleId);

            String nonLoginPreUrl = serviceCatalogViewService.getNonLoginPreUrl(productId, exampleName, region, user, userRole, productVersionId, portfolioId, regionSelect);
            result.setData(nonLoginPreUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

}
