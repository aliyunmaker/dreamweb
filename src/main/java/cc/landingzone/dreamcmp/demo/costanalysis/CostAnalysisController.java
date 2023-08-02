package cc.landingzone.dreamcmp.demo.costanalysis;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.model.WebResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/costAnalysis")
public class CostAnalysisController extends BaseController {
    @RequestMapping("/describeBillBarchart.do")
    public void describeBillBarchart(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String billingCycle = request.getParameter("billingCycle");
            String tagKey = request.getParameter("tagKey");
            String productCode = request.getParameter("productCode");

            Map<String, Float> totalAmount = CostAnalysisUtil.getTotalAmount(billingCycle, tagKey, productCode);
            result.setTotal(totalAmount.size());
            result.setData(totalAmount);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/describeBillLinechart.do")
    public void describeBillLinechart(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String billingCycleStart = request.getParameter("billingCycleStart");
            String billingCycleEnd = request.getParameter("billingCycleEnd");
            String tagKey = request.getParameter("tagKey");
            String productCode = request.getParameter("productCode");

            Map<String, Map<String, Float>> periodTotalAmounts = CostAnalysisUtil.getPeriodTotalAmounts(billingCycleStart, billingCycleEnd, tagKey, productCode);
            result.setTotal(periodTotalAmounts.size());
            result.setData(periodTotalAmounts);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
