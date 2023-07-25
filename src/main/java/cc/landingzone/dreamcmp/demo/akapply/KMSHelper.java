package cc.landingzone.dreamcmp.demo.akapply;

import com.aliyun.kms20160120.models.ListSecretsResponseBody;
import com.aliyun.tea.TeaConverter;
import com.aliyun.tea.TeaPair;
import com.aliyun.teautil.models.RuntimeOptions;

import cc.landingzone.dreamcmp.common.ClientHelper;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.RamHelper;
import cc.landingzone.dreamcmp.common.utils.JsonUtils;
import cc.landingzone.dreamcmp.demo.akapply.model.Tag;
import cc.landingzone.dreamcmp.demo.akapply.model.ak.AccessKey;
import cc.landingzone.dreamcmp.demo.akapply.model.ak.AccessKeys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author：珈贺
 * Description：
 */
public class KMSHelper {

    public static Logger logger = LoggerFactory.getLogger(KMSHelper.class);

    public static void main(String[] args) throws Exception {
//        initKMS();

//        createSecret("applicationName","environment",
//                "tianyu",CommonConstants.Aliyun_AccessKeyId,CommonConstants.Aliyun_AccessKeySecret);
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag(CommonConstants.APPLICATION_TAG_KEY,"applicationName"));
        tags.add(new Tag(CommonConstants.ENVIRONMENT_TYPE_TAG_KEY,"environment"));
        System.out.println(JsonUtils.toJsonString(tags));
    }

//    /**
//     * query main key, get keyId, if not exist, create it
//     * not finished
//     */
//    public static String getMainKeyId() throws Exception{
//        com.aliyun.kms20160120.Client client = ClientHelper.createKmsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
//        com.aliyun.kms20160120.models.ListKeysRequest listKeysRequest = new com.aliyun.kms20160120.models.ListKeysRequest()
//                .setFilters("[{\"Key\":\"KeyState\", \"Values\":[\"Enabled\"]}]");
//        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
//        ListKeysResponseBody listKeysBody = client.listKeysWithOptions(listKeysRequest, runtime).getBody();
//        if (listKeysBody.getTotalCount() == 0){
//            // create main key
//
//            return null;
//        }else {
//            return listKeysBody.getKeys().getKey().get(0).getKeyId();
//        }
//    }

    /**
     * create a new secret(ramCredentials),and add tags
     * not finished
     */
    public static String createSecret(String applicationName,String environment,String ramUserName,
                                      String accessKeyId,String accessKeySecret) throws Exception {
        com.aliyun.kms20160120.Client client = ClientHelper.createKmsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        // set extendedConfig
        java.util.Map<String, String> extendedConfig = TeaConverter.buildMap(
                new TeaPair("SecretSubType", "RamUserAccessKey"),
                new TeaPair("UserName", ramUserName)
        );
        // generate secret data
        AccessKeys accessKeys = new AccessKeys();
        AccessKey accessKey = new AccessKey(accessKeyId,accessKeySecret);
        accessKeys.setAccessKeys(Arrays.asList(accessKey));

        // set tags
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag(CommonConstants.APPLICATION_TAG_KEY,applicationName));
        tags.add(new Tag(CommonConstants.ENVIRONMENT_TYPE_TAG_KEY,environment));

        com.aliyun.kms20160120.models.CreateSecretRequest createSecretRequest = new com.aliyun.kms20160120.models.CreateSecretRequest()
                .setSecretName("$Auto")
                .setVersionId("v1")
                .setSecretData(JsonUtils.toJsonString(accessKeys))
//                .setEncryptionKeyId(mainKeyId)
                .setSecretDataType("text")
//                .setDescription("dreamweb")
                .setTags(JsonUtils.toJsonString(tags))
                .setSecretType("RAMCredentials")
                .setExtendedConfig(extendedConfig)
                .setDKMSInstanceId(CommonConstants.DKMSInstanceId)
                .setEncryptionKeyId(CommonConstants.EncryptionKeyId);
        RuntimeOptions runtime = new RuntimeOptions();
        return client.createSecretWithOptions(createSecretRequest, runtime).getBody().getSecretName();
    }

    /**
     * Store a new version value for the credential
     */
    public static void putSecretValue(String versionId,String secretName,String secretData) throws Exception {
        com.aliyun.kms20160120.Client client = ClientHelper.createKmsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.kms20160120.models.PutSecretValueRequest putSecretValueRequest = new com.aliyun.kms20160120.models.PutSecretValueRequest()
                .setVersionId(versionId)
                .setSecretName(secretName)
                .setSecretData(secretData);
        RuntimeOptions runtime = new RuntimeOptions();
        client.putSecretValueWithOptions(putSecretValueRequest, runtime);
    }

    public static void deleteSecret(String secretName) throws Exception {
        com.aliyun.kms20160120.Client client = ClientHelper.createKmsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.kms20160120.models.DeleteSecretRequest deleteSecretRequest = new com.aliyun.kms20160120.models.DeleteSecretRequest()
                .setSecretName(secretName)
                .setForceDeleteWithoutRecovery("true");
        RuntimeOptions runtime = new RuntimeOptions();
        client.deleteSecretWithOptions(deleteSecretRequest, runtime);
    }

    public static List<String> listSecrets(String filters) throws Exception{
        com.aliyun.kms20160120.Client client = ClientHelper.createKmsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.kms20160120.models.ListSecretsRequest listSecretsRequest = new com.aliyun.kms20160120.models.ListSecretsRequest()
                .setFilters(filters);
        RuntimeOptions runtime = new RuntimeOptions();
        List<String> secretNames = new ArrayList<>();
        for (ListSecretsResponseBody.ListSecretsResponseBodySecretListSecret listSecret : client.listSecretsWithOptions(listSecretsRequest, runtime).getBody().getSecretList().getSecret()) {
            secretNames.add(listSecret.getSecretName());
        }
        return secretNames;
    }

    /**
     * list main keys, return keyIds
     */
    public static List<String> listKeys(String dKMSInstanceId) throws Exception {
        String filters = "[{\"Key\":\"KeyState\", \"Values\":[\"Enabled\"]}, {\"Key\":\"KeySpec\", \"Values\":[\"Aliyun_AES_256\"]}, " +
                "{\"Key\":\"DKMSInstanceId\", \"Values\":[\"" + dKMSInstanceId + "\"]}]";
        com.aliyun.kms20160120.Client client = ClientHelper.createKmsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.kms20160120.models.ListKeysRequest listKeysRequest = new com.aliyun.kms20160120.models.ListKeysRequest()
                .setFilters(filters);
        RuntimeOptions runtime = new RuntimeOptions();
        List<String> keyIds = new ArrayList<>();
        client.listKeysWithOptions(listKeysRequest, runtime).getBody().getKeys().getKey().forEach(key -> keyIds.add(key.getKeyId()));
        return keyIds;
    }

    public static String createKey(String dKMSInstanceId,String tags) throws Exception{
        com.aliyun.kms20160120.Client client = ClientHelper.createKmsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.kms20160120.models.CreateKeyRequest createKeyRequest = new com.aliyun.kms20160120.models.CreateKeyRequest()
                .setDescription("dreamweb")
                .setKeySpec("Aliyun_AES_256")
                .setDKMSInstanceId(dKMSInstanceId)
                .setTags(tags);
        RuntimeOptions runtime = new RuntimeOptions();
        return client.createKeyWithOptions(createKeyRequest, runtime).getBody().getKeyMetadata().getKeyId();
    }



    public static void initKMS() throws Exception {
        List<String> roleList = RamHelper.listRoles();
        String roleName = "AliyunKMSManagedRAMCrendentialsRole";
        if (roleList.contains(roleName)){
            logger.info("role {} already exist",roleName);
            return;
        }
        logger.info("start to create role {} ",roleName);
        // create policy
        String policyName = "AliyunKMSManagedRAMCrendentialsRolePolicy";
        String policyDocument = "{\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Action\": [\n" +
                "                \"ram:ListAccessKeys\",\n" +
                "                \"ram:CreateAccessKey\",\n" +
                "                \"ram:DeleteAccessKey\",\n" +
                "                \"ram:UpdateAccessKey\"\n" +
                "            ],\n" +
                "            \"Resource\": \"*\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"Version\": \"1\"\n" +
                "}";
        RamHelper.createPolicy(policyName, policyDocument);

        // create role

        String assumeRolePolicyDocument = "{\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Action\": \"sts:AssumeRole\",\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Principal\": {\n" +
                "        \"Service\": [\n" +
                "          \"kms.aliyuncs.com\"\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"Version\": \"1\"\n" +
                "}";
        RamHelper.createRamRole(roleName,assumeRolePolicyDocument);

        // attach policy to role
        RamHelper.attachPolicyToRole(roleName,policyName,"Custom");
    }




}
