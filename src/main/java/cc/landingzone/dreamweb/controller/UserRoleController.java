package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.UserRoleService;
import cc.landingzone.dreamweb.utils.JsonUtils;
import cc.landingzone.dreamweb.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userRole")
public class UserRoleController extends BaseController {
    @Autowired
    private UserRoleService userRoleService;

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
            SecurityUtils.xssFilter(userRole);
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
            SecurityUtils.xssFilter(userRole);
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

}
