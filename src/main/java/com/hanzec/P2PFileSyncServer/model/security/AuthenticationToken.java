package com.hanzec.P2PFileSyncServer.model.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class AuthenticationToken extends PreAuthenticatedAuthenticationToken {

    @Setter
    @Getter
    private String requestUrl;

    public AuthenticationToken(Object aPrincipal, Object aCredentials,String requestUrl) {
        super(aPrincipal, aCredentials);
        this.requestUrl = requestUrl;
    }
}
