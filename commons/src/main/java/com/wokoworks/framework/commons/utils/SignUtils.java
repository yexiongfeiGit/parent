package com.wokoworks.framework.commons.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wokoworks.framework.commons.encrypt.RSAEncryption;
import lombok.NonNull;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * SignUtils class
 *
 * @author timtang
 * @date 2019/2/27
 */
public class SignUtils {

	public static String sign(final Map<String, String[]> argParams, final String secretKey, @NonNull String pubKey) throws GeneralSecurityException {
		final String hash = convertParams(argParams, secretKey);

		final RSAPublicKey rsaPublicKey = RSAEncryption.generatePublicKey(Base64.getDecoder().decode(pubKey));
		final RSAEncryption rsaEncryption = new RSAEncryption(rsaPublicKey, null);
		final byte[] encrypt = rsaEncryption.encrypt(hash.getBytes());

		return Base64.getEncoder().encodeToString(encrypt);
	}

	public static boolean verify(final Map<String, String[]> argParams, final String secretKey, String sign, @NonNull String priKey) throws GeneralSecurityException {
		final String hash = convertParams(argParams, secretKey);

		final RSAPrivateKey rsaPrivateKey = RSAEncryption.generatePrivateKey(Base64.getDecoder().decode(priKey));
		final RSAEncryption rsaEncryption = new RSAEncryption(null, rsaPrivateKey);

		final byte[] encrypt = rsaEncryption.decrypt(Base64.getDecoder().decode(sign));
		final String decodeHash = new String(encrypt);
		return hash.equals(decodeHash);
	}


	private static String convertParams(final Map<String, String[]> argParams, final String secretKey) {
		final Map<String, String[]> params = Maps.newHashMap(argParams);
		params.remove("sign");

		final List<String> lists = Lists.newArrayListWithCapacity(params.size() + 1);
		params.forEach((key, values) -> {
			for (String value : values) {
				lists.add(key + "=" + value);
			}
		});

		lists.sort(String::compareTo);
		lists.add(secretKey);

		final String signStr = String.join("&", lists);
		return HashUtils.sha256(signStr);
	}
}
