package cc.landingzone.dreamweb.demo.akapply.model.ak;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Author：珈贺
 * Description：
 */
@Data
public class AccessKeys {
    @JSONField(name = "AccessKeys")
    private List<AccessKey> accessKeys;
}
