package cc.landingzone.dreamweb.demo.sso;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.core.impl.*;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SamlAssertionProducer {

    private static Logger logger = LoggerFactory.getLogger(SamlAssertionProducer.class);

    public static Response createSAMLResponse(String samlRequestID, String identifier, String replyUrl, String nameID,
                                              HashMap<String, List<String>> attributes) throws Exception {

        Assert.hasText(identifier, "identifier can not be blank!");
        Assert.hasText(replyUrl, "replyUrl can not be blank!");
        Assert.hasText(nameID, "nameID can not be blank!");
        // Assert.notEmpty(attributes, "attributes can not be empty!");

        logger.info("**********************************SAML INFO**********************************");
        logger.info("samlRequestID: " + samlRequestID);
        logger.info("identifier: " + identifier);
        logger.info("replyUrl: " + replyUrl);
        logger.info("nameID: " + nameID);
        logger.info("attributes: " + attributes);
        logger.info("*****************************************************************************");

        // ****************默认参数***************
        Instant authenticationTime = Instant.now();
        String issuer = SSOConstants.IDP_ENTITY_ID;
        Integer samlAssertionDays = 2;
        // ****************默认参数***************

        Signature signature = createSignature();
        Status status = createStatus();
        Issuer responseIssuer = null;
        Issuer assertionIssuer = null;
        Subject subject = null;
        AttributeStatement attributeStatement = null;

        if (issuer != null) {
            responseIssuer = createIssuer(issuer);
            assertionIssuer = createIssuer(issuer);
        }

        if (attributes != null) {
            attributeStatement = createAttributeStatement(attributes);
        }

        if (nameID != null) {
            subject = createSubject(replyUrl, nameID, samlAssertionDays, samlRequestID);
        }

        AuthnStatement authnStatement = createAuthnStatement(authenticationTime);

        Assertion assertion = createAssertion(authenticationTime, subject, assertionIssuer, authnStatement,
                attributeStatement, samlAssertionDays, identifier);

        Response response = createResponse(authenticationTime, responseIssuer, status, assertion);

        if (null != samlRequestID) {
            response.setInResponseTo(samlRequestID);
        }
        // aliyun cloud sso 开启新的校验
        response.setDestination(replyUrl);
        // id不能以数字开头,所以统一加"id"
        response.setID("id" + response.getID());
        response.getAssertions().get(0).setID("id" + response.getAssertions().get(0).getID());

        // aliyun 两种都可以,aws需要把signature放在assertion里
        // response.setSignature(signature);
        response.getAssertions().get(0).setSignature(signature);

        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory()
                .getMarshaller(response.getElementQName());
        marshaller.marshall(response);

        if (signature != null) {
            Signer.signObject(signature);
        }
        return response;
    }

    private static Conditions createConditions(Instant notOnOrAfter, final String audienceUri) {
        ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
        final Conditions conditions = conditionsBuilder.buildObject();
        conditions.setNotOnOrAfter(notOnOrAfter);

        AudienceRestrictionBuilder audienceRestrictionBuilder = new AudienceRestrictionBuilder();
        final AudienceRestriction audienceRestriction = audienceRestrictionBuilder.buildObject();
        AudienceBuilder audienceBuilder = new AudienceBuilder();
        final Audience audience = audienceBuilder.buildObject();
        audience.setURI(audienceUri);
        audienceRestriction.getAudiences().add(audience);
        conditions.getAudienceRestrictions().add(audienceRestriction);
        return conditions;
    }

    private static Response createResponse(Instant issueDate, Issuer issuer, Status status, Assertion assertion) {

        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = responseBuilder.buildObject();
        response.setID(UUID.randomUUID().toString());
        response.setIssueInstant(issueDate);
        response.setVersion(SAMLVersion.VERSION_20);
        response.setIssuer(issuer);
        response.setStatus(status);
        response.getAssertions().add(assertion);
        return response;
    }

    private static Assertion createAssertion(Instant issueDate, Subject subject, Issuer issuer,
                                             AuthnStatement authnStatement, AttributeStatement attributeStatement, final Integer samlAssertionDays,
                                             final String identifier) {
        AssertionBuilder assertionBuilder = new AssertionBuilder();
        Assertion assertion = assertionBuilder.buildObject();
        assertion.setID(UUID.randomUUID().toString());
        assertion.setIssueInstant(issueDate);
        assertion.setSubject(subject);
        assertion.setIssuer(issuer);

        Instant currentDate = Instant.now();
        if (samlAssertionDays != null) {
            currentDate = currentDate.plus(samlAssertionDays, ChronoUnit.DAYS);
        }
        Conditions conditions = createConditions(currentDate, identifier);
        assertion.setConditions(conditions);

        if (authnStatement != null) {
            assertion.getAuthnStatements().add(authnStatement);
        }

        if (attributeStatement != null) {
            assertion.getAttributeStatements().add(attributeStatement);
        }

        return assertion;
    }

    private static Issuer createIssuer(final String issuerName) {
        // create Issuer object
        IssuerBuilder issuerBuilder = new IssuerBuilder();
        Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(issuerName);
        return issuer;
    }

    private static Subject createSubject(final String replyUrl, final String nameID, final Integer samlAssertionDays,
                                         String samlRequestID) {
        Instant currentDate = Instant.now();
        if (samlAssertionDays != null) {
            currentDate = currentDate.plus(samlAssertionDays, ChronoUnit.DAYS);
        }

        // create name element
        NameIDBuilder nameIdBuilder = new NameIDBuilder();
        NameID nameId = nameIdBuilder.buildObject();
        nameId.setValue(nameID);
        nameId.setFormat(NameIDType.EMAIL);

        SubjectConfirmationDataBuilder dataBuilder = new SubjectConfirmationDataBuilder();
        SubjectConfirmationData subjectConfirmationData = dataBuilder.buildObject();
        subjectConfirmationData.setNotOnOrAfter(currentDate);
        subjectConfirmationData.setRecipient(replyUrl);
        if (null != samlRequestID) {
            subjectConfirmationData.setInResponseTo(samlRequestID);
        }

        SubjectConfirmationBuilder subjectConfirmationBuilder = new SubjectConfirmationBuilder();
        SubjectConfirmation subjectConfirmation = subjectConfirmationBuilder.buildObject();
        subjectConfirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer");
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

        // create subject element
        SubjectBuilder subjectBuilder = new SubjectBuilder();
        Subject subject = subjectBuilder.buildObject();
        subject.setNameID(nameId);
        subject.getSubjectConfirmations().add(subjectConfirmation);

        return subject;
    }

    private static AuthnStatement createAuthnStatement(Instant issueDate) {
        // create authcontextclassref object
        AuthnContextClassRefBuilder classRefBuilder = new AuthnContextClassRefBuilder();
        AuthnContextClassRef classRef = classRefBuilder.buildObject();
        // classRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
        classRef.setURI("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");

        // create authcontext object
        AuthnContextBuilder authContextBuilder = new AuthnContextBuilder();
        AuthnContext authnContext = authContextBuilder.buildObject();
        authnContext.setAuthnContextClassRef(classRef);

        // create authenticationstatement object
        AuthnStatementBuilder authStatementBuilder = new AuthnStatementBuilder();
        AuthnStatement authnStatement = authStatementBuilder.buildObject();
        authnStatement.setAuthnInstant(issueDate);
        authnStatement.setAuthnContext(authnContext);

        return authnStatement;
    }

    private static AttributeStatement createAttributeStatement(HashMap<String, List<String>> attributes) {
        // create authenticationstatement object
        AttributeStatementBuilder attributeStatementBuilder = new AttributeStatementBuilder();
        AttributeStatement attributeStatement = attributeStatementBuilder.buildObject();

        AttributeBuilder attributeBuilder = new AttributeBuilder();
        if (attributes != null) {
            for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
                Attribute attribute = attributeBuilder.buildObject();
                attribute.setName(entry.getKey());

                for (String value : entry.getValue()) {
                    XSStringBuilder stringBuilder = new XSStringBuilder();
                    XSString attributeValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME,
                            XSString.TYPE_NAME);
                    attributeValue.setValue(value);
                    attribute.getAttributeValues().add(attributeValue);
                }

                attributeStatement.getAttributes().add(attribute);
            }
        }

        return attributeStatement;
    }

    private static Status createStatus() {
        StatusCodeBuilder statusCodeBuilder = new StatusCodeBuilder();
        StatusCode statusCode = statusCodeBuilder.buildObject();
        statusCode.setValue(StatusCode.SUCCESS);
        StatusBuilder statusBuilder = new StatusBuilder();
        Status status = statusBuilder.buildObject();
        status.setStatusCode(statusCode);
        return status;
    }

    private static Signature createSignature() throws Exception {
        SignatureBuilder builder = new SignatureBuilder();
        Signature signature = builder.buildObject();
        signature.setSigningCredential(CertManager.getCredential());
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        // cloudfare need keyinfo test
        signature.setKeyInfo(getKeyInfo(signature.getSigningCredential()));
        return signature;
    }

    private static KeyInfo getKeyInfo(Credential credential) throws Exception {
        EncryptionConfiguration secConfiguration = SecurityConfigurationSupport.getGlobalEncryptionConfiguration();
        NamedKeyInfoGeneratorManager namedKeyInfoGeneratorManager = secConfiguration.getDataKeyInfoGeneratorManager();
        KeyInfoGeneratorManager keyInfoGeneratorManager = namedKeyInfoGeneratorManager.getDefaultManager();
        KeyInfoGeneratorFactory keyInfoGeneratorFactory = keyInfoGeneratorManager.getFactory(credential);
        KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();
        KeyInfo keyInfo = keyInfoGenerator.generate(credential);
//	    keyInfo.getX509Datas().clear();
        return keyInfo;
    }
}
