package cc.landingzone.dreamcmp.demo.ssologin.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SSOUserRole {
    private String provider;
    private String name;
    private String account;
    private String id;
    private String url;
}
