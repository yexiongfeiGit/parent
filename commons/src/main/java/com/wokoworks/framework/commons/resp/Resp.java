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
	 * 构建Resp对象
	 *
	 * @param respCode 响应代码 (只支持枚举)
	 * @param data     响应数据
	 * @param <T>      响应的数据类型
	 * @return
	 */
	public static <T> Resp<T> make(RespCode respCode, T data) {
		return make(respCode, respCode.getMessage(), data);
	}

	/**
	 * 构建Resp对象
	 *
	 * @param respCode 响应代码 (只支持枚举)
	 * @param <T>      响应的数据类型
	 * @return
	 */
	public static <T> Resp<T> make(RespCode respCode) {
		return make(respCode, respCode.getMessage(), null);
	}

	/**
	 * 构建Resp对象
	 *
	 * @param respCode 响应代码 (只支持枚举)
	 * @param msg      响应消息
	 * @param data     响应数据
	 * @param <T>      data的类型
	 * @return
	 */
	public static <T> Resp<T> make(RespCode respCode, String msg, T data) {
		// 检查只支持枚举
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
