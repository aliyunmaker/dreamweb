package cc.landingzone.dreamcmp.common;

import org.apache.commons.lang3.StringUtils;

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
     * fc服务接入入口，支持地域：
     * cn-hangzhou, cn-qingdao, cn-beijing, cn-zhangjiakou, cn-huhehaote, cn-shanghai, cn-shenzhen, cn-hongkong, cn-chengdu
     */
    FC("fcv3.%s.aliyuncs.com"),

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

    private String endpointPattern;

    EndpointEnum(String endpointPattern) {
        this.endpointPattern = endpointPattern;
    }

    public String getEndpoint() {
        String region = "";
        boolean useVpc = false;

        if (StringUtils.isBlank(region)) {
            region = CommonConstants.Aliyun_REGION_HANGZHOU;
        }

        if (SLS == this || FC == this) {
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

}
