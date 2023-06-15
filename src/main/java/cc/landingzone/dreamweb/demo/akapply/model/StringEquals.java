package cc.landingzone.dreamweb.demo.akapply.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class StringEquals {
    @JSONField(name = "ram:ServiceName")
    List<String> ramServiceName;
}
