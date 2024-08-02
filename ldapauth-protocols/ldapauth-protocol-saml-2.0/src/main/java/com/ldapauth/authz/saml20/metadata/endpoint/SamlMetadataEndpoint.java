package com.ldapauth.authz.saml20.metadata.endpoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ldapauth.authz.saml20.metadata.domain.Saml20Metadata;
import com.ldapauth.autoconfigure.ApplicationAutoConfiguration;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.constants.ContentType;
import com.ldapauth.crypto.keystore.KeyStoreLoader;
import com.ldapauth.web.WebContext;
import org.apache.commons.lang.Validate;
import com.ldapauth.authz.saml.common.TrustResolver;
import com.ldapauth.authz.saml20.metadata.MetadataGenerator;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "2-2-SAML v2.0 API文档模块")
@Controller
@RequestMapping(value = { "/metadata/saml20/" })
public class SamlMetadataEndpoint {
	private final static Logger logger = LoggerFactory
			.getLogger(SamlMetadataEndpoint.class);

	@Autowired
	@Qualifier("keyStoreLoader")
	private KeyStoreLoader keyStoreLoader;

	@Autowired
	@Qualifier("issuerEntityName")
	private String issuerEntityName;

	@Autowired
	@Qualifier("saml20Metadata")
	private Saml20Metadata saml20Metadata;

	@Autowired
	private ApplicationConfig applicationConfig;

	private Credential signingCredential;

	@Operation(summary = "SAML 2.0 元数据接口", description = "参数APPID",method="POST,GET")
	@RequestMapping(value = "/{appid}.xml",
					method={RequestMethod.POST, RequestMethod.GET},
					produces = "application/xml"
					)
	@ResponseBody
	public String  metadata(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("appid") String appId) {
	    response.setContentType(ContentType.APPLICATION_XML_UTF8);
		if (signingCredential == null){
	        TrustResolver trustResolver = new TrustResolver();
	        CredentialResolver credentialResolver = (CredentialResolver) trustResolver.buildKeyStoreCredentialResolver(
	        		keyStoreLoader.getKeyStore(),
					keyStoreLoader.getEntityName(),
					keyStoreLoader.getKeystorePassword());

	         CriteriaSet criteriaSet = new CriteriaSet();

	 		 criteriaSet.add(new EntityIDCriteria(keyStoreLoader.getEntityName()));

	 		 criteriaSet.add(new UsageCriteria(UsageType.SIGNING));

			try {
				signingCredential = credentialResolver.resolveSingle(criteriaSet);
			}catch (SecurityException e) {
				logger.error("Credential Resolver error .", e);

			}
		}

		Validate.notNull(signingCredential);

		try{

			MetadataGenerator metadataGenerator =new MetadataGenerator();

	        IDPSSODescriptor descriptor = metadataGenerator.buildIDPSSODescriptor();

	        descriptor.getSingleSignOnServices().add(
	        		metadataGenerator.getSingleSignOnService(applicationConfig.getAuthzUri() + "/authz/saml20/" + appId , null));

	        descriptor.getSingleSignOnServices().add(
	        		metadataGenerator.getSingleSignOnService(applicationConfig.getAuthzUri()  + "/authz/saml20/" + appId , SAMLConstants.SAML2_REDIRECT_BINDING_URI));

	        descriptor.getSingleSignOnServices().add(
	        		metadataGenerator.getSingleSignOnService(applicationConfig.getAuthzUri()  + "/authz/saml20/" + appId , SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI));

	        descriptor.getSingleLogoutServices().add(
	        		metadataGenerator.getSingleLogoutService(applicationConfig.getAuthzUri()  + "/force/logout" , null));

	        descriptor.getManageNameIDServices().add(
	        		metadataGenerator.getManageNameIDService(applicationConfig.getAuthzUri()  + "/metadata/saml20/"+ appId + ".xml"));

	        descriptor.getKeyDescriptors().add(metadataGenerator.generateEncryptionKeyDescriptor(signingCredential));

	        descriptor.getKeyDescriptors().add(metadataGenerator.generateSignKeyDescriptor(signingCredential));

	        descriptor.getNameIDFormats().add(metadataGenerator.generateNameIDFormat(NameIDType.TRANSIENT));
	        descriptor.getNameIDFormats().add(metadataGenerator.generateNameIDFormat(NameIDType.PERSISTENT));
	        descriptor.getNameIDFormats().add(metadataGenerator.generateNameIDFormat(NameIDType.EMAIL));
	        descriptor.getNameIDFormats().add(metadataGenerator.generateNameIDFormat(NameIDType.ENTITY));

	        ContactPersonTypeEnumeration contactPersonType=null;
	        if(saml20Metadata.getContactType().equalsIgnoreCase(Saml20Metadata.ContactPersonType.ADMINISTRATIVE)){
	        	contactPersonType=ContactPersonTypeEnumeration.ADMINISTRATIVE;
	        }else if(saml20Metadata.getContactType().equalsIgnoreCase(Saml20Metadata.ContactPersonType.TECHNICAL)){
	        	contactPersonType=ContactPersonTypeEnumeration.TECHNICAL;
	        }else if(saml20Metadata.getContactType().equalsIgnoreCase(Saml20Metadata.ContactPersonType.BILLING)){
	        	contactPersonType=ContactPersonTypeEnumeration.BILLING;
	        }else if(saml20Metadata.getContactType().equalsIgnoreCase(Saml20Metadata.ContactPersonType.SUPPORT)){
	        	contactPersonType=ContactPersonTypeEnumeration.SUPPORT;
			}else if(saml20Metadata.getContactType().equalsIgnoreCase(Saml20Metadata.ContactPersonType.OTHER)){
				contactPersonType=ContactPersonTypeEnumeration.OTHER;
			}
	        descriptor.getContactPersons().add(metadataGenerator.getContactPerson(
	        					saml20Metadata.getCompany(),
	        					saml20Metadata.getGivenName(),
	        					saml20Metadata.getSurName(),
	        					saml20Metadata.getEmailAddress(),
	        					saml20Metadata.getTelephoneNumber(),
	        					contactPersonType));

	        descriptor.setOrganization(metadataGenerator.getOrganization(
	        					saml20Metadata.getOrgName(),
	        					saml20Metadata.getOrgDisplayName(),
	        					saml20Metadata.getOrgURL()));

	        EntityDescriptor entityDescriptor = metadataGenerator.buildEntityDescriptor(issuerEntityName,descriptor);

	        String entityDescriptorXml = XMLHelper.prettyPrintXML(metadataGenerator.marshallerMetadata(entityDescriptor));

	        logger.trace("EntityDescriptor element XML : \\n");
	        logger.trace(entityDescriptorXml);

	        return entityDescriptorXml;
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
        }


		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<root>1.0.0</root>";
	}

	/**
	 * @param keyStoreLoader
	 *            the keyStoreLoader to set
	 */
	public void setKeyStoreLoader(KeyStoreLoader keyStoreLoader) {
		this.keyStoreLoader = keyStoreLoader;
	}


	public void setIssuerEntityName(String issuerEntityName) {
		this.issuerEntityName = issuerEntityName;
	}

}
