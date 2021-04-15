package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import cc.landingzone.dreamweb.framework.MyAuthenticationProvider;
import cc.landingzone.dreamweb.model.enums.SSOSpEnum;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.UserRoleService;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.sso.CertManager;
import cc.landingzone.dreamweb.sso.SSOConstants;
import cc.landingzone.dreamweb.sso.SamlGenerator;
import cc.landingzone.dreamweb.sso.sp.RAMSamlHelper;
import cc.landingzone.dreamweb.sso.sp.SPHelper;
import cc.landingzone.dreamweb.utils.FreeMarkerUtils;
import cc.landingzone.dreamweb.utils.JsonUtils;
import com.aliyuncs.profile.DefaultProfile;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.DefaultBootstrap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sso")
public class SSOController extends BaseController implements InitializingBean {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        CertManager.initSigningCredential();
        DefaultBootstrap.bootstrap();
    }

    @RequestMapping("/getSamlResponse.do")
    public void getSamlResponse(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("username");

            User user = userService.getUserByLoginName(username);
            if (null == user) {
                throw new UsernameNotFoundException(username);
            }

            // 密码策略: md5(salt+password)  equals  user.getAuthkey()
            if (!MyAuthenticationProvider.buildMd5Password(password).equals(user.getPassword())) {
                throw new BadCredentialsException("password error!");
            }

            // 根据SSOSp过滤role
            List<UserRole> roleList = userRoleService.getRoleListByUserId(user.getId(), SSOSpEnum.aliyun);
            Assert.notEmpty(roleList, "roleList can not be empty!");

            SSOSpEnum ssoSp = SSOSpEnum.aliyun;

            String nameID = user.getLoginName();
            String identifier = SSOConstants.getSSOSpIdentifier(ssoSp);
            String replyUrl = SSOConstants.getSSOSpReplyUrl(ssoSp);

            HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
            // 只有role sso 才需要这些参数
            Set<String> roleSet = new HashSet<String>();
            for (UserRole userRole : roleList) {
                roleSet.add(userRole.getRoleValue());
            }
            List<String> roleStringList = new ArrayList<String>(roleSet);
            attributes.put(SSOConstants.getSSOSpAttributeKeyRole(ssoSp), roleStringList);
            List<String> sessionNameList = new ArrayList<String>();
            sessionNameList.add(nameID);
            attributes.put(SSOConstants.getSSOSpAttributeKeyRoleSessionName(ssoSp), sessionNameList);

            String samlAssertion = SamlGenerator.generateResponse(identifier, replyUrl, nameID, attributes);
            result.setData(samlAssertion);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);

    }

    /**
     * 测试sso
     *
     * @param request
     * @param response
     */
    @RequestMapping("/login.do")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        try {
            String sp = request.getParameter("sp");
            String userRoleId = request.getParameter("userRoleId");
            // 默认是aliyun的role sso
            if (StringUtils.isBlank(sp)) {
                sp = SSOSpEnum.aliyun.toString();
            }
            SSOSpEnum ssoSp = SSOSpEnum.valueOf(sp);

            // 获取已经登录用户的信息
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(username);
            // 根据SSOSp过滤role
            List<UserRole> roleList = userRoleService.getRoleListByUserId(user.getId(), ssoSp);

            logger.info("===============roleList=================");
            logger.info(JsonUtils.toJsonString(roleList));

            // 参考 templates/saml2-post-binding.vm
            String onloadSubmit = "";
            String formVisible = "";
            if (SSOConstants.SSO_FORM_AUTO_SUBMIT) {
                onloadSubmit = "onload=\"document.forms[0].submit()\"";
                formVisible = "style=\"visibility: hidden;\"";

            }

            // *************************************根据 ssoSP的类型解析roleList************
            String nameID = user.getLoginName();
            String identifier = SSOConstants.getSSOSpIdentifier(ssoSp);
            String replyUrl = SSOConstants.getSSOSpReplyUrl(ssoSp);
            HashMap<String, List<String>> attributes = null;
            Assert.notEmpty(roleList, "roleList can not be empty!");
            // 如果是user sso,需要特殊处理,拆分出uid和nameid,而且不支持多个
            if (SSOSpEnum.aliyun_user.equals(ssoSp) || SSOSpEnum.aws_user.equals(ssoSp)) {
                UserRole choosedRole = roleList.get(0);
                logger.info("user sso choosed:" + choosedRole);
                replyUrl = choosedRole.getRoleValue().split(",")[0];
                identifier = choosedRole.getRoleValue().split(",")[1];
                nameID = choosedRole.getRoleValue().split(",")[2];
            } else {
                attributes = new HashMap<String, List<String>>();
                // 只有role sso 才需要这些参数
                Set<String> roleSet = new HashSet<String>();
                for (UserRole userRole : roleList) {
                    //如果指定roleId,则只添加该role
                    if (StringUtils.isNotBlank(userRoleId) && !userRoleId.equals(userRole.getId().toString())) {
                        continue;
                    }
                    roleSet.add(userRole.getRoleValue());
                }
                List<String> roleStringList = new ArrayList<String>(roleSet);
                attributes.put(SSOConstants.getSSOSpAttributeKeyRole(ssoSp), roleStringList);
                List<String> sessionNameList = new ArrayList<String>();
                sessionNameList.add(nameID);
                attributes.put(SSOConstants.getSSOSpAttributeKeyRoleSessionName(ssoSp), sessionNameList);
                logger.info("role sso list:" + roleStringList);
            }

            // ***************************************************************************
            String samlResponse = SamlGenerator.generateResponse(identifier, replyUrl, nameID, attributes);
            response.setContentType("text/html;charset=UTF-8");
            String responseStr = FreeMarkerUtils.getSSOPage(replyUrl, onloadSubmit, samlResponse, formVisible);
            response.getWriter().write(responseStr);
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            outputToString(response, e.getMessage());
        }

    }


    @RequestMapping("/downloadToken.do")
    public void downloadToken(HttpServletRequest request, HttpServletResponse response) {
        String result = new String();
        try {

            String sp = request.getParameter("sp");
            String userRoleId = request.getParameter("userRoleId");
            // 默认是aliyun的role sso
            if (StringUtils.isBlank(sp)) {
                sp = SSOSpEnum.aliyun.toString();
            }
            SSOSpEnum ssoSp = SSOSpEnum.valueOf(sp);

            if (!SSOSpEnum.aliyun.equals(ssoSp)) {
                throw new IllegalArgumentException("not support:" + ssoSp);
            }

            // 如果没有传AK信息就默认用自己的AK
            DefaultProfile profile = DefaultProfile.getProfile(
                    cc.landingzone.dreamweb.common.CommonConstants.Aliyun_REGION_HANGZHOU,
                    cc.landingzone.dreamweb.common.CommonConstants.Aliyun_AccessKeyId,
                    cc.landingzone.dreamweb.common.CommonConstants.Aliyun_AccessKeySecret);
            // 获取已经登录用户的信息
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(username);
            // 根据SSOSp过滤role
            List<UserRole> roleList = userRoleService.getRoleListByUserId(user.getId(), SSOSpEnum.aliyun);

            Assert.notEmpty(roleList, "roleList can not be empty!");

            String nameID = user.getLoginName();
            String identifier = SSOConstants.getSSOSpIdentifier(ssoSp);
            String replyUrl = SSOConstants.getSSOSpReplyUrl(ssoSp);

            HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
            // 只有role sso 才需要这些参数
            Set<String> roleSet = new HashSet<String>();
            for (UserRole userRole : roleList) {
                //如果指定roleId,则只添加该role
                if (StringUtils.isNotBlank(userRoleId) && !userRoleId.equals(userRole.getId().toString())) {
                    continue;
                }
                roleSet.add(userRole.getRoleValue());
            }
            List<String> roleStringList = new ArrayList<String>(roleSet);
            attributes.put(SSOConstants.getSSOSpAttributeKeyRole(ssoSp), roleStringList);
            List<String> sessionNameList = new ArrayList<String>();
            sessionNameList.add(nameID);
            attributes.put(SSOConstants.getSSOSpAttributeKeyRoleSessionName(ssoSp), sessionNameList);

            String samlAssertion = SamlGenerator.generateResponse(identifier, replyUrl, nameID, attributes);

            String roleArn = roleStringList.get(0).split(",")[0];
            String samlProviderArn = roleStringList.get(0).split(",")[1];
            logger.info("roleArn:" + roleArn);
            logger.info("samlProviderArn:" + samlProviderArn);

            result = RAMSamlHelper.querySAMLToken(profile, samlProviderArn, roleArn, samlAssertion);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        outputToString(response, result);
    }

    /**
     * 下载meta.xml
     *
     * @param request
     * @param response
     */
    @RequestMapping("/metaxml.do")
    public void metaxml(HttpServletRequest request, HttpServletResponse response) {
        try {
            String metaxml = SamlGenerator.generateMetaXML();
            outputToFile(response,metaxml,"meta.xml","application/xml;charset=UTF-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            outputToString(response, e.getMessage());
        }

    }

    /**
     * init sp,目前只支持阿里云的Role SSO
     * <p>
     * https://chengchao.name/springrun/sso/initSP.htm?idpProviderName=charlesIDP&roleName=charlesRole&accessKeyId=&accessKeySecret=
     *
     * @param request
     * @param response
     */
    @RequestMapping("/initSP.do")
    public void initSP(HttpServletRequest request, HttpServletResponse response) {
        String result = new String();
        try {
            String idpProviderName = request.getParameter("idpProviderName");
            String roleName = request.getParameter("roleName");
            String accessKeyId = request.getParameter("accessKeyId");
            String accessKeySecret = request.getParameter("accessKeySecret");

            // 如果没有传AK信息就默认用自己的AK
            DefaultProfile profile = DefaultProfile.getProfile(
                    cc.landingzone.dreamweb.common.CommonConstants.Aliyun_REGION_HANGZHOU,
                    cc.landingzone.dreamweb.common.CommonConstants.Aliyun_AccessKeyId,
                    cc.landingzone.dreamweb.common.CommonConstants.Aliyun_AccessKeySecret);
            if (StringUtils.isNotBlank(accessKeyId) && StringUtils.isNotBlank(accessKeySecret)) {
                profile = DefaultProfile.getProfile(
                        cc.landingzone.dreamweb.common.CommonConstants.Aliyun_REGION_HANGZHOU, accessKeyId,
                        accessKeySecret);
            }
            result = SPHelper.initSP(profile, idpProviderName, roleName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        outputToString(response, result);

    }

    @RequestMapping("/getSAMLToken.do")
    public void getSAMLToken(HttpServletRequest request, HttpServletResponse response) {
        String result = new String();
        try {
            // 如果没有传AK信息就默认用自己的AK
            DefaultProfile profile = DefaultProfile.getProfile(
                    cc.landingzone.dreamweb.common.CommonConstants.Aliyun_REGION_HANGZHOU,
                    cc.landingzone.dreamweb.common.CommonConstants.Aliyun_AccessKeyId,
                    cc.landingzone.dreamweb.common.CommonConstants.Aliyun_AccessKeySecret);
            // 获取已经登录用户的信息
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(username);
            // 根据SSOSp过滤role
            List<UserRole> roleList = userRoleService.getRoleListByUserId(user.getId(), SSOSpEnum.aliyun);

            Assert.notEmpty(roleList, "roleList can not be empty!");

            SSOSpEnum ssoSp = SSOSpEnum.aliyun;

            String nameID = user.getLoginName();
            String identifier = SSOConstants.getSSOSpIdentifier(ssoSp);
            String replyUrl = SSOConstants.getSSOSpReplyUrl(ssoSp);

            HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
            // 只有role sso 才需要这些参数
            Set<String> roleSet = new HashSet<String>();
            for (UserRole userRole : roleList) {
                roleSet.add(userRole.getRoleValue());
            }
            List<String> roleStringList = new ArrayList<String>(roleSet);
            attributes.put(SSOConstants.getSSOSpAttributeKeyRole(ssoSp), roleStringList);
            List<String> sessionNameList = new ArrayList<String>();
            sessionNameList.add(nameID);
            attributes.put(SSOConstants.getSSOSpAttributeKeyRoleSessionName(ssoSp), sessionNameList);

            String samlAssertion = SamlGenerator.generateResponse(identifier, replyUrl, nameID, attributes);

            String roleArn = roleStringList.get(0).split(",")[0];
            String samlProviderArn = roleStringList.get(0).split(",")[1];
            logger.info("roleArn:" + roleArn);
            logger.info("samlProviderArn:" + samlProviderArn);

            result = RAMSamlHelper.querySAMLToken(profile, samlProviderArn, roleArn, samlAssertion);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        outputToString(response, result);

    }

}
