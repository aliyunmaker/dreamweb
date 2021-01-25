package cc.landingzone.dreamweb.sso.tools;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.BasicCredentials;
import com.aliyuncs.auth.STSAssumeRoleSessionCredentialsProvider;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.ram.model.v20150501.ListRolesRequest;
import com.aliyuncs.ram.model.v20150501.ListRolesResponse;


/**
 * 批量修改RD下面所有子账号中所有Role的MaxSessionDuration
 */
public class BatchModifyMaxSessionDuration {


    public static void main(String[] args) throws Exception {
        BasicCredentials basicCredentials = new BasicCredentials("", "");
        String uid = "";
        DefaultProfile AliyunProfile = DefaultProfile.getProfile("cn-hangzhou");
        IAcsClient masterClient = new DefaultAcsClient(AliyunProfile, basicCredentials);

        STSAssumeRoleSessionCredentialsProvider provider = new STSAssumeRoleSessionCredentialsProvider(basicCredentials, "acs:ram::" + uid + ":role/resourcedirectoryaccountaccessrole", AliyunProfile);
        //这个功能跟region没有关系,可以不用修改
        IAcsClient subClient = new DefaultAcsClient(AliyunProfile, provider);


        List<String> accountList = listAccounts(masterClient);
        for (String accountUid : accountList) {
            List<ListRolesResponse.Role> roleList = getRoleNameListByUid(accountUid, subClient);
            System.out.println("=================accountUid: " + accountUid);
            for (ListRolesResponse.Role role : roleList) {
                System.out.println("role: " + role.getRoleName());
                // 这里可以按照uid和role的组合条件来过滤
                if ("1668299748235410".equalsIgnoreCase(accountUid) && "resourcedirectoryaccountaccessrole".equalsIgnoreCase(role.getRoleName())) {
                    String updateresult = updateRoleByName(accountUid, role.getRoleName(), 4096, subClient);
                    System.out.println("update bingo:" + updateresult);
                }
            }
        }

    }


    /**
     * 更新账号下的Role的最大过期时间
     *
     * @param uid                       账号ID
     * @param roleName                  role name
     * @param maxSessionDurationSeconds 过期时长,单位:秒
     * @throws Exception
     */
    public static String updateRoleByName(String uid, String roleName, int maxSessionDurationSeconds, IAcsClient client) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("ram.aliyuncs.com");
        request.setSysVersion("2015-05-01");
        request.setSysAction("UpdateRole");
        request.putQueryParameter("RoleName", roleName);
        request.putQueryParameter("NewMaxSessionDuration", String.valueOf(maxSessionDurationSeconds));
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        return response.getData();
    }


    /**
     * 根据UID获取该账号下所有的Role
     *
     * @param uid
     * @return
     * @throws Exception
     */
    public static List<ListRolesResponse.Role> getRoleNameListByUid(String uid, IAcsClient client) throws Exception {
        ListRolesRequest request = new ListRolesRequest();
        ListRolesResponse response = client.getAcsResponse(request);
        return response.getRoles();
    }


    /**
     * get resource account list
     *
     * @return
     * @throws Exception
     */
    public static List<String> listAccounts(IAcsClient client) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("ListAccounts");
        //TODO 暂时先设置成100,如果账号数量超过100,需要修改代码,增加翻页的逻辑
        request.putQueryParameter("PageSize", "100");
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray accountList = jsonObject.getJSONObject("Accounts").getJSONArray("Account");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < accountList.size(); i++) {
            list.add(accountList.getJSONObject(i).getString("AccountId"));
        }
        return list;
    }

}
