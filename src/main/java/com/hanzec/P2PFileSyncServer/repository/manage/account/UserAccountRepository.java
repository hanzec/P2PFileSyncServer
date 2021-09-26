package com.hanzec.P2PFileSyncServer.repository.manage.account;

import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
}