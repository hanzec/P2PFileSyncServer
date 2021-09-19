package com.hanzec.P2PFileSyncServer.model.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class TokenNotFoundException extends AuthenticationException {
    public TokenNotFoundException(String tokenId) {
        super("token [" + tokenId + "] not belongs for current user!");
    }
}
