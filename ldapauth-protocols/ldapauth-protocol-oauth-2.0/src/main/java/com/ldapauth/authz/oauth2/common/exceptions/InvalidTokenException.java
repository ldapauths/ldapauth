package com.ldapauth.authz.oauth2.common.exceptions;

/**
 * @author Ryan Heaton
 * @author Dave Syer
 */
@SuppressWarnings("serial")
public class InvalidTokenException extends ClientAuthenticationException {

	public InvalidTokenException(String msg, Throwable t) {
		super(msg, t);
	}

	public InvalidTokenException(String msg) {
		super(msg);
	}

	@Override
	public int getHttpErrorCode() {
		return 401;
	}

	@Override
	public String getOAuth2ErrorCode() {
		return "invalid_token";
	}
}
