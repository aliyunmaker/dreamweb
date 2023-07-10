package cc.landingzone.dreamweb.demo.akapply.model.ak;

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
public class AccessKey {
    @JSONField(name = "AccessKeyId")
    private String accessKeyId;
    @JSONField(name = "AccessKeySecret")
    private String accessKeySecret;
}
