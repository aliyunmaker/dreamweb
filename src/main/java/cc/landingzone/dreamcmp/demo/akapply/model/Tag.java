package cc.landingzone.dreamcmp.demo.akapply.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author：珈贺
 * Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
    @JSONField(name = "TagKey")
    String tagKey;
    @JSONField(name = "TagValue")
    String tagValue;
}
