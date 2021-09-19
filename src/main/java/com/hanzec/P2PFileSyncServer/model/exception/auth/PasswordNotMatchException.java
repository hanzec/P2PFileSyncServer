package com.hanzec.P2PFileSyncServer.model.exception.auth;

import javax.naming.AuthenticationException;

public class PasswordNotMatchException extends AuthenticationException {
    public PasswordNotMatchException(String username){super("Password is not correct with username: " + username);}
}
