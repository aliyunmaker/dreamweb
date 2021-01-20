package cc.landingzone.dreamweb.controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.jar.Manifest;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.model.WebResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/system")
public class SystemController extends BaseController implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(SystemController.class);

    private LocalDateTime startTime;

    @Override
    public void afterPropertiesSet() throws Exception {
        startTime = LocalDateTime.now();
    }

    /**
     * 开放权限
     *
     * @param request
     * @param response
     */
    @RequestMapping("/getStartInfo")
    public void getStartInfo(HttpServletRequest request, HttpServletResponse response) {
        String result = new String();
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(inputStream);
            // String manifest = FileUtils.readFileToString(new File(url.getFile()));
            result = "Start: " + startTime.format(DateTimeFormatter.ofPattern("YYYYMMdd_HHmm")) + "<br/>Version: "
                    + manifest.getMainAttributes().getValue("Version");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        outputToString(response, result);
    }

    @RequestMapping("/getIndexLogoPage.do")
    public void getIndexLogoPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WebResult result = new WebResult();
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String logoDiv = null;
            if (CommonConstants.ENV_ONLINE) {
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(inputStream);
                String version = manifest.getMainAttributes().getValue("Version");
                logoDiv = "<div align=\"center\"><i style=\"font-size:30px;margin-top:5px;color:#CFDEEF;animation-duration: 20s;\" class=\"fa fa-sun-o fa-spin\" aria-hidden=\"true\"></i></div><div align='center' style='background-color:#FF594C;margin-top:5px;font-size: 12px;'><font "
                        + "style='color: white;'>" + username + "<br>" + version + "</font></div>";
            } else {
                String version = "test version";
                logoDiv = "<div align=\"center\"><i style=\"font-size:30px;margin-top:5px;color:#CFDEEF;animation-duration: 1s;\" class=\"fa fa-sun-o fa-spin\" aria-hidden=\"true\"></i></div><div align='center' style='background-color:rgb(93,168,48);margin-top:5px;font-size: 12px;"
                        + "'><font style='color: white;'>" + username + "<br>" + version + "</font></div>";
            }
            result.setData(logoDiv);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }


}
