package cc.landingzone.dreamweb.demo.appcenter.model;

import lombok.Data;

import java.util.Map;

@Data
public class Application {
    private String appName;
    private String description;
    private Map<String, Integer> servicesCount;
}
