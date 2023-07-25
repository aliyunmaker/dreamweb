package cc.landingzone.dreamcmp.demo.akapply;

import com.aliyun.ram20150501.models.CreateAccessKeyResponseBody;

import cc.landingzone.dreamcmp.common.*;
import cc.landingzone.dreamcmp.common.model.WebResult;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/akApply")
public class AkApplyController extends BaseController {

    @Autowired
    private ApplicationContext applicationContext;

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
        String generatePolicyDocument = ServiceHelper.generatePolicyDocument
                (resourceType, resourceNameList, actionCode, accountId);
        logger.info("generatePolicyDocument: " + generatePolicyDocument);
        outputToString(response, generatePolicyDocument);
    }

    @PostMapping(
            path = "/akApplySubmit.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void akApplySubmit(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String applicationName = request.getParameter("applicationName");
            String environment = request.getParameter("environment");
            String policyDocument = request.getParameter("policyDocument");
            String policyName = applicationName + "-" + environment + "-" + UUID.randomUUID();
            String username = applicationName + "-" + environment;
            logger.info("username: " + username);
            logger.info("policyName: " + policyName);
            long startTime = System.currentTimeMillis();
            RamHelper.createPolicy(policyName, policyDocument);
            long createPolicyTime = System.currentTimeMillis();
            logger.info("createPolicyTime: " + (createPolicyTime - startTime) + "ms");
            AkApplyUtil.createRamUser(username);
            long createRamUserTime = System.currentTimeMillis();
            logger.info("createRamUserTime: " + (createRamUserTime - createPolicyTime) + "ms");
            RamHelper.attachPolicyToUser(username, policyName, "Custom");
            long attachPolicyToUserTime = System.currentTimeMillis();
            logger.info("attachPolicyToUserTime: " + (attachPolicyToUserTime - createRamUserTime) + "ms");
            CreateAccessKeyResponseBody.CreateAccessKeyResponseBodyAccessKey accessKey = RamHelper.createAccessKey(username);
            assert accessKey != null;
            logger.info("accessKeyId: " + accessKey.accessKeyId);
            logger.info("accessKeySecret: " + accessKey.accessKeySecret);
            long createAccessKeyTime = System.currentTimeMillis();
            logger.info("createAccessKeyTime: " + (createAccessKeyTime - attachPolicyToUserTime) + "ms");
            String secretName = AkApplyUtil.createSecretByExist(applicationName,environment,username,
                    accessKey.accessKeyId, accessKey.accessKeySecret);
            long createSecretTime = System.currentTimeMillis();
            logger.info("createSecretTime: " + (createSecretTime - createAccessKeyTime) + "ms");
            logger.info("secretName: " + secretName);
            result.setData(secretName);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping(
            path = "/listResourcesByAppEnvAndResType.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void listResourcesByAppEnvAndResType(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String applicationName = request.getParameter("applicationName");
            String environment = request.getParameter("environment");
            String resourceType = Objects.requireNonNull(ServiceEnum.getServiceEnumByResourceName
                    (request.getParameter("resourceType"))).getResourceType();
            Assert.isTrue(StringUtils.isNotEmpty(applicationName), "applicationName can not be empty");
            Assert.isTrue(StringUtils.isNotEmpty(environment), "environment can not be empty");
            Assert.isTrue(StringUtils.isNotEmpty(resourceType), "resourceType can not be empty");
            logger.info("applicationName: " + applicationName);
            logger.info("environment: " + environment);
            logger.info("resourceType: " + resourceType);
            long startTime = System.currentTimeMillis();
            List<String> resourceNames = ServiceHelper.listResourcesByTag(applicationName, environment, resourceType);
            logger.info("resourceNames: " + resourceNames);
            logger.info("listResourcesByTagTime: " + (System.currentTimeMillis() - startTime) + "ms");
            result.setData(resourceNames);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getSecretNameUseSample.do")
    public void getSecretNameUseSample(HttpServletRequest request, HttpServletResponse response) {
       WebResult result = new WebResult();
       try {
           String filePath = "classpath:secretNameUseSample.md";
           Resource resource = applicationContext.getResource(filePath);
           Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
           logger.info("filePath: " + filePath);
           String useSample = FileCopyUtils.copyToString(reader);
           result.setData(useSample);
       }catch (Exception e){
           logger.error(e.getMessage());
           result.setSuccess(false);
           result.setErrorMsg(e.getMessage());
       }
       outputToJSON(response, result);
    }

    @RequestMapping("/checkSecretName.do")
    public void checkSecretNameByApplication(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String applicationName = request.getParameter("applicationName");
            Assert.isTrue(StringUtils.isNotEmpty(applicationName), "applicationName can not be empty");
            String environment = "product";
            String ramUserName = applicationName + "-" + environment;
            String filters = "[{\"Key\":\"SecretName\", \"Values\":[\"" + ramUserName + "\"]},"
                    + "{\"Key\":\"DKMSInstanceId\", \"Values\":[\"" + CommonConstants.DKMSInstanceId + "\"]}]";
            List<String> listSecrets = KMSHelper.listSecrets(filters);
            String secretName = null;
            if (listSecrets.size() > 0) {
                secretName = listSecrets.get(0);
            }
            result.setData(secretName);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }


}