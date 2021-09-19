//package com.hanzec.syncdisk_server.security.provider;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.JWTVerifier;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTVerificationException;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import com.hanzec.syncdisk_server.service.TokenService;
//import com.hanzec.syncdisk_server.model.exception.auth.TokenAlreadyExpireException;
//import com.hanzec.syncdisk_server.model.exception.auth.TokenVerifyFaildException;
//import com.hanzec.syncdisk_server.model.data_model.auth.UserToken;
//import com.hanzec.syncdisk_server.model.security_model.AuthenticationToken;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
//import org.springframework.stereotype.Component;
//
//import java.sql.Time;
//
//
//
//@Component
//public class JwtTokenAuthenticationProvider implements AuthenticationProvider {
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    TokenService tokenService;
//
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        PreAuthenticatedAuthenticationToken credential = (PreAuthenticatedAuthenticationToken) authentication;
//        DecodedJWT decodedJWT = (DecodedJWT) credential.getCredentials();
//        UserToken token = tokenService.getTokenObject((String) credential.getPrincipal());
//
//        if (token.getExpireTime().after(new Time(System.currentTimeMillis())))
//            throw new TokenAlreadyExpireException();
//
//
////        String result = decodedJWT.getClaim("requestUrl").asString();
////        //Verify Token details
////        if(!credential.getRequestUrl().equals(decodedJWT.getClaim("requestUrl").asString()))
////            throw new TokenNotCorrectException();
//
//        //Verify Token changed or not
//        Algorithm algorithm = Algorithm.HMAC256(token.getSecret());
//        try {
//            JWTVerifier verifier = JWT.require(algorithm)
//                    .withIssuer("CySchedule")
//                    .acceptLeeway(1)   //1 sec for nbf and iat
//                    .acceptExpiresAt(5)   //5 secs for exp
//                    .build();
//            DecodedJWT jwt = verifier.verify(decodedJWT);
//        } catch (JWTVerificationException exception) {
//            throw new TokenVerifyFaildException();
//        }
//
//        logger.debug("A new Request for user [" + token.getOwner().getUserID() + "] is success authenticated");
//        return new UsernamePasswordAuthenticationToken(token.getOwner().getUserID(),null,token.getUserRoles());
//    }
//
//    @Override
//    public boolean supports(Class<?> aClass) {
//        return aClass.equals(AuthenticationToken .class);
//    }
//}
