package cc.landingzone.dreamcmp.demo.accountfactory;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyun.resourcedirectorymaster20220419.models.ListFoldersForParentResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：珈贺
 * Description：
 */
public class AccountFactoryUtil {

    public static Logger logger = LoggerFactory.getLogger(AccountFactoryUtil.class);

    public static String getFileTree() throws Exception {
        List<TreeNode> nodes = new ArrayList<>();
        String rootFolderId = ResourceDirectoryHelper.getResourceDirectory().getRootFolderId();
        TreeNode root = new TreeNode();
        root.setId(rootFolderId);
        root.setText("Root");
        backTraceTree(root);
        nodes.add(root);
        return JSON.toJSONString(nodes, SerializerFeature.PrettyFormat);
    }

    public static void backTraceTree(TreeNode node) throws Exception {
        ListFoldersForParentResponseBody listFoldersForParentResponseBody = ResourceDirectoryHelper.listFoldersForParent(node.getId());
        if (listFoldersForParentResponseBody.getTotalCount() > 0) {
            List<TreeNode> nodes = new ArrayList<>();
            for (ListFoldersForParentResponseBody.ListFoldersForParentResponseBodyFoldersFolder folder :
                    listFoldersForParentResponseBody.getFolders().getFolder()) {
                TreeNode treeNode = new TreeNode();
                treeNode.setId(folder.getFolderId());
                treeNode.setText(folder.getFolderName());
                backTraceTree(treeNode);
                nodes.add(treeNode);
            }
            node.setNodes(nodes);
        }
    }

}
