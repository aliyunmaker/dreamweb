package cc.landingzone.dreamweb.controller;

import io.jsonwebtoken.lang.Assert;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.ProductService;
import cc.landingzone.dreamweb.service.PreViewService;
import cc.landingzone.dreamweb.service.SystemConfigService;
import cc.landingzone.dreamweb.service.UserRoleService;
import cc.landingzone.dreamweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/preView")
public class PreViewController extends BaseController{

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private PreViewService preViewService;

    @GetMapping("/getRoles.do")
    public void getRoles(HttpServletRequest request, HttpServletResponse response) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.println(userName);
        WebResult result = new WebResult();
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();  //拿到用户名
//            System.out.println(userName);
            User user = userService.getUserByLoginName(userName);
            List<UserRole> userRoleList = userRoleService.getRoleListByUserId(user.getId());

            result.setTotal(userRoleList.size());
            result.setData(userRoleList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);

    }

    @GetMapping("/getProduct.do")
    public void getApplication(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        List<String> applicationList = productService.getApplication();
//        System.out.println("applicationList:" + applicationList);

        result.setTotal(applicationList.size());
        result.setData(applicationList);

        outputToJSON(response, result);
    }

    @GetMapping("/getScenes.do")
    public void getScenes(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String getApplication = request.getParameter("select_Application");
//        System.out.println("getApplication: " + getApplication);
        List<String> scenesList = productService.getScenes(getApplication);
//        System.out.println("scenesList: " + scenesList);

        result.setTotal(scenesList.size());
        result.setData(scenesList);

        outputToJSON(response, result);
    }

    @GetMapping("/getProductId.do")
    public void getProductId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String getApplication = request.getParameter("select_Application");
        String getScene = request.getParameter("select_Scene");
//        System.out.println("getApplication + getScene ： " + getApplication + getScene);
        String productId = productService.getProductId(getApplication, getScene);
//        System.out.println(productId);
        if (productId == null)
            result.setSuccess(false);
        result.setData(productId);
        outputToJSON(response, result);
    }

    @GetMapping("/getExampleName.do")
    public void getExampleName(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        Random r = new Random();
        String productId = request.getParameter("productId");
        
        Integer exampleRandom = r.nextInt(1000);
        String exampleName = Integer.toString(exampleRandom);
        // exampleName = "985";
        Integer id = productService.getExampleId(productId, exampleName);
        // System.out.println("id: " + id);
        while (id != null) {
            // System.out.println("yes");
            exampleRandom = r.nextInt(1000);
            exampleName = Integer.toString(exampleRandom);
            id = productService.getExampleId(productId, exampleName);
        }
        // System.out.println((exampleName));
        // Integer exampleRandom = r.nextInt(1000);
        // String exampleName = Integer.toString(exampleRandom);
        // // exampleName = "894";
        // Integer id = productService.getExampleId(productId, exampleName);
        // // System.out.println("id:" + id);
        // Assert.isNull(id, "产品实例名称已存在");
        productService.addExample(productId, exampleName);
        result.setData(exampleName);
        outputToJSON(response, result);

    }

    @RequestMapping("/getExample.do")
    public void getExample(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String processid = request.getParameter("processid");
//        System.out.println(processid);
        productService.getExample(processid);
        Map<String, String> example1 = new HashMap<>();
        example1.put("应用", "application1");
        example1.put("场景", "预发");
        result.setData(example1);
        outputToJSON(response, result);
    }

    @GetMapping("/getNonLoginPreUrl.do")
    public void getNonLoginPreUrl(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String productId = request.getParameter("productId");
            Assert.hasText(productId, "产品号不能为空!");
            String roleIdStr = request.getParameter("roleId");
            Assert.hasText(roleIdStr, "roleId不能为空!");
            String exampleName = request.getParameter("exampleName");
            // System.out.println("exampleName: " + exampleName);
            Assert.hasText(exampleName, "实例名称不能为空！");
            // System.out.println("exampleName: " + exampleName);

            Integer roleId = Integer.valueOf(roleIdStr);
            String region = systemConfigService.getStringValueFromCache("region");

            // 获取当前用户信息以及所需要使用的ram角色信息
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(userName);
            UserRole userRole = userRoleService.getUserRoleById(roleId);

            String nonLoginPreUrl = preViewService.getNonLoginPreUrl(productId, exampleName, region, user, userRole);
            // System.out.println(nonLoginPreUrl);
            result.setData(nonLoginPreUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

}
