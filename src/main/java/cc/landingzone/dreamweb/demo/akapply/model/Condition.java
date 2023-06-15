package cc.landingzone.dreamweb.demo.akapply.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Condition {
    @JSONField(name = "StringEquals")
    StringEquals stringEquals;

}
