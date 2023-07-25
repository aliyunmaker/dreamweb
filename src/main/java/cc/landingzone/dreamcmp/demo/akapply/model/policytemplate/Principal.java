package cc.landingzone.dreamcmp.demo.akapply.model.policytemplate;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class Principal {
    @JSONField(name = "Ram")
    List<String> ram;
    @JSONField(name = "Service")
    String service;
}
