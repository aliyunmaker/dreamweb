package cc.landingzone.dreamweb.demo.resourcecenter;

import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.ResourceUtil;
import cc.landingzone.dreamweb.common.model.WebResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/resources/")
public class ResourceCenterController extends BaseController {
    @RequestMapping("/listResources.do")
    public void listResources(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Map<String, Map<String, Map<String, Integer>>> resourcesCounts = ResourceUtil.listAccountRegionResourcesCounts();
            result.setTotal(resourcesCounts.size());
            result.setData(resourcesCounts);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
