package com.jiebao.platfrom.common.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * token过期异常
 */
public class TokenTimeoutException extends AuthenticationException {

    private static final long serialVersionUID = -8313101744886192005L;

    public TokenTimeoutException(String message) {
        super(message);
    }
}
