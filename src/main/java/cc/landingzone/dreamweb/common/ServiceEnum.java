package cc.landingzone.dreamweb.common;

public enum ServiceEnum {
    /**
     * ECS
     */
    ECS("ALIYUN::ECS::INSTANCE"),
    /**
     * OSS
     */
    OSS("ALIYUN::OSS::BUCKET"),
    /**
     * SLB
     */
    SLB("ALIYUN::SLB::INSTANCE"),
    /**
     * RDS
     */
    RDS("ALIYUN::RDS::INSTANCE"),
    /**
     * SLS
     */
    SLS("ALIYUN::LOG::PROJECT");



    private String resourceType;

    ServiceEnum(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return this.resourceType;
    }

}
