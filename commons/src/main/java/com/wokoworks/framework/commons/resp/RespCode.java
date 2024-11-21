package com.wokoworks.framework.commons.resp;

import java.io.Serializable;

/**
 * RespCode class
 *
 * @author timtang
 * @date 2018/8/24
 */
public interface RespCode extends Serializable {

	/**
	 * Get the return error code
	 *
	 * @return code
	 */
	int getCode();

	/**
	 * Get the error message
	 *
	 * @return msg
	 */
	String getMessage();

	enum Default implements RespCode {

		/**
		 * success
		 */
		SUCCESS(200, "success"),

		/**
		 * Not login
		 */
		NOT_LOGIN(300, "not login"),

		/**
		 * Unauthorized
		 */
		PERMISSION_DENIED(301, "permission denied"),

		/**
		 * Client parameter error
		 */
		CLIENT_PARAM_ERROR(400, "param error"),

		/**
		 * Encryption and decryption
		 */
		BAD_REQUEST(401, "bad request"),

		/**
		 * Can't find the interface
		 */
		NOT_FOUND_URL(404, "not found"),

		/**
		 * Internal server error
		 */
		SERVER_ERROR(500, "server error"),
		;


		public final int value;
		public final String remark;

		Default(int value, String remark) {
			this.value = value;
			this.remark = remark;
		}

		public static Default valueOf(int value) {
			for (Default respCode : values()) {
				if (respCode.value == value) {
					return respCode;
				}
			}
			return null;
		}

		@Override
		public int getCode() {
			return value;
		}

		@Override
		public String getMessage() {
			return remark;
		}
	}
}
