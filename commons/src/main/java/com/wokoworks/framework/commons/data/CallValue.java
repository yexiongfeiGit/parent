package com.wokoworks.framework.commons.data;

import lombok.Data;

import java.io.Serializable;

/**
 * CallValue class
 *
 * @author timtang
 * @date 2018/7/21
 */
@Data
public class CallValue<T, E extends Enum> implements Serializable {

	private T data;
	private E error;

	private CallValue() {

	}

	public boolean hasError() {
		return error != null;
	}

	public static <T, E extends Enum> CallValue<T, E> callOk(T data) {
		final CallValue<T, E> value = new CallValue<>();
		value.setData(data);
		return value;
	}

	public static <T, E extends Enum> CallValue<T, E> callError(E error) {
		final CallValue<T, E> value = new CallValue<>();
		value.setError(error);
		return value;
	}
}
