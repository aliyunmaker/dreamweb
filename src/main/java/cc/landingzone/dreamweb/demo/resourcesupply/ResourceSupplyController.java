package cc.landingzone.dreamweb.demo.resourcesupply;

import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.ServiceEnum;
import cc.landingzone.dreamweb.common.ServiceHelper;
import cc.landingzone.dreamweb.common.utils.FileUtil;
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

@Controller
@RequestMapping("/resourceSupply")
public class ResourceSupplyController extends BaseController {

    @PostMapping(
            path = "/getTemplateByResourceType.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void getTemplateByResourceType(HttpServletRequest request, HttpServletResponse response) {
        String resourceType = request.getParameter("resourceType");
        logger.info("resourceType: " + resourceType);
        String template = "";
        switch (resourceType) {
            case "ecs":
                template = FileUtil.fileToString("src/main/resources/terraform/ecs_module.tf");
                break;
            case "oss":
                template = FileUtil.fileToString("src/main/resources/terraform/oss.tf");
                break;
            case "log":
                template = FileUtil.fileToString("src/main/resources/terraform/sls.tf");
                break;
            default:
                break;
        }
        logger.info("template: " + template);
        outputToString(response, template);
    }

    @PostMapping(
            path = "/createEcsInstance.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void createEcsInstance(HttpServletRequest request, HttpServletResponse response) {
        try {
            String applicationName = request.getParameter("applicationName");
            String environmentName = request.getParameter("environmentName");
            String regionId = request.getParameter("regionId");
            String vSwitchId = request.getParameter("vSwitchId");
            String instanceType = request.getParameter("instanceType");
            int amount = Integer.parseInt(request.getParameter("amount"));
            String result = ResourceSupplyUtil.createEcsInstance
                    (regionId, vSwitchId, instanceType, amount, applicationName, environmentName);
            outputToString(response, result);
        }catch (Exception e){
            logger.error(e.getMessage());
            outputToString(response, e.getMessage());
        }
    }

    @PostMapping(
            path = "/createOssBucket.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void createOssBucket(HttpServletRequest request, HttpServletResponse response) {
         try {
             String bucketName = request.getParameter("bucketName");
             String applicationName = request.getParameter("applicationName");
             String environmentName = request.getParameter("environmentName");
             Assert.isTrue(bucketName.length() <= 63, "bucketName length must be less than 63");
            Assert.isTrue(bucketName.length() >= 3, "bucketName length must be more than 3");
            Assert.isTrue(bucketName.matches("^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$"),
                    "bucketName 只能包括小写字母、数字和短划线（-）, 且必须以小写字母或者数字开头和结尾");
            logger.info("bucketName: " + bucketName);
            logger.info("applicationName: " + applicationName);
            logger.info("environment: " + environmentName);
            String result = ResourceSupplyUtil.createOssBucket(bucketName, applicationName, environmentName);
            logger.info("result: " + result);
            outputToString(response, result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            outputToString(response, e.getMessage());
        }
    }

    @PostMapping(
            path = "/createLogProject.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void createLogProject(HttpServletRequest request, HttpServletResponse response) {
         try {
             String projectName = request.getParameter("projectName");
             String description = request.getParameter("description");
             String applicationName = request.getParameter("applicationName");
             String environmentName = request.getParameter("environmentName");
             Assert.isTrue(projectName.length() <= 63, "projectName length must be less than 63");
            Assert.isTrue(projectName.length() >= 3, "projectName length must be more than 3");
            Assert.isTrue(projectName.matches("^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$"),
                    "projectName 只能包括小写字母、数字和短划线（-）, 且必须以小写字母或者数字开头和结尾");
            Assert.isTrue(StringUtils.isNotEmpty(description), "description can not be empty");
            String result = ResourceSupplyUtil.createLogProject(projectName, description, applicationName, environmentName);
            logger.info("result: " + result);
            outputToString(response, result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            outputToString(response, e.getMessage());
        }
    }

    @PostMapping(
            path = "/getVSwitches.do",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public void getVSwitches(HttpServletRequest request, HttpServletResponse response) {
        String applicationName = request.getParameter("applicationName");
        String environment = request.getParameter("environmentName");
        Assert.isTrue(StringUtils.isNotEmpty(applicationName), "applicationName can not be empty");
        Assert.isTrue(StringUtils.isNotEmpty(environment), "environment can not be empty");
        logger.info("applicationName: " + applicationName);
        logger.info("environment: " + environment);
        long startTime = System.currentTimeMillis();
        String resourceType = ServiceEnum.VSWITCH.getResourceType();
        List<String> resourceIds = ServiceHelper.listResourcesByTag(applicationName, environment,resourceType);
        logger.info("listResourcesByTag(): " + (System.currentTimeMillis() - startTime) + "ms");
        List<String> vSwitches = new ArrayList<>();
        for (String resourceId : resourceIds) {
           String vSwitchName = ServiceHelper.describeVSwitchAttribute(resourceId);
           vSwitches.add(vSwitchName + " / " + resourceId);
        }
        logger.info("vSwitches: " + vSwitches);
        logger.info("getVSwitches(): " + (System.currentTimeMillis() - startTime) + "ms");
        outputToJSON(response, vSwitches);
    }
}
