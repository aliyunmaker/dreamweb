package cc.landingzone.dreamweb.demo.akapply;

import cc.landingzone.dreamweb.common.ClientHelper;
import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.utils.JsonUtils;
import cc.landingzone.dreamweb.demo.akapply.model.ak.AccessKey;
import cc.landingzone.dreamweb.demo.akapply.model.ak.AccessKeys;
import cc.landingzone.dreamweb.demo.akapply.model.Tag;
import com.aliyun.kms20160120.models.ListKeysResponseBody;
import com.aliyun.tea.TeaConverter;
import com.aliyun.tea.TeaPair;
import com.aliyun.teautil.models.RuntimeOptions;
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
        String mainKeyId = getMainKeyId();
        createSecret("applicationName","environment",
                "tianyu",CommonConstants.Aliyun_AccessKeyId,CommonConstants.Aliyun_AccessKeySecret,mainKeyId);
    }

    /**
     * query main key, get keyId, if not exist, create it
     * not finished
     */
    public static String getMainKeyId() throws Exception{
        com.aliyun.kms20160120.Client client = ClientHelper.createKmsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.kms20160120.models.ListKeysRequest listKeysRequest = new com.aliyun.kms20160120.models.ListKeysRequest()
                .setFilters("[{\"Key\":\"KeyState\", \"Values\":[\"Enabled\"]}]");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        ListKeysResponseBody listKeysBody = client.listKeysWithOptions(listKeysRequest, runtime).getBody();
        if (listKeysBody.getTotalCount() == 0){
            // create main key

            return null;
        }else {
            return listKeysBody.getKeys().getKey().get(0).getKeyId();
        }
    }

    /**
     * create a new secret
     * not finished
     */
    public static String createSecret(String applicationName,String environment,String ramUserName,
                                      String accessKeyId,String accessKeySecret,String mainKeyId) throws Exception {
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
                .setExtendedConfig(extendedConfig);
        RuntimeOptions runtime = new RuntimeOptions();
        return client.createSecretWithOptions(createSecretRequest, runtime).getBody().getSecretName();
    }

    public static void initKMS(){

    }

}
