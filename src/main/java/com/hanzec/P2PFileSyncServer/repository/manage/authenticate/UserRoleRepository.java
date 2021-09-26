package com.hanzec.P2PFileSyncServer.repository.manage.authenticate;

import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer>{
    boolean existsByName(String name);

    UserRole getUserRoleByName(String name);
}
