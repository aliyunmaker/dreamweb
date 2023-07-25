package cc.landingzone.dreamcmp.demo.employeelist.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.utils.JsonUtils;
import cc.landingzone.dreamcmp.demo.employeelist.model.Page;
import cc.landingzone.dreamcmp.demo.employeelist.model.ScimGroup;
import cc.landingzone.dreamcmp.demo.employeelist.model.ScimGroupResponse;
import cc.landingzone.dreamcmp.demo.employeelist.utils.OkHttpClientUtils;

import java.util.*;

public class ScimGroupService {

    // private static Logger logger = LoggerFactory.getLogger(ScimGroupService.class);
    public static void main(String[] args) throws Exception {
        ScimGroup scimGroup = new ScimGroup();
//        scimGroup.setId("id");
        scimGroup.setDisplayName("displayName1");
        scimGroup.setExternalId("externalIdd");
        addGroup(scimGroup);
    }

    public static final String ScimGroupsURL = CommonConstants.SCIM_URL + "/Groups";

    private static final Map<String, String> AuthHeader = new HashMap<>();

    static {
        AuthHeader.put("Authorization", "Bearer " + CommonConstants.SCIM_KEY);
    }

    public static List<ScimGroup> searchScimGroup(String filter, Page page) throws Exception {
        ScimUserService.RATE_LIMITER.acquire(1);
        Map<String, String> params = new HashMap<>();
        if (null != page) {
            // 服务端的bug已经修复,startIndex含义恢复正常,startIndex是从1开始的
            params.put("startIndex", String.valueOf(page.getStart() + 1));
            params.put("count", String.valueOf(page.getLimit()));
        }
        if (StringUtils.isNotBlank(filter)) {
            params.put("filter", filter);
        }
        String result = OkHttpClientUtils.get(ScimGroupsURL, params, AuthHeader);
        ScimGroupResponse scimGroupResponse = JsonUtils.parseObject(result, ScimGroupResponse.class);
        List<ScimGroup> list = scimGroupResponse.getResources();
        if (null != page) {
            page.setTotal(scimGroupResponse.getTotalResults());
        }
        if (null == list) {
            return new ArrayList<>();
        }
        return list;
    }

    public static List<ScimGroup> getAllScimGroup() throws Exception {
        List<ScimGroup> result = new ArrayList<>();
        int numPerPage = 100;
        int pageNum = 1;
        int start = 0;
        Page page = new Page(start, numPerPage, pageNum);
        while (true) {
            List<ScimGroup> pageResult = searchScimGroup(null, page);
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

    public static String addGroup(ScimGroup scimGroup) throws Exception {
        ScimUserService.RATE_LIMITER.acquire(1);
        Assert.notNull(scimGroup, "scimGroup can not be null!");
        scimGroup.setSchemas(Collections.singletonList("urn:ietf:params:scim:schemas:core:2.0:Group"));
        String result = OkHttpClientUtils.post(ScimGroupsURL, AuthHeader, JsonUtils.toJsonStringDefault(scimGroup));
        ScimGroup addedScimGroup = JsonUtils.parseObject(result, ScimGroup.class);
        return addedScimGroup.getId();
    }

    public static void updateGroup(ScimGroup scimGroup) throws Exception {
        ScimUserService.RATE_LIMITER.acquire(1);
        Assert.notNull(scimGroup, "scimGroup can not be null!");
        Assert.hasText(scimGroup.getId(), "id can not be blank!");
        String id = scimGroup.getId();
        // user.setId(null);
        scimGroup.setSchemas(Collections.singletonList("urn:ietf:params:scim:schemas:core:2.0:Group"));
        OkHttpClientUtils.put(ScimGroupsURL + "/" + id, AuthHeader, JsonUtils.toJsonStringDefault(scimGroup));

    }

    public static void deleteGroup(String id) throws Exception {
        ScimUserService.RATE_LIMITER.acquire(1);
        Assert.hasText(id, "id can not be blank!");
        OkHttpClientUtils.delete(ScimGroupsURL + "/" + id, AuthHeader);
    }

    public static void removeAllMembersByGroupId(String groupId) throws Exception {
        ScimUserService.RATE_LIMITER.acquire(1);
        Assert.hasText(groupId, "groupId can not be blank!");
        String jsonBody =
            "{\"schemas\": [\"urn:ietf:params:scim:api:messages:2.0:PatchOp\"],\"Operations\": [{\"op\": \"remove\",\"path\": \"members\"}]}";
        OkHttpClientUtils.patch(ScimGroupsURL + "/" + groupId, AuthHeader, jsonBody);
    }

    public static void addMembersByGroupId(String groupId, List<String> userIdList) throws Exception {
        ScimUserService.RATE_LIMITER.acquire(1);
        Assert.hasText(groupId, "groupId can not be blank!");
        List<Map<String, String>> mapList = new ArrayList<>();
        for (String userId : userIdList) {
            Map<String, String> map = new HashMap<>();
            map.put("value", userId);
            mapList.add(map);
        }
        String values = JsonUtils.toJsonString(mapList);
        String jsonBody =
            "{\"schemas\": [\"urn:ietf:params:scim:api:messages:2.0:PatchOp\"],\"Operations\": [{\"name\": \"addMember\",\"op\": \"add\",\"path\": \"members\",\"value\": "
                + values + "}]}";
        OkHttpClientUtils.patch(ScimGroupsURL + "/" + groupId, AuthHeader, jsonBody);
    }

}
