package cc.landingzone.dreamcmp.common;

import com.aliyun.cloudsso20210515.Client;
import com.aliyun.teautil.models.RuntimeOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：珈贺
 * Description：
 */
public class CloudSSOHelper {

    /**
     * list all directories,usually there is only one directory
     */
    public static List<com.aliyun.cloudsso20210515.models.ListDirectoriesResponseBody.ListDirectoriesResponseBodyDirectories> listDirectories() throws Exception{
        Client client = ClientHelper.createCloudSSOClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        RuntimeOptions runtime = new RuntimeOptions();
        return client.listDirectoriesWithOptions(runtime).getBody().getDirectories();
    }

    /**
     * create a new directory,return directoryId
     */
    public static String createDirectory(String directoryName) throws Exception{
        Client client = ClientHelper.createCloudSSOClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.cloudsso20210515.models.CreateDirectoryRequest createDirectoryRequest = new com.aliyun.cloudsso20210515.models.CreateDirectoryRequest()
                .setDirectoryName(directoryName);
        RuntimeOptions runtime = new RuntimeOptions();
        return client.createDirectoryWithOptions(createDirectoryRequest,runtime).getBody().getDirectory().getDirectoryId();
    }


    /**
     * create a new SCIMServerCredential,return credentialSecret
     */
    public static String createSCIMServerCredential(String directoryId) throws Exception{
        Client client = ClientHelper.createCloudSSOClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.cloudsso20210515.models.CreateSCIMServerCredentialRequest createSCIMServerCredentialRequest = new com.aliyun.cloudsso20210515.models.CreateSCIMServerCredentialRequest()
                .setDirectoryId(directoryId);
        RuntimeOptions runtime = new RuntimeOptions();
        return client.createSCIMServerCredentialWithOptions(createSCIMServerCredentialRequest,runtime).getBody().
                getSCIMServerCredential().getCredentialSecret();
    }

    /**
     * list all SCIMServerCredentials,return credentialSecretIds
     */
    public static List<String> listSCIMServerCredentials(String directoryId) throws Exception{
        Client client = ClientHelper.createCloudSSOClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.cloudsso20210515.models.ListSCIMServerCredentialsRequest listSCIMServerCredentialsRequest = new com.aliyun.cloudsso20210515.models.ListSCIMServerCredentialsRequest()
                .setDirectoryId(directoryId);
        RuntimeOptions runtime = new RuntimeOptions();
        List<String> credentialSecretIds = new ArrayList<>();
        client.listSCIMServerCredentialsWithOptions(listSCIMServerCredentialsRequest,runtime).getBody().getSCIMServerCredentials()
                .forEach(scimServerCredential -> credentialSecretIds.add(scimServerCredential.getCredentialId()));
        return credentialSecretIds;
    }

    /**
     * delete a SCIMServerCredential
     */
    public static void deleteSCIMServerCredential(String directoryId,String credentialId) throws Exception {
        Client client = ClientHelper.createCloudSSOClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.cloudsso20210515.models.DeleteSCIMServerCredentialRequest deleteSCIMServerCredentialRequest = new com.aliyun.cloudsso20210515.models.DeleteSCIMServerCredentialRequest()
                .setDirectoryId(directoryId)
                .setCredentialId(credentialId);
        RuntimeOptions runtime = new RuntimeOptions();
        client.deleteSCIMServerCredentialWithOptions(deleteSCIMServerCredentialRequest, runtime);
    }
}
