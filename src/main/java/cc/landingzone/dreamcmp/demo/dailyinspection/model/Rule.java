package cc.landingzone.dreamcmp.demo.dailyinspection.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * Config rule model
 */
@Data
public class Rule {
    private String name;
    private String id;
    private JSONObject compliance;
    private String state;
}