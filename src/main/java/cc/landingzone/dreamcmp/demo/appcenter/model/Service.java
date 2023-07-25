package cc.landingzone.dreamcmp.demo.appcenter.model;

import lombok.Data;

import java.util.List;

@Data
public class Service {
    private String serviceName;
    private Integer resourceCount;
    private List<String> resourceIds;
}