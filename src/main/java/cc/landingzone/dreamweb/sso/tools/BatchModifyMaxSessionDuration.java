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


    public static final String Aliyun_AccessKeyId = "LTAI4GE8oLW1akw6nHcdyfoq";
    public static final String Aliyun_AccessKeySecret = "cvOX2knEzodrqpkNtwISSSCZf7ncO9";

    //这个功能跟region没有关系,可以不用修改
    public static final DefaultProfile AliyunProfile = DefaultProfile.getProfile("cn-hangzhou");

    public static final BasicCredentials BasicCredentials = new BasicCredentials(Aliyun_AccessKeyId, Aliyun_AccessKeySecret);


    public static void main(String[] args) throws Exception {
        List<String> accountList = listAccounts();
        for (String accountUid : accountList) {
            List<ListRolesResponse.Role> roleList = getRoleNameListByUid(accountUid);
            System.out.println("=================accountUid: " + accountUid);
            for (ListRolesResponse.Role role : roleList) {
                System.out.println("role: " + role.getRoleName());
                // 这里可以按照uid和role的组合条件来过滤
                if ("1668299748235410".equalsIgnoreCase(accountUid) && "resourcedirectoryaccountaccessrole".equalsIgnoreCase(role.getRoleName())) {
                    String updateresult = updateRoleByName(accountUid, role.getRoleName(), 4123);
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
    public static String updateRoleByName(String uid, String roleName, int maxSessionDurationSeconds) throws Exception {
        STSAssumeRoleSessionCredentialsProvider provider = new STSAssumeRoleSessionCredentialsProvider(BasicCredentials, "acs:ram::" + uid + ":role/resourcedirectoryaccountaccessrole", AliyunProfile);
        DefaultAcsClient client = new DefaultAcsClient(AliyunProfile, provider);
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
    public static List<ListRolesResponse.Role> getRoleNameListByUid(String uid) throws Exception {
        STSAssumeRoleSessionCredentialsProvider provider = new STSAssumeRoleSessionCredentialsProvider(BasicCredentials, "acs:ram::" + uid + ":role/resourcedirectoryaccountaccessrole", AliyunProfile);
        DefaultAcsClient client = new DefaultAcsClient(AliyunProfile, provider);
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
    public static List<String> listAccounts() throws Exception {
        IAcsClient client = new DefaultAcsClient(AliyunProfile, BasicCredentials);
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
