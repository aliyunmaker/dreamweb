package cc.landingzone.dreamweb.common;

import cc.landingzone.dreamweb.common.model.WebResult;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/common")
public class CommonController extends BaseController{

    @RequestMapping("/getApplication.do")
    public void getApplication(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        result.setData(ApplicationEnum.names());
        outputToJSON(response, result);
    }

    @RequestMapping("/getDocumentByModule.do")
    public void getDocumentByModule(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String module = request.getParameter("module");
            String filePath = "src/main/resources/document/" + module + ".md";
            logger.info("filePath: " + filePath);
            String document = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            result.setData(document);
        }catch (Exception e){
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
