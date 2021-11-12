package com.hanzec.P2PFileSyncServer.model.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class TokenCanNotParse extends AuthenticationException {
    public TokenCanNotParse(Throwable cause) {
        super("Token parse failed!", cause);
    }
}
