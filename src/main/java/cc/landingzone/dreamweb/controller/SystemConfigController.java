package cc.landingzone.dreamweb.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamweb.model.SystemConfig;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.SystemConfigService;

@Controller
@RequestMapping("/systemConfig")
public class SystemConfigController extends BaseController {

    @Autowired
    SystemConfigService systemConfigService;

    private Logger logger = LoggerFactory.getLogger(SystemConfigController.class);

    @RequestMapping("/listSystemConfig.do")
    public void listSystemConfig(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            List<SystemConfig> systemConfigs = systemConfigService.listSystemConfig();
            result.setTotal(systemConfigs.size());
            result.setData(systemConfigs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/addSystemConfig.do")
    public void addSystemConfig(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String configName = request.getParameter("configName");
            SystemConfig systemConfig = systemConfigService.getSystemConfigByName(configName);
            String configValue = request.getParameter("configValue");
            String comment = request.getParameter("comment");
            Boolean changeable = Boolean.parseBoolean(request.getParameter("changeable"));

            systemConfig = new SystemConfig();
            systemConfig.setConfigName(configName);
            systemConfig.setConfigValue(configValue);
            systemConfig.setComment(comment);
            systemConfig.setChangeable(changeable);
            systemConfigService.addSystemConfig(systemConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateSystemConfig.do")
    public void updateSystemConfig(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String idStr = request.getParameter("id");
            Assert.hasText(idStr, "id不能为空!");
            Integer id = Integer.valueOf(idStr);
            SystemConfig systemConfig = systemConfigService.getSystemConfigById(id);
            Assert.notNull(systemConfig, "配置不能为空!");

            String configValue = request.getParameter("configValue");
            String comment = request.getParameter("comment");
            Boolean changeable = Boolean.parseBoolean(request.getParameter("changeable"));
            
            systemConfig.setConfigValue(configValue);
            systemConfig.setComment(comment);
            systemConfig.setChangeable(changeable);
            systemConfigService.updateSystemConfig(systemConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteSystemConfig.do")
    public void deleteSystemConfig(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String idStr = request.getParameter("id");
            Assert.hasText(idStr, "id不能为空!");
            Integer id = Integer.valueOf(idStr);

            systemConfigService.deleteSystemConfig(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
