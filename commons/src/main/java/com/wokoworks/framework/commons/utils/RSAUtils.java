package com.wokoworks.framework.commons.utils;

import com.wokoworks.framework.commons.encrypt.RSAEncryption;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * RSAUtils class
 *
 * @author timtang
 * @date 2018/7/27
 */
public class RSAUtils {

	public static String encrypt(final String content, String publicKey) throws GeneralSecurityException {
		RSAPublicKey rsaPublicKey = RSAEncryption.generatePublicKey(Base64.getDecoder().decode(publicKey));
		RSAEncryption rsaEncryption = new RSAEncryption(rsaPublicKey, null);
		byte[] encrypt = rsaEncryption.encrypt(content.getBytes());
		return Base64.getEncoder().encodeToString(encrypt);
	}

	public static String decrypt(final String ciphertext, String priKey) throws GeneralSecurityException {
		RSAPrivateKey rsaPrivateKey = RSAEncryption.generatePrivateKey(Base64.getDecoder().decode(priKey));
		RSAEncryption rsaEncryption = new RSAEncryption(null, rsaPrivateKey);
		byte[] encrypt = rsaEncryption.decrypt(Base64.getDecoder().decode(ciphertext));
		return new String(encrypt);
	}
}
