package cc.landingzone.dreamcmp.demo.akapply.model.policytemplate;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class StringEquals {
    @JSONField(name = "ram:ServiceName")
    List<String> ramServiceName;
}
