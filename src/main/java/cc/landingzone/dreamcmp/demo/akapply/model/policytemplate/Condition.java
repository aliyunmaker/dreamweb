package cc.landingzone.dreamcmp.demo.akapply.model.policytemplate;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Condition {
    @JSONField(name = "StringEquals")
    StringEquals stringEquals;

}
