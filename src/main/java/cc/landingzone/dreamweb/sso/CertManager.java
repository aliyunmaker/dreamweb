package cc.landingzone.dreamweb.sso;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import org.opensaml.xml.security.x509.BasicX509Credential;
import org.springframework.util.FileCopyUtils;

import cc.landingzone.dreamweb.common.CommonConstants;

/**
 * CertManager
 *
 * @author charles
 * @date 2020-09-29
 */
public class CertManager {

    public static BasicX509Credential credential;

    public static void initSigningCredential() throws Exception {

        InputStream inStream = CertManager.class.getResourceAsStream(SSOConstants.PUBLIC_KEY_PATH);
        if (inStream == null) {
            inStream = new FileInputStream(CommonConstants.CONFIG_PATH + SSOConstants.PUBLIC_KEY_PATH);
        }
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate publicKey = (X509Certificate) cf.generateCertificate(inStream);
        inStream.close();

        InputStream keyInStream = CertManager.class.getResourceAsStream(SSOConstants.PRIVATE_KEY_PATH);
        if (keyInStream == null) {
            keyInStream = new FileInputStream(CommonConstants.CONFIG_PATH + SSOConstants.PRIVATE_KEY_PATH);
        }
        byte[] keyBuf = FileCopyUtils.copyToByteArray(keyInStream);

        // create private key
//        RandomAccessFile raf = new RandomAccessFile(privateKeyLocation, "r");
//        byte[] buf = new byte[(int) raf.length()];
//        raf.readFully(buf);
//        raf.close();

        PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(keyBuf);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(kspec);

        // create credential and initialize
        BasicX509Credential basicX509Credential = new BasicX509Credential();
        basicX509Credential.setEntityCertificate(publicKey);
        basicX509Credential.setPrivateKey(privateKey);

        credential = basicX509Credential;
    }

    public static BasicX509Credential getCredential() {
        return credential;
    }

}
