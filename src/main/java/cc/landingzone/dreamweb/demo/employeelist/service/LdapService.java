package cc.landingzone.dreamweb.demo.employeelist.service;

import cc.landingzone.dreamweb.common.utils.JsonUtils;
import cc.landingzone.dreamweb.demo.employeelist.model.ScimGroup;
import cc.landingzone.dreamweb.demo.employeelist.model.ScimUser;
import cc.landingzone.dreamweb.demo.employeelist.model.SyncRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 *
 * @author charles
 * @date 2021-10-19
 */
@Component
public class LdapService {

    private static Logger logger = LoggerFactory.getLogger(LdapService.class);

    private static ThreadLocal<String> TaskTraceId = new ThreadLocal<>();
    private static List<ScimGroup> scimGroupServerList;

    public static String syncLdaptoScim(List<SyncRequest> list, Boolean removeUnselected) throws Exception {
        TaskTraceId.set("task_" + System.currentTimeMillis());
        List<ScimUser> scimUserServerList = ScimUserService.getAllScimUser();
        List<ScimGroup> scimGroupServerList = ScimGroupService.getAllScimGroup();
        Map<String, ScimUser> scimUserServerMap = scimUserServerList.stream()
            .collect(Collectors.toMap(x -> x.getExternalId(), x -> x));
        Map<String, ScimGroup> scimGroupServerMap = scimGroupServerList.stream()
            .collect(Collectors.toMap(x -> x.getExternalId(), x -> x));
        int scimUserCount = scimUserServerList.size();
        int scimGroupCount = scimGroupServerList.size();
        int ldapUserCount = 0;
        int ldapGroupCount = 0;
        AtomicInteger scimUserAddCount = new AtomicInteger(0);
        AtomicInteger scimUserUpdateCount = new AtomicInteger(0);
        AtomicInteger scimUserNoChangeCount = new AtomicInteger(0);
        AtomicInteger scimUserDeleteCount = new AtomicInteger(0);
        AtomicInteger scimGroupAddCount = new AtomicInteger(0);
        AtomicInteger scimGroupUpdateCount = new AtomicInteger(0);
        AtomicInteger scimGroupNoChangeCount = new AtomicInteger(0);
        AtomicInteger scimGroupDeleteCount = new AtomicInteger(0);
        AtomicInteger scimErrorCount = new AtomicInteger(0);
        Map<String, String> groupMemberMap = new HashMap<>();
        Map<String, String> userIdMap = new HashMap<>();
        for (SyncRequest syncRequest: list) {
            try {
                if ("user".equals(syncRequest.getTypeName())) {
                    ldapUserCount++;
                    ScimUser scimUser = new ScimUser();
                    scimUser.setDisplayName(syncRequest.getDisplayName());
                    scimUser.setEmail(syncRequest.getEmail());
                    scimUser.setExternalId(syncRequest.getExternalId());
                    scimUser.setFamilyName(syncRequest.getFamilyName());
                    scimUser.setGivenName(syncRequest.getGivenName());
                    scimUser.setUserName(syncRequest.getUserName());
                    String userId = syncLdapUsertoScim(scimUser, scimUserServerMap, scimUserAddCount,
                        scimUserUpdateCount, scimUserNoChangeCount);
                    userIdMap.put(scimUser.getExternalId(), userId);
                } else if ("userGroup".equals(syncRequest.getTypeName())) {
                    ldapGroupCount++;
                    ScimGroup scimGroup = new ScimGroup();
                    scimGroup.setDisplayName(syncRequest.getUserName());
                    scimGroup.setExternalId(syncRequest.getExternalId());
                    String groupId = syncLdapGrouptoScim(scimGroup, scimGroupServerMap, scimGroupAddCount,
                        scimGroupUpdateCount, scimGroupNoChangeCount);
//                    String memberStr = ldapItem.get("member");
//                    if (null != memberStr) {
//                        groupMemberMap.put(groupId, memberStr);
//                    }
                } else {
                    // only support objectClass is user or group
                    logger.info("[not support type: {}]: {}", syncRequest.getTypeName(), JsonUtils.toJsonString(syncRequest));
                }
            } catch (Exception e) {
                scimErrorCount.getAndIncrement();
                logger.error(e.getMessage(), e);
            }
        }

        // 同步组成员,前置: 先在组同步的时候把组成员全部remove
        try {
            Map<String, List<String>> groupMemberResult = mergeGroupMemberMap(groupMemberMap, userIdMap);
            for (Map.Entry<String, List<String>> entry : groupMemberResult.entrySet()) {
                ScimGroupService.addMembersByGroupId(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // 删除Ldap中已经删除的用户和组
        if (removeUnselected) {
            logger.info("[{}]remove users and groups not exist in ldap", TaskTraceId.get());
            for (ScimUser scimUserServer : scimUserServerMap.values()) {
                ScimUserService.deleteUser(scimUserServer.getId());
                logger.info("[{}][user-delete][{}]:{}", TaskTraceId.get(), scimUserServer.getId(),
                    JsonUtils.toJsonString(scimUserServer));
            }

            for (ScimGroup scimGroupServer : scimGroupServerMap.values()) {
                ScimGroupService.deleteGroup(scimGroupServer.getId());
                logger.info("[{}][group-delete][{}]:{}", TaskTraceId.get(), scimGroupServer.getId(),
                    JsonUtils.toJsonString(scimGroupServer));
            }

            scimUserDeleteCount.set(scimUserServerMap.size());
            scimGroupDeleteCount.set(scimGroupServerMap.size());
        }

        String result = String.format(
            "[syncLdaptoScim][%s]:\nldapTotal[%d],scimError[%d],\nuser[ldap:%d,scim:%d]\n[add:%d,update:%d,delete:%d,nochange:%d],\ngroup[ldap:%d,scim:%d]\n[add:%d,update:%d,delete:%d,nochange:%d]",
            TaskTraceId.get(), list.size(), scimErrorCount.get(), ldapUserCount, scimUserCount, scimUserAddCount.get(),
            scimUserUpdateCount.get(), scimUserDeleteCount.get(), scimUserNoChangeCount.get(), ldapGroupCount,
            scimGroupCount, scimGroupAddCount.get(), scimGroupUpdateCount.get(), scimGroupDeleteCount.get(),
            scimGroupNoChangeCount.get());

        // String result = "[" + TaskTraceId.get() + "][syncLdaptoScim] total[" +
        // list.size()
        // + "],user[][add:,update:,delete:],group[][add:,update:,delete:]";

        logger.info(result);
        TaskTraceId.remove();

        return result;
    }


    /**
     * 使用 userName当做查询主键
     *
     * @param scimUser
     * @throws Exception
     */
    public static String syncLdapUsertoScim(ScimUser scimUser, Map<String, ScimUser> scimUserServerMap,
        AtomicInteger scimUserAddCount, AtomicInteger scimUserUpdateCount, AtomicInteger scimUserNoChangeCount)
        throws Exception {
        Assert.notNull(scimUser, "scimUser can not be null!");
        Assert.hasText(scimUser.getUserName(), "scimUser's userName can not be blank!");
        ScimUser scimUserInServer = scimUserServerMap.get(scimUser.getExternalId());
        String userId = null;
        // add
        if (null == scimUserInServer) {
            scimUserAddCount.incrementAndGet();
            userId = ScimUserService.addUser(scimUser);
            logger.info("[{}][user-add][{}]:{}", TaskTraceId.get(), scimUser.getUserName(),
                JsonUtils.toJsonString(scimUser));
        } else if (!scimUser.equals(scimUserInServer)) {
            scimUserUpdateCount.incrementAndGet();
            userId = scimUserInServer.getId();
            // update
            scimUser.setId(userId);
            ScimUserService.updateUser(scimUser);
            logger.info("[{}][user-update][{}]:{}", TaskTraceId.get(), scimUser.getUserName(),
                JsonUtils.toJsonString(scimUser));
        } else {
            // logger.info("[{}][user-nochange][{}]:{}", TaskTraceId.get(), scimUser.getUserName(),
            // JsonUtils.toJsonString(scimUser));
            scimUserNoChangeCount.incrementAndGet();
        }

        // 这样最后剩下的就是Ldap中已经删除的
        if (null != scimUserInServer) {
            scimUserServerMap.remove(scimUserInServer.getExternalId());
        }
        return userId;
    }

    /**
     * 使用 displayName当做查询主键
     *
     * @param scimGroup
     */
    public static String syncLdapGrouptoScim(ScimGroup scimGroup, Map<String, ScimGroup> scimGroupServerMap,
        AtomicInteger scimGroupAddCount, AtomicInteger scimGruopUpdateCount, AtomicInteger scimGroupNoChangeCount)
        throws Exception {
        Assert.notNull(scimGroup, "scimGroup can not be null!");
        Assert.hasText(scimGroup.getDisplayName(), "scimGroup's displayName can not be blank!");
        ScimGroup scimGroupInServer = scimGroupServerMap.get(scimGroup.getExternalId());
        String groupId = null;
        // add
        if (null == scimGroupInServer) {
            scimGroupAddCount.incrementAndGet();
            groupId = ScimGroupService.addGroup(scimGroup);
            logger.info("[{}][group-add][{}]:{}", TaskTraceId.get(), scimGroup.getDisplayName(),
                JsonUtils.toJsonString(scimGroup));
        } else if (!scimGroup.equals(scimGroupInServer)) {
            scimGruopUpdateCount.incrementAndGet();
            groupId = scimGroupInServer.getId();
            // update
            scimGroup.setId(groupId);
            ScimGroupService.updateGroup(scimGroup);
            logger.info("[{}][group-update][{}]:{}", TaskTraceId.get(), scimGroup.getDisplayName(),
                JsonUtils.toJsonString(scimGroup));

            // remove all member from group, 由于scim没有返回group的member列表,所以暂时只能先删除,再重建
            ScimGroupService.removeAllMembersByGroupId(groupId);
        } else {
            // logger.info("[{}][group-nochange][{}]:{}", TaskTraceId.get(), scimGroup.getDisplayName(),
            // JsonUtils.toJsonString(scimGroup));
            scimGroupNoChangeCount.incrementAndGet();
        }

        // 这样最后剩下的就是Ldap中已经删除的
        if (null != scimGroupInServer) {
            scimGroupServerMap.remove(scimGroupInServer.getExternalId());
        }
        return groupId;
    }

    public static Map<String, List<String>> mergeGroupMemberMap(Map<String, String> groupMemberMap,
                                                                Map<String, String> userIdMap) {
        Map<String, List<String>> result = new HashMap<>();
        for (Map.Entry<String, String> entry : groupMemberMap.entrySet()) {
            String[] memberArray = entry.getValue().split("\\|");
            List<String> userIdList = new ArrayList<>();
            for (String memberDN : memberArray) {
                if (StringUtils.isNotBlank(userIdMap.get(memberDN))) {
                    userIdList.add(userIdMap.get(memberDN));
                }
            }
            if (!userIdList.isEmpty()) {
                result.put(entry.getKey(), userIdList);
            }
        }
        return result;
    }
}
