package com.wokoworks.framework.commons.utils;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * SignUtilsTest class
 *
 * @author timtang
 * @date 2019/2/27
 */
public class SignUtilsTest {

	private final static String pubKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIUw2nG3dazMLzhFgB5P3g/CNMs//y6ArZqX8hESMES506ZfeUMm87OFQ5XQFKXrSjugcsvyrbxkflgus+bDLkECAwEAAQ==";
	private final static String priKey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAhTDacbd1rMwvOEWAHk/eD8I0yz//LoCtmpfyERIwRLnTpl95Qybzs4VDldAUpetKO6Byy/KtvGR+WC6z5sMuQQIDAQABAkBiqwxIjeZI+oJIp4P/8J7LAc3mfCRoqL7y9XlIGDdV0Vek2ufi0TWxoI3drQlwD4qjxe9o0aFz+j0r+qcMpIJ5AiEA1IIq48wCb70Q994fmlHZeCXgU4XoGLHdnQhAYwHwFPMCIQCgcwsKOECIdhw4PPksx3T4BZzBeZreaRfLezDa0N/M+wIgK+a0O5kyy6iwb7R4rOXJ2nNgs4CPYUKleMJyosGMupcCIQCdDR6wSTF5oUqsDZrFJxMqNqwigTqDG5FL/nBqrutxgwIgO1w3+JkpCwehkyCY1o7KN01TT5KcAoQpVM5UUtkVgac=";
	private final static String secretKey = "900150983cd24fb0d6963f7d28e17f72";

	@Test
	public void testSign() throws GeneralSecurityException {
		Map<String, String[]> argParams = Maps.newHashMap();
		argParams.put("name", new String[] {"tim"});
		argParams.put("age", new String[] {"24"});
		argParams.put("sex", new String[] {"male"});

		String sign = SignUtils.sign(argParams, secretKey, pubKey);
		boolean verify = SignUtils.verify(argParams, secretKey, sign, priKey);

		Assert.assertTrue("verify sign is true", verify);
	}
}
