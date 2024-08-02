package com.ldapauth.authz.saml20.provider.xml;

import org.joda.time.DateTime;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.impl.AudienceBuilder;
import org.opensaml.saml2.core.impl.AudienceRestrictionBuilder;
import org.opensaml.saml2.core.impl.ConditionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionsGenerator {
	private final static Logger logger = LoggerFactory.getLogger(ConditionsGenerator.class);

	public Conditions generateConditions(String audienceUrl,int validInSeconds) {
		Conditions conditions = new ConditionsBuilder().buildObject();
		conditions.setNotBefore(new DateTime());
		conditions.setNotOnOrAfter(new DateTime().plus(validInSeconds*1000));

		AudienceRestriction audienceRestriction=builderAudienceRestriction(audienceUrl);
		conditions.getAudienceRestrictions().add(audienceRestriction);

		return conditions;
	}

	public AudienceRestriction builderAudienceRestriction(String audienceUrl){
		AudienceRestriction audienceRestriction = new AudienceRestrictionBuilder().buildObject();

		Audience audience = new AudienceBuilder().buildObject();
		audience.setAudienceURI(audienceUrl);

		audienceRestriction.getAudiences().add(audience);
		logger.debug("Audience URL "+audienceUrl);
		return audienceRestriction;

	}
}
