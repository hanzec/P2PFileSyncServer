package com.hanzec.P2PFileSyncServer.service;


import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.UserToken;
import com.hanzec.P2PFileSyncServer.repository.manage.authenticate.UserTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class TokenService {
    UserTokenRepository userTokenRepository;

    public TokenService(UserTokenRepository userTokenRepository){
        this.userTokenRepository = userTokenRepository;
    }

    @Transactional
    public UserToken create(UserAccount userAccount) {
        UserToken userToken = new UserToken(userAccount, ZonedDateTime.now().plusDays(10));
        return userTokenRepository.save(userToken);
    }

    public void delete(UserToken userToken){
        userTokenRepository.delete(userToken);
    }

    public UserToken getTokenObject(UUID tokenID, String userEmail){
        return userTokenRepository.getFirstByUserIDAndTokenID(userEmail,tokenID);
    }

    public List<UserToken> getAllTokenBelongToUser(UUID userID){
        return userTokenRepository.findAllByUserID(userID);
    }
}
