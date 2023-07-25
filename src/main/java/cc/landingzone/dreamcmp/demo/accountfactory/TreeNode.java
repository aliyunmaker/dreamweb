package cc.landingzone.dreamcmp.demo.accountfactory;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Author：珈贺
 * Description：
 */
@Data
public class TreeNode {
    @JSONField(name = "text")
    private String text;
    @JSONField(name = "id")
    private String id;
    @JSONField(name = "nodes")
    private List<TreeNode> nodes;

}
