package com.wokoworks.framework.data;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.Serializable;

/**
 * @author 0x0001
 */
@Data
@Slf4j
public class ReturnValue<T, E extends Enum> implements Serializable {

    private T data;
    private E error;

    private ReturnValue() {
        //
    }

    public boolean hasError() {
        return error != null;
    }

    public static <T, E extends Enum> ReturnValue<T, E> withOk(T data) {
        final ReturnValue<T, E> value = new ReturnValue<>();
        value.setData(data);
        return value;
    }

    public static <T, E extends Enum> ReturnValue<T, E> withError(E error) {
        // Rollback
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (NoTransactionException e) {
            log.debug(e.getMessage(), e);
            // quiet
        }
        final ReturnValue<T, E> value = new ReturnValue<>();
        value.setError(error);
        return value;
    }
}
