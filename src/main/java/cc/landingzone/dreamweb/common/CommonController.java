package cc.landingzone.dreamweb.common;

import cc.landingzone.dreamweb.common.model.WebResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/common")
public class CommonController extends BaseController{

    @RequestMapping("/getApplication.do")
    public void getApplication(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        result.setData(CommonConstants.applicationList);
        outputToJSON(response, result);
    }



}
