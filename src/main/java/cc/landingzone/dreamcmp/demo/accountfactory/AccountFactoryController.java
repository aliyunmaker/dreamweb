package cc.landingzone.dreamcmp.demo.accountfactory;


import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.model.WebResult;
import com.aliyun.governance20210120.models.ListAccountFactoryBaselinesResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Author：珈贺
 * Description：
 */
@Controller
@RequestMapping("/accountFactory")
public class AccountFactoryController extends BaseController {

    public static Logger logger = LoggerFactory.getLogger(AccountFactoryController.class);

    @RequestMapping("/getAccountNameSuffix.do")
    public void accountNameSuffix(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String resourceDirectoryId = ResourceDirectoryHelper.getResourceDirectory().getResourceDirectoryId();
            String accountNameSuffix = "@" + resourceDirectoryId + ".aliyunid.com";
            result.setData(accountNameSuffix);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getFileTree.do")
    public void getFileTree(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String fileTree = AccountFactoryUtil.getFileTree();
            result.setData(fileTree);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getMasterAccount.do")
    public void getMasterAccount(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String masterAccountName = ResourceDirectoryHelper.getResourceDirectory().getMasterAccountName();
            String masterAccountId = ResourceDirectoryHelper.getResourceDirectory().getMasterAccountId();
            result.setData(masterAccountName + "/" + masterAccountId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getBaselines.do")
    public void getBaselines(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            List<String> baselines = new ArrayList<>();
            for (ListAccountFactoryBaselinesResponseBody.ListAccountFactoryBaselinesResponseBodyBaselines baseline :
                    GovernanceHelper.listAccountFactoryBaselines()) {
                baselines.add(baseline.getBaselineName() + "/" + baseline.getBaselineId());
            }
            result.setData(baselines);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/createCloudAccount.do")
    public void createCloudAccount(HttpServletRequest request, HttpServletResponse response){
        WebResult result = new WebResult();
        try {
            String accountNamePrefix = request.getParameter("accountNamePrefix");
            String displayName = request.getParameter("displayName");
            String folderId = request.getParameter("folderId");
            Long payerAccountUid;
            if ("myself".equals(request.getParameter("payerAccountUid"))){
                payerAccountUid = null;
            }else {
                payerAccountUid = Long.valueOf(request.getParameter("payerAccountUid"));
            }
            String baselineId = request.getParameter("baselineId");
            Long accountId = GovernanceHelper.enrollAccount(accountNamePrefix, displayName, folderId, payerAccountUid, baselineId);
            result.setData(accountId);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}
