package com.hanzec.P2PFileSyncServer.repository.manage.auth;

import com.hanzec.P2PFileSyncServer.model.data.manage.auth.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {
    @Query("select user.userTokens from User user where user.email = ?1")
    List<Token> findAllByUserID(UUID userID);

    @Query("select token from Token token where token.tokenID = ?2 AND token.owner.userID = ?1")
    Token getFirstByUserIDAndTokenID(UUID userID, UUID tokenID);
}
