package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.*;
import cc.landingzone.dreamweb.service.SlsViewService;
import cc.landingzone.dreamweb.service.SystemConfigService;
import cc.landingzone.dreamweb.service.UserRoleService;
import cc.landingzone.dreamweb.service.UserService;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

@Controller
@RequestMapping("/slsView")
public class SlsViewController extends BaseController {

    @Autowired
    SlsViewService slsViewService;

    @Autowired
    public UserService userService;

    @Autowired
    public UserRoleService userRoleService;

    @Autowired
    public SystemConfigService systemConfigService;

    /**
     * 获取当前SLS配置下的全部Projects信息
     *
     * @param request
     * @param response
     */
    @GetMapping("/getProjects.do")
    public void getProjects(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String roleIdStr = request.getParameter("roleId");
            Assert.hasText(roleIdStr, "roleId不能为空!");

            Integer roleId = Integer.valueOf(roleIdStr);

            // 获取当前用户有权访问的所有Project
            List<String> projectList = slsViewService.getProjectsWithRoleIdFromCache(roleId);

            result.setTotal(projectList.size());
            result.setData(projectList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    /**
     * 获取当前SLS配置下指定Project中的全部Logstore信息
     *
     * @param request
     * @param response
     */
    @GetMapping("/getLogstores.do")
    public void getLogstores(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String projectName = request.getParameter("projectName");
            Assert.hasText(projectName, "project名称不能为空!");
            String startStr = request.getParameter("start");
            Assert.hasText(startStr, "start不能为空!");
            String limitStr = request.getParameter("limit");
            Assert.hasText(limitStr, "limit不能为空!");
            String roleIdStr = request.getParameter("roleId");
            Assert.hasText(roleIdStr, "roleId不能为空!");

            Integer roleId = Integer.valueOf(roleIdStr);
            Integer start = Integer.valueOf(startStr);
            Integer limit = Integer.valueOf(limitStr);

            Page page = new Page(start, limit);
            String region = systemConfigService.getStringValueFromCache("region");

            // 获取当前用户信息以及所需要使用的ram角色信息
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(userName);
            UserRole userRole = userRoleService.getUserRoleById(roleId);

            List<String> logstoreList = slsViewService.listLogstoresInfo(projectName, page, region, user, userRole);

            result.setTotal(logstoreList.size());
            result.setData(logstoreList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    /**
     * 通过Project和Logstore名称，获取免登录Url
     *
     * @param request
     * @param response
     */
    @GetMapping("/getNonLoginSlsUrl.do")
    public void getNonLoginSlsUrl(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String projectName = request.getParameter("projectName");
            String logstoreName = request.getParameter("logstoreName");

            Assert.hasText(projectName, "project名称不能为空!");
            Assert.hasText(logstoreName, "logstore名称不能为空!");
            String roleIdStr = request.getParameter("roleId");
            Assert.hasText(roleIdStr, "roleId不能为空!");

            Integer roleId = Integer.valueOf(roleIdStr);
            String region = systemConfigService.getStringValueFromCache("region");

            // 获取当前用户信息以及所需要使用的ram角色信息
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(userName);
            UserRole userRole = userRoleService.getUserRoleById(roleId);

            String nonLoginSlsUrl = slsViewService.getNonLoginSlsUrl(projectName, logstoreName, region, user, userRole);

            result.setData(nonLoginSlsUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    @GetMapping("/getRoles.do")
    public void getRoles(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
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
}
