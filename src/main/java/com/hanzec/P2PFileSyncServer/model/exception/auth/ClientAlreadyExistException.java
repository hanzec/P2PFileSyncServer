package com.hanzec.P2PFileSyncServer.model.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class ClientAlreadyExistException extends AuthenticationException {
    public ClientAlreadyExistException(String machineID, String ip){
        super("Client：[" + ip +":" + machineID + "] is already existed");
    }
}

