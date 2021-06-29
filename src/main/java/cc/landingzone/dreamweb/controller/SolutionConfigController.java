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

import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.SolutionConfig;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.SolutionConfigService;

@Controller
@RequestMapping("/solutionConfig")
public class SolutionConfigController extends BaseController {
    
    @Autowired
    SolutionConfigService solutionConfigService;

    private Logger logger = LoggerFactory.getLogger(SolutionConfigController.class);

    @RequestMapping("/listSolutionConfig.do")
    public void listSolutionConfig(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            List<SolutionConfig> solutionConfigs = solutionConfigService.listSolutionConfig();
            result.setTotal(solutionConfigs.size());
            result.setData(solutionConfigs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getSolutionConfigByName.do")
    public void getSolutionConfigByName(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String name = request.getParameter("name");
            Assert.hasText(name, "名称不能为空!");

            SolutionConfig solutionConfig = solutionConfigService.getSolutionConfigByName(name);
            result.setData(solutionConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getSolutionNumber.do") 
    public void getSolutionNumber(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String module = request.getParameter("module");
            Assert.hasText(module, "模块不能为空!");
            String searchInput = request.getParameter("searchInput");
            Assert.notNull(searchInput, "搜索输入不能为空!");

            int solutionNumber = solutionConfigService.getSolutionNumber(module, searchInput);
            result.setData(solutionNumber);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/searchSolution.do")
    public void searchSolution(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String module = request.getParameter("module");
            Assert.hasText(module, "模块不能为空!");
            String searchInput = request.getParameter("searchInput");
            Assert.notNull(searchInput, "搜索输入不能为空!");
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Page page = new Page(start, limit);

            List<SolutionConfig> solutionConfigs = solutionConfigService.searchSolution(module, searchInput, page);
            result.setTotal(solutionConfigs.size());
            result.setData(solutionConfigs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/addSolutionConfig.do")
    public void addSolutionConfig(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String name = request.getParameter("name");
            Assert.hasText(name, "名称不能为空!");
            String intro = request.getParameter("intro");
            Assert.hasText(intro, "简介不能为空!");
            String webConfig = request.getParameter("webConfig");
            Assert.hasText(webConfig, "网页配置不能为空");
            String creator = request.getParameter("creator");
            Assert.hasText(creator, "创建人不能为空!");
            String version = request.getParameter("version");
            Assert.hasText(version, "版本不能为空!");
            String module = request.getParameter("module");
            Assert.hasText(module, "所属模块不能为空!");

            SolutionConfig solutionConfig = solutionConfigService.getSolutionConfigByName(name);
            Assert.isNull(solutionConfig, "该解决方案已存在!");
            solutionConfig = new SolutionConfig();
            solutionConfig.setName(name);
            solutionConfig.setIntro(intro);
            solutionConfig.setWebConfig(webConfig);
            solutionConfig.setCreator(creator);
            solutionConfig.setVersion(version);
            solutionConfig.setModule(module);
            solutionConfigService.addSolutionConfig(solutionConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/updateSolutionConfig.do")
    public void updateSolutionConfig(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String idStr =request.getParameter("id");
            Assert.hasText(idStr, "id不能为空!");
            Integer id = Integer.valueOf(idStr);
            String name = request.getParameter("name");
            Assert.hasText(name, "名称不能为空!");
            String intro = request.getParameter("intro");
            Assert.hasText(intro, "简介不能为空!");
            String webConfig = request.getParameter("webConfig");
            Assert.hasText(webConfig, "网页配置不能为空");
            String creator = request.getParameter("creator");
            Assert.hasText(creator, "创建人不能为空!");
            String version = request.getParameter("version");
            Assert.hasText(version, "版本不能为空!");
            String module = request.getParameter("module");
            Assert.hasText(module, "所属模块不能为空!");
            
            SolutionConfig solutionConfig = solutionConfigService.getSolutionConfigById(id);
            Assert.notNull(solutionConfig, "该解决方案不存在!");
            solutionConfig.setName(name);
            solutionConfig.setIntro(intro);
            solutionConfig.setWebConfig(webConfig);
            solutionConfig.setCreator(creator);
            solutionConfig.setVersion(version);
            solutionConfig.setModule(module);
            solutionConfigService.updateSolutionConfig(solutionConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deleteSolutionConfig.do")
    public void deleteSolutionConfig(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String name = request.getParameter("name");
            Assert.hasText(name, "名称不能为空!");

            solutionConfigService.deleteSolutionConfigByName(name);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
