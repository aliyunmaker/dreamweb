package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.*;
import cc.landingzone.dreamweb.service.SlsConfigService;
import cc.landingzone.dreamweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/slsConfig")
public class SlsConfigController extends BaseController{
    @Autowired
    SlsConfigService slsConfigService;

    @Autowired
    UserService userService;

    /**
     * 获取所有用户的SLS配置信息
     * @param request
     * @param response
     */
    @GetMapping("/getSlsConfigs.do")
    public void getSlsConfigs(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            List<SlsConfig> slsConfigList = slsConfigService.listSlsConfig();

            result.setTotal(slsConfigList.size());
            result.setData(slsConfigList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    /**
     * 获取当前用户下的SLS配置信息
     * @param request
     * @param response
     */
    @GetMapping("/getSlsConfigByOwnerId.do")
    public void getSlsConfigByOwnerId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            // 获取当前用户id
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(username);
            Integer ownerId = user.getId();

            SlsConfig slsConfig = slsConfigService.getSlsConfigByOwnerId(ownerId);

            result.setData(slsConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    /**
     * 在当前用户下添加SLS配置
     * @param request
     * @param response
     */
    @PostMapping("/addSlsConfig.do")
    public void addSlsConfig(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String configName = request.getParameter("configName");
            Assert.hasText(configName, "配置名不能为空!");
            String configValue = request.getParameter("configValue");
            Assert.hasText(configValue, "配置值不能为空!");
            String comment = request.getParameter("comment");

            // 获取当前用户id
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(username);

            SlsConfig slsConfig = new SlsConfig();
            slsConfig.setConfigName(configName);
            slsConfig.setConfigValue(configValue);
            slsConfig.setComment(comment);
            slsConfig.setConfigOwnerId(user.getId());

            slsConfigService.addSlsConfig(slsConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    /**
     * 更新当前用户下的SLS配置
     * @param request
     * @param response
     */
    @PostMapping("/updateSlsConfig.do")
    public void updateSlsConfig(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String idStr = request.getParameter("id");
            Assert.hasText(idStr, "id不能为空!");
            Integer id = Integer.valueOf(idStr);
            String configName = request.getParameter("configName");
            Assert.hasText(configName, "配置名不能为空");
            String configValue = request.getParameter("configValue");
            Assert.hasText(configValue, "配置值不能为空!");
            String comment = request.getParameter("comment");

            SlsConfig slsConfig = slsConfigService.getSlsConfigById(id);
            Assert.notNull(slsConfig, "该配置不存在!");
            slsConfig.setConfigName(configName);
            slsConfig.setConfigValue(configValue);
            slsConfig.setComment(comment);

            slsConfigService.updateSlsConfig(slsConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    /**
     * 删除当前用户下的SLS配置
     * @param request
     * @param response
     */
    @PostMapping("/deleteSlsConfigById.do")
    public void deleteSlsConfigById(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String idStr = request.getParameter("id");
            Assert.hasText(idStr, "id不能为空!");
            Integer id = Integer.valueOf(idStr);

            slsConfigService.deleteSlsConfig(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
