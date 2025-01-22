package cc.landingzone.dreamcmp.demo.workshop.service;

import cc.landingzone.dreamcmp.common.utils.AliyunAPIUtils;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.exception.LogException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static cc.landingzone.dreamcmp.common.CommonConstants.Aliyun_TestAccount_SecurityToken;

/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Service
public class SlsService {

    @Value("${dreamcmp.workshop.sls_project}")
    private String projectName;

    @Value("${dreamcmp.workshop.sls_logstore}")
    private String logStoreName;

    @Autowired
    private Client slsClient;

    @Autowired
    private Client slsClientEcsRole;

    public Client getSlsClient() {
        return slsClient == null ? slsClientEcsRole : slsClient;
    }

    public void putLog(String log) throws LogException {
        List<LogItem> logGroup = new ArrayList<>();
        LogItem logItem = new LogItem() {{
            PushBack("content", log);
        }};
        logGroup.add(logItem);
        getSlsClient().PutLogs(projectName, logStoreName, "", logGroup, "dreamcmp");
    }

    public String getSignedSlsUrl() {
        // 分享链接
        String slsUrl = String.format(
            "https://sls4service.console.aliyun.com/lognext/project/%s/logsearch/%s?slsRegion=cn-hangzhou&hideTopbar=true&hideSidebar=true&ignoreTabLocalStorage=true&isShare=true",
            projectName,
            logStoreName
        );

        String ak = getSlsClient().getAccessId();
        String sk = getSlsClient().getAccessKey();
        String token = getSlsClient().getSecurityToken();
        String signinToken = AliyunAPIUtils.getSigninToken(ak, sk, token);

        return AliyunAPIUtils.getSigninUrl("https://aliyun.com", slsUrl, signinToken);
    }
}
