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
    @GetMapping("/getApps.do")
    public void getApps(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String Id = request.getParameter("productId");
        Integer productId = Integer.valueOf(Id);
        System.out.println(productId);

        List<String> appList = productVersionService.listApps(productId);
        result.setTotal(appList.size());
        result.setData(appList);

        outputToJSON(response, result);
    }

    /**
         * 用户 + 产品ID 获取产品组合ID
         *
         *
         * @return 
         * @throws Exception
         */
    @GetMapping("/getServicecatalogPortfolioId.do")
    public void getServicecatalogPortfolioId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String Id = request.getParameter("productId");
        Integer productId = Integer.valueOf(Id);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByLoginName(userName);

        String portfolioId = userProductService.getServicecatalogPortfolioId(productId, user.getId());
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
    @GetMapping("/getEnvironment.do")
    public void getEnvironment(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String getApp = request.getParameter("select_app");
            Integer productId = Integer.valueOf(request.getParameter("productId"));
            List<String> environmentsList = productVersionService.listEnvironments(productId, getApp);
            result.setTotal(environmentsList.size());
            result.setData(environmentsList);
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
    @GetMapping("/getServicecatalogProductVersionId.do")
    public void getServicecatalogProductVersionId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String getApp = request.getParameter("select_app");
            Assert.hasText(getApp, "应用不能为空！");
            String getEnvironment = request.getParameter("select_environment");
            Assert.hasText(getEnvironment, "场景不能为空！");
            String getProductId = request.getParameter("productId");
            Assert.hasText(getProductId, "产品Id不能为空！");
            Integer productId = Integer.valueOf(getProductId);
            String servicecatalogProductVersionId = productVersionService.getServicecatalogProductVersionId(productId, getApp, getEnvironment);
            Assert.hasText(servicecatalogProductVersionId, "未找到对应servicecatalogProductVersionId！");
            result.setData(servicecatalogProductVersionId);
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
        String Id = request.getParameter("productId");
        Integer productId = Integer.valueOf(Id);
        String productName = productService.getProductById(productId).getProductName();
        
        Integer exampleRandom = r.nextInt(10000000);
        String provisionedProductName = productName + "-" + exampleRandom;
        ProvisionedProduct provisionedProduct = provisionedProductService.getProvisionedProductByProvisionedProductName(provisionedProductName);
        while (provisionedProduct != null) {
            exampleRandom = r.nextInt(10000000);
            provisionedProductName = productName + "-" + exampleRandom;
            provisionedProduct = provisionedProductService.getProvisionedProductByProvisionedProductName(provisionedProductName);
        }
        result.setData(provisionedProductName);
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
            String Id = request.getParameter("productId");
            System.out.println(Id);
            Assert.hasText(Id, "产品号不能为空!");
            String roleIdStr = request.getParameter("roleId");
            System.out.println(roleIdStr);
            Assert.hasText(roleIdStr, "roleId不能为空!");
            String provisionedProductName = request.getParameter("provisionedProductName");
            System.out.println(provisionedProductName);
            Assert.hasText(provisionedProductName, "实例名称不能为空！");

            String servicecatalogProductVersionId = request.getParameter("servicecatalogProductVersionId");
            System.out.println(servicecatalogProductVersionId);
            Assert.hasText(servicecatalogProductVersionId, "产品版本ID不能为空！");
            String servicecatalogPortfolioId = request.getParameter("servicecatalogPortfolioId");
            System.out.println(servicecatalogPortfolioId);
            Assert.hasText(servicecatalogPortfolioId, "产品组合ID不能为空！");
            String regionSelect = request.getParameter("region");
            System.out.println(regionSelect);
            Assert.hasText(regionSelect, "地域不能为空！");

            Integer productId = Integer.valueOf(Id);
            Product product = productService.getProductById(productId);
            String servicecatalogProductId = product.getServicecatalogProductId();
            Integer roleId = Integer.valueOf(roleIdStr);
            String region = systemConfigService.getStringValueFromCache("region");

            // 获取当前用户信息以及所需要使用的ram角色信息
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(userName);
            UserRole userRole = userRoleService.getUserRoleById(roleId);

            String nonLoginPreUrl = serviceCatalogViewService.getNonLoginPreUrl(servicecatalogProductId, provisionedProductName, region, user, userRole, servicecatalogProductVersionId, servicecatalogPortfolioId, regionSelect);
            result.setData(nonLoginPreUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

}
