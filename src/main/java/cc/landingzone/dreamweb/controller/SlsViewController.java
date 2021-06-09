package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.*;
import cc.landingzone.dreamweb.service.SlsConfigService;
import cc.landingzone.dreamweb.service.SlsViewService;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/slsView")
public class SlsViewController extends BaseController{
    @Autowired
    SlsViewService slsViewService;

    @Autowired
    SlsConfigService slsConfigService;

    /**
     * 获取当前SLS配置下的全部Projects信息
     * @param request
     * @param response
     */
    @GetMapping("/getProjects.do")
    public void getProjects(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String startStr = request.getParameter("start");
            Assert.hasText(startStr, "start不能为空!");
            String limitStr = request.getParameter("limit");
            Assert.hasText(limitStr, "limit不能为空!");

            Integer start = Integer.valueOf(startStr);
            Integer limit = Integer.valueOf(limitStr);

            Page page = new Page(start, limit);
            // 获取当前账号下的SLS配置信息
            SlsConfigInfo slsConfigInfo = slsConfigService.getSlsConfigInfoFromCache();
            Assert.notNull(slsConfigInfo, "账号下SLS配置不存在！请先进行SLS配置");
            List<SlsProjectInfo> projectInfoList = slsViewService.listProjectsInfo(page, slsConfigInfo);

            result.setTotal(projectInfoList.size());
            result.setData(projectInfoList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    /**
     * 获取当前SLS配置下指定Project中的全部Logstore信息
     * @param request
     * @param response
     */
    @GetMapping("/getLogstores.do")
    public void getLogstores(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String projectName = request.getParameter("projectName");
            String region = request.getParameter("region");
            Assert.hasText(projectName, "project名称不能为空!");
            Assert.hasText(region, "region不能为空!");
            String startStr = request.getParameter("start");
            Assert.hasText(startStr, "start不能为空!");
            String limitStr = request.getParameter("limit");
            Assert.hasText(limitStr, "limit不能为空!");

            Integer start = Integer.valueOf(startStr);
            Integer limit = Integer.valueOf(limitStr);

            Page page = new Page(start, limit);
            // 获取当前账号下的SLS配置信息
            SlsConfigInfo slsConfigInfo = slsConfigService.getSlsConfigInfoFromCache();
            Assert.notNull(slsConfigInfo, "账号下SLS配置不存在！请先进行SLS配置");
            List<String> logstoreList = slsViewService.listLogstoresInfo(projectName, page, slsConfigInfo);

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
     * @param request
     * @param response
     */
    @GetMapping("/getNonLoginSlsUrl.do")
    public void getNonLoginSlsUrl(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String projectName = request.getParameter("projectName");
            String region = request.getParameter("region");
            String logstoreName = request.getParameter("logstoreName");

            Assert.hasText(projectName, "project名称不能为空!");
            Assert.hasText(logstoreName, "logstore名称不能为空!");
            Assert.hasText(region, "region不能为空!");

            SlsConfigInfo slsConfigInfo = slsConfigService.getSlsConfigInfoFromCache();
            Assert.notNull(slsConfigInfo, "账号下SLS配置不存在！请先进行SLS配置");
            String nonLoginSlsUrl = slsViewService.getNonLoginSlsUrl(projectName, logstoreName, slsConfigInfo);

            result.setData(nonLoginSlsUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }
}
