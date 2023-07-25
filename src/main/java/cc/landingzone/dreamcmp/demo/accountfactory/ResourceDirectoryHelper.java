package cc.landingzone.dreamcmp.demo.accountfactory;


import cc.landingzone.dreamcmp.common.ClientHelper;
import cc.landingzone.dreamcmp.common.CommonConstants;
import com.aliyun.resourcedirectorymaster20220419.Client;
import com.aliyun.resourcedirectorymaster20220419.models.GetResourceDirectoryResponseBody;
import com.aliyun.resourcedirectorymaster20220419.models.ListFoldersForParentResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author：珈贺
 * Description：
 */
public class ResourceDirectoryHelper {

    public static Logger logger = LoggerFactory.getLogger(ResourceDirectoryHelper.class);

    public static GetResourceDirectoryResponseBody.GetResourceDirectoryResponseBodyResourceDirectory getResourceDirectory() throws Exception{
        Client client = ClientHelper.createRDClient(CommonConstants.Aliyun_TestAccount_AccessKeyId, CommonConstants.Aliyun_TestAccount_AccessKeySecret);
        RuntimeOptions runtime = new RuntimeOptions();
        return client.getResourceDirectoryWithOptions(runtime).getBody().getResourceDirectory();
    }

    public static ListFoldersForParentResponseBody listFoldersForParent(String parentFolderId) throws Exception{
        Client client = ClientHelper.createRDClient(CommonConstants.Aliyun_TestAccount_AccessKeyId, CommonConstants.Aliyun_TestAccount_AccessKeySecret);
        RuntimeOptions runtime = new RuntimeOptions();
        com.aliyun.resourcedirectorymaster20220419.models.ListFoldersForParentRequest listFoldersForParentRequest =
                new com.aliyun.resourcedirectorymaster20220419.models.ListFoldersForParentRequest()
                .setParentFolderId(parentFolderId);
        return client.listFoldersForParentWithOptions(listFoldersForParentRequest, runtime).getBody();
    }

}
