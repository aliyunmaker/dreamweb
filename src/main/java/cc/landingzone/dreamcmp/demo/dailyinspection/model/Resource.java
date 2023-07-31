package cc.landingzone.dreamcmp.demo.dailyinspection.model;

import lombok.Data;

@Data
public class Resource {
    private String id;
    private String name;
    private String mainstay;
    private String resourceType;
    private String compliance;
    private String regionId;
}
