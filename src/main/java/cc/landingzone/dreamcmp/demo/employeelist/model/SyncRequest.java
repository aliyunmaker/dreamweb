package cc.landingzone.dreamcmp.demo.employeelist.model;

import lombok.Data;

/**
 * Author：珈贺
 * Description：
 */
@Data
public class SyncRequest {
    private String id;
    private String externalId;
    private String userName;
    private String displayName;
    private String givenName;
    private String familyName;
    private String email;
    private String typeName;
}
