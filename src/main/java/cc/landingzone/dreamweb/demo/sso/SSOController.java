package cc.landingzone.dreamweb.demo.sso;

import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.EndpointEnum;
import cc.landingzone.dreamweb.common.model.WebResult;
import cc.landingzone.dreamweb.common.model.enums.SSOSpEnum;
import cc.landingzone.dreamweb.common.utils.FreeMarkerUtils;
import cc.landingzone.dreamweb.common.utils.JsonUtils;
import cc.landingzone.dreamweb.demo.sso.sp.RAMSamlHelper;
import cc.landingzone.dreamweb.demo.sso.sp.SPHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.profile.DefaultProfile;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.core.config.InitializationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/sso")
public class SSOController extends BaseController implements InitializingBean {

    /**
     * 初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
//        CertManager.initSigningCredential();
//        DefaultBootstrap.bootstrap();
        logger.info("-------------------- cert init start ----------------------");
        CertManager.initSigningCredential();
        InitializationService.initialize();
        logger.info("-------------------- cert init end ----------------------");
    }

    @RequestMapping("/getSamlResponse.do")
    public void getSamlResponse(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            SSOSpEnum ssoSp = SSOSpEnum.aliyun;

            String nameID = "test";
            String identifier = SSOConstants.getSSOSpIdentifier(ssoSp);
            String replyUrl = SSOConstants.getSSOSpReplyUrl(ssoSp);

            HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
            // 只有role sso 才需要这些参数
            Set<String> roleSet = new HashSet<String>();
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
     * 首页单点登录sso
     *
     * @param request
     * @param response
     */
    @RequestMapping("/login.do")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        try {
            String sp = request.getParameter("sp");
            String userRoleId = request.getParameter("userRoleId");
            String relayState = "";
            // 默认是aliyun的role sso
            if (StringUtils.isBlank(sp)) {
                sp = SSOSpEnum.aliyun.toString();
            }
            SSOSpEnum ssoSp = SSOSpEnum.valueOf(sp);

            // 参考 templates/saml2-post-binding.vm
            String onloadSubmit = "";
            String formVisible = "";
            if (SSOConstants.SSO_FORM_AUTO_SUBMIT) {
                onloadSubmit = "onload=\"document.forms[0].submit()\"";
                formVisible = "style=\"visibility: hidden;\"";

            }

            // 获取已经登录用户的信息
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // *************************************根据 ssoSP的类型解析roleList************
            String nameID = username;
            String identifier = SSOConstants.getSSOSpIdentifier(ssoSp);
            String replyUrl = SSOConstants.getSSOSpReplyUrl(ssoSp);
            // String idpEntityId = SSOConstants.IDP_ENTITY_ID;
            HashMap<String, List<String>> attributes = null;
            // 如果是user sso,需要特殊处理,拆分出uid和nameid,而且不支持多个
            if (SSOSpEnum.aliyun_user.equals(ssoSp)) {
                String uid = SSOConstants.getSSOSpUserId(ssoSp);
                identifier = identifier.replace("{uid}", uid);
                nameID = SSOConstants.ALIYUN_SSO_LOGIN_USER_PRINCIPAL_NAME.get(userRoleId);
                // user现在只需要跳转云效
                relayState = SSOConstants.YUNXIAO_URL;
            } else if (SSOSpEnum.aws_user.equals(ssoSp) || SSOSpEnum.aliyun_user_cloudsso.equals(ssoSp)) {
                nameID = userRoleId;
            } else {
                attributes = new HashMap<String, List<String>>();
                // 只有role sso 才需要这些参数
                Set<String> roleSet = new HashSet<String>();
                String userRoleValue = null;
                if (SSOSpEnum.aliyun.equals(ssoSp)) {
                    // userRoleValue = "acs:ram::" + uid + ":role/" + userRoleId + ",acs:ram::" + uid + ":saml-provider/" + idpEntityId;
                    userRoleValue = SSOConstants.ALIYUN_SSO_LOGIN_ROLE_ID_ARN.get(userRoleId);
                } else if (SSOSpEnum.aws.equals(ssoSp)) {
                    // userRoleValue = "arn:aws:iam::" + uid + ":role/" + userRoleId + ",arn:aws:iam::" + uid + ":saml-provider/dreamweb";
                    userRoleValue = SSOConstants.AWS_SSO_LOGIN_ROLE_ID_ARN.get(userRoleId);
                }
                roleSet.add(userRoleValue);
                List<String> roleStringList = new ArrayList<String>(roleSet);
                attributes.put(SSOConstants.getSSOSpAttributeKeyRole(ssoSp), roleStringList);
                List<String> sessionNameList = new ArrayList<String>();
                sessionNameList.add(nameID);
                attributes.put(SSOConstants.getSSOSpAttributeKeyRoleSessionName(ssoSp), sessionNameList);
                logger.info("role sso list:" + roleStringList);

                // 如果有标签,就添加到saml的attribute中
//                if (StringUtils.isNotBlank(CommonConstants.SSO_SAML_ATTRIBUTE_LIST)) {
//                    Map<String, String> tags = JsonUtils.parseObject(CommonConstants.SSO_SAML_ATTRIBUTE_LIST,
//                            new TypeReference<Map<String, String>>() {});
//                    for (Map.Entry<String, String> entry : tags.entrySet()) {
//                        attributes.put(entry.getKey(), List.of(entry.getValue()));
//                    }
//                }
            }

            // ***************************************************************************
            String samlResponse = SamlGenerator.generateResponse(identifier, replyUrl, nameID, attributes);
            response.setContentType("text/html;charset=UTF-8");
//            String responseStr = FreeMarkerUtils.getSSOPage(replyUrl, onloadSubmit, samlResponse, formVisible);
//            response.getWriter().write(responseStr);
//            response.getWriter().flush();

            Map<String, Object> contextMap = new HashMap<String, Object>();
            contextMap.put("ssoURL", replyUrl);
            contextMap.put("onloadSubmit", onloadSubmit);
            contextMap.put("samlResponse", samlResponse);
            contextMap.put("samlResponseDecode",
                    new String(java.util.Base64.getDecoder().decode(samlResponse), StandardCharsets.UTF_8));
            if (!relayState.isBlank()) {
                contextMap.put("relayState", relayState);
            }
            contextMap.put("formVisible", formVisible);
            contextMap.put("ssoSp", ssoSp);

            String responseStr = FreeMarkerUtils.buildFreemarkPage("sso.ftl", contextMap);
            response.getWriter().write(responseStr);
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            outputToString(response, e.getMessage());
        }

    }

    /**
     * 测试sso
     *
     * @param request
     * @param response
     */
//    @RequestMapping("/login.do")
//    public void login(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            String sp = request.getParameter("sp");
//            // 默认是aliyun的role sso
//            if (StringUtils.isBlank(sp)) {
//                sp = SSOSpEnum.aliyun.toString();
//            }
//            SSOSpEnum ssoSp = SSOSpEnum.valueOf(sp);
//
//            // 获取已经登录用户的信息
//            String username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//            // 参考 templates/saml2-post-binding.vm
//            String onloadSubmit = "";
//            String formVisible = "";
//            if (SSOConstants.SSO_FORM_AUTO_SUBMIT) {
//                onloadSubmit = "onload=\"document.forms[0].submit()\"";
//                formVisible = "style=\"visibility: hidden;\"";
//
//            }
//
//            // *************************************根据 ssoSP的类型解析roleList************
//            String nameID = username;
//            String identifier = SSOConstants.getSSOSpIdentifier(ssoSp);
//            String replyUrl = SSOConstants.getSSOSpReplyUrl(ssoSp);
//            String uid = SSOConstants.getSSOSpUserId(ssoSp);
//            String userRoleId = request.getParameter("userRoleId");
//            String idpEntityId = SSOConstants.IDP_ENTITY_ID;
//            HashMap<String, List<String>> attributes = null;
//            // 如果是user sso,需要特殊处理,拆分出uid和nameid,而且不支持多个
//            if (SSOSpEnum.aliyun_user.equals(ssoSp)) {
////                String choosedRole = "";
////                logger.info("user sso choosed:" + choosedRole);
////                replyUrl = choosedRole.split(",")[0];
////                identifier = choosedRole.split(",")[1];
////                nameID = choosedRole.split(",")[2];
//                identifier = identifier.replace("{uid}", uid);
//                replyUrl = replyUrl.replace("{uid}", uid);
//                nameID = userRoleId + "@" + uid + ".onaliyun.com";
//            } else if (SSOSpEnum.aws_user.equals(ssoSp)) {
//                nameID = userRoleId;
//            } else if (SSOSpEnum.aliyun_user_cloudsso.equals(ssoSp)) {
//                nameID = userRoleId;
//            } else {
//                nameID = "kenmako555@gmail.com";
//                attributes = new HashMap<String, List<String>>();
//                // 只有role sso 才需要这些参数
//                Set<String> roleSet = new HashSet<String>();
//                String userRoleValue = null;
//                if (SSOSpEnum.aliyun.equals(ssoSp)) {
//                    userRoleValue = "acs:ram::" + uid + ":role/" + userRoleId + ",acs:ram::" + uid + ":saml-provider/" + idpEntityId;
//                } else if (SSOSpEnum.aws.equals(ssoSp)) {
//                    userRoleValue = "arn:aws:iam::" + uid + ":role/" + userRoleId + ",arn:aws:iam::" + uid + ":saml-provider/dreamweb";
//                }
//                roleSet.add(userRoleValue);
//                List<String> roleStringList = new ArrayList<String>(roleSet);
//                attributes.put(SSOConstants.getSSOSpAttributeKeyRole(ssoSp), roleStringList);
//                List<String> sessionNameList = new ArrayList<String>();
//                sessionNameList.add(nameID);
//                attributes.put(SSOConstants.getSSOSpAttributeKeyRoleSessionName(ssoSp), sessionNameList);
//                logger.info("role sso list:" + roleStringList);
//            }
//
//            // ***************************************************************************
//            String samlResponse = SamlGenerator.generateResponse(identifier, replyUrl, nameID, attributes);
//            response.setContentType("text/html;charset=UTF-8");
//            String responseStr = FreeMarkerUtils.getSSOPage(replyUrl, onloadSubmit, samlResponse, formVisible);
//            response.getWriter().write(responseStr);
//            response.getWriter().flush();
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            outputToString(response, e.getMessage());
//        }
//
//    }

    @RequestMapping("/downloadToken.do")
    public void downloadToken(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {

            String sp = request.getParameter("sp");
            // 默认是aliyun的role sso
            if (StringUtils.isBlank(sp)) {
                sp = SSOSpEnum.aliyun.toString();
            }
            SSOSpEnum ssoSp = SSOSpEnum.valueOf(sp);

            if (!SSOSpEnum.aliyun.equals(ssoSp)) {
                throw new IllegalArgumentException("not support:" + ssoSp);
            }

            String stsEndpoint = EndpointEnum.STS.getEndpoint();

            // 如果没有传AK信息就默认用自己的AK
            DefaultProfile profile = DefaultProfile.getProfile(
                CommonConstants.Aliyun_REGION_HANGZHOU,
                cc.landingzone.dreamweb.common.CommonConstants.Aliyun_AccessKeyId,
                cc.landingzone.dreamweb.common.CommonConstants.Aliyun_AccessKeySecret);
            // 获取已经登录用户的信息
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            String nameID = username;
            String identifier = SSOConstants.getSSOSpIdentifier(ssoSp);
            String replyUrl = SSOConstants.getSSOSpReplyUrl(ssoSp);
            // String uid = SSOConstants.getSSOSpUserId(ssoSp);
            String userRoleId = request.getParameter("userRoleId");
            // String idpEntityId = SSOConstants.IDP_ENTITY_ID;

            HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
            // 只有role sso 才需要这些参数
            Set<String> roleSet = new HashSet<String>();
            // String userRoleValue = "acs:ram::" + uid + ":role/" + userRoleId + ",acs:ram::" + uid + ":saml-provider/" + idpEntityId;
            String userRoleValue = SSOConstants.ALIYUN_SSO_LOGIN_ROLE_ID_ARN.get(userRoleId);
            roleSet.add(userRoleValue);
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

            result.setData(RAMSamlHelper.querySAMLToken(profile, samlProviderArn, roleArn, samlAssertion, stsEndpoint));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
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
            outputToFile(response, metaxml, "meta.xml", "application/xml;charset=UTF-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            outputToString(response, e.getMessage());
        }

    }

    /**
     * init sp,目前只支持阿里云的Role SSO
     * <p>
     *
     * @param request
     * @param response
     */
    @RequestMapping("/ssoConfig.do")
    public void ssoConfig(HttpServletRequest request, HttpServletResponse response) {
        String result = new String();
        try {
            String idpProviderName = request.getParameter("idpProviderName");
            String roleJson = request.getParameter("roleJson");
            String accessKeyId = request.getParameter("accessKeyId");
            String accessKeySecret = request.getParameter("accessKeySecret");
            Assert.hasText(accessKeyId, "accessKeyId can not be blank!");
            Assert.hasText(accessKeySecret, "accessKeySecret can not be blank!");
            Assert.hasText(idpProviderName, "idpProviderName can not be blank!");

            Map<String, List<String>> roleMap = JSON.parseObject(roleJson,
                new TypeReference<Map<String, List<String>>>() {});
            // DefaultProfile profile = DefaultProfile.getProfile(
            // cc.landingzone.dreamweb.common.CommonConstants.Aliyun_REGION_HANGZHOU,
            // accessKeyId,
            // accessKeySecret);
            // }
            result = SPHelper.initMultiAccountSP(accessKeyId, accessKeySecret, idpProviderName, roleMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        outputToString(response, result);
    }

    public static void main(String[] args) {
        String json = "{\"admin\":[\"role1\",\"role2\"]}";
        Map<String, List<String>> list2 = JSON.parseObject(json, new TypeReference<Map<String, List<String>>>() {});
        System.out.println(JsonUtils.toJsonString(list2));
    }

    @RequestMapping("/getSAMLToken.do")
    public void getSAMLToken(HttpServletRequest request, HttpServletResponse response) {
        String result = new String();
        try {
            String stsEndpoint = EndpointEnum.STS.getEndpoint();
            // 如果没有传AK信息就默认用自己的AK
            DefaultProfile profile = DefaultProfile.getProfile(
                CommonConstants.Aliyun_REGION_HANGZHOU,
                cc.landingzone.dreamweb.common.CommonConstants.Aliyun_AccessKeyId,
                cc.landingzone.dreamweb.common.CommonConstants.Aliyun_AccessKeySecret);
            // 获取已经登录用户的信息
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            SSOSpEnum ssoSp = SSOSpEnum.aliyun;

            String nameID = username;
            String identifier = SSOConstants.getSSOSpIdentifier(ssoSp);
            String replyUrl = SSOConstants.getSSOSpReplyUrl(ssoSp);

            HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
            // 只有role sso 才需要这些参数
            Set<String> roleSet = new HashSet<String>();
            //TODO
            roleSet.add("");
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

            result = RAMSamlHelper.querySAMLToken(profile, samlProviderArn, roleArn, samlAssertion, stsEndpoint);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        outputToString(response, result);

    }

}
