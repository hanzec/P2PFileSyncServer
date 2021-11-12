package com.hanzec.P2PFileSyncServer.security.filter;

import com.google.gson.Gson;
import com.hanzec.P2PFileSyncServer.model.security.JwtPayload;
import com.nimbusds.jose.JWSObject;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

public class ClientLoginFilter extends OncePerRequestFilter {
    private final Gson gson;
    private final AuthenticationManager authenticationManager;

    /**
     * This filter will only handle all incoming request under "/api/v1/client" API
     */
    public ClientLoginFilter(Gson gson, AuthenticationManager authenticationManager) {
        this.gson = gson;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request.getHeader("Authorization") != null){
            UsernamePasswordAuthenticationToken authRequest;
            try {
                JWSObject jwsObject = JWSObject.parse(request.getHeader("Authorization"));
                var payload = gson.fromJson(jwsObject.getPayload().toString(), JwtPayload.class);
                this.logger.trace(LogMessage.format("Found username '%s' in Basic Authorization header", payload.getClientID()));
                authRequest = new UsernamePasswordAuthenticationToken(payload.getClientID(), jwsObject, null);
                Authentication authResult = this.authenticationManager.authenticate(authRequest);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            } catch (AuthenticationException | ParseException ex) {
                SecurityContextHolder.clearContext();
                this.logger.debug("Failed to process authentication request", ex);
            }
        }
        chain.doFilter(request, response);
    }
}
