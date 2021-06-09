package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.SlsConfigInfo;
import cc.landingzone.dreamweb.model.SlsProjectInfo;
import cc.landingzone.dreamweb.utils.SlsUtils;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlsViewService {
    private static Logger logger = LoggerFactory.getLogger(LoginRecordService.class);
    private static final String ROLE_SESSION = "console-role-session"; // 用户自定义参数。此参数用来区分不同的令牌，可用于用户级别的访问审计。
    private static final String SLS_HOST_SUFFIX = ".log.aliyuncs.com";

    /**
     * 根据sls配置信息获取所有Project信息
     * @param page 分页参数
     * @param slsConfigInfo sls配置信息
     * @return 全部Project信息
     */
    public List<SlsProjectInfo> listProjectsInfo(Page page, SlsConfigInfo slsConfigInfo) {
        String host = slsConfigInfo.getSlsRegion() + SLS_HOST_SUFFIX; //服务入口
        String requestUrl = String.format("http://%s/?offset=%d&size=%d", host, page.getStart(), page.getLimit()); // 访问地址

        // 创建Http Get请求
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet projectHttpGet = new HttpGet(requestUrl);

        try {
            // 设置访问aliyun sls所需的Http请求头
            projectHttpGet.setHeader("x-log-apiversion", "0.6.0");
            projectHttpGet.setHeader("x-log-bodyrawsize", "0");
            projectHttpGet.setHeader("x-log-signaturemethod", "hmac-sha1");
            // 使用secretKey对http请求签名
            projectHttpGet.setHeader("authorization", SlsUtils.generateAuthorization(projectHttpGet,
                    slsConfigInfo.getSlsAccessKey(),
                    slsConfigInfo.getSlsSecretKey()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        List<SlsProjectInfo> projectInfoList = new ArrayList<>();
        try {
            // 发送Http请求
            CloseableHttpResponse httpResponse = httpClient.execute(projectHttpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String projectInfoRes = EntityUtils.toString(httpResponse.getEntity());
                logger.info("received projectInfo: {}", projectInfoRes);
                projectInfoList = JSON.parseObject(projectInfoRes).getJSONArray("projects").toJavaList(SlsProjectInfo.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return projectInfoList;
    }


    /**
     * 获取Project下的Logstores信息
     * @param projectName 项目名称
     * @param page 分页信息
     * @param slsConfigInfo SLS配置信息
     * @return Logstore信息
     */
    public List<String> listLogstoresInfo(String projectName, Page page, SlsConfigInfo slsConfigInfo) {
        String host = projectName + "." + slsConfigInfo.getSlsRegion() + SLS_HOST_SUFFIX; // 服务入口
        String requestUrl = String.format("http://%s/logstores?logstoreName&offset=%d&size=%d", host, page.getStart(), page.getLimit()); // 请求路径

        // 创建Http Get请求
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet logstoreHttpGet = new HttpGet(requestUrl);

        try {
            // 设置访问aliyun sls所需的Http请求头
            logstoreHttpGet.setHeader("x-log-apiversion", "0.6.0");
            logstoreHttpGet.setHeader("x-log-bodyrawsize", "0");
            logstoreHttpGet.setHeader("x-log-signaturemethod", "hmac-sha1");
            // 使用secretKey对http请求签名
            logstoreHttpGet.setHeader("authorization", SlsUtils.generateAuthorization(logstoreHttpGet,
                    slsConfigInfo.getSlsAccessKey(),
                    slsConfigInfo.getSlsSecretKey()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        List<String> logstoreList = new ArrayList<>();
        try {
            // 发送Http请求
            CloseableHttpResponse httpResponse = httpClient.execute(logstoreHttpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String logstoreRes = EntityUtils.toString(httpResponse.getEntity());
                logger.info("received projectInfo: {}", logstoreRes);
                logstoreList = JSON.parseObject(logstoreRes).getJSONArray("logstores").toJavaList(String.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logstoreList;
    }

    /**
     * 获取登录token并且免登录链接
     * @param projectName 项目名称
     * @param logstroeName 日志库名称
     * @param slsConfigInfo SLS配置信息
     * @return 免登录Url
     */
    public String getNonLoginSlsUrl(String projectName, String logstroeName, SlsConfigInfo slsConfigInfo) {
        String signInUrl = "";
        try {
            // 1. 访问令牌服务获取临时AK和Token
            AssumeRoleResponse assumeRoleRes = SlsUtils.requestAccessKeyAndSecurityToken(
                    slsConfigInfo.getSlsRegion(),
                    slsConfigInfo.getSlsAccessKey(),
                    slsConfigInfo.getSlsSecretKey(),
                    slsConfigInfo.getSlsArn(),
                    ROLE_SESSION);
            Assert.notNull(assumeRoleRes, "assumeRole获取失败");

            // 2. 通过临时AK & Token获取登录Token
            String signInToken = SlsUtils.requestSignInToken(assumeRoleRes);
            Assert.notNull(signInToken, "signInToken获取失败");

            // 3. 通过登录token生成日志服务web访问链接进行跳转
            signInUrl = SlsUtils.generateSignInUrl(signInToken, projectName, logstroeName);
            Assert.notNull(signInUrl, "signInUrl生成失败");
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signInUrl;
    }
}
