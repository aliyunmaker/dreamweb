package cc.landingzone.dreamweb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cc.landingzone.dreamweb.model.AccountResourceInfo;
import cc.landingzone.dreamweb.model.aliyunapi.result.config.DiscoveredResourceProfile;
import cc.landingzone.dreamweb.model.aliyunapi.result.resourcemanager.Account;
import cc.landingzone.dreamweb.utils.AliyunApiUtils;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * @author merc-bottle
 * @date 2021/02/05
 */
@Component
public class ResourceViewService {

    private static Logger logger = LoggerFactory.getLogger(ResourceViewService.class);

    private static Map<String, String> resourceMap = new HashMap<>();

    static {
        ResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("resourcemanager/resourceInfoList.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String readline;
            while ((readline = br.readLine()) != null) {
                if (readline.startsWith("#")) {
                    continue;
                }
                String[] arr = readline.split(",");
                String resourceTypeName = arr[0];
                String resourceType = arr[1];
                resourceMap.put(resourceType, resourceTypeName);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public List<AccountResourceInfo> listAccountResourceInfo(String regionId, String accessKeyId, String accessKeySecret) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        List<Account> accounts;
        try {
            // 查询子账号列表
            accounts = AliyunApiUtils.listAccounts(profile);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
        Map<String, Map<String, Long>> countByAccountAndType = accounts.stream()
            .map(Account::getAccountId)
            .collect(Collectors.toMap(accountId -> accountId, accountId -> {
                List<DiscoveredResourceProfile> resourceProfiles;
                try {
                    // 根据子账号ID查询子账号的资源列表
                    resourceProfiles = AliyunApiUtils.listDiscoveredResources(profile, accountId);
                } catch (ClientException e) {
                    throw new RuntimeException(e);
                }
                return resourceProfiles.stream()
                    .filter(resourceProfile -> resourceProfile.getResourceDeleted() == 1)
                    // 按资源类型分组统计数量
                    .collect(Collectors.groupingBy(DiscoveredResourceProfile::getResourceType, Collectors.counting()));
            }));

        List<AccountResourceInfo> accountResourceInfoList = countByAccountAndType.entrySet().stream()
            .map(entry -> {
                String accountId = entry.getKey();
                Map<String, Long> resourceCountByType = entry.getValue();
                String resourceCount = resourceCountByType.entrySet().stream()
                    .map(e -> {
                        String resourceType = e.getKey();
                        // 将 [资源类型] 映射成 [资源类型名称]
                        // 例如: ACS::ECS::Instance -> ECS实例
                        String resourceTypeName = resourceMap.get(resourceType);
                        return (StringUtils.isNotBlank(resourceTypeName) ? resourceTypeName : resourceType) + ": " + e
                            .getValue();
                    })
                    .sorted()
                    .collect(Collectors.joining("\n"));
                return new AccountResourceInfo(accountId, resourceCount);
            })
            .collect(Collectors.toList());
        return accountResourceInfoList;
    }
}
