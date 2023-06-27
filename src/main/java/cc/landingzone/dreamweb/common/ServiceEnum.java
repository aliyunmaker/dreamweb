package cc.landingzone.dreamweb.common;

public enum ServiceEnum {
    /**
     * ECS
     */
    ECS("ecs", "ALIYUN::ECS::INSTANCE"),
    /**
     * OSS
     */
    OSS("oss", "ALIYUN::OSS::BUCKET"),
    /**
     * SLB
     */
    SLB("slb", "ALIYUN::SLB::INSTANCE"),
    /**
     * RDS
     */
    RDS("rds", "ALIYUN::RDS::INSTANCE"),
    /**
     * SLS
     */
    SLS("log", "ALIYUN::LOG::PROJECT"),

    VSWITCH("vSwitch", "ALIYUN::VPC::VSWITCH"),
    ;


    private String resourceName;
    private String resourceType;

    ServiceEnum(String resourceType) {
        this.resourceType = resourceType;
    }

    ServiceEnum(String resourceName, String resourceType) {
        this.resourceName = resourceName;
        this.resourceType = resourceType;
    }


    public String getResourceType() {
        return this.resourceType;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public static ServiceEnum getServiceEnumByResourceName(String resourceName) {
        for (ServiceEnum serviceEnum : ServiceEnum.values()) {
            if (serviceEnum.getResourceName().equals(resourceName)) {
                return serviceEnum;
            }
        }
        return null;
    }

}
