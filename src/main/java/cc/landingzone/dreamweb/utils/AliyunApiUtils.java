package cc.landingzone.dreamweb.utils;

import java.util.ArrayList;
import java.util.List;

import cc.landingzone.dreamweb.model.aliyunapi.result.config.DiscoveredResourceProfile;
import cc.landingzone.dreamweb.model.aliyunapi.result.config.ListDiscoveredResourcesResult;
import cc.landingzone.dreamweb.model.aliyunapi.result.resourcemanager.Account;
import cc.landingzone.dreamweb.model.aliyunapi.result.resourcemanager.ListAccountsResult;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author merc-bottle
 * @date 2021/02/03
 */
public class AliyunApiUtils {

    private static Logger logger = LoggerFactory.getLogger(AliyunApiUtils.class);

    private static final String RESOURCE_MANAGER_SYS_DOMAIN = "resourcemanager.aliyuncs.com";
    private static final String RESOURCE_MANAGER_SYS_VERSION = "2020-03-31";
    private static final String CONFIG_SYS_DOMAIN = "config.cn-shanghai.aliyuncs.com";
    private static final String CONFIG_SYS_VERSION = "2019-01-08";

    private static final int PAGE_SIZE = 100;

    public static List<Account> listAccounts(DefaultProfile profile) throws ClientException {
        List<Account> accounts = new ArrayList<>();
        // first query
        int pageNumber = 1;
        ListAccountsResult result = doListAccounts(profile, pageNumber);
        int total = result.getTotalCount();
        accounts.addAll(result.getAccounts().getAccount());

        // common query
        while (pageNumber * PAGE_SIZE < total) {
            pageNumber++;
            result = doListAccounts(profile, pageNumber);
            accounts.addAll(result.getAccounts().getAccount());
        }
        return accounts;
    }

    private static ListAccountsResult doListAccounts(DefaultProfile profile, int pageNumber) throws ClientException {
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysDomain(RESOURCE_MANAGER_SYS_DOMAIN);
        request.setSysVersion(RESOURCE_MANAGER_SYS_VERSION);
        request.setSysAction("ListAccounts");
        request.setSysProtocol(ProtocolType.HTTPS);
        request.putQueryParameter("PageNumber", String.valueOf(pageNumber));
        request.putQueryParameter("PageSize", String.valueOf(PAGE_SIZE));
        CommonResponse response = client.getCommonResponse(request);
        return JsonUtils.parseObject(response.getData(), ListAccountsResult.class);
    }

    public static List<DiscoveredResourceProfile> listDiscoveredResources(DefaultProfile profile, String memberId)
        throws ClientException {
        List<DiscoveredResourceProfile> resourceProfiles = new ArrayList<>();
        // first query
        int pageNumber = 1;
        ListDiscoveredResourcesResult result = doListDiscoveredResources(profile, pageNumber, memberId);
        int total = result.getDiscoveredResourceProfiles().getTotalCount();
        resourceProfiles.addAll(result.getDiscoveredResourceProfiles().getDiscoveredResourceProfileList());

        // common query
        while (pageNumber * PAGE_SIZE < total) {
            pageNumber++;
            result = doListDiscoveredResources(profile, pageNumber, memberId);
            resourceProfiles.addAll(result.getDiscoveredResourceProfiles().getDiscoveredResourceProfileList());
        }
        return resourceProfiles;
    }

    public static ListDiscoveredResourcesResult doListDiscoveredResources(DefaultProfile profile, int pageNumber,
                                                                          String memberId) throws ClientException {
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysDomain(CONFIG_SYS_DOMAIN);
        request.setSysVersion(CONFIG_SYS_VERSION);
        request.setSysAction("ListDiscoveredResources");
        request.setSysProtocol(ProtocolType.HTTPS);
        request.putQueryParameter("PageNumber", String.valueOf(pageNumber));
        request.putQueryParameter("PageSize", String.valueOf(PAGE_SIZE));
        request.putQueryParameter("MemberId", memberId);
        request.putQueryParameter("MultiAccount", "true");
        CommonResponse response = client.getCommonResponse(request);
        return JsonUtils.parseObject(response.getData(), ListDiscoveredResourcesResult.class);
    }
}
