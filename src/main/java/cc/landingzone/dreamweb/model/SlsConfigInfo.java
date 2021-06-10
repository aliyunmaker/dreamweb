package cc.landingzone.dreamweb.model;

public class SlsConfigInfo {
    private String slsAccessKey;
    private String slsSecretKey;
    private String slsArn;
    private String slsRegion;

    public String getSlsAccessKey() {
        return slsAccessKey;
    }

    public void setSlsAccessKey(String slsAccessKey) {
        this.slsAccessKey = slsAccessKey;
    }

    public String getSlsSecretKey() {
        return slsSecretKey;
    }

    public void setSlsSecretKey(String slsSecretKey) {
        this.slsSecretKey = slsSecretKey;
    }

    public String getSlsArn() {
        return slsArn;
    }

    public void setSlsArn(String slsArn) {
        this.slsArn = slsArn;
    }

    public String getSlsRegion() {
        return slsRegion;
    }

    public void setSlsRegion(String slsRegion) {
        this.slsRegion = slsRegion;
    }
}
