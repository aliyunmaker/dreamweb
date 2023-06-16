package cc.landingzone.dreamweb.demo.appcenter.model;

import lombok.Data;

import java.util.List;

@Data
public class Service {
    private String serviceName;
    private Integer resourceSize;
    private List<Resource> resources;
}