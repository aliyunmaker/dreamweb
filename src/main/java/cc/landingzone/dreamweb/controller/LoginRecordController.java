package cc.landingzone.dreamweb.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamweb.model.LoginRecord;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.LoginRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author merc-bottle
 * @date 2021/02/07
 */
@Controller
@RequestMapping("/loginRecord")
public class LoginRecordController extends BaseController {

    @Autowired
    private LoginRecordService loginRecordService;

    private Logger logger = LoggerFactory.getLogger(LoginRecordController.class);

    @RequestMapping("/listLoginRecord.do")
    public void listLoginRecord(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            Page page = new Page(start, limit);

            List<LoginRecord> loginRecords = loginRecordService.listLoginRecord(page);
            result.setTotal(page.getTotal());
            result.setData(loginRecords);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}