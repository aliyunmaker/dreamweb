package cc.landingzone.dreamcmp.common;

import java.util.ArrayList;
import java.util.List;

public enum ApplicationEnum {
    /**
     * Application 1
     */
    application1("demo-app-1","Description of app1"),
    application2("demo-app-2","Description of app2"),
    application3("demo-app-3","Description of app3");

//    application1("application1","Description of app1"),
//    application2("application2","Description of app2"),
//    application3("application3","Description of app3");


    private final String name;
    private final String description;


    ApplicationEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public static List<String> names() {
        List<String> names = new ArrayList<>();
        for (ApplicationEnum applicationEnum: ApplicationEnum.values()) {
            names.add(applicationEnum.getName());
        }
        return names;
    }
}
