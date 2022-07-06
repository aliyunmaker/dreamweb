package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.WebResult;
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
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();  //拿到用户名
            Integer roleId = userRoleService.getRoleIdByUserName(userName);
            if(roleId != null) {
                UserRole userRole = userRoleService.getUserRoleById(roleId);
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
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();  //拿到用户名
            Integer role = userRoleService.getRoleIdByUserName(userName);//数据库中结果
            String Id = request.getParameter("id");
            Integer roleId = Integer.parseInt(Id);
            if(role == null) {
                userRoleService.saveUserRole(userName, roleId);
            } else {
                userRoleService.updateUserRole2(userName, roleId);
            }
            result.setSuccess(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getRoleIdByUserName.do")
    public void getRoleIdByUserName(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();  //拿到用户名
        Integer role = userRoleService.getRoleIdByUserName(userName);//数据库中结果
        if(role == null) {
            result.setSuccess(true);
        } else {
            result.setData(role);
            result.setSuccess(true);
        }
        outputToJSON(response, result);
    }
}
