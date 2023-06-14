package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import cc.landingzone.dreamweb.model.SystemConfig;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.SystemConfigService;
import cc.landingzone.dreamweb.service.UserRoleService;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userRole")
public class UserRoleController extends BaseController {
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;

    @RequestMapping("/getUserRolesByGroupId.do")
    public void getUserRolesByGroupId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer userGroupId = Integer.valueOf(request.getParameter("userGroupId"));
            List<UserRole> list = userRoleService.getUserRolesByGroupId(userGroupId);
            result.setTotal(list.size());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/addUserRole.do")
    public void addUserRole(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            Integer userGroupId = Integer.valueOf(request.getParameter("userGroupId"));
            UserRole userRole = JsonUtils.parseObject(formString, UserRole.class);
            userRole.setUserGroupId(userGroupId);
            userRoleService.addUserRole(userRole);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateUserRole.do")
    public void updateUserRole(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            UserRole userRole = JsonUtils.parseObject(formString, UserRole.class);
            userRoleService.updateUserRole(userRole);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteUserRole.do")
    public void deleteUserRole(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer id = Integer.valueOf(request.getParameter("id"));
            userRoleService.deleteUserRole(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getRolesByUser.do")
    public void getRolesByUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();  //拿到用户名
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

    @RequestMapping("/getRoleCurrent.do")
    public void getRoleCurrent(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            SystemConfig systemConfig = systemConfigService.getSystemConfigByName("roleId");
            if(systemConfig != null) {
                Integer roleid = Integer.valueOf(systemConfig.getConfigValue());
                UserRole userRole = userRoleService.getUserRoleById(roleid);
                result.setTotal(1);
                result.setData(userRole);
            } else {
                result.setSuccess(true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/roleSelect.do")
    public void roleSelect(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String key = "roleId";
            SystemConfig systemConfig =  systemConfigService.getSystemConfigByName(key);//数据库中结果
            String value = request.getParameter("id");
            if(systemConfig == null) {
                SystemConfig systemConfig1 = new SystemConfig();
                systemConfig1.setConfigName(key);
                systemConfig1.setConfigValue(value);
                systemConfig1.setComment("关联角色");
                systemConfig1.setChangeable(true);
                systemConfigService.addSystemConfig(systemConfig1);
            } else {
                systemConfig.setConfigValue(value);
                systemConfigService.updateSystemConfig(systemConfig);
            }
            result.setSuccess(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getRoleId.do")
    public void getRoleId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String key = "roleId";
        SystemConfig systemConfig = systemConfigService.getSystemConfigByName(key);
        if(systemConfig == null) {
            result.setSuccess(true);
        } else {
            String role = systemConfig.getConfigValue();//数据库中结果
            result.setData(role);
            result.setSuccess(true);
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getUserRole.do")
    public void getUserRole(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByLoginName(userName);
        String role = user.getRole();
        result.setData(role);
        result.setSuccess(true);
        outputToJSON(response, result);
    }
}
