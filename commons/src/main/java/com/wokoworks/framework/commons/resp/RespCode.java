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
	 * 获取返回错误码
	 *
	 * @return code
	 */
	int getCode();

	/**
	 * 获取返回错误消息
	 *
	 * @return msg
	 */
	String getMessage();

	enum Default implements RespCode {

		/**
		 * 成功
		 */
		SUCCESS(200, "success"),

		/**
		 * 未登陆
		 */
		NOT_LOGIN(300, "not login"),

		/**
		 * 未授权
		 */
		PERMISSION_DENIED(301, "permission denied"),

		/**
		 * 客户端参数错误
		 */
		CLIENT_PARAM_ERROR(400, "param error"),

		/**
		 * 加密解密错误
		 */
		BAD_REQUEST(401, "bad request"),

		/**
		 * 找不到接口
		 */
		NOT_FOUND_URL(404, "not found"),

		/**
		 * 服务器内部错误
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
