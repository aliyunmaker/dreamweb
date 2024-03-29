package cc.landingzone.dreamcmp.demo.akapply.model.policytemplate;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class PolicyDocument {
    @JSONField(name = "Version")
    private String version;
    @JSONField(name = "Statement")
    private List<Statement> statement;

    public PolicyDocument(){
        version = "1";
    }
}
