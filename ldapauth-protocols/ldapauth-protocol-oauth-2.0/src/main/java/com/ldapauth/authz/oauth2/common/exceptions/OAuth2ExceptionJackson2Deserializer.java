package com.ldapauth.authz.oauth2.common.exceptions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ldapauth.authz.oauth2.common.util.OAuth2Utils;

/**
 * @author Brian Clozel
 *
 */
@SuppressWarnings("serial")
public class OAuth2ExceptionJackson2Deserializer extends StdDeserializer<OAuth2Exception> {

	public OAuth2ExceptionJackson2Deserializer() {
		super(OAuth2Exception.class);
	}

	@Override
	public OAuth2Exception deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
			JsonProcessingException {

		JsonToken t = jp.getCurrentToken();
		if (t == JsonToken.START_OBJECT) {
			t = jp.nextToken();
		}
		Map<String, Object> errorParams = new HashMap<String, Object>();
		for (; t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
			// Must point to field name
			String fieldName = jp.getCurrentName();
			// And then the value...
			t = jp.nextToken();
			// Note: must handle null explicitly here; value deserializers won't
			Object value;
			if (t == JsonToken.VALUE_NULL) {
				value = null;
			}
			// Some servers might send back complex content
			else if (t == JsonToken.START_ARRAY) {
				value = jp.readValueAs(List.class);
			}
			else if (t == JsonToken.START_OBJECT) {
				value = jp.readValueAs(Map.class);
			}
			else {
				value = jp.getText();
			}
			errorParams.put(fieldName, value);
		}

		Object errorCode = errorParams.get("error");
		String errorMessage = errorParams.containsKey("error_description") ? errorParams.get("error_description")
				.toString() : null;
		if (errorMessage == null) {
			errorMessage = errorCode == null ? "OAuth Error" : errorCode.toString();
		}

		OAuth2Exception ex;
		if ("invalid_client".equals(errorCode)) {
			ex = new InvalidClientException(errorMessage);
		}
		else if ("unauthorized_client".equals(errorCode)) {
			ex = new UnauthorizedUserException(errorMessage);
		}
		else if ("invalid_grant".equals(errorCode)) {
			if (errorMessage.toLowerCase().contains("redirect") && errorMessage.toLowerCase().contains("match")) {
				ex = new RedirectMismatchException(errorMessage);
			}
			else {
				ex = new InvalidGrantException(errorMessage);
			}
		}
		else if ("invalid_scope".equals(errorCode)) {
			ex = new InvalidScopeException(errorMessage);
		}
		else if ("invalid_token".equals(errorCode)) {
			ex = new InvalidTokenException(errorMessage);
		}
		else if ("invalid_request".equals(errorCode)) {
			ex = new InvalidRequestException(errorMessage);
		}
		else if ("redirect_uri_mismatch".equals(errorCode)) {
			ex = new RedirectMismatchException(errorMessage);
		}
		else if ("unsupported_grant_type".equals(errorCode)) {
			ex = new UnsupportedGrantTypeException(errorMessage);
		}
		else if ("unsupported_response_type".equals(errorCode)) {
			ex = new UnsupportedResponseTypeException(errorMessage);
		}
		else if ("insufficient_scope".equals(errorCode)) {
			ex = new InsufficientScopeException(errorMessage, OAuth2Utils.parseParameterList((String) errorParams
					.get("scope")));
		}
		else if ("access_denied".equals(errorCode)) {
			ex = new UserDeniedAuthorizationException(errorMessage);
		}
		else {
			ex = new OAuth2Exception(errorMessage);
		}

		Set<Map.Entry<String, Object>> entries = errorParams.entrySet();
		for (Map.Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			if (!"error".equals(key) && !"error_description".equals(key)) {
				Object value = entry.getValue();
				ex.addAdditionalInformation(key, value == null ? null : value.toString());
			}
		}

		return ex;

	}

}
