package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.AccountEcsInfo;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.SlsAutoConfigService;
import cc.landingzone.dreamweb.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/slsAutoConfig")
public class SlsAutoConfigController extends BaseController {

    private static String INSTALL = "install";
    private static String UNINSTALL = "uninstall";

    @Autowired
    SlsAutoConfigService slsAutoConfigService;

    @GetMapping("/getEcsList.do")
    public void getAccounts(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String accessKey = request.getParameter("accessKey");
            Assert.hasText(accessKey, "accessKey不能为空!");
            String secretKey = request.getParameter("secretKey");
            Assert.hasText(secretKey, "secretKey不能为空!");

            List<AccountEcsInfo> ecsList = slsAutoConfigService.getEcsList(accessKey, secretKey);
            result.setData(ecsList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        outputToJSON(response, result);
    }

    @PostMapping("/slsAutoConfig.do")
    public void slsAutoConfig(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder result = new StringBuilder();
        try {
            String accessKey = request.getParameter("accessKey");
            Assert.hasText(accessKey, "accessKey不能为空!");
            String secretKey = request.getParameter("secretKey");
            Assert.hasText(secretKey, "secretKey不能为空!");
            String ecsListJson = request.getParameter("ecsJson");

            List<AccountEcsInfo> ecsList;
            if(ecsListJson == null || ecsListJson.trim().equals("")) {
                ecsList = slsAutoConfigService.getEcsList(accessKey, secretKey);
            }else {
                ecsList = JsonUtils.parseArray(ecsListJson, AccountEcsInfo.class);
            }

            result.append(slsAutoConfigService.initLogtail(ecsList, accessKey, secretKey, INSTALL));
            result.append(slsAutoConfigService.initSlsService(ecsList, accessKey, secretKey, INSTALL));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.append(e.getMessage());
        }

        outputToJSON(response, result.toString());
    }

    @PostMapping("/slsConfigRollback.do")
    public void slsConfigRollback(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder result = new StringBuilder();
        try {
            String accessKey = request.getParameter("accessKey");
            Assert.hasText(accessKey, "accessKey不能为空!");
            String secretKey = request.getParameter("secretKey");
            Assert.hasText(secretKey, "secretKey不能为空!");
            String ecsListJson = request.getParameter("ecsList");

            List<AccountEcsInfo> ecsList;
            if(ecsListJson == null) {
                ecsList = slsAutoConfigService.getEcsList(accessKey, secretKey);
            }else {
                ecsList = JsonUtils.parseArray(ecsListJson, AccountEcsInfo.class);
            }

            result.append(slsAutoConfigService.initLogtail(ecsList, accessKey, secretKey, UNINSTALL));
            result.append(slsAutoConfigService.initSlsService(ecsList, accessKey, secretKey, UNINSTALL));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.append(e.getMessage());
        }

        outputToJSON(response, result.toString());
    }
}
