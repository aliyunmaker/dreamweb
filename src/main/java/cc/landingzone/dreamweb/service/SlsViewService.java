package cc.landingzone.dreamweb.service;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.request.ListLogStoresRequest;
import com.aliyun.openservices.log.request.ListProjectRequest;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import com.aliyun.openservices.log.response.ListProjectResponse;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.SlsConfigInfo;
import cc.landingzone.dreamweb.utils.SlsUtils;

@Service
public class SlsViewService {

    private static final String ROLE_SESSION = "console-role-session"; // 用户自定义参数。此参数用来区分不同的令牌，可用于用户级别的访问审计。
    private static final String SLS_HOST_SUFFIX = ".log.aliyuncs.com";

    /**
     * 根据sls配置信息获取所有Project信息
     *
     * @param page          分页参数
     * @param slsConfigInfo sls配置信息
     * @return 全部Project信息
     */
    public List<Project> listProjectsInfo(Page page, SlsConfigInfo slsConfigInfo) throws Exception {
        String host = slsConfigInfo.getSlsRegion() + SLS_HOST_SUFFIX; //服务入口

        List<Project> projectList = new ArrayList<>();
        AssumeRoleResponse assumeRoleRes = SlsUtils.requestAccessKeyAndSecurityToken(
            slsConfigInfo.getSlsRegion(),
            slsConfigInfo.getSlsAccessKey(),
            slsConfigInfo.getSlsSecretKey(),
            slsConfigInfo.getSlsArn(),
            ROLE_SESSION);
        Client slsClient = new Client(host,
            assumeRoleRes.getCredentials().getAccessKeyId(),
            assumeRoleRes.getCredentials().getAccessKeySecret());
        slsClient.setSecurityToken(assumeRoleRes.getCredentials().getSecurityToken());

        ListProjectRequest request = new ListProjectRequest("", page.getStart(), page.getLimit());
        ListProjectResponse response = slsClient.ListProject(request);
        projectList = response.getProjects();

        return projectList;
    }

    /**
     * 获取Project下的Logstores信息
     *
     * @param projectName   项目名称
     * @param page          分页信息
     * @param slsConfigInfo SLS配置信息
     * @return Logstore信息
     */
    public List<String> listLogstoresInfo(String projectName, Page page, SlsConfigInfo slsConfigInfo) throws Exception {
        String host = slsConfigInfo.getSlsRegion() + SLS_HOST_SUFFIX; // 服务入口

        AssumeRoleResponse assumeRoleRes = SlsUtils.requestAccessKeyAndSecurityToken(
            slsConfigInfo.getSlsRegion(),
            slsConfigInfo.getSlsAccessKey(),
            slsConfigInfo.getSlsSecretKey(),
            slsConfigInfo.getSlsArn(),
            ROLE_SESSION);
        Client slsClient = new Client(host,
            assumeRoleRes.getCredentials().getAccessKeyId(),
            assumeRoleRes.getCredentials().getAccessKeySecret());
        slsClient.setSecurityToken(assumeRoleRes.getCredentials().getSecurityToken());

        ListLogStoresRequest logStoresRequest = new ListLogStoresRequest(projectName, page.getStart(), page.getLimit(),
            "");
        ListLogStoresResponse logStoresResponse = slsClient.ListLogStores(logStoresRequest);
        List<String> logstoreList = logStoresResponse.GetLogStores();

        return logstoreList;
    }

    /**
     * 获取登录token并且免登录链接
     *
     * @param projectName   项目名称
     * @param logstroeName  日志库名称
     * @param slsConfigInfo SLS配置信息
     * @return 免登录Url
     */
    public String getNonLoginSlsUrl(String projectName, String logstroeName, SlsConfigInfo slsConfigInfo)
        throws Exception {
        String signInUrl = "";

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

        return signInUrl;
    }
}
