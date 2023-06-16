package cc.landingzone.dreamweb.common;

public enum ServiceEnum {
    /**
     * ECS
     */
    ECS("ALIYUN::ECS::INSTANCE"),
    OSS("ALIYUN::OSS::BUCKET"),
    SLB("ALIYUN::SLB::INSTANCE"),
    RDS("ALIYUN::RDS::INSTANCE"),
    SLS("ALIYUN::LOG::PROJECT");

    private final String resourceType;

    ServiceEnum(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return this.resourceType;
    }
}
