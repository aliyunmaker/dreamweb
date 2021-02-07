package cc.landingzone.dreamweb.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cc.landingzone.dreamweb.dao.LoginRecordDao;
import cc.landingzone.dreamweb.model.LoginRecord;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.enums.LoginMethodEnum;
import cc.landingzone.dreamweb.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author merc-bottle
 * @date 2021/02/07
 */
@Component
public class LoginRecordService {

    @Autowired
    private LoginRecordDao loginRecordDao;

    private static Logger logger = LoggerFactory.getLogger(LoginRecordService.class);

    /**
     * 添加登录记录
     *
     * @param request
     * @param loginName
     * @param loginMethod
     */
    public void addLoginRecord(HttpServletRequest request, String loginName, LoginMethodEnum loginMethod) {
        try {
            LoginRecord loginRecord = new LoginRecord();
            loginRecord.setClientIpAddr(IpUtils.getClientIpAddr(request));
            loginRecord.setLoginName(loginName);
            loginRecord.setLoginMethod(loginMethod.name());
            loginRecord.setComment(loginMethod.getComment());
            addLoginRecord(loginRecord);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 添加登录记录
     *
     * @param loginRecord
     */
    @Transactional
    public void addLoginRecord(LoginRecord loginRecord) {
        Assert.notNull(loginRecord, "数据不能为空!");
        Assert.hasText(loginRecord.getClientIpAddr(), "客户端IP地址不能为空!");
        Assert.hasText(loginRecord.getLoginName(), "登录名不能为空!");
        Assert.hasText(loginRecord.getLoginMethod(), "登录方式不能为空!");
        loginRecordDao.addLoginRecord(loginRecord);
    }

    /**
     * 查询登录记录
     *
     * @param page
     * @return
     */
    public List<LoginRecord> listLoginRecord(Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        List<LoginRecord> list = loginRecordDao.listLoginRecord(map);
        if (null != page) {
            if (null != page.getStart() && null != page.getLimit()) {
                Integer total = loginRecordDao.countLoginRecord(map);
                page.setTotal(total);
            } else {
                page.setTotal(list.size());
            }
        }
        return list;
    }
}
