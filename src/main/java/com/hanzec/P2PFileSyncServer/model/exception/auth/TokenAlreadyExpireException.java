package com.hanzec.P2PFileSyncServer.model.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class TokenAlreadyExpireException extends AuthenticationException {
    public TokenAlreadyExpireException(){super("Token is already expired");}
}
