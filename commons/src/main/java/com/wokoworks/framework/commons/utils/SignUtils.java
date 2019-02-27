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
import java.util.stream.Collectors;

/**
 * SignUtils class
 *
 * @author timtang
 * @date 2019/2/27
 */
public class SignUtils {

	public static String sign(final Map<String, String[]> argParams, final String secretKey, @NonNull String pubKey) throws GeneralSecurityException {
		String hash = convertParams(argParams, secretKey);

		RSAPublicKey rsaPublicKey = RSAEncryption.generatePublicKey(Base64.getDecoder().decode(pubKey));
		RSAEncryption rsaEncryption = new RSAEncryption(rsaPublicKey, null);
		byte[] encrypt = rsaEncryption.encrypt(hash.getBytes());

		return Base64.getEncoder().encodeToString(encrypt);
	}

	public static boolean verify(final Map<String, String[]> argParams, final String secretKey, String sign, String priKey) throws GeneralSecurityException {
		String hash = convertParams(argParams, secretKey);

		RSAPrivateKey rsaPrivateKey = RSAEncryption.generatePrivateKey(Base64.getDecoder().decode(priKey));
		RSAEncryption rsaEncryption = new RSAEncryption(null, rsaPrivateKey);

		byte[] encrypt = rsaEncryption.decrypt(Base64.getDecoder().decode(sign));
		String decodeHash = new String(encrypt);
		if (hash.equals(decodeHash)) {
			return true;
		}

		return false;
	}


	private static String convertParams(final Map<String, String[]> argParams, final String secretKey) {
		final Map<String, String[]> params = Maps.newHashMap(argParams);
		params.remove("sign");

		List<String> lists = Lists.newArrayList();
		params.forEach((key, values) -> {
			for (String value : values) {
				lists.add(key + "=" + value);
			}
		});

		lists.sort(String::compareTo);
		String signStr = lists.stream().collect(Collectors.joining("&")) + "&" + secretKey;
		return HashUtils.sha256(signStr);
	}
}
