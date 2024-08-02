


package com.ldapauth.crypto.jwt;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.util.Base64URL;

public class HMAC512Service {

	public final static String MXK_AUTH_JWK = "mxk_auth_jwk";

	JWSSigner signer;

	MACVerifier verifier;

	public HMAC512Service() {
		super();
	}

	public HMAC512Service(String secretString) throws JOSEException {
		Base64URL secret= new Base64URL(secretString);
		OctetSequenceKey octKey=  new OctetSequenceKey.Builder(secret)
				.keyID(MXK_AUTH_JWK)
				.keyUse(KeyUse.SIGNATURE)
				.algorithm(JWSAlgorithm.HS512)
				.build();
		signer = new MACSigner(octKey);
		verifier = new MACVerifier(octKey);
	}

	public String sign(Payload payload) {
		try {
			// Prepare JWS object with payload HS512
			JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS512), payload);
			// Apply the HMAC
			jwsObject.sign(signer);
			String jwt = jwsObject.serialize();
			return jwt;
		} catch (JOSEException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String sign(String  payload) {
		return sign(new Payload(payload));
	}


	public boolean verify(String jwt) {
		try {
		JWSObject jwsObjected =JWSObject.parse(jwt);
		boolean isVerifier = verifier.verify(
								jwsObjected.getHeader(),
								jwsObjected.getSigningInput(),
								jwsObjected.getSignature());
		return isVerifier;
		}catch(JOSEException JOSEException) {

		}catch(ParseException ParseException) {

		}
		return false;
	}
}
