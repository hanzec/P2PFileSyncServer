package com.hanzec.P2PFileSyncServer.repository.manage.account;

import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, String> {
    boolean existsByName(String name);

    UserGroup getGroupByName(String name);
}
