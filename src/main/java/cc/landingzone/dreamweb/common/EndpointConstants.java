package cc.landingzone.dreamweb.common;

public class EndpointConstants {

    private static final String SLS_PUBLIC_ENDPOINT_PATTERN = "%s.log.aliyuncs.com";
    private static final String SLS_VPC_ENDPOINT_PATTERN = "%s-intranet.log.aliyuncs.com";
    private static final String STS_PUBLIC_ENDPOINT_PATTERN = "sts.%s.aliyuncs.com";
    private static final String STS_VPC_ENDPOINT_PATTERN = "sts-vpc.%s.aliyuncs.com";
    private static final String ECS_PUBLIC_ENDPOINT_PATTERN = "ecs.%s.aliyuncs.com";
    private static final String ECS_VPC_ENDPOINT_PATTERN = "ecs-vpc.%s.aliyuncs.com";
    private static final String OOS_PUBLIC_ENDPOINT_PATTERN = "oos.%s.aliyuncs.com";
    private static final String OOS_VPC_ENDPOINT_PATTERN = "oos-vpc.%s.aliyuncs.com";

    // 以下接口没有vpc入口，也不需要region
    private static final String SIGN_IN_ENDPOINT = "https://signin.aliyun.com";
    private static final String RESOURCE_MANAGER_ENDPOINT = "resourcemanager.aliyuncs.com";
    private static final String IMS_ENDPOINT = "ims.aliyuncs.com";
    private static final String RAM_ENDPOINT = "ram.aliyuncs.com";


    public static String getSlsEndpoint(String region, Boolean useVpc) {
        if (useVpc) {
            return String.format(SLS_VPC_ENDPOINT_PATTERN, region);
        } else {
            return String.format(SLS_PUBLIC_ENDPOINT_PATTERN, region);
        }
    }

    public static String getStsEndpoint(String region, Boolean useVpc) {
        if (useVpc) {
            return String.format(STS_VPC_ENDPOINT_PATTERN, region);
        } else {
            return String.format(STS_PUBLIC_ENDPOINT_PATTERN, region);
        }
    }

    public static String getEcsEndpoint(String region, Boolean useVpc) {
        if (useVpc) {
            return String.format(ECS_VPC_ENDPOINT_PATTERN, region);
        } else {
            return String.format(ECS_PUBLIC_ENDPOINT_PATTERN, region);
        }
    }

    public static String getOosEndpoint(String region, Boolean useVpc) {
        if (useVpc) {
            return String.format(OOS_VPC_ENDPOINT_PATTERN, region);
        } else {
            return String.format(OOS_PUBLIC_ENDPOINT_PATTERN, region);
        }
    }

    public static String getSignInEndpoint(String region, Boolean useVpc) {
        return SIGN_IN_ENDPOINT;
    }

    public static String getResourceManagerEndpoint(String region, Boolean useVpc) {
        return RESOURCE_MANAGER_ENDPOINT;
    }

    public static String getImsEndpoint(String region, Boolean useVpc) {
        return IMS_ENDPOINT;
    }

    public static String getRamEndpoint(String region, Boolean useVpc) {
        return RAM_ENDPOINT;
    }
}
