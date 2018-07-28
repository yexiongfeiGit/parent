package com.wokoworks.framework.commons.utils;

import com.wokoworks.framework.commons.encrypt.RSAEncryption;
import org.junit.Assert;
import org.junit.Test;

import java.security.GeneralSecurityException;

/**
 * RSAUtilsTest class
 *
 * @author timtang
 * @date 2018/7/28
 */
public class RSAUtilsTest {

	private final static String pubKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIUw2nG3dazMLzhFgB5P3g/CNMs//y6ArZqX8hESMES506ZfeUMm87OFQ5XQFKXrSjugcsvyrbxkflgus+bDLkECAwEAAQ==";
	private final static String priKey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAhTDacbd1rMwvOEWAHk/eD8I0yz//LoCtmpfyERIwRLnTpl95Qybzs4VDldAUpetKO6Byy/KtvGR+WC6z5sMuQQIDAQABAkBiqwxIjeZI+oJIp4P/8J7LAc3mfCRoqL7y9XlIGDdV0Vek2ufi0TWxoI3drQlwD4qjxe9o0aFz+j0r+qcMpIJ5AiEA1IIq48wCb70Q994fmlHZeCXgU4XoGLHdnQhAYwHwFPMCIQCgcwsKOECIdhw4PPksx3T4BZzBeZreaRfLezDa0N/M+wIgK+a0O5kyy6iwb7R4rOXJ2nNgs4CPYUKleMJyosGMupcCIQCdDR6wSTF5oUqsDZrFJxMqNqwigTqDG5FL/nBqrutxgwIgO1w3+JkpCwehkyCY1o7KN01TT5KcAoQpVM5UUtkVgac=";

	@Test
	public void encrypt() throws GeneralSecurityException {
		String[] strArrs = {"a", "b", "234", "bddrh"};
		for (String str: strArrs) {
			String encrypt = RSAUtils.encrypt(str, pubKey);
			String decrypt = RSAUtils.decrypt(encrypt, priKey);
			Assert.assertEquals("rsa encrypt result equal encrypt", str, decrypt);
		}
	}

	@Test
	public void decrypt() throws GeneralSecurityException {
		RSAEncryption.EncryptKey encryptKey = RSAEncryption.generateEncryptKey(512);
		String priK = encryptKey.getPriKey();
		String pubK = encryptKey.getPubKey();
		String[] strArrs = {"a", "b", "234", "bddrh"};
		for (String str: strArrs) {
			String encrypt = RSAUtils.encrypt(str, pubK);
			String decrypt = RSAUtils.decrypt(encrypt, priK);
			Assert.assertEquals("rsa encrypt result equal encrypt", str, decrypt);
		}
	}

}