package cc.landingzone.dreamcmp.demo.resourcecenter;

import com.alibaba.fastjson.JSONObject;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.ResourceUtil;
import cc.landingzone.dreamcmp.common.model.WebResult;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/resources/")
public class ResourceCenterController extends BaseController {
    @RequestMapping("/listResources.do")
    public void listResources(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String resourceDirectoryId = ResourceUtil.getResourceDirectoryId();
            Map<String, Map<String, Map<String, Integer>>> accountRegionResourcesCounts = ResourceUtil.listAccountRegionResourcesCounts(resourceDirectoryId);
            List<String> accountsWithoutResources = ResourceUtil.listAccountsWithoutResources(accountRegionResourcesCounts);
            JSONObject data = new JSONObject();
            data.put("resourceDirectoryId", resourceDirectoryId);
            data.put("resourceCenterAdminName", CommonConstants.RESOURCE_CENTER_ADMIN_NAME);
            data.put("accountRegionResourcesCounts", accountRegionResourcesCounts);
            data.put("accountsWithoutResources", accountsWithoutResources);
            result.setTotal(data.size());
            result.setData(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
