package cc.landingzone.dreamweb.demo.employeelist.service;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.utils.JsonUtils;
import cc.landingzone.dreamweb.demo.employeelist.utils.OkHttpClientUtils;
import cc.landingzone.dreamweb.demo.employeelist.model.*;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.*;

public class ScimUserService {

    public static void main(String[] args) throws Exception {
        List<ScimUser> allScimUser = getAllScimUser();
        for (ScimUser scimUser : allScimUser) {
            System.out.println(scimUser);
        }

//        ScimUser scimUser = new ScimUser();
////        scimUser.setId("id1");
//        scimUser.setUserName("jia199yvcyjvj");
//        scimUser.setDisplayName("jia199yvcyjvj");
//        scimUser.setFamilyName("jia199yvcyjvj");
//        scimUser.setGivenName("jia199yvcyjvj");
//        // 唯一标识用户
//        scimUser.setExternalId(String.valueOf(UUID.randomUUID()));
//        scimUser.setEmail("2360200937@qq.com");
//        addUser(scimUser);
    }


    public static final String ScimUsersURL = CommonConstants.SCIM_URL + "/Users";
    private static final Map<String, String> AuthHeader = new HashMap<>();

    static {
        AuthHeader.put("Authorization", "Bearer " + CommonConstants.SCIM_KEY);
    }

    // private static Logger logger = LoggerFactory.getLogger(ScimUserService.class);

    public static final RateLimiter RATE_LIMITER = RateLimiter.create(5);

    public static List<ScimUser> searchScimUser(String filter, Page page) throws Exception {
        RATE_LIMITER.acquire(1);
        Map<String, String> params = new HashMap<>();
        if (null != page) {
            // 服务端的bug已经修复,startIndex含义恢复正常,startIndex是从1开始的
            params.put("startIndex", String.valueOf(page.getStart() + 1));
            params.put("count", String.valueOf(page.getLimit()));
        }
        if (StringUtils.isNotBlank(filter)) {
            params.put("filter", filter);
        }
        String result = OkHttpClientUtils.get(ScimUsersURL, params, AuthHeader);
        ScimUserResponse scimUserResponse = JsonUtils.parseObject(result, ScimUserResponse.class);
        List<ScimUserResource> list = scimUserResponse.getResources();
        if (null != page) {
            page.setTotal(scimUserResponse.getTotalResults());
        }
        // logger.info("filter:" + filter);
        // logger.info("==================================");
        // logger.info(String.valueOf(list.size()));
        return convertToScimUserList(list);
    }

    public static List<ScimUser> getAllScimUser() throws Exception {
        List<ScimUser> result = new ArrayList<>();
        int numPerPage = 100;
        int pageNum = 1;
        int start = 0;
        Page page = new Page(start, numPerPage, pageNum);
        while (true) {
            List<ScimUser> pageResult = searchScimUser(null, page);
            result.addAll(pageResult);
            pageNum++;
            start += numPerPage;
            page.setStart(start);
            page.setPage(pageNum);
            if (start > page.getTotal()) {
                break;
            }
        }
        return result;
    }

    /**
     * Note: Email and externalId must be different
     */
    public static String addUser(ScimUser scimUser) throws Exception {
        RATE_LIMITER.acquire(1);
        Assert.notNull(scimUser, "scimUser can not be null!");
        ScimUserResource user = convertToUserResource(scimUser);
        String result = OkHttpClientUtils.post(ScimUsersURL, AuthHeader, JsonUtils.toJsonStringDefault(user));
        ScimUser addedScimUser = JsonUtils.parseObject(result, ScimUser.class);
        return addedScimUser.getId();
    }

    public static void updateUser(ScimUser scimUser) throws Exception {
        RATE_LIMITER.acquire(1);
        Assert.notNull(scimUser, "scimUser can not be null!");
        Assert.hasText(scimUser.getId(), "id can not be blank!");
        ScimUserResource user = convertToUserResource(scimUser);
        String id = user.getId();
        // user.setId(null);
        OkHttpClientUtils.put(ScimUsersURL + "/" + id, AuthHeader, JsonUtils.toJsonStringDefault(user));
    }

    public static void deleteUser(String id) throws Exception {
        RATE_LIMITER.acquire(1);
        Assert.hasText(id, "id can not be blank!");
        OkHttpClientUtils.delete(ScimUsersURL + "/" + id, AuthHeader);
    }

    public static List<ScimUser> convertToScimUserList(List<ScimUserResource> userResourceList) {
        if (userResourceList == null || userResourceList.isEmpty()) {
            return new ArrayList<>();
        }
        Assert.notEmpty(userResourceList, "userResourceList can not be null!");
        List<ScimUser> list = new ArrayList<ScimUser>();
        for (ScimUserResource userResource : userResourceList) {
            list.add(convertToScimUser(userResource));
        }
        return list;
    }

    public static List<ScimUserResource> convertToUserResourceList(List<ScimUser> scimUserList) {
        Assert.notEmpty(scimUserList, "scimUserList can not be null!");
        List<ScimUserResource> list = new ArrayList<ScimUserResource>();
        for (ScimUser scimUser : scimUserList) {
            list.add(convertToUserResource(scimUser));
        }
        return list;
    }

    public static ScimUser convertToScimUser(ScimUserResource userResource) {
        Assert.notNull(userResource, "userResource can not be null!");

        ScimUser scimUser = new ScimUser();
        scimUser.setId(userResource.getId());
        scimUser.setExternalId(userResource.getExternalId());
        scimUser.setUserName(userResource.getUserName());
        scimUser.setDisplayName(userResource.getDisplayName());
        if (userResource.getName() != null) {
            scimUser.setGivenName(userResource.getName().getGivenName());
            scimUser.setFamilyName(userResource.getName().getFamilyName());
        }
        if (userResource.getEmails() != null && userResource.getEmails().size() > 0) {
            scimUser.setEmail(userResource.getEmails().get(0).getValue());
        }
        return scimUser;
    }

    public static ScimUserResource convertToUserResource(ScimUser scimUser) {
        Assert.notNull(scimUser, "scimUser can not be null!");
        ScimUserResource user = new ScimUserResource();
        user.setId(scimUser.getId());
        user.setExternalId(scimUser.getExternalId());
        user.setUserName(scimUser.getUserName());
        user.setName(new ScimUserResourceName(scimUser.getFamilyName(), scimUser.getGivenName()));
        user.setActive(true);
        user.setDisplayName(scimUser.getDisplayName());
        ScimUserResourceEmail email = new ScimUserResourceEmail();// Email().setType("work").setPrimary(true).setValue(scimUser.getEmail());
        email.setPrimary(true);
        email.setType("work");
        email.setValue(scimUser.getEmail());
        user.setEmails(Collections.singletonList(email));
        user.setSchemas(Collections.singletonList("urn:ietf:params:scim:schemas:core:2.0:User"));
        return user;
    }

}
