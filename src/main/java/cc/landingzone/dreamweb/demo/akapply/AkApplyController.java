package cc.landingzone.dreamweb.demo.akapply;

import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.CommonConstants;
import com.aliyun.ram20150501.models.CreateAccessKeyResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/akApply")
public class AkApplyController extends BaseController {

    @PostMapping(
            path = "/generatePolicyDocument.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void generatePolicyDocument(HttpServletRequest request, HttpServletResponse response) {
        String resourceType = request.getParameter("resourceType");
        Assert.isTrue(StringUtils.isNotEmpty(resourceType), "resourceType can not be empty");
        List<String> resourceNameList = new ArrayList<>();
        Assert.isTrue(StringUtils.isNotEmpty(request.getParameter("resourceName")), "resourceName can not be empty");
        resourceNameList.add(request.getParameter("resourceName"));
        Assert.isTrue(StringUtils.isNotEmpty(request.getParameter("actionCode")), "actionCode can not be empty");
        int actionCode = Integer.parseInt(request.getParameter("actionCode"));
        String accountId = CommonConstants.Aliyun_UserId;

        logger.info("resourceType: " + resourceType);
        logger.info("resourceNameList: " + resourceNameList);
        logger.info("actionCode: " + actionCode);
        String generatePolicyDocument = AkApplyUtil.generatePolicyDocument
                (resourceType, resourceNameList, actionCode, accountId);
        logger.info("generatePolicyDocument: " + generatePolicyDocument);
        outputToString(response, generatePolicyDocument);
    }

    @PostMapping(
            path = "/akApplySubmit.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void akApplySubmit(HttpServletRequest request, HttpServletResponse response) {
        String applicationName = request.getParameter("applicationName");
        String environment = request.getParameter("environment");
        String policyDocument = request.getParameter("policyDocument");
        String policyName = applicationName + "-" + environment + "-" + UUID.randomUUID();
        String username = applicationName + "-" + environment;
        logger.info("username: " + username);
        logger.info("policyName: " + policyName);
        long startTime = System.currentTimeMillis();
        AkApplyUtil.createPolicy(policyName, policyDocument);
        long createPolicyTime = System.currentTimeMillis();
        logger.info("createPolicyTime: " + (createPolicyTime - startTime) + "ms");
        AkApplyUtil.createRamUser(username);
        long createRamUserTime = System.currentTimeMillis();
        logger.info("createRamUserTime: " + (createRamUserTime - createPolicyTime) + "ms");
        AkApplyUtil.attachPolicyToUser(username, policyName, "Custom");
        long attachPolicyToUserTime = System.currentTimeMillis();
        logger.info("attachPolicyToUserTime: " + (attachPolicyToUserTime - createRamUserTime) + "ms");
        CreateAccessKeyResponseBody.CreateAccessKeyResponseBodyAccessKey accessKey = AkApplyUtil.createAccessKey(username);
        assert accessKey != null;
        logger.info("accessKeyId: " + accessKey.accessKeyId);
        logger.info("accessKeySecret: " + accessKey.accessKeySecret);
        logger.info("createAccessKeyTime: " + (System.currentTimeMillis() - attachPolicyToUserTime) + "ms");
        outputToJSON(response, accessKey);
    }

    @PostMapping(
            path = "/listResourcesByAppEnvAndResType.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void listResourcesByAppEnvAndResType(HttpServletRequest request, HttpServletResponse response) {
        String applicationName = request.getParameter("applicationName");
        String environment = request.getParameter("environment");
        String resourceType = request.getParameter("resourceType");
        Assert.isTrue(StringUtils.isNotEmpty(applicationName), "applicationName can not be empty");
        Assert.isTrue(StringUtils.isNotEmpty(environment), "environment can not be empty");
        Assert.isTrue(StringUtils.isNotEmpty(resourceType), "resourceType can not be empty");
        logger.info("applicationName: " + applicationName);
        logger.info("environment: " + environment);
        logger.info("resourceType: " + resourceType);
        long startTime = System.currentTimeMillis();
        List<String> resourceNames = AkApplyUtil.listResourcesByTag(applicationName, environment,resourceType);
        logger.info("listResourcesByTagTime: " + (System.currentTimeMillis() - startTime) + "ms");
        outputToJSON(response, resourceNames);
    }
}