//package com.hanzec.syncdisk_server.security.filter;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import com.hanzec.syncdisk_server.model.security_model.AuthenticationToken;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.ApplicationEventPublisherAware;
//import org.springframework.security.authentication.AuthenticationDetailsSource;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.util.Assert;
//import org.springframework.web.filter.GenericFilterBean;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//public class JwtTokenFilter extends GenericFilterBean implements ApplicationEventPublisherAware {
//    private DecodedJWT authenticatedCredential = null;
//    private ApplicationEventPublisher eventPublisher = null;
//    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
//    private AuthenticationManager authenticationManager = null;
//    private boolean continueFilterChainOnUnsuccessfulAuthentication = true;
//    private AuthenticationSuccessHandler authenticationSuccessHandler = null;
//    private AuthenticationFailureHandler authenticationFailureHandler = null;
//
//    public JwtTokenFilter(AuthenticationManager authenticationManager){
//        this.setAuthenticationManager(authenticationManager);
//    }
//
//    public void afterPropertiesSet() {
//        try {
//            super.afterPropertiesSet();
//        } catch (ServletException var2) {
//            throw new RuntimeException(var2);
//        }
//        Assert.notNull(this.authenticationManager, "An AuthenticationManager must be set");
//    }
//
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        if (((HttpServletRequest) request).getHeader("Authorization") != null){
//            this.authenticatedCredential = JWT.decode(((HttpServletRequest) request).getHeader("Authorization"));
//            this.doAuthenticate((HttpServletRequest)request, (HttpServletResponse)response);
//        }
//        chain.doFilter(request,response);
//    }
//
//    protected boolean principalChanged(HttpServletRequest request, Authentication currentAuthentication) {
//        Object principal = this.authenticatedCredential.getId();
//
//        if (principal != null && currentAuthentication.getName().equals(principal)) {
//            return false;
//        } else if (principal != null && principal.equals(currentAuthentication.getPrincipal())) {
//            return false;
//        } else {
//            if (this.logger.isDebugEnabled()) {
//                this.logger.debug("Pre-authenticated principal has changed to " + principal + " and will be reauthenticated");
//            }
//
//            return true;
//        }
//    }
//
//    private void doAuthenticate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        Object principal = this.authenticatedCredential.getId();
//        if (principal == null) {
//            if (this.logger.isDebugEnabled()) {
//                this.logger.debug("No pre-authenticated principal found in request");
//            }
//
//        } else {
//            if (this.logger.isDebugEnabled()) {
//                this.logger.debug("preAuthenticatedPrincipal = " + principal + ", trying to authenticate");
//            }
//
//            try {
//                AuthenticationToken authRequest = new AuthenticationToken(principal, this.authenticatedCredential, request.getRequestURI());
//                authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
//                Authentication authResult = this.authenticationManager.authenticate(authRequest);
//                this.successfulAuthentication(request, response, authResult);
//            } catch (AuthenticationException var7) {
//                this.unsuccessfulAuthentication(request, response, var7);
//                if (!this.continueFilterChainOnUnsuccessfulAuthentication) {
//                    throw var7;
//                }
//            }
//
//        }
//    }
//
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
//        if (this.logger.isDebugEnabled()) {
//            this.logger.debug("Authentication success: " + authResult);
//        }
//
//        SecurityContextHolder.getContext().setAuthentication(authResult);
//        if (this.eventPublisher != null) {
//            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
//        }
//
//        if (this.authenticationSuccessHandler != null) {
//            this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
//        }
//
//    }
//
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//        SecurityContextHolder.clearContext();
//        if (this.logger.isDebugEnabled()) {
//            this.logger.debug("Cleared security context due to exception", failed);
//        }
//
//        request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", failed);
//        if (this.authenticationFailureHandler != null) {
//            this.authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
//        }
//
//    }
//
//    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher anApplicationEventPublisher) {
//        this.eventPublisher = anApplicationEventPublisher;
//    }
//
//    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
//        Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
//        this.authenticationDetailsSource = authenticationDetailsSource;
//    }
//
//    protected AuthenticationDetailsSource<HttpServletRequest, ?> getAuthenticationDetailsSource() {
//        return this.authenticationDetailsSource;
//    }
//
//    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }
//
//    public void setContinueFilterChainOnUnsuccessfulAuthentication(boolean shouldContinue) {
//        this.continueFilterChainOnUnsuccessfulAuthentication = shouldContinue;
//    }
//
//    public void setCheckForPrincipalChanges(boolean checkForPrincipalChanges) {
//    }
//
//    public void setInvalidateSessionOnPrincipalChange(boolean invalidateSessionOnPrincipalChange) {
//    }
//
//    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
//        this.authenticationSuccessHandler = authenticationSuccessHandler;
//    }
//
//    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
//        this.authenticationFailureHandler = authenticationFailureHandler;
//    }
//}
