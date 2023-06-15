package cc.landingzone.dreamweb.demo.akapply.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class Statement {
    @JSONField(name = "Effect")
    private String effect;
    @JSONField(name = "Action")
    private List<String> action;
    @JSONField(name = "Resource")
    private List<String> resource;
    @JSONField(name = "Condition")
    private Condition condition;
    @JSONField(name = "Principal")
    private Principal principal;

    public Statement(){
        effect = "Allow";
    }


}
