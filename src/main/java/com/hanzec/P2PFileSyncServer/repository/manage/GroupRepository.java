package com.hanzec.P2PFileSyncServer.repository.manage;

import com.hanzec.P2PFileSyncServer.model.data.manage.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, String> {
    boolean existsByName(String name);

    Group getGroupByName(String name);
}
