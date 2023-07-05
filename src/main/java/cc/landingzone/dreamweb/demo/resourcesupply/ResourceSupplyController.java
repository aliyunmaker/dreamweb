package cc.landingzone.dreamweb.demo.resourcesupply;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aliyun.vpc20160428.models.DescribeVSwitchAttributesResponseBody;
import com.aliyun.vpc20160428.models.DescribeVpcAttributeResponseBody;

import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.ServiceHelper;
import cc.landingzone.dreamweb.common.model.WebResult;

@Controller
@RequestMapping("/resourceSupply")
public class ResourceSupplyController extends BaseController {

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
            String filePath = "src/main/resources/resource_supply_template/" +
                    fileType + "/" + resourceType + "/" + StringUtils.capitalize(resourceType);

            switch (fileType) {
                case "terraform":
                   filePath += ".tf";
                   break;
                case "java":
                     filePath += ".java";
                     break;
                case "ccapi":
                    filePath += "Ccapi" + ".java";
                    break;
                default:
                    throw new Exception("fileType not supported");
            }
            logger.info("filePath: " + filePath);
            String template = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
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
            String vSwitchId = request.getParameter("vSwitchId");
            String instanceType = request.getParameter("instanceType");
            String instanceName = request.getParameter("instanceName");
            int amount = Integer.parseInt(request.getParameter("amount"));
            Assert.isTrue(StringUtils.isNotBlank(applicationName), "applicationName must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(environmentName), "environmentName must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(regionId), "regionId must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(vSwitchId), "vSwitchId must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(instanceType), "instanceType must not be blank");
            Assert.isTrue(StringUtils.isNotBlank(instanceName), "instanceName must not be blank");
            Assert.isTrue(amount > 0, "amount must be greater than 0");
            Assert.isTrue(amount <= 100, "amount must be less than 100");
            ResourceSupplyUtil.createEcsInstance(regionId, vSwitchId, instanceType, amount, applicationName,
                    environmentName,instanceName);
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
            ResourceSupplyUtil.createOssBucket(bucketName, applicationName, environmentName);
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
            ResourceSupplyUtil.createLogProject(projectName, description, applicationName, environmentName);
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
            String applicationName = request.getParameter("applicationName");
            String environment = request.getParameter("environmentName");
            String vpcId = request.getParameter("vpcId");
            Assert.isTrue(StringUtils.isNotEmpty(applicationName), "applicationName can not be empty");
            Assert.isTrue(StringUtils.isNotEmpty(environment), "environment can not be empty");
            Assert.isTrue(StringUtils.isNotEmpty(vpcId), "vpcId can not be empty");
            logger.info("applicationName: " + applicationName);
            logger.info("environment: " + environment);
            logger.info("vpcId: " + vpcId);
            DescribeVpcAttributeResponseBody.DescribeVpcAttributeResponseBodyVSwitchIds vSwitchIds =
                    ServiceHelper.describeVpcAttribute(vpcId).getVSwitchIds();
            List<String> vSwitches = new ArrayList<>();
            for (String vSwitchId : vSwitchIds.getVSwitchId()) {
                DescribeVSwitchAttributesResponseBody responseBody =
                        ServiceHelper.describeVSwitchAttribute(vSwitchId);
                if(ResourceSupplyUtil.isVSwitchTagMatch(responseBody, applicationName, environment)) {
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
            String applicationName = request.getParameter("applicationName");
            String environment = request.getParameter("environmentName");
            Assert.isTrue(StringUtils.isNotEmpty(applicationName), "applicationName can not be empty");
            Assert.isTrue(StringUtils.isNotEmpty(environment), "environment can not be empty");
            logger.info("applicationName: " + applicationName);
            logger.info("environment: " + environment);
            String resourceType = CommonConstants.VPC_RESOURCETYPE;
            List<String> resourceIds = ServiceHelper.listResourcesByTag(applicationName, environment, resourceType);
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
