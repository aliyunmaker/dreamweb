package cc.landingzone.dreamweb.demo.sso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.impl.*;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.X509Certificate;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.X509CertificateBuilder;
import org.opensaml.xml.signature.impl.X509DataBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class SamlGenerator {
//    static {
//        try {
//            // 初始化证书
//            CertManager.initSigningCredential();
//            DefaultBootstrap.bootstrap();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }

    private static Logger logger = LoggerFactory.getLogger(SamlGenerator.class);

    /**
     * 生成base64的SAMLResponse
     *
     * @return
     * @throws Throwable
     */
    public static String generateResponse(String identifier, String replyUrl, String nameID,
                                          HashMap<String, List<String>> attributes) throws Exception {
        Response responseInitial = SamlAssertionProducer.createSAMLResponse(identifier, replyUrl, nameID, attributes);
        // output Response
        ResponseMarshaller marshaller = new ResponseMarshaller();
        Element element = marshaller.marshall(responseInitial);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLHelper.writeNode(element, baos);
        String responseStr = new String(baos.toByteArray());
        String base64Encode = java.util.Base64.getEncoder().encodeToString(responseStr.getBytes());

        logger.info("********************************SAML Response XML*******************************");
        logger.info(responseStr);
        logger.info("********************************************************************************");
//        logger.info("************************Response Base64*************************");
//        logger.info(base64Encode);
//        logger.info("****************************************************************");
        return base64Encode;
    }

    public static String generateMetaXML() throws Exception {

        EntityDescriptorBuilder entityDescriptorBuilder = new EntityDescriptorBuilder();
        EntityDescriptor entityDescriptor = entityDescriptorBuilder.buildObject();
        entityDescriptor.setEntityID(SSOConstants.IDP_ENTITY_ID);

        IDPSSODescriptorBuilder idpssoDescriptorBuilder = new IDPSSODescriptorBuilder();
        IDPSSODescriptor idpssoDescriptor = idpssoDescriptorBuilder.buildObject();
        idpssoDescriptor.setWantAuthnRequestsSigned(false);
        idpssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
        entityDescriptor.getRoleDescriptors().add(idpssoDescriptor);

        KeyInfoBuilder keyInfoBuilder = new KeyInfoBuilder();
        KeyInfo keyInfo = keyInfoBuilder.buildObject();

        X509CertificateBuilder x509CertificateBuilder = new X509CertificateBuilder();
        X509Certificate x509Certificate = x509CertificateBuilder.buildObject();

        x509Certificate.setValue(new String(
                java.util.Base64.getEncoder().encode(CertManager.getCredential().getEntityCertificate().getEncoded())));

        X509DataBuilder x509DataBuilder = new X509DataBuilder();
        X509Data x509Data = x509DataBuilder.buildObject();
        x509Data.getX509Certificates().add(x509Certificate);
        keyInfo.getX509Datas().add(x509Data);

        KeyDescriptorBuilder keyDescriptorBuilder = new KeyDescriptorBuilder();
        KeyDescriptor keyDescriptor = keyDescriptorBuilder.buildObject();
        keyDescriptor.setUse(UsageType.SIGNING);
        keyDescriptor.setKeyInfo(keyInfo);
        idpssoDescriptor.getKeyDescriptors().add(keyDescriptor);

//        NameIDFormatBuilder nameIDFormatBuilder = new NameIDFormatBuilder();
//        NameIDFormat nameIDFormat = nameIDFormatBuilder.buildObject();
//        nameIDFormat.setFormat(NameIDType.UNSPECIFIED);
//        idpssoDescriptor.getNameIDFormats().add(nameIDFormat);

        // 符合aws user sso的要求,需要增加
//        <?xml version="1.0" encoding="UTF-8"?>
//        <md:EntityDescriptor xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata" entityID="https://chengchao.name/b65d76ce4260/">
//            <md:IDPSSODescriptor WantAuthnRequestsSigned="false" protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
//                <md:KeyDescriptor use="signing">
//                    <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
//                        <ds:X509Data>
//                            <ds:X509Certificate>XBhPpgWPLxLmuuFslanasyQjvREB</ds:X509Certificate>
//                        </ds:X509Data>
//                    </ds:KeyInfo>
//                </md:KeyDescriptor>
//                <md:SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="https://chengchao.name/login" />
//                <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="https://chengchao.name/login" />
//                <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="https://chengchao.name/login" />
//            </md:IDPSSODescriptor>
//        </md:EntityDescriptor>

        SingleSignOnServiceBuilder singleSignOnServiceBuilder = new SingleSignOnServiceBuilder();
        SingleSignOnService singleSignOnService = singleSignOnServiceBuilder.buildObject();
        singleSignOnService.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
        //cloud sso会校验这个字段
        singleSignOnService.setLocation(SSOConstants.IDP_ENTITY_ID);
        idpssoDescriptor.getSingleSignOnServices().add(singleSignOnService);

        // output EntityDescriptor
        EntityDescriptorMarshaller marshaller = new EntityDescriptorMarshaller();
        Element element = marshaller.marshall(entityDescriptor);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLHelper.writeNode(element, baos);
        String metaXMLStr = new String(baos.toByteArray());

        System.out.println("===============MetaXML================");
        System.out.println(metaXMLStr);
        System.out.println("===============MetaXML================");

        return metaXMLStr;

    }

}
