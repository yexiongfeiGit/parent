package com.wokoworks.framework.commons.resp;

import com.google.common.base.Preconditions;
import lombok.Data;

/**
 * Resp class
 *
 * @author timtang
 * @date 2018/8/25
 */
@Data
public class Resp<T> {

	private int code;
	private String msg;
	private T data;

	private Resp() {

	}

	/**
	 * Build Resp Object
	 *
	 * @param respCode Response code (Only support enumeration)
	 * @param data     Response data
	 * @param <T>      Response data type
	 * @return
	 */
	public static <T> Resp<T> make(RespCode respCode, T data) {
		return make(respCode, respCode.getMessage(), data);
	}

	/**
	 * Build Resp Object
	 *
	 * @param respCode Response code (Only support enumeration)
	 * @param <T>      Response data type
	 * @return
	 */
	public static <T> Resp<T> make(RespCode respCode) {
		return make(respCode, respCode.getMessage(), null);
	}

	/**
	 * Build Resp Object
	 *
	 * @param respCode Response code (Only support enumeration)
	 * @param msg      Response message
	 * @param data     Response data
	 * @param <T>      data Type
	 * @return
	 */
	public static <T> Resp<T> make(RespCode respCode, String msg, T data) {
		// Check only support enumeration
		Preconditions.checkArgument(respCode.getClass().isEnum(), "resp code must be enum");
		return make(respCode.getCode(), msg, data);
	}

	public static <T> Resp<T> failed(T data) {
		return make(RespCode.Default.SERVER_ERROR, "server error", data);
	}

	public static <T> Resp<T> ok(T data) {
		return make(RespCode.Default.SUCCESS, "success", data);
	}



	private static <T> Resp<T> make(int value, String msg, T data) {
		Resp<T> resp = new Resp<>();
		resp.setCode(value);
		resp.setMsg(msg);
		resp.setData(data);
		return resp;
	}
}
