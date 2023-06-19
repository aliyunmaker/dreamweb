package cc.landingzone.dreamweb.demo.appcenter.model;

import lombok.Data;

import java.util.List;

@Data
public class Application {
    private String appName;
    private String description;
    private List<Service> services;
}
