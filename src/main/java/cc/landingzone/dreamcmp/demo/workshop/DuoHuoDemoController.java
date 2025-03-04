package cc.landingzone.dreamcmp.demo.workshop;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.model.WebResult;
import cc.landingzone.dreamcmp.demo.workshop.service.AckService;
import cc.landingzone.dreamcmp.demo.workshop.service.FcService;
import com.aliyun.fc20230330.models.InvokeFunctionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 * Author: laodouza
 * Date: 2025/2/14
 */
@RestController
@RequestMapping(value = "workshop/duohuo")
public class DuoHuoDemoController extends BaseController {

    @Autowired
    private FcService fcService;

    @Autowired
    private AckService ackService;

    @Value("${dreamcmp.workshop.grafana_url}")
    private String WORKSHOP_GRAFANA_URL;

    @Value("${dreamcmp.workshop.duohuo_function_name}")
    private String DUOHUO_FC_FUNCTION_NAME;

    @Value("${dreamcmp.workshop.ack_hangzhou_i_id}")
    private String ACK_HANGZHOU_I_ID;

    @Value("${dreamcmp.workshop.ack_hangzhou_j_id}")
    private String ACK_HANGZHOU_J_ID;

    public static final String REGION_HANGZHOU_I = "izone";

    public static final String REGION_HANGZHOU_J = "jzone";

    @PostMapping("/getGrafanaUrl.do")
    public void getGrafanaUrl(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String redirectUrl = WORKSHOP_GRAFANA_URL;
            result.setData(redirectUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/processContainerGroup.do")
    public void processContainerGroup(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 调FC -> 调系统接口 获取故障状态8080
        WebResult result = new WebResult();
        String action = params.get("action");
        String regionId = params.get("regionId");
        String clusterId;
        if (REGION_HANGZHOU_I.equals(regionId)) {
            clusterId = ACK_HANGZHOU_I_ID;
        } else {
            clusterId = ACK_HANGZHOU_J_ID;
        }
        String clusterUserKubeconfig = ackService.describeClusterUserKubeconfig(clusterId);
        JSONObject payload = new JSONObject()
            .fluentPut("action", action)
            .fluentPut("clusterUserKubeconfig", clusterUserKubeconfig);
        try {
            InvokeFunctionResponse invokeFunctionResponse = fcService.invokeFunction(DUOHUO_FC_FUNCTION_NAME, JSON.toJSONString(payload));
            result.setSuccess(true);
            result.setData(invokeFunctionResponse.getBody().toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
