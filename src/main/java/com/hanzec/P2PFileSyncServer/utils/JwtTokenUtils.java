package com.hanzec.P2PFileSyncServer.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class JwtTokenUtils {
    static String generateToken(String requestUrl, String tokenID, String password, Integer expireTime){
        Algorithm algorithmHS = Algorithm.HMAC256(password);

        return JWT.create()
                .withIssuer("CySchedule")
                .withJWTId(tokenID)
                .withClaim("requestUrl",requestUrl)
                .withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
                .sign(algorithmHS);
    }
}
