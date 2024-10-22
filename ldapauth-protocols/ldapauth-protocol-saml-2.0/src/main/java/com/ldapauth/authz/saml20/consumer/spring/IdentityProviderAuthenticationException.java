package com.ldapauth.authz.saml20.consumer.spring;

import org.springframework.security.core.AuthenticationException;

/**
 * Indicates that the Identity Provider has failed the user's attempt to log in.
 * This could be because the user has entered invalid credentials, the account is locked, etc...
 *
 * The user may be able to re-auththenticate given a second chance, or at the very least,
 * be able to see in the IDP's ui as to why the authentication was not successful.
 *
 *
 *
 * @author jcox
 *
 */
public class IdentityProviderAuthenticationException extends AuthenticationException {

	/**
	 *
	 */
	private static final long serialVersionUID = -1106622672393663684L;


	public IdentityProviderAuthenticationException(String msg, Object extraInformation) {
		super(msg, (Throwable) extraInformation);
	}


	public IdentityProviderAuthenticationException(String msg) {
		super(msg);
	}

}
