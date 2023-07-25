package cc.landingzone.dreamcmp.demo.appcenter.model;

import lombok.Data;

@Data
public class Event {
    private String eventTime;
    private String userName;
    private String userType;
    private String eventName;
    private String resource;
    private Object userIdentity;
}
