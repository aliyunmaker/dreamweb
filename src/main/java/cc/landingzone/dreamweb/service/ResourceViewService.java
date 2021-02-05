package cc.landingzone.dreamweb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

    private static final String TOTAL = "total";
    private static final String DELETED = "deleted";
    private static final String GLOBAL = "global";

    private static Map<String, String> resourceTypeMap = new HashMap<>();
    static {
        ResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("aliyunapi/resourceTypeList.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String readline;
            while ((readline = br.readLine()) != null) {
                if (readline.startsWith("#")) {
                    continue;
                }
                String[] arr = readline.split(",");
                String resourceType = arr[0];
                String resourceTypeName = arr[1];
                resourceTypeMap.put(resourceType, resourceTypeName);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static Map<String, String> endpointMap = new HashMap<>();
    static {
        ResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("aliyunapi/endpointList.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String readline;
            while ((readline = br.readLine()) != null) {
                if (readline.startsWith("#")) {
                    continue;
                }
                String[] arr = readline.split(",");
                String region = arr[0];
                String regionName = arr[1];
                endpointMap.put(region, regionName);
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

        List<AccountResourceInfo> accountResourceInfoList = new ArrayList<>();
        for (Account account : accounts) {
            String accountId = account.getAccountId();
            List<DiscoveredResourceProfile> resourceProfiles;
            try {
                // 根据子账号ID查询子账号的资源列表
                resourceProfiles = AliyunApiUtils.listDiscoveredResources(profile, accountId);
            } catch (ClientException e) {
                throw new RuntimeException(e);
            }
            Map<String/*resourceType*/, Map<String/*region*/, Integer>> resourceTypeCountMap = new HashMap<>();
            for (DiscoveredResourceProfile resourceProfile : resourceProfiles) {
                String resourceType = resourceProfile.getResourceType();
                String resourceTypeName = getResourceTypeName(resourceType);
                resourceTypeCountMap.putIfAbsent(resourceTypeName, new HashMap<>());
                Map<String, Integer> regionCountMap = resourceTypeCountMap.get(resourceTypeName);
                if (resourceProfile.getResourceDeleted() == 1) {
                    regionCountMap.putIfAbsent(TOTAL, 0);
                    regionCountMap.put(TOTAL, regionCountMap.get(TOTAL) + 1);

                    String region = resourceProfile.getRegion();
                    if (!GLOBAL.equals(region)) {
                        regionCountMap.putIfAbsent(region, 0);
                        regionCountMap.put(region, regionCountMap.get(region) + 1);
                    }
                } else {
                    regionCountMap.putIfAbsent(DELETED, 0);
                    regionCountMap.put(DELETED, regionCountMap.get(DELETED) + 1);
                }
            }

            String resourceCount = resourceTypeCountMap.entrySet().stream()
                .sorted(Entry.comparingByKey())
                .map(entry -> {
                    String resourceTypeName = entry.getKey();
                    Map<String/*region*/, Integer> regionCountMap = entry.getValue();
                    String regionCount = regionCountMap.entrySet().stream()
                        .filter(e -> !TOTAL.equals(e.getKey()) && !DELETED.equals(e.getKey()))
                        .sorted(Comparator.comparingInt(Entry::getValue))
                        .map(e -> getRegionName(e.getKey()) + ": " + e.getValue())
                        .collect(Collectors.joining(", "));
                    int total = regionCountMap.get(TOTAL);
                    if (StringUtils.isNotBlank(regionCount)) {
                        regionCount = " | " + regionCount;
                    }
                    return resourceTypeName + ": " + total + regionCount;
                })
                .collect(Collectors.joining("\n"));

            String resourceCountDeleted = resourceTypeCountMap.entrySet().stream()
                .filter(entry -> entry.getValue().containsKey(DELETED) && entry.getValue().get(DELETED) > 0)
                .sorted(Entry.comparingByKey())
                .map(entry -> getRegionName(entry.getKey()) + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));

            AccountResourceInfo accountResourceInfo = new AccountResourceInfo();
            accountResourceInfo.setAccountId(accountId);
            accountResourceInfo.setDisplayName(account.getDisplayName());
            accountResourceInfo.setResourceCount(resourceCount);
            accountResourceInfo.setResourceCountDeleted(resourceCountDeleted);
            accountResourceInfoList.add(accountResourceInfo);
        }
        return accountResourceInfoList;
    }

    private String getResourceTypeName(String resourceType) {
        String resourceTypeName = resourceTypeMap.get(resourceType);
        if (StringUtils.isBlank(resourceTypeName)) {
            resourceTypeName = resourceType;
        }
        return resourceTypeName;
    }

    private String getRegionName(String region) {
        String regionName = endpointMap.get(region);
        if (StringUtils.isBlank(regionName)) {
            regionName = region;
        }
        return regionName;
    }
}
