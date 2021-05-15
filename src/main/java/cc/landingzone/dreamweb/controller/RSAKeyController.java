package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.RSAService;

@Controller
@RequestMapping("/rsakey")
public class RSAKeyController extends BaseController{
    @Autowired
    private RSAService rsaService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/updateRSAKey.do")
    public void updateRSAKey(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            rsaService.UpdateKey();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
