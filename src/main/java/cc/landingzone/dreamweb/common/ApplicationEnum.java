package cc.landingzone.dreamweb.common;

public enum ApplicationEnum {
    /**
     * Application 1
     */
    application1("Description of app1"),
    application2("Description of app2"),
    application3("Description of app3"),
    application4("Description of app4"),
    application5("Description of app5");

    private String description;

    ApplicationEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
