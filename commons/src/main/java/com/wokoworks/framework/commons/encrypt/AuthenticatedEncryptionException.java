package com.wokoworks.framework.commons.encrypt;

/**
 * @author 0x0001
 */
public class AuthenticatedEncryptionException extends Exception {

    public AuthenticatedEncryptionException(String message) {
        super(message);
    }

    public AuthenticatedEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}