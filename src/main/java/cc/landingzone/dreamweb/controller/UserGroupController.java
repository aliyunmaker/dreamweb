package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserGroup;
import cc.landingzone.dreamweb.model.UserGroupAssociate;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.UserGroupAssociateService;
import cc.landingzone.dreamweb.service.UserGroupService;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userGroup")
public class UserGroupController extends BaseController {
    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private UserGroupAssociateService userGroupAssociateService;

    @Autowired
    private UserService userService;

    @RequestMapping("/getAllUserGroups.do")
    public void getAllUserGroups(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            List<UserGroup> list = userGroupService.getAllUserGroups();
            result.setTotal(list.size());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/addUserGroup.do")
    public void addUserGroup(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            UserGroup userGroup = JsonUtils.parseObject(formString, UserGroup.class);
            userGroupService.addUserGroup(userGroup);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateUserGroup.do")
    public void updateUserGroup(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String formString = request.getParameter("formString");
            UserGroup userGroup = JsonUtils.parseObject(formString, UserGroup.class);
            userGroupService.updateUserGroup(userGroup);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteUserGroup.do")
    public void deleteUserGroup(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer id = Integer.valueOf(request.getParameter("id"));
            userGroupService.deleteUserGroup(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    // *********************************************************************

    @RequestMapping("/getUsersByUserGroupId.do")
    public void getUsersByUserGroupId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer userGroupId = Integer.valueOf(request.getParameter("userGroupId"));
            List<User> list = userGroupService.getUsersByUserGroupId(userGroupId);
            result.setTotal(list.size());
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/addUserGroupAssociate.do")
    public void addUserGroupAssociate(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer userId = Integer.valueOf(request.getParameter("userId"));
            Integer userGroupId = Integer.valueOf(request.getParameter("userGroupId"));
            UserGroupAssociate userGroupAssociate = new UserGroupAssociate();
            userGroupAssociate.setUserGroupId(userGroupId);
            userGroupAssociate.setUserId(userId);
            userGroupAssociateService.addUserGroupAssociate(userGroupAssociate);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/batchAddUserGroupAssociate.do")
    public void batchAddUserGroupAssociate(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer userGroupId = Integer.valueOf(request.getParameter("userGroupId"));
            List<String> userLoginNames = Arrays
                    .asList(request.getParameter("userLoginNames").replaceAll("\\s", "").split(","));
            List<User> users = userService.getUsersByLoginNames(userLoginNames);
            List<UserGroupAssociate> userGroupAssociates = users.stream()
                .map(user -> {
                    UserGroupAssociate userGroupAssociate = new UserGroupAssociate();
                    userGroupAssociate.setUserGroupId(userGroupId);
                    userGroupAssociate.setUserId(user.getId());
                    return userGroupAssociate;
                })
                .collect(Collectors.toList());
            userGroupAssociateService.addUserGroupAssociates(userGroupAssociates);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteUserGroupAssociate.do")
    public void deleteUserGroupAssociate(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer userId = Integer.valueOf(request.getParameter("userId"));
            Integer userGroupId = Integer.valueOf(request.getParameter("userGroupId"));
            userGroupAssociateService.deleteUserGroupAssociate(userId, userGroupId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
