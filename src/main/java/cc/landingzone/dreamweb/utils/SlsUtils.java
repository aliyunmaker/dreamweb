package cc.landingzone.dreamweb.utils;

public class SlsUtils {

    public static String drawWithColor(String action) {
        String actionWithColor = action;
        if ("install".equals(action) || "Success".equals(action)) {
            actionWithColor = "<font color=\"green\">" + action + "</font>";
        } else if ("uninstall".equals(action) || "Failed".equals(action)) {
            actionWithColor = "<font color=\"red\">" + action + "</font>";
        } else if ("create".equals(action)) {
            actionWithColor = "<font color=\"green\">+ " + action + "</font>";
        } else if ("delete".equals(action)) {
            actionWithColor = "<font color=\"red\">- " + action + "</font>";
        }

        return actionWithColor;
    }
}
