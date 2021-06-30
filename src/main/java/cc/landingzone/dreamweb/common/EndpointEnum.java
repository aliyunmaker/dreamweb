package cc.landingzone.dreamweb.common;

import javax.annotation.PostConstruct;

import cc.landingzone.dreamweb.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public enum EndpointEnum {

    /**
     * sls服务接入入口，支持地域：
     * cn-hangzhou, cn-shanghai, cn-shenzhen, cn-heyuan, cn-qingdao, cn-beijing, cn-zhangjiakou, cn-huhehaote
     * cn-wulanchabu, cn-chengdu, cn-hongkong, cn-guangzhou
     */
    SLS("%s.log.aliyuncs.com"),

    /**
     * sts服务接入入口，支持地域：
     * cn-hangzhou, cn-shanghai, cn-shenzhen, cn-heyuan, cn-qingdao, cn-beijing, cn-zhangjiakou, cn-huhehaote
     * cn-wulanchabu, cn-chengdu, cn-hongkong, cn-guangzhou
     */
    STS("sts%s.%s.aliyuncs.com"),

    /**
     * ecs服务接入入口，支持地域：
     * cn-hangzhou, cn-shanghai, cn-shenzhen, cn-heyuan, cn-qingdao, cn-beijing, cn-zhangjiakou, cn-huhehaote
     * cn-wulanchabu, cn-chengdu, cn-hongkong, cn-guangzhou
     */
    ECS("ecs%s.%s.aliyuncs.com"),

    /**
     * oos服务接入入口，支持地域：
     * cn-hangzhou, cn-shanghai, cn-shenzhen, cn-heyuan, cn-qingdao, cn-beijing, cn-zhangjiakou, cn-huhehaote
     * cn-wulanchabu, cn-chengdu, cn-hongkong, cn-guangzhou
     */
    OOS("oos%s.%s.aliyuncs.com"),

    /**
     * 登录接口，不支持地域和vpc访问
     */
    SIGN_IN("https://signin.aliyun.com"),

    /**
     * 资源管理接口，不支持地域和vpc访问
     */
    RESOURCE_MANAGER("resourcemanager.aliyuncs.com"),

    /**
     * ims接口，不支持地域和vpc访问
     */
    IMS("ims.aliyuncs.com"),

    /**
     * ram接口，不支持地域和vpc访问
     */
    RAM("ram.aliyuncs.com");

    private static final String REGION = "region";
    private static final String USE_VPC = "useVpc";

    private String endpointPattern;
    private SystemConfigService systemConfigService;

    EndpointEnum(String endpointPattern) {
        this.endpointPattern = endpointPattern;
    }

    public String getEndpoint() {
        String region = systemConfigService.getStringValueFromCache(REGION);
        Boolean useVpc = systemConfigService.getBooleanValueFromCache(USE_VPC);

        if (SLS == this) {
            if (useVpc) {
                region += "-intranet";
            }
            return String.format(endpointPattern, region);
        } else if (IMS == this || RAM == this || RESOURCE_MANAGER == this || SIGN_IN == this) {
            return endpointPattern;
        } else {
            String vpc = "";
            if (useVpc) {
                vpc = "-vpc";
            }
            return String.format(endpointPattern, vpc, region);
        }
    }

    @Component
    public static class EndpointServiceInjector {

        @Autowired
        private SystemConfigService systemConfigService;

        @PostConstruct
        public void postConstruct() {
            for (EndpointEnum endpoint : EndpointEnum.values()) {
                endpoint.setSystemConfigService(systemConfigService);
            }
        }
    }

    private void setSystemConfigService(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }
}
