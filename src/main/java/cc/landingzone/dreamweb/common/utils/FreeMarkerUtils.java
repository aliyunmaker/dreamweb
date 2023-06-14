package cc.landingzone.dreamweb.common.utils;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FreeMarkerUtils {

    private static Logger logger = LoggerFactory.getLogger(FreeMarkerUtils.class);

    private static Configuration cfg;

    static {
        // Create your Configuration instance, and specify if up to what
        // FreeMarker
        // version (here 2.3.24) do you want to apply the fixes that are not
        // 100%
        // backward-compatible. See the Configuration JavaDoc for details.
        cfg = new Configuration(Configuration.VERSION_2_3_24);

        // Specify the source where the template files come from. Here I set a
        // plain directory for it, but non-file-system sources are possible too:
        cfg.setClassForTemplateLoading(FreeMarkerUtils.class, "/freemarker");

        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        cfg.setDefaultEncoding("UTF-8");

        // Sets how errors will appear.
        // During web page *development*
        // TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Don't log exceptions inside FreeMarker that it will thrown at you
        // anyway:
        cfg.setLogTemplateExceptions(false);
    }

    public static void main(String[] args) throws Exception {
        Template temp = cfg.getTemplate("test.html");
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("name", "charles11");
        Writer out = new StringWriter();
        temp.process(root, out);
        System.out.println(out.toString());
    }


    public static String getSSOPage(String ssoURL, String onloadSubmit, String samlResponse, String formVisible) {
        String result = "";
        try {
            Template temp = cfg.getTemplate("sso.ftl");
            Map<String, Object> root = new HashMap<String, Object>();
            root.put("ssoURL", ssoURL);
            root.put("onloadSubmit", onloadSubmit);
            root.put("samlResponse", samlResponse);
            root.put("formVisible", formVisible);
            Writer out = new StringWriter();
            temp.process(root, out);
            result = out.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

}
