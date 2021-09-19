package com.hanzec.P2PFileSyncServer.model.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class TokenNotCorrectException extends AuthenticationException {
    public TokenNotCorrectException(){super("Token does not contain correct endpoint");}
}
