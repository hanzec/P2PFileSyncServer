package com.hanzec.P2PFileSyncServer.security.provider;

import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.Permission;
import com.hanzec.P2PFileSyncServer.model.exception.auth.TokenCanNotParse;
import com.hanzec.P2PFileSyncServer.security.filter.ClientLoginFilter;
import com.hanzec.P2PFileSyncServer.service.AccountService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Component
public class ClientAuthenticationProvider implements AuthenticationProvider {
    private final AccountService accountService;

    public ClientAuthenticationProvider(AccountService accountService){
        this.accountService = accountService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String clientID = authentication.getName();
        ClientAccount account = accountService.loadClientByClientID(clientID);

        // Credential must be instasnce ofJWSObject
        if(!(authentication.getCredentials() instanceof JWSObject)) return null;

        JWSObject jwtObject = (JWSObject) authentication.getCredentials();

        // account should be enabled
        if(!account.isEnabled()) return null;

        try {
            if (accountService.verifyClientToken(account, jwtObject)) {
                return new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
            } else {
                return null;
            }
        } catch (JOSEException e) {
            throw new TokenCanNotParse(e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
