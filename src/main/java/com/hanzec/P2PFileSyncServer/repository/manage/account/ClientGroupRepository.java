package com.hanzec.P2PFileSyncServer.repository.manage.account;

import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientGroup;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientGroupRepository extends JpaRepository<ClientGroup, String> {
    boolean existsByName(String name);

    ClientGroup getGroupByName(String name);
}
