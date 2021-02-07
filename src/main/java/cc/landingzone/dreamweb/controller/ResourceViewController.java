package cc.landingzone.dreamweb.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamweb.model.AccountResourceInfo;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.ResourceViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author merc-bottle
 * @date 2021/02/04
 */
@Controller
@RequestMapping("/resourceView")
public class ResourceViewController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ResourceViewController.class);

    @Autowired
    private ResourceViewService resourceViewService;

    @RequestMapping("/listAccountResourceInfo.do")
    public void listAccountResourceInfo(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            List<AccountResourceInfo> accountResourceInfoList = resourceViewService.listAccountResourceInfo();
            result.setData(accountResourceInfoList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
