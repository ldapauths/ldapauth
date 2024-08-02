package com.ldapauth.authz.oauth2.provider.token.store;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ldapauth.authz.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import com.ldapauth.authz.oauth2.common.DefaultOAuth2AccessToken;
import com.ldapauth.authz.oauth2.common.DefaultOAuth2RefreshToken;
import com.ldapauth.authz.oauth2.common.ExpiringOAuth2RefreshToken;
import com.ldapauth.authz.oauth2.common.OAuth2AccessToken;
import com.ldapauth.authz.oauth2.common.OAuth2RefreshToken;
import com.ldapauth.authz.oauth2.common.exceptions.InvalidTokenException;
import com.ldapauth.authz.oauth2.common.util.JsonParser;
import com.ldapauth.authz.oauth2.common.util.JsonParserFactory;
import com.ldapauth.authz.oauth2.common.util.RandomValueStringGenerator;
import com.ldapauth.authz.oauth2.jwt.Jwt;
import com.ldapauth.authz.oauth2.jwt.JwtHelper;
import com.ldapauth.authz.oauth2.jwt.crypto.sign.InvalidSignatureException;
import com.ldapauth.authz.oauth2.jwt.crypto.sign.MacSigner;
import com.ldapauth.authz.oauth2.jwt.crypto.sign.RsaSigner;
import com.ldapauth.authz.oauth2.jwt.crypto.sign.RsaVerifier;
import com.ldapauth.authz.oauth2.jwt.crypto.sign.SignatureVerifier;
import com.ldapauth.authz.oauth2.jwt.crypto.sign.Signer;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;
import com.ldapauth.authz.oauth2.provider.token.AccessTokenConverter;
import com.ldapauth.authz.oauth2.provider.token.DefaultAccessTokenConverter;
import com.ldapauth.authz.oauth2.provider.token.TokenEnhancer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Helper that translates between JWT encoded token values and OAuth
 * authentication information (in both directions). Also acts as a
 * {@link TokenEnhancer} when tokens are granted.
 *
 * @see TokenEnhancer
 * @see AccessTokenConverter
 *
 * @author Dave Syer
 * @author Luke Taylor
 */
public class JwtAccessTokenConverter implements TokenEnhancer, AccessTokenConverter, InitializingBean {

    /**
     * Field name for token id.
     */
    public static final String TOKEN_ID = AccessTokenConverter.JTI;

    private static final Log logger = LogFactory.getLog(JwtAccessTokenConverter.class);

    private AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();

    private JsonParser objectMapper = JsonParserFactory.create();

    private String verifierKey = new RandomValueStringGenerator().generate();

    private Signer signer = new MacSigner(verifierKey);

    private String signingKey = verifierKey;

    private SignatureVerifier verifier;

    /**
     * @param tokenConverter the tokenConverter to set
     */
    public void setAccessTokenConverter(AccessTokenConverter tokenConverter) {
        this.tokenConverter = tokenConverter;
    }

    /**
     * @return the tokenConverter in use
     */
    public AccessTokenConverter getAccessTokenConverter() {
        return tokenConverter;
    }

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        return tokenConverter.convertAccessToken(token, authentication);
    }

    @Override
    public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
        return tokenConverter.extractAccessToken(value, map);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        return tokenConverter.extractAuthentication(map);
    }

    /**
     * Get the verification key for the token signatures.
     *
     * @return the key used to verify tokens
     */
    public Map<String, String> getKey() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        result.put("alg", signer.algorithm());
        result.put("value", verifierKey);
        return result;
    }

    public void setKeyPair(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        Assert.state(privateKey instanceof RSAPrivateKey, "KeyPair must be an RSA ");
        signer = new RsaSigner((RSAPrivateKey) privateKey);
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        verifier = new RsaVerifier(publicKey);
        verifierKey = "-----BEGIN PUBLIC KEY-----\n" + new String(Base64.getMimeEncoder().encodeToString(publicKey.getEncoded()))
                + "\n-----END PUBLIC KEY-----";
    }

    /**
     * Sets the JWT signing key. It can be either a simple MAC key or an RSA key.
     * RSA keys should be in OpenSSH format, as produced by <tt>ssh-keygen</tt>.
     *
     * @param key the key to be used for signing JWTs.
     */
    public void setSigningKey(String key) {
        Assert.hasText(key,"key must not be empty");
        key = key.trim();

        this.signingKey = key;

        if (isPublic(key)) {
            signer = new RsaSigner(key);
            logger.info("Configured with RSA signing key");
        } else {
            // Assume it's a MAC key
            this.verifierKey = key;
            signer = new MacSigner(key);
        }
    }

    /**
     * @return true if the key has a public verifier
     */
    private boolean isPublic(String key) {
        return key.startsWith("-----BEGIN");
    }

    /**
     * @return true if the signing key is a public key
     */
    public boolean isPublic() {
        return signer instanceof RsaSigner;
    }

    /**
     * The key used for verifying signatures produced by this class. This is not
     * used but is returned from the endpoint to allow resource servers to obtain
     * the key.
     *
     * For an HMAC key it will be the same value as the signing key and does not
     * need to be set. For and RSA key, it should be set to the String
     * representation of the public key, in a standard format (e.g. OpenSSH keys)
     *
     * @param key the signature verification key (typically an RSA public key)
     */
    public void setVerifierKey(String key) {
        this.verifierKey = key;
        try {
            new RsaSigner(verifierKey);
            throw new IllegalArgumentException("Private key cannot be set as verifierKey property");
        } catch (Exception expected) {
            // Expected
        }
    }

    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
        String tokenId = result.getValue();
        if (!info.containsKey(TOKEN_ID)) {
            info.put(TOKEN_ID, tokenId);
        }
        result.setAdditionalInformation(info);
        result.setValue(encode(result, authentication));
        OAuth2RefreshToken refreshToken = result.getRefreshToken();
        if (refreshToken != null) {
            DefaultOAuth2AccessToken encodedRefreshToken = new DefaultOAuth2AccessToken(accessToken);
            encodedRefreshToken.setValue(refreshToken.getValue());
            Map<String, Object> refreshTokenInfo = new LinkedHashMap<String, Object>(
                    accessToken.getAdditionalInformation());
            refreshTokenInfo.put(TOKEN_ID, encodedRefreshToken.getValue());
            encodedRefreshToken.setAdditionalInformation(refreshTokenInfo);
            DefaultOAuth2RefreshToken token = new DefaultOAuth2RefreshToken(
                    encode(encodedRefreshToken, authentication));
            if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                Date expiration = ((ExpiringOAuth2RefreshToken) refreshToken).getExpiration();
                encodedRefreshToken.setExpiration(expiration);
                token = new DefaultExpiringOAuth2RefreshToken(encode(encodedRefreshToken, authentication), expiration);
            }
            result.setRefreshToken(token);
        }
        return result;
    }

    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String content;
        try {
            content = objectMapper.formatMap(tokenConverter.convertAccessToken(accessToken, authentication));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot convert access token to JSON", e);
        }
        String token = JwtHelper.encode(content, signer).getEncoded();
        return token;
    }

    protected Map<String, Object> decode(String token) {
        try {
            Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
            String content = jwt.getClaims();
            Map<String, Object> map = objectMapper.parseMap(content);
            if (map.containsKey(EXP) && map.get(EXP) instanceof Integer) {
                Integer intValue = (Integer) map.get(EXP);
                map.put(EXP, Integer.toUnsignedLong(intValue));
            }
            return map;
        } catch (Exception e) {
            throw new InvalidTokenException("Cannot convert access token to JSON", e);
        }
    }

    public void afterPropertiesSet() throws Exception {
        // Check the signing and verification keys match
        if (signer instanceof RsaSigner) {
            RsaVerifier verifier;
            try {
                verifier = new RsaVerifier(verifierKey);
            } catch (Exception e) {
                logger.warn("Unable to create an RSA verifier from verifierKey");
                return;
            }

            byte[] test = "test".getBytes();
            try {
                verifier.verify(test, signer.sign(test));
                logger.info("Signing and verification RSA keys match");
            } catch (InvalidSignatureException e) {
                logger.error("Signing and verification RSA keys do not match");
            }
        } else {
            // Avoid a race condition where setters are called in the wrong order. Use of ==
            // is intentional.
            Assert.state(this.signingKey == this.verifierKey,
                    "For MAC signing you do not need to specify the verifier key separately, and if you do it must match the signing key");
        }
        SignatureVerifier verifier = new MacSigner(verifierKey);
        try {
            verifier = new RsaVerifier(verifierKey);
        } catch (Exception e) {
            logger.warn("Unable to create an RSA verifier from verifierKey");
        }
        this.verifier = verifier;
    }
}
