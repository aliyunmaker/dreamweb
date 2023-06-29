package cc.landingzone.dreamweb.demo.sso;

import cc.landingzone.dreamweb.common.CommonConstants;
import org.opensaml.security.x509.BasicX509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * CertManager
 *
 * @author charles
 * @date 2020-09-29
 */
public class CertManager {

    private static final Logger logger = LoggerFactory.getLogger(CertManager.class);

    private static BasicX509Credential credential;
    private static BasicX509Credential publicCredential;

    public static void initSigningCredential() throws Exception {
        InputStream inStream = CertManager.class.getResourceAsStream(SSOConstants.PUBLIC_KEY_PATH);
        if (inStream == null) {
            inStream = new FileInputStream(CommonConstants.CONFIG_PATH + SSOConstants.PUBLIC_KEY_PATH);
            logger.info("read cert file from:" + CommonConstants.CONFIG_PATH + SSOConstants.PUBLIC_KEY_PATH);
        } else {
            logger.info("read cert file from jar");
        }
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate publicKey = (X509Certificate)cf.generateCertificate(inStream);
        inStream.close();

        InputStream keyInStream = CertManager.class.getResourceAsStream(SSOConstants.PRIVATE_KEY_PATH);
        if (keyInStream == null) {
            keyInStream = new FileInputStream(CommonConstants.CONFIG_PATH + SSOConstants.PRIVATE_KEY_PATH);
        }
        byte[] keyBuf = FileCopyUtils.copyToByteArray(keyInStream);

        // create private key
        // RandomAccessFile raf = new RandomAccessFile(privateKeyLocation, "r");
        // byte[] buf = new byte[(int) raf.length()];
        // raf.readFully(buf);
        // raf.close();

        PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(keyBuf);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(kspec);

        // create credential and initialize
        BasicX509Credential basicX509Credential = new BasicX509Credential(publicKey, privateKey);
        credential = basicX509Credential;
        publicCredential = new BasicX509Credential(publicKey);

    }

    public static BasicX509Credential getCredential() {
        return credential;
    }

    public static BasicX509Credential getPublicCredential() {
        return publicCredential;
    }

}
