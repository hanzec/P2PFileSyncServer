package com.hanzec.P2PFileSyncServer.service;


import com.hanzec.P2PFileSyncServer.model.data.manage.User;
import com.hanzec.P2PFileSyncServer.model.data.manage.auth.Token;
import com.hanzec.P2PFileSyncServer.repository.manage.auth.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class TokenService {
    TokenRepository userTokenRepository;

    public TokenService(TokenRepository userTokenRepository){
        this.userTokenRepository = userTokenRepository;
    }

    @Transactional
    public Token create(User user) {
        Token token = new Token(user, ZonedDateTime.now().plusDays(10));
        return userTokenRepository.save(token);
    }

    public void delete(Token userToken){
        userTokenRepository.delete(userToken);
    }

    public Token getTokenObject(UUID tokenID, UUID userID){
        return userTokenRepository.getFirstByUserIDAndTokenID(userID,tokenID);
    }

    public List<Token> getAllTokenBelongToUser(UUID userID){
        return userTokenRepository.findAllByUserID(userID);
    }
}
