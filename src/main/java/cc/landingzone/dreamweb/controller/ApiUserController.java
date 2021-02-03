package cc.landingzone.dreamweb.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamweb.model.ApiUser;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.ApiUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author merc-bottle
 * @date 2021/02/02
 */
@Controller
@RequestMapping("/apiUser")
public class ApiUserController extends BaseController {

    @Autowired
    private ApiUserService apiUserService;

    private Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @RequestMapping("/addApiUser.do")
    public void addApiUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String comment = request.getParameter("comment");
            boolean valid = Boolean.parseBoolean(request.getParameter("valid"));

            ApiUser apiUser = new ApiUser();
            apiUser.setComment(comment);
            apiUser.setValid(valid);
            apiUserService.addApiUser(apiUser);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateApiUser.do")
    public void updateApiUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String idStr = request.getParameter("id");
            Assert.hasText(idStr, "id不能为空!");
            Integer id = Integer.valueOf(idStr);

            ApiUser apiUser = apiUserService.getApiUserById(id);
            Assert.notNull(apiUser, "API账号不存在!");

            String comment = request.getParameter("comment");
            boolean valid = Boolean.parseBoolean(request.getParameter("valid"));

            apiUser.setComment(comment);
            apiUser.setValid(valid);
            apiUserService.updateApiUser(apiUser);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getAllApiUsers.do")
    public void getAllApiUsers(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            List<ApiUser> apiUsers = apiUserService.getAllApiUsers();
            result.setData(apiUsers);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteApiUser.do")
    public void deleteApiUser(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String idStr = request.getParameter("id");
            Assert.hasText(idStr, "id不能为空!");
            Integer id = Integer.valueOf(idStr);

            apiUserService.deleteApiUser(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}