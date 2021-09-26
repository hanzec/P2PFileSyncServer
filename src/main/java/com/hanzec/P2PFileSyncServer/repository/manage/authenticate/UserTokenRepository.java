package com.hanzec.P2PFileSyncServer.repository.manage.authenticate;

import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {
    @Query("select user.tokens from UserAccount user where user.email = ?1")
    List<UserToken> findAllByUserID(UUID userID);

    @Query("select token from UserToken token where token.tokenID = ?2 AND token.owner.email = ?1")
    UserToken getFirstByUserIDAndTokenID(String user_email, UUID tokenID);
}
