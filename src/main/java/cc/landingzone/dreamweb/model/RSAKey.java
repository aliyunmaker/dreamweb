package cc.landingzone.dreamweb.model;

public class RSAKey {
    private Integer id;
    private String publicKey;
    private String privateKey;

    public RSAKey(String publicKey, String privateKey) {
        this.id = 0;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public RSAKey(int id, String publicKey, String privateKey) {
        this.id = id;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
