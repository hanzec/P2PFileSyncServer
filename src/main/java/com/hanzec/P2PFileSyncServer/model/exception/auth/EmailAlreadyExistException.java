package com.hanzec.P2PFileSyncServer.model.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class EmailAlreadyExistException extends AuthenticationException {
    public EmailAlreadyExistException(String email){super("Email : " + email + "is already existed");}

}
