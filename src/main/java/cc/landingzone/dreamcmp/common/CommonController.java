package cc.landingzone.dreamcmp.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamcmp.common.model.WebResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/common")
public class CommonController extends BaseController{

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("/getApplication.do")
    public void getApplication(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        result.setData(ApplicationEnum.names());
        outputToJSON(response, result);
    }

    @RequestMapping("/listServices.do")
    public void listServices(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        result.setData(ServiceEnum.values());
        outputToJSON(response, result);
    }

    @RequestMapping("/getDocumentByModule.do")
    public void getDocumentByModule(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String module = request.getParameter("module");
            String filePath = "classpath:document/" + module + ".md";
            Resource resource = applicationContext.getResource(filePath);
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            logger.info("filePath: " + filePath);
            String document = FileCopyUtils.copyToString(reader);
            result.setData(document);
        }catch (Exception e){
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
