package com.hanzec.P2PFileSyncServer.model.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class PermissionDeniedException extends AuthenticationException {
    public PermissionDeniedException(String username){super("User :" + username + "does not have enough permission to do that");}
}
