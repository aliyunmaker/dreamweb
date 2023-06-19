package cc.landingzone.dreamweb.common;

public enum ApplicationEnum {
    /**
     * Application 1
     */
    APP1("Description of app1"),
    APP2("Description of app2"),
    APP3("Description of app3"),
    APP4("Description of app4"),
    APP5("Description of app5");

    private String description;

    ApplicationEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
