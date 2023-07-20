package cc.landingzone.dreamweb.demo.resourceapply;

import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.ServiceHelper;
import cc.landingzone.dreamweb.common.model.WebResult;
import com.aliyun.vpc20160428.models.DescribeVSwitchAttributesResponseBody;
import com.aliyun.vpc20160428.models.DescribeVpcAttributeResponseBody;
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

@Controller
@RequestMapping("/resourceSupply")
public class ResourceApplyController extends BaseController {

    @Autowired
    private ApplicationContext applicationContext;

    @PostMapping(
            path = "/getTemplateByResourceType.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void getTemplateByResourceType(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String resourceType = request.getParameter("resourceType");
            String fileType = request.getParameter("fileType");
            logger.info("resourceType: " + resourceType);
            logger.info("fileType: " + fileType);
            String filePath = "classpath:" +
                    "resource_apply_template/" + fileType + "/" + resourceType + "/" + resourceType + ".txt";
            logger.info("filePath: " + filePath);
            Resource resource = applicationContext.getResource(filePath);
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            String template = FileCopyUtils.copyToString(reader);
            logger.info("template: " + template);
            result.setData(template);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping(
            path = "/createEcsInstance.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void createEcsInstance(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String applicationName = request.getParameter("applicationName");
            String environmentName = request.getParameter("environmentName");
            String regionId = request.getParameter("regionId");
            String vpcId = request.getParameter("vpcId");
            String vSwitchId = request.getParameter("vSwitchId");
            String instanceType = request.getParameter("instanceType");
//            String instanceName = request.getParameter("instanceName");
            int amount = Integer.parseInt(request.getParameter("amount"));
            Assert.isTrue(StringUtils.isNotBlank(applicationName), "applicationName must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(environmentName), "environmentName must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(regionId), "regionId must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(vpcId), "vpcId must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(vSwitchId), "vSwitchId must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(instanceType), "instanceType must not be blank");
//            Assert.isTrue(StringUtils.isNotBlank(instanceName), "instanceName must not be blank");
            Assert.isTrue(amount > 0, "amount must be greater than 0");
            Assert.isTrue(amount <= 100, "amount must be less than 100");
            ResourceApplyUtil.createEcsInstance(regionId, vpcId,vSwitchId, instanceType, amount, applicationName,
                    environmentName);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping(
            path = "/createOssBucket.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void createOssBucket(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String bucketName = request.getParameter("bucketName");
            String applicationName = request.getParameter("applicationName");
            String environmentName = request.getParameter("environmentName");
            Assert.isTrue(StringUtils.isNotBlank(applicationName), "applicationName must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(environmentName), "environmentName must not be blank");
            Assert.isTrue(bucketName.length() <= 63, "bucketName length must be less than 63");
            Assert.isTrue(bucketName.length() >= 3, "bucketName length must be more than 3");
            Assert.isTrue(bucketName.matches("^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$"),
                    "bucketName 只能包括小写字母、数字和短划线（-）, 且必须以小写字母或者数字开头和结尾");
            logger.info("bucketName: " + bucketName);
            logger.info("applicationName: " + applicationName);
            logger.info("environment: " + environmentName);
            ResourceApplyUtil.createOssBucket(bucketName, applicationName, environmentName);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping(
            path = "/createLogProject.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void createLogProject(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String projectName = request.getParameter("projectName");
            String description = request.getParameter("description");
            String applicationName = request.getParameter("applicationName");
            String environmentName = request.getParameter("environmentName");
            Assert.isTrue(StringUtils.isNotBlank(applicationName), "applicationName must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(environmentName), "environmentName must not be blank");
            Assert.isTrue(projectName.length() <= 63, "projectName length must be less than 63");
            Assert.isTrue(projectName.length() >= 3, "projectName length must be more than 3");
            Assert.isTrue(projectName.matches("^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$"),
                    "projectName 只能包括小写字母、数字和短划线（-）, 且必须以小写字母或者数字开头和结尾");
            Assert.isTrue(StringUtils.isNotEmpty(description), "description can not be empty");
            ResourceApplyUtil.createLogProject(projectName, description, applicationName, environmentName);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping(
            path = "/getVSwitches.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void getVSwitches(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
//            String applicationName = request.getParameter("applicationName");
            String environment = request.getParameter("environmentName");
            String vpcId = request.getParameter("vpcId");
//            Assert.isTrue(StringUtils.isNotEmpty(applicationName), "applicationName can not be empty");
            Assert.isTrue(StringUtils.isNotEmpty(environment), "environment can not be empty");
            Assert.isTrue(StringUtils.isNotEmpty(vpcId), "vpcId can not be empty");
//            logger.info("applicationName: " + applicationName);
            logger.info("environment: " + environment);
            logger.info("vpcId: " + vpcId);
            DescribeVpcAttributeResponseBody.DescribeVpcAttributeResponseBodyVSwitchIds vSwitchIds =
                    ServiceHelper.describeVpcAttribute(vpcId).getVSwitchIds();
            List<String> vSwitches = new ArrayList<>();
            for (String vSwitchId : vSwitchIds.getVSwitchId()) {
                DescribeVSwitchAttributesResponseBody responseBody =
                        ServiceHelper.describeVSwitchAttribute(vSwitchId);
                if(ResourceApplyUtil.isVSwitchTagMatch(responseBody, environment)) {
                    vSwitches.add(responseBody.getVSwitchName() + " / " + vSwitchId);
                }
            }
            logger.info("vSwitches: " + vSwitches);
            result.setData(vSwitches);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping(
            path = "/getVpcList.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void getVpcList(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
//            String applicationName = request.getParameter("applicationName");
            String environment = request.getParameter("environmentName");
//            Assert.isTrue(StringUtils.isNotEmpty(applicationName), "applicationName can not be empty");
            Assert.isTrue(StringUtils.isNotEmpty(environment), "environment can not be empty");
//            logger.info("applicationName: " + applicationName);
            logger.info("environment: " + environment);
            String resourceType = CommonConstants.VPC_RESOURCETYPE;
            List<String> resourceIds = ServiceHelper.listResourcesByTag(environment, resourceType);
            List<String> vpcList = new ArrayList<>();
            for (String resourceId : resourceIds) {
                String vpcName = ServiceHelper.describeVpcAttribute(resourceId).getVpcName();
                vpcList.add(vpcName + " / " + resourceId);
            }
            logger.info("vpcList: " + vpcList);
            result.setData(vpcList);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
