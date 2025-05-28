package cc.landingzone.dreamcmp.demo.workshop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.model.WebResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description:
 * Author: laodouza
 * Date: 2025/5/27
 */
@Controller
@RequestMapping(value = "workshop/cms2")
public class Cms2Controller extends BaseController {

    @Value("${dreamcmp.workshop.cms2.biz_grafana_url}")
    private String BIZ_GRAFANA_URL;

    @Value("${dreamcmp.workshop.cms2.infrastructure_grafana_url}")
    private String INFRASTRUCTURE_GRAFANA_URL;

    @RequestMapping("/getBizGrafanaDashboard.do")
    public void getBizGrafanaDashboard(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String redirectUrl = BIZ_GRAFANA_URL;
            result.setData(redirectUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getInfrastructureGrafanaDashboard.do")
    public void getInfrastructureGrafanaDashboard(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String redirectUrl = INFRASTRUCTURE_GRAFANA_URL;
            result.setData(redirectUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
